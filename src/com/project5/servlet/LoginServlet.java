package com.project5.servlet;

import com.project5.dao.User;
import com.project5.dao.UserDAO;
import com.project5.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Validates user ID + password against the `users` table and starts a
 * session on success. Redirects back to login.jsp with a fixed error
 * code on failure (never echoes raw user input into the error message).
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = trim(request.getParameter("userId"));
        String password = request.getParameter("password");

        if (userId.isEmpty() || password == null || password.isEmpty()) {
            response.sendRedirect("login.jsp?error=missing");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.findByUserId(userId);

            if (user == null || !PasswordUtil.verify(password, user.passwordHash, user.passwordSalt)) {
                response.sendRedirect("login.jsp?error=invalid");
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.userId);
            session.setAttribute("firstName", user.firstName);
            session.setAttribute("photoPath", user.photoPath);
            response.sendRedirect("landing.jsp");

        } catch (SQLException e) {
            request.setAttribute("errorMsg", "A database error occurred. Please try again.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("login.jsp");
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
