<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.project5.util.HtmlUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%--
  Landing page shown right after login. AuthFilter (mapped to this URL
  in AuthFilter.java via @WebFilter) already redirected to login.jsp if
  there was no active session, so by the time this code runs we know
  the user is authenticated.
--%>
<%
    String firstName = HtmlUtil.escape((String) session.getAttribute("firstName"));
    String today = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home - Project 5</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="card landing">
    <p class="date"><%= today %></p>
    <h1>Welcome, <%= firstName %>!</h1>

    <img class="photo" src="PhotoServlet" alt="Profile photo">

    <form action="LogoutServlet" method="post">
        <button type="submit" class="btn">Logout</button>
    </form>
</div>
</body>
</html>
