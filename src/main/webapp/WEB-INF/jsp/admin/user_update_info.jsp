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
                <h2 id="firstHeading" class="">用户编辑个人资料</h2>
                <div class="info"></div>
            </div>
            <div id="bodyContent" class="bodyContent">
                <p><b>接口调用请求说明</b></p>
                <pre>http请求方式: POST
<a href="http://confidant.jd-app.com/user/updateInfo?nick_name=xxx" class="external free" rel="nofollow" target="_blank">http://confidant.jd-app.com/user/updateInfo?nick_name=xxx</a>
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
                        <td> nick_name</td>
                        <td> 是</td>
                        <td> 用户注册使用的邮箱账号</td>
                    </tr>
                    <tr>
                        <td> 其他</td>
                        <td> 否</td>
                        <td> 其他可编辑信息待定</td>
                    </tr>
                    </tbody>
                </table>

                <p><b>返回说明</b></p>

                <p>编辑失败：</p>
                <pre>{"status":0}</pre>
                <p>编辑成功：</p>
                <pre>{"status":1,"user":{"account":"aa","id":2,"nick_name":"cccc","sex":"M"}}</pre>

                <table border="1" cellspacing="0" cellpadding="4" align="center" width="640px">
                    <tbody>
                    <tr>
                        <th style="width:240px">参数</th>
                        <th>说明</th>
                    </tr>
                    <tr>
                        <td> status</td>
                        <td> 是否编辑成功（0：否；1：是）</td>
                    </tr>
                    <tr>
                        <td> user</td>
                        <td> 用户信息（根据实际需要进行调整）</td>
                    </tr>
                </table>
            </div>
        </div>

    </div>
</div>
</body>
</html>
