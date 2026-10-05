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
 * Step 1 of password reset: verify the user ID + security answer.
 * On success, marks the session as "allowed to reset" for that specific
 * user ID and forwards to reset-password.jsp. The actual password change
 * (ResetPasswordServlet) only trusts that session marker -- never a
 * value posted directly from the form -- so the security question check
 * cannot be bypassed by POSTing straight to the reset endpoint.
 *
 * Uses the same generic error message whether the user ID doesn't exist
 * or the answer is wrong, to avoid revealing which accounts exist.
 */
@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = trim(request.getParameter("userId"));
        String answer = trim(request.getParameter("securityAnswer"));
        request.setAttribute("v_userId", userId);

        if (userId.isEmpty() || answer.isEmpty()) {
            request.setAttribute("errorMsg", "Please enter your User ID and security answer.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.findByUserId(userId);

            boolean ok = user != null
                    && PasswordUtil.verify(answer.toLowerCase(), user.securityAnswerHash, user.securityAnswerSalt);

            if (!ok) {
                request.setAttribute("errorMsg", "User ID or security answer is incorrect.");
                request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("pwResetUserId", user.userId);
            request.setAttribute("verifiedUserId", user.userId);
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("errorMsg", "A database error occurred. Please try again.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("forgot-password.jsp");
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
