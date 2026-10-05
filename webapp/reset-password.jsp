<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.project5.util.HtmlUtil" %>
<%--
  Step 2 of password reset. Only reachable after ForgotPasswordServlet
  verifies the user ID + security answer and marks the session. The
  posted-to servlet (ResetPasswordServlet) reads the user ID from the
  session, not from this page, so this form cannot be used to change
  someone else's password.
--%>
<%
    String verifiedUserId = HtmlUtil.escape((String) request.getAttribute("verifiedUserId"));
    if (verifiedUserId == null || verifiedUserId.isEmpty()) {
        response.sendRedirect("forgot-password.jsp");
        return;
    }
    String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Reset Password - Project 5</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="card">
    <h1>Set a New Password</h1>
    <p class="subtitle">Identity verified for <strong><%= verifiedUserId %></strong></p>

    <% if (errorMsg != null) { %>
        <div class="message error"><%= errorMsg %></div>
    <% } %>

    <form action="ResetPasswordServlet" method="post">
        <label for="newPassword">New Password</label>
        <input type="password" id="newPassword" name="newPassword" minlength="8" required autofocus>

        <label for="confirmPassword">Confirm New Password</label>
        <input type="password" id="confirmPassword" name="confirmPassword" minlength="8" required>

        <button type="submit" class="btn">Reset Password</button>
    </form>

    <div class="links">
        <a href="login.jsp">Back to Login</a>
    </div>
</div>
</body>
</html>
