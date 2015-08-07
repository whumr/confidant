<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <!--[if IE]>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"><![endif]-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册会员</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/main.css" rel="stylesheet">
    <script src="/js/jquery-1.11.3.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>
    <script src="/js/check.js"></script>
</head>
<body>

<%@include file="../common/header.jsp"%>

<div class="container">
    <!-- Three columns of text below the carousel -->
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div class="panel panel-default">
                <div class="panel-heading text-center">注册会员</div>
                <div class="panel-body">
                    <form id="login_form" action="reg" method="post">
                        <div class="form-group alert <c:if test="${not empty error}">alert-danger</c:if>">
                            <label id="error"><c:out value="${error}" escapeXml="false"></c:out></label>
                        </div>
                        <div class="input-group margin-bottom-20">
                            <span class="input-group-addon glyphicon glyphicon-envelope top0"></span>
                            <input id="account" name="account" type="text" class="form-control" placeholder="请输入邮箱" value="<c:out value="${account}"></c:out>">
                        </div>
                        <div class="input-group margin-bottom-20">
                            <span class="input-group-addon glyphicon glyphicon-lock top0"></span>
                            <input id="password" name="password" type="password" class="form-control" placeholder="请输入密码">
                        </div>
                        <div class="input-group margin-bottom-20">
                            <span class="input-group-addon glyphicon glyphicon-lock top0"></span>
                            <input id="password_ag" type="password" class="form-control" placeholder="请再次输入密码">
                        </div>
                        <div class="form-group">
                            <button id="reg_btn" type="button" class="btn btn-primary form-control">登录</button>
                        </div>
                        <div class="form-group">
                            <span class="col-md-3 col-md-offset-6">已注册?<a href="login">去登录</a></span>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="../common/footer.jsp"%>

<script type="text/javascript">
    var error = '';
    function checkAccount() {
        var account = $('#account');
        if (Check.empty(account.val())) {
            account.parent().removeClass("has-success");
            account.parent().addClass("has-error");
            $('#error').text('请输入账号');
            $('#error').parent().addClass('alert-danger');
            error = 'account';
            return false;
        } else {
            account.parent().removeClass("has-error");
            account.parent().addClass("has-success");
            if (error == 'account' || error == '') {
                $('#error').text('');
                $('#error').parent().removeClass('alert-danger');
                error = '';
            }
            return true;
        }
    }

    function checkPassword() {
        var password = $('#password');
        if (!Check.between(password.val(), 6, 30)) {
            password.parent().removeClass("has-success");
            password.parent().addClass("has-error");
            $('#error').text('请输入6-30位的密码');
            $('#error').parent().addClass('alert-danger');
            error = 'password';
            return false;
        } else {
            password.parent().removeClass("has-error");
            password.parent().addClass("has-success");
            if (error == 'password' || error == '') {
                $('#error').text('');
                $('#error').parent().removeClass('alert-danger');
                error = '';
            }
            return true;
        }
    }
    $('#account').bind({
        blur:function() {checkAccount();}
    });
    $('#password').bind({
        blur:function() {checkPassword();}
    });
    $('#login_btn').click(function() {
        var btn = $('#login_btn');
        btn.attr('disabled', true);
        if (checkAccount() && checkPassword())
            $('#login_form').submit();
        else
            btn.attr('disabled', false);
    });
</script>
</body>
</html>