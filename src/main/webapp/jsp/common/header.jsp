<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<nav class="navbar navbar-default navbar-fixed-top opacity75">--%>
<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="/">肉思思</a>
        </div>

<c:choose>
    <c:when test="${not empty MEMBER}">
        <div class="navbar-right navbar-form">
            <span>
                <a href="/member"><c:out value="${MEMBER.nick_name}"></c:out></a>
                ，欢迎您
                <a href="/logout">退出</a>
            </span>
        </div>
    </c:when>
    <c:otherwise>
        <div class="navbar-right navbar-form">
            <span>
                <a href="/login">马上登录</a>
                没有账号?<a href="/reg">注册</a>
            </span>
        </div>
    </c:otherwise>
</c:choose>

    </div>
</nav>