<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.project5.util.HtmlUtil" %>
<%--
  Step 1 of password reset: user enters their User ID and the answer to
  their security question. ForgotPasswordServlet verifies both before
  allowing access to reset-password.jsp.
--%>
<%
    String errorMsg = (String) request.getAttribute("errorMsg");
    if (errorMsg == null && "session".equals(request.getParameter("error"))) {
        errorMsg = "Your session expired. Please verify your identity again.";
    }
    String v_userId = HtmlUtil.escape((String) request.getAttribute("v_userId"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Forgot Password - Project 5</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="card">
    <h1>Forgot Password</h1>
    <p class="subtitle">Enter your User ID and the answer to your security question</p>

    <% if (errorMsg != null) { %>
        <div class="message error"><%= errorMsg %></div>
    <% } %>

    <form action="ForgotPasswordServlet" method="post">
        <label for="userId">User ID</label>
        <input type="text" id="userId" name="userId" value="<%= v_userId %>" required autofocus>

        <label for="securityAnswer">Security Answer</label>
        <input type="text" id="securityAnswer" name="securityAnswer" required>

        <button type="submit" class="btn">Verify</button>
    </form>

    <div class="links">
        <a href="login.jsp">Back to Login</a>
    </div>
</div>
</body>
</html>
