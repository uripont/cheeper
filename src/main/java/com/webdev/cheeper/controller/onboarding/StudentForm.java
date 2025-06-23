package com.webdev.cheeper.controller.onboarding;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.model.Student;
import com.webdev.cheeper.repository.StudentRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.StudentService;
import com.webdev.cheeper.service.UserService;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@MultipartConfig
@WebServlet("/auth/student-form")
public class StudentForm extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserRepository userRepository;
    private UserService userService;
    private StudentRepository studentRepository;
    private StudentService studentService;

    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.userService = new UserService(userRepository);
        this.studentRepository = new StudentRepository();
        this.studentService = new StudentService(userRepository, studentRepository);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mode = request.getParameter("mode");
        
        if ("edit".equals(mode)) {
            // Get current user from session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            String email = (String) session.getAttribute("email");
            Optional<User> currentUserOpt = userService.getUserByEmail(email);
            
            if (currentUserOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Current user not found");
                return;
            }
            
            User currentUser = currentUserOpt.get();
            User targetUser = currentUser; // Default to current user's profile
            
            String userIdParam = request.getParameter("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                try {
                    int userId = Integer.parseInt(userIdParam);
                    // If current user is an ENTITY, they can edit any profile
                    if (currentUser.getRoleType() == com.webdev.cheeper.model.RoleType.ENTITY) {
                        Optional<User> targetUserOpt = userRepository.findById(userId);
                        if (targetUserOpt.isEmpty()) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Target user not found");
                            return;
                        }
                        targetUser = targetUserOpt.get();
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
                    return;
                }
            }

            // Get full student profile for the target user
            Optional<Student> studentOpt = studentService.getProfile(targetUser.getId());
            
            if (studentOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student profile not found for target user");
                return;
            }
            
            // Pre-populate form with student data
            Student student = studentOpt.get();
            request.setAttribute("student", student);
            request.setAttribute("mode", "edit");
        }
        
        // Forward to form view
        request.getRequestDispatcher("/WEB-INF/views/onboarding/student-form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mode = request.getParameter("mode");
        Student student = new Student();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = (String) session.getAttribute("email");
        String name = (String) session.getAttribute("name");

        if ("edit".equals(mode)) {
            String userIdParam = request.getParameter("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                try {
                    int userId = Integer.parseInt(userIdParam);
                    student.setId(userId);
                    // Fetch existing user to get current picture
                    Optional<User> existingUserOpt = userService.getUserById(userId);
                    if (existingUserOpt.isPresent()) {
                        student.setPicture(existingUserOpt.get().getPicture());
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Target user not found for update");
                        return;
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
                    return;
                }
            } else {
                // Fallback to current user if no userId is provided in edit mode
                Optional<User> userOpt = userService.getUserByEmail(email);
                if (userOpt.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                student.setId(userOpt.get().getId());
                student.setPicture(userOpt.get().getPicture()); // Set existing picture for current user
            }
        } else { // Register mode
            // For registration mode, ensure picture is not null if no file is uploaded
            student.setPicture("default.png"); 
            // Set Full Name and Email from session
            student.setFullName(name);
            student.setEmail(email);
        }
        
        Map<String, String> errors = new HashMap<>();
       
        try {
            
            // Set Full Name and Email from the database if in edit mode
            if("edit".equals(mode)){
                String userIdParam = request.getParameter("userId");
                int userId = Integer.parseInt(userIdParam);
                Optional<User> tempUser = userService.getUserById(userId);
                if (tempUser.isPresent()) {
                    student.setFullName(tempUser.get().getFullName());
                    student.setEmail(tempUser.get().getEmail());
                }
            }

            // Manually decode and populate fields (simpler approach)
            student.setUsername(request.getParameter("username"));
            student.setBiography(request.getParameter("biography"));
            student.setRoleType(RoleType.STUDENT);	
            
            Part filePart = request.getPart("picture");
	        
	        // Handle birthdate
	        String birthdateStr = request.getParameter("birthdate");
	        if (birthdateStr == null || birthdateStr.isEmpty()) {
	            errors.put("birthdate", "Birthdate is required");
	        } else {
	            try {
	                LocalDate localDate = LocalDate.parse(birthdateStr);
	                student.setBirthdate(Date.valueOf(localDate));
	            } catch (Exception e) {
	                errors.put("birthdate", "Invalid date format. Use YYYY-MM-DD");
	            }
	        }
            
            // Process map fields
            processMapField(request, "socialLinkKey", "socialLinkValue", student::setSocialLinks);
            processMapField(request, "degreeKey", "degreeValue", student::setDegrees);
            processMapField(request, "subjectKey", "subjectValue", student::setEnrolledSubjects);
            
            
            if (!errors.isEmpty()) {
                request.setAttribute("student", student);
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/WEB-INF/views/onboarding/student-form.jsp").forward(request, response);
                return;
            }
            
            Map<String, String> validationErrors;
            
            if ("edit".equals(mode)) {
                validationErrors = studentService.update(student, filePart);
            } else {
                validationErrors = studentService.register(student, filePart);
            }
            
            if (validationErrors.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("student", student);
                request.setAttribute("errors", validationErrors);
                request.setAttribute("mode", mode);
                request.getRequestDispatcher("/WEB-INF/views/onboarding/student-form.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  
    
    private void processMapField(HttpServletRequest request, String keyParam, 
                               String valueParam, java.util.function.Consumer<Map<String, String>> setter) {
        String[] keys = request.getParameterValues(keyParam);
        String[] values = request.getParameterValues(valueParam);
        
        if (keys != null && values != null && keys.length == values.length) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < keys.length; i++) {
                if (!keys[i].isEmpty() && !values[i].isEmpty()) {
                    map.put(keys[i], values[i]);
                }
            }
            setter.accept(map);
        }
    }
	
}