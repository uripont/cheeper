package com.webdev.cheeper.controller.user;

import com.webdev.cheeper.model.RoleType;
import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.UserRepository;
import com.webdev.cheeper.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/deleteUser")
public class DeleteUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "User not logged in.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getRoleType() != RoleType.ENTITY) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Only ENTITY users can delete other users.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        int userIdToDelete;
        try {
            userIdToDelete = Integer.parseInt(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid user ID.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        // Prevent an ENTITY user from deleting themselves
        if (currentUser.getId().equals(userIdToDelete)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "You cannot delete your own account from this interface.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        try (UserRepository userRepository = new UserRepository()) {
            UserService userService = new UserService(userRepository);
            boolean deleted = userService.deleteUser(userIdToDelete);

            if (deleted) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "User deleted successfully.");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to delete user. User might not exist or an internal error occurred.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred during user deletion: " + e.getMessage());
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
