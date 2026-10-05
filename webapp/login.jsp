<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Login page. Pure view -- the form POSTs to LoginServlet, which does the
  real validation against the database. Error/success text is picked from
  a fixed list of codes (never echoes the raw query string) to avoid
  reflected XSS via the URL.
--%>
<%
    String errorCode = request.getParameter("error");
    String successCode = request.getParameter("success");
    String errorMsg = (String) request.getAttribute("errorMsg");
    String successMsg = null;

    if (errorCode != null) {
        switch (errorCode) {
            case "missing": errorMsg = "Please enter both your User ID and password."; break;
            case "invalid": errorMsg = "Invalid User ID or password."; break;
            case "session": errorMsg = "Please log in to continue."; break;
            default: errorMsg = "Something went wrong. Please try again.";
        }
    }
    if (successCode != null) {
        switch (successCode) {
            case "registered": successMsg = "Account created successfully! You can now log in."; break;
            case "reset": successMsg = "Your password has been reset. Please log in."; break;
            case "logout": successMsg = "You have been logged out successfully."; break;
            default: successMsg = null;
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - Project 5</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="card">
    <h1>Project 5 Login</h1>
    <p class="subtitle">Sign in with your User ID and password</p>

    <% if (errorMsg != null) { %>
        <div class="message error"><%= errorMsg %></div>
    <% } %>
    <% if (successMsg != null) { %>
        <div class="message success"><%= successMsg %></div>
    <% } %>

    <form action="LoginServlet" method="post">
        <label for="userId">User ID</label>
        <input type="text" id="userId" name="userId" required autofocus>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>

        <button type="submit" class="btn">Log In</button>
    </form>

    <div class="links">
        <a href="register.jsp">Create an account</a> &nbsp;|&nbsp;
        <a href="forgot-password.jsp">Forgot password?</a>
    </div>
</div>
</body>
</html>
