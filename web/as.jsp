<%@ page import="app.mrquan.tomcat.test.pojo.Book" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
</head>
<body>
<h1>hello JSP</h1>
<%
    Book book = new Book("Thinking in java",1);
    out.print(book);
    out.print(request.getParameter("name"));
%>
</body>
</html>
