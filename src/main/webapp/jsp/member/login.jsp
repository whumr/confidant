<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <!--[if IE]>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"><![endif]-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登陆</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/main.css" rel="stylesheet">
    <script src="/js/jquery-1.11.3.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>
</head>
<body>

<%@include file="../common/header.jsp"%>

<div class="container">
    <!-- Three columns of text below the carousel -->
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div class="panel panel-default">
                <div class="panel-heading text-center">login</div>
                <div class="panel-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group has-error" style="display: none;">
                            <label class="control-label">
                                输入错误
                            </label>
                        </div>
                        <div class="form-group" style="margin-bottom:20px;">
                            <span class="input-group-addon glyphicon glyphicon-user"></span>
                            <input id="account" name="account" type="text" class="form-control" placeholder="请输入账号">
                        </div>
                        <div class="form-group">
                            <span class="input-group-addon glyphicon glyphicon-lock"></span>
                            <input id="password" name="password" type="password" class="form-control" placeholder="请输入密码">
                        </div>
                        <div class="form-group">
                            <button id="btn" type="button" class="btn btn-primary form-control">登录</button>
                        </div>
                        <div class="form-group">
                            <a href="resetpwd" class="col-md-3 col-md-offset-3">忘记密码?</a>
                            <a href="reg" class="col-md-3">立即注册</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="../common/footer.jsp"%>

</body>
</html>