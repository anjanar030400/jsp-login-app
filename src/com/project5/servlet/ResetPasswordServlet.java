package com.project5.servlet;

import com.project5.util.PasswordUtil;
import com.project5.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Step 2 of password reset: sets the new password for the user ID that
 * was verified in the session by ForgotPasswordServlet (not from any
 * value submitted by the browser).
 */
@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userId = session == null ? null : (String) session.getAttribute("pwResetUserId");

        if (userId == null) {
            response.sendRedirect("forgot-password.jsp?error=session");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.length() < 8) {
            request.setAttribute("errorMsg", "New password must be at least 8 characters.");
            request.setAttribute("verifiedUserId", userId);
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMsg", "Passwords do not match.");
            request.setAttribute("verifiedUserId", userId);
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            return;
        }

        try {
            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hash(newPassword, salt);
            new UserDAO().updatePassword(userId, hash, salt);
            session.removeAttribute("pwResetUserId");
            response.sendRedirect("login.jsp?success=reset");
        } catch (SQLException e) {
            request.setAttribute("errorMsg", "A database error occurred. Please try again.");
            request.setAttribute("verifiedUserId", userId);
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("forgot-password.jsp");
    }
}
