<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<nav class="navbar navbar-default navbar-fixed-top opacity75">--%>
<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">肉思思</a>
        </div>

        <div id="navbar" class="navbar-collapse collapse">
            <form class="navbar-form navbar-right">
                <div class="form-group">
                    <input type="text" placeholder="账号" class="form-control">
                </div>
                <div class="form-group">
                    <input type="password" placeholder="密码" class="form-control">
                </div>
                <button type="submit" class="btn btn-success">登录</button>
                <a href="/reg">没有账号?注册</a>
            </form>
        </div>
    </div>
</nav>