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
                    <form id="reg_form" action="save" method="post">
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
                            <button id="reg_btn" type="button" class="btn btn-primary form-control">注册</button>
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
        if (Check.empty(account.val()) || !Check.email(account.val().trim())) {
            account.parent().removeClass("has-success");
            account.parent().addClass("has-error");
            $('#error').text('请输入正确的邮箱');
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
            checkAccountRepeat(function(succeed, exists) {
                if (succeed && exists == 1) {
                    account.parent().removeClass("has-success");
                    account.parent().addClass("has-error");
                    $('#error').text('邮箱已经注册过了');
                    $('#error').parent().addClass('alert-danger');
                    error = 'account';
                }
            });
            return true;
        }
    }

    function checkAccountRepeat(callback) {
        $.ajax({
            url:'checkAccount',
            type: 'get',
            timeout: 5000,
            dataType: 'json',
            data: {account:$('#account').val().trim()},
            success: function(result) {
                if (result.status == 1)
                    callback(true, result.exists);
                else
                    callback(false);
            },
            error: function(){
                callback(false);
            }
        });
    }

    function checkPassword() {
        var password = $('#password');
        var msg = '';
        if (!Check.between(password.val(), 6, 30)) {
            error = 'password';
            msg = '请输入6-30位的密码';
        } else if (!Check.password(password.val())) {
            error = 'password';
            msg = '密码只能包含数字、大小写字母、下划线';
        } else if($('#password_ag').val() != '' && $('#password_ag').val() != password.val()) {
            msg = '两次输入的密码不一致';
            error = 'password_unmatch';
        }
        if (msg != '') {
            password.parent().removeClass("has-success");
            password.parent().addClass("has-error");
            $('#error').text(msg);
            $('#error').parent().addClass('alert-danger');
            return false;
        } else {
            password.parent().removeClass("has-error");
            password.parent().addClass("has-success");
            if (error == 'password' || error == 'password_unmatch' || error == '') {
                $('#error').text('');
                $('#error').parent().removeClass('alert-danger');
                error = '';
            }
            return true;
        }
    }

    function checkPasswordAg() {
        var password = $('#password_ag');
        var msg = '';
        if (!Check.between(password.val(), 6, 30)) {
            error = 'password_ag';
            msg = '请输入6-30位的确认密码';
        } else if (!Check.password(password.val())) {
            error = 'password_ag';
            msg = '密码只能包含数字、大小写字母、下划线';
        } else if($('#password').val() != '' && $('#password').val() != password.val()) {
            msg = '两次输入的密码不一致';
            error = 'password_unmatch';
        }
        if (msg != '') {
            password.parent().removeClass("has-success");
            password.parent().addClass("has-error");
            $('#error').text(msg);
            $('#error').parent().addClass('alert-danger');
            return false;
        } else {
            password.parent().removeClass("has-error");
            password.parent().addClass("has-success");
            if (error == 'password_ag' || error == 'password_unmatch' || error == '') {
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
    $('#password_ag').bind({
        blur:function() {checkPasswordAg();}
    });
    $('#reg_btn').click(function() {
        var btn = $('#reg_btn');
        btn.attr('disabled', true);
        if (checkAccount() && checkPassword() && checkPasswordAg()) {
            checkAccountRepeat(function(succeed, exists) {
                if (succeed) {
                    if (exists == 1) {
                        $('#account').parent().removeClass("has-success");
                        $('#account').parent().addClass("has-error");
                        $('#error').text('邮箱已经注册过了');
                        $('#error').parent().addClass('alert-danger');
                        error = 'account';
                    } else
                        $('#reg_form').submit();
                } else
                    alert('网络异常');
                btn.attr('disabled', false);
            });
        } else
            btn.attr('disabled', false);
    });
</script>
</body>
</html>