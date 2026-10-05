package com.project5.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Streams the profile photo of the *currently logged-in* user only --
 * it never takes a filename from the request, so there is no way to
 * request another user's photo by guessing/changing a parameter.
 */
@WebServlet("/PhotoServlet")
public class PhotoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String photoPath = (String) session.getAttribute("photoPath");
        if (photoPath == null || photoPath.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/images/default-avatar.svg");
            return;
        }

        String uploadDir = request.getServletContext().getRealPath("/WEB-INF/uploads");
        Path file = Path.of(uploadDir).resolve(photoPath).normalize();

        if (!file.startsWith(Path.of(uploadDir)) || !Files.exists(file)) {
            response.sendRedirect(request.getContextPath() + "/images/default-avatar.svg");
            return;
        }

        String contentType = Files.probeContentType(file);
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        try (InputStream in = Files.newInputStream(file)) {
            in.transferTo(response.getOutputStream());
        }
    }
}
