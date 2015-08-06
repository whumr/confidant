<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <!--[if IE]>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"><![endif]-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reactive - Free HTML5 Template</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/main.css" rel="stylesheet">
    <script src="/js/jquery-1.11.3.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>
</head>
<body>

<%@include file="common/header.jsp"%>

<div id="myCarousel" class="carousel slide" data-ride="carousel">
    <ol class="carousel-indicators">
        <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
        <li data-target="#myCarousel" data-slide-to="1"></li>
        <li data-target="#myCarousel" data-slide-to="2"></li>
    </ol>
    <div class="carousel-inner" role="listbox">
        <div class="item active">
            <img src="images/banner_slide_01.jpg" alt="First slide">
        </div>
        <div class="item">
            <img src="images/banner_slide_02.jpg" alt="Second slide">
        </div>
        <div class="item">
            <img src="images/banner_slide_03.jpg" alt="Third slide">
        </div>
    </div>
    <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
        <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
        <span class="sr-only">Previous</span>
    </a>
    <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
        <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
        <span class="sr-only">Next</span>
    </a>
</div>

<div class="container">

    <!-- Three columns of text below the carousel -->
    <div class="row" style="margin-top:20px;margin-bottom:20px;">
        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_01.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户dddddddddd名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_02.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户d名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row" style="margin-top:20px;margin-bottom:20px;">
        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_01.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户dddddddddd名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_02.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户d名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>
    </div>
    <div class="row" style="margin-top:20px;margin-bottom:20px;">
        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_01.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户dddddddddd名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_02.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户d名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row" style="margin-top:20px;margin-bottom:20px;">
        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_01.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户dddddddddd名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_02.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户d名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row" style="margin-top:20px;margin-bottom:20px;">
        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_01.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户dddddddddd名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="row" style="margin-top:20px;margin-bottom:20px;">
                <div class="col-xs-3">
                    <img class="img-polaroid center-block" src="images/event_02.jpg" style="margin-top: 10px; margin-bottom: 10px;" width="50" height="50">
                    <p class="text-center"><a href="#">用户d名</a></p>
                </div>
                <div class="col-xs-9">
                    <h4 class="text-info">8月10日 深圳->丽江</h4>
                    <h4 class="text-warning">深圳->阳朔->大理->西双版纳->丽江</h4>
                    <h4 class="text-warning">11天旅程  |  预算8000元  |  QQ 1730005219</h4>
                    <p class="text-muted">最后一个假期，想去云南圆梦，有一起的小伙伴可以联系我。计划8月12号以后，大概玩一个星期，正在做旅游攻略，有一起出行的小伙伴加QQ363658561 或者微信zzy128075</p>
                </div>
            </div>
        </div>
    </div>

</div>

<%@include file="common/footer.jsp"%>

<script>
    $(function () {
        // 初始化轮播
        $("#myCarousel").carousel({interval: 2000}).carousel('cycle');
    });
</script>
</body>
</html>