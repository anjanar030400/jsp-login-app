<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.project5.util.HtmlUtil" %>
<%--
  Confirmation page shown once after a successful registration
  (reached only via forward from RegisterServlet, never bookmarked).
--%>
<%
    String userId = HtmlUtil.escape((String) request.getAttribute("registeredUserId"));
    if (userId == null || userId.isEmpty()) {
        response.sendRedirect("register.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration Complete - Project 5</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="card">
    <h1>You're All Set!</h1>
    <div class="message success">
        Account <strong><%= userId %></strong> was created successfully.
    </div>
    <p class="subtitle">You can now log in using your new User ID and password.</p>
    <a href="login.jsp" class="btn" style="display:block;text-align:center;text-decoration:none;">Go to Login</a>
</div>
</body>
</html>
