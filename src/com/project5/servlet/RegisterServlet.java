package com.project5.servlet;

import com.project5.dao.User;
import com.project5.dao.UserDAO;
import com.project5.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Handles the registration form submission: validates every field,
 * hashes the password and security answer (salted, never stored in
 * plain text), saves the uploaded photo to WEB-INF/uploads under a
 * random generated name, and inserts the new row via a PreparedStatement.
 *
 * On any validation failure, the original (safe) field values are put
 * back on the request and the form is re-rendered with an error message
 * instead of losing what the user typed.
 */
@WebServlet("/RegisterServlet")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class RegisterServlet extends HttpServlet {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = trim(request.getParameter("userId"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = trim(request.getParameter("firstName"));
        String lastName = trim(request.getParameter("lastName"));
        String email = trim(request.getParameter("email"));
        String dob = trim(request.getParameter("dob"));
        String securityQuestion = trim(request.getParameter("securityQuestion"));
        String securityAnswer = trim(request.getParameter("securityAnswer"));

        // Keep submitted values available for re-display on error.
        request.setAttribute("v_userId", userId);
        request.setAttribute("v_firstName", firstName);
        request.setAttribute("v_lastName", lastName);
        request.setAttribute("v_email", email);
        request.setAttribute("v_dob", dob);
        request.setAttribute("v_securityQuestion", securityQuestion);

        if (userId.isEmpty() || password == null || password.isEmpty() || firstName.isEmpty()
                || lastName.isEmpty() || email.isEmpty() || dob.isEmpty()
                || securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
            fail(request, response, "All fields are required, including the security question and answer.");
            return;
        }
        if (!USER_ID_PATTERN.matcher(userId).matches()) {
            fail(request, response, "User ID must be 3-20 characters: letters, numbers, underscore only.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            fail(request, response, "Please enter a valid email address.");
            return;
        }
        if (password.length() < 8) {
            fail(request, response, "Password must be at least 8 characters.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            fail(request, response, "Password and confirmation do not match.");
            return;
        }

        Part photoPart;
        try {
            photoPart = request.getPart("photo");
        } catch (IOException | ServletException e) {
            fail(request, response, "The uploaded photo was too large or invalid.");
            return;
        }
        if (photoPart == null || photoPart.getSize() == 0) {
            fail(request, response, "Please choose a profile photo to upload.");
            return;
        }
        String contentType = photoPart.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg")
                || contentType.equals("image/png") || contentType.equals("image/gif"))) {
            fail(request, response, "Photo must be a JPEG, PNG, or GIF image.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            if (dao.isUserIdTaken(userId)) {
                fail(request, response, "That User ID is already taken.");
                return;
            }
            if (dao.isEmailTaken(email)) {
                fail(request, response, "An account with that email already exists.");
                return;
            }

            String storedFileName = savePhoto(photoPart, request);

            User u = new User();
            u.userId = userId;
            String passwordSalt = PasswordUtil.generateSalt();
            u.passwordSalt = passwordSalt;
            u.passwordHash = PasswordUtil.hash(password, passwordSalt);
            u.firstName = firstName;
            u.lastName = lastName;
            u.email = email;
            u.dateOfBirth = dob;
            u.photoPath = storedFileName;
            u.securityQuestion = securityQuestion;
            String answerSalt = PasswordUtil.generateSalt();
            u.securityAnswerSalt = answerSalt;
            u.securityAnswerHash = PasswordUtil.hash(securityAnswer.toLowerCase(), answerSalt);

            dao.insertUser(u);

            request.setAttribute("registeredUserId", userId);
            request.getRequestDispatcher("register-success.jsp").forward(request, response);

        } catch (SQLException e) {
            fail(request, response, "A database error occurred while creating the account. Please try again.");
        }
    }

    private String savePhoto(Part photoPart, HttpServletRequest request) throws IOException {
        String submitted = photoPart.getSubmittedFileName();
        String ext = "";
        if (submitted != null && submitted.contains(".")) {
            ext = submitted.substring(submitted.lastIndexOf('.')).toLowerCase();
        }
        if (!ext.matches("\\.(jpg|jpeg|png|gif)")) {
            ext = ".img";
        }
        String storedFileName = UUID.randomUUID() + ext;

        String uploadDir = request.getServletContext().getRealPath("/WEB-INF/uploads");
        Path uploadPath = Path.of(uploadDir);
        Files.createDirectories(uploadPath);
        Path target = uploadPath.resolve(storedFileName);

        try (InputStream in = photoPart.getInputStream()) {
            Files.copy(in, target);
        }
        return storedFileName;
    }

    private void fail(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMsg", message);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("register.jsp");
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
