<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.project5.util.HtmlUtil" %>
<%--
  Registration form. Posts (multipart, for the photo) to RegisterServlet.
  On a validation error the servlet forwards back here with the
  previously entered values in request attributes so the user doesn't
  have to retype everything; all of it is HTML-escaped before display.
--%>
<%
    String errorMsg = (String) request.getAttribute("errorMsg");
    String v_userId = HtmlUtil.escape((String) request.getAttribute("v_userId"));
    String v_firstName = HtmlUtil.escape((String) request.getAttribute("v_firstName"));
    String v_lastName = HtmlUtil.escape((String) request.getAttribute("v_lastName"));
    String v_email = HtmlUtil.escape((String) request.getAttribute("v_email"));
    String v_dob = HtmlUtil.escape((String) request.getAttribute("v_dob"));
    String v_question = (String) request.getAttribute("v_securityQuestion");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - Project 5</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="card wide">
    <h1>Create Account</h1>
    <p class="subtitle">Fill in your details to register</p>

    <% if (errorMsg != null) { %>
        <div class="message error"><%= errorMsg %></div>
    <% } %>

    <form action="RegisterServlet" method="post" enctype="multipart/form-data">
        <div class="form-row">
            <div>
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" value="<%= v_firstName %>" required>
            </div>
            <div>
                <label for="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName" value="<%= v_lastName %>" required>
            </div>
        </div>

        <label for="email">Email</label>
        <input type="email" id="email" name="email" value="<%= v_email %>" required>

        <label for="dob">Date of Birth</label>
        <input type="date" id="dob" name="dob" value="<%= v_dob %>" required>

        <label for="photo">Profile Photo (JPEG, PNG, or GIF)</label>
        <input type="file" id="photo" name="photo" accept="image/jpeg,image/png,image/gif" required>

        <label for="userId">Choose a User ID</label>
        <input type="text" id="userId" name="userId" value="<%= v_userId %>"
               pattern="[A-Za-z0-9_]{3,20}" title="3-20 letters, numbers, or underscore" required>

        <div class="form-row">
            <div>
                <label for="password">Password</label>
                <input type="password" id="password" name="password" minlength="8" required>
            </div>
            <div>
                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" minlength="8" required>
            </div>
        </div>

        <label for="securityQuestion">Security Question</label>
        <select id="securityQuestion" name="securityQuestion" required>
            <option value="">-- Select a question --</option>
            <%
                String[] questions = {
                    "What was the name of your first pet?",
                    "What is your mother's maiden name?",
                    "What city were you born in?",
                    "What was the name of your first school?",
                    "What is your favorite book?"
                };
                for (String q : questions) {
                    String selected = q.equals(v_question) ? " selected" : "";
            %>
                <option value="<%= q %>"<%= selected %>><%= q %></option>
            <% } %>
        </select>

        <label for="securityAnswer">Answer</label>
        <input type="text" id="securityAnswer" name="securityAnswer" required>

        <button type="submit" class="btn">Register</button>
    </form>

    <div class="links">
        Already have an account? <a href="login.jsp">Log in</a>
    </div>
</div>
</body>
</html>
