<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/2/6
  Time: 11:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.confidant.controllers.admin.AdminController" %>
<html>
<head>
    <title>hello</title>
</head>
<body>
    hello, world...
    <div align="center">
        <form method="post" action="admin/enter">
            <table align="center">
                <tr>
                    <td colspan="3"><p style="float: left">entry</p></td>
                </tr>
                <%
                    if (request.getSession().getAttribute(AdminController.ENTRY_KEY) == null) {
                %>
                <tr>
                    <td>password:</td>
                    <td><input type="password" name="pass" size="20"></td>
                    <td><input type="submit" value="enter"></td>
                </tr>
                <%
                    } else {
                %>
                <tr>
                    <td><a href="/admin">admin</a></td>
                </tr>
                <%
                    }
                %>
            </table>
        </form>
    </div>
</body>
</html>
