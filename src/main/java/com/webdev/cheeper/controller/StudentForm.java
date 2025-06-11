package com.webdev.cheeper.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.Student;
import com.webdev.cheeper.repository.StudentRepository;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.StudentService;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@MultipartConfig
@WebServlet("/auth/student-form")
public class StudentForm extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	Student student = new Student();
	    Map<String, String> errors = new HashMap<>();
	   
	    try {
	        // Manually decode and populate fields (simpler approach)
	        student.setFullName(request.getParameter("fullName"));
	        student.setEmail(request.getParameter("email"));
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
            
            try (StudentRepository studentRepository = new StudentRepository();
                 UserRepository userRepository = new UserRepository()) {
                
                StudentService studentService = new StudentService(userRepository, studentRepository);
                Map<String, String> validationErrors = studentService.register(student, filePart);
                
                if (validationErrors.isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/main-page.html");
                } else {
                    request.setAttribute("student", student);
                    request.setAttribute("errors", validationErrors);
                    request.getRequestDispatcher("/WEB-INF/views/onboarding/student-form.jsp").forward(request, response);
                }
            }
            
        } catch (Exception e) {
            handleException(request, response, student, e);
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
    
    private void handleException(HttpServletRequest request, HttpServletResponse response,
                                Student student, Exception e) throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("student", student);
        request.setAttribute("error", "Registration failed: " + e.getMessage());
        request.getRequestDispatcher("/WEB-INF/views/onboarding/student-form.jsp").forward(request, response);
    }
	
}
