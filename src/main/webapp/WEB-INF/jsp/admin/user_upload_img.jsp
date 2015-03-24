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
                <h2 id="firstHeading" class="">上传头像（未测试，需要联调）</h2>
                <div class="info"></div>
            </div>
            <div id="bodyContent" class="bodyContent">
                <p><b>接口调用请求说明</b></p>
                <pre>http请求方式: POST
<a href="#" class="external free" rel="nofollow" target="_blank">http://confidant.jd-app.com/user/uploadImg?file=xx.jpg</a>
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
                        <td> file</td>
                        <td> 是</td>
                        <td> 上传文件名</td>
                    </tr>
                    <tr>
                        <td> http头：Content-Length</td>
                        <td> 是</td>
                        <td> 图片字节大小</td>
                    </tr>
                    <tr>
                        <td> http头：Content-Type</td>
                        <td> 是</td>
                        <td> Content-Type:multipart/form-data</td>
                    </tr>
                    </tbody>
                </table>

                <p><b>返回说明</b></p>

                <p>上传失败：</p>
                <pre>{"status":0, "msg":"file too large"} （目前大小超过5m服务端拒收）</pre>
                <p>登陆成功：</p>
                <pre>{"status":1,"url":"xxx.jpg"}</pre>

                <table border="1" cellspacing="0" cellpadding="4" align="center" width="640px">
                    <tbody>
                    <tr>
                        <th style="width:240px">参数</th>
                        <th>说明</th>
                    </tr>
                    <tr>
                        <td> status</td>
                        <td> 是否登陆成功（0：否；1：是）</td>
                    </tr>
                    <tr>
                        <td> msg</td>
                        <td> 上传失败原因</td>
                    </tr>
                    <tr>
                        <td> url</td>
                        <td> 图片保存相对路径</td>
                    </tr>
                </table>
            </div>
        </div>

    </div>
</div>
</body>
</html>
