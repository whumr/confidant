<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/2/6
  Time: 13:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>admin</title>
    <link href="/css/t.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div class="mainwrapper">
    <div class="inner">
        <div id="mw-panel" class="noprint">
            <%-- 树形菜单 --%>
            <%@include file="menu.jsp"%>
        </div>
        <div id="content">
            <div class="content_hd">
                <h2 id="firstHeading" class="">用户注册</h2>
                <div class="info"></div>
            </div>
            <div id="bodyContent" class="bodyContent">
                <p><b>接口调用请求说明</b></p>
                <pre>http请求方式: POST
<a href="http://confidant.jd-app.com/user/save?account=aaa@111.com&amp;password=bbb&amp;nick_name=cccc&amp;sex=M" class="external free" rel="nofollow" target="_blank">http://confidant.jd-app.com/user/save?account=aaa@111.com&password=bbb&nick_name=cccc&sex=M</a>
</pre>
                <p><b>参数说明</b>
                </p>
                <table border="1" cellspacing="0" cellpadding="4" align="center" width="640px">
                    <tbody>
                    <tr>
                        <th style="width:120px">参数</th>
                        <th style="width:120px">是否必须</th>
                        <th>说明</th>
                    </tr>
                    <tr>
                        <td> account</td>
                        <td> 是</td>
                        <td> 用户注册使用的邮箱账号</td>
                    </tr>
                    <tr>
                        <td> password</td>
                        <td> 是</td>
                        <td> 账号密码</td>
                    </tr>
                    <tr>
                        <td> nick_name</td>
                        <td> 是</td>
                        <td> 账号昵称</td>
                    </tr>
                    <tr>
                        <td> sex</td>
                        <td> 是</td>
                        <td> 性别</td>
                    </tr>
                    </tbody>
                </table>

                <p><b>返回说明</b></p>

                <p>正常情况下，会返回用户信息JSON数据包（目前调试阶段返回所以信息，生产环境只返回所需信息）：</p>
                <pre>{"account":"aaa@111.com","account_type":1,"create_time":1423450983830,"id":2,"last_updated":1423450983830,"nick_name":"cccc","password":"bbb","sex":"M"}</pre>

                <table border="1" cellspacing="0" cellpadding="4" align="center" width="640px">
                    <tbody>
                    <tr>
                        <th style="width:240px">参数</th>
                        <th>说明</th>
                    </tr>
                    <tr>
                        <td> id</td>
                        <td> 用户id</td>
                    </tr>
                    <tr>
                        <td> account</td>
                        <td> 注册使用的邮箱账号</td>
                    </tr>
                    </tbody>
                </table>

                <p><br>错误时会返回错误码等信息，JSON数据包示例如下（该示例为邮箱账号已被占用）:</p>

                <pre>{"status":0,"msg":"user exists"}</pre>
            </div>
        </div>

    </div>
</div>
</body>
</html>
