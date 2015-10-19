<%@page language="java" contentType="text/html;charset=UTF-8"
	session="false" pageEncoding="UTF-8"
	import="com.royalstone.vss.VSSConfig"%>
<!DOCTYPE html>
<html>
<head>
<title>商业供应商服务系统零售商登陆</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="Keywords" content="商业,VSS,供应商,零售商,服务,富基融通,JAVA" />
<meta name="Description" content="商业供应商服务系统。北京富基融通科技有限公司维护。" />
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="../resources/bootstrap-3.0.2-dist/css/bootstrap.min.css" />
<!-- Optional theme -->
<link rel="stylesheet"
	href="../resources/bootstrap-3.0.2-dist/css/bootstrap-theme.min.css" />
<script src="../resources/script/jquery-1.10.2.min.js"
	type="text/javascript"></script>
<!--[if lt IE 9]>
    <script src="../resources/script/html5shiv.js"></script>
    <script src="../resources/script/respond.min.js"></script>
    <![endif]-->
<script type="text/javascript" src="../resources/script/holder.js"></script>
<script type="text/javascript" src="../AW/runtime/lib/aw.js"></script>

<script type="text/javascript" type="text/javascript">
        var loading_html = '<img src="../img/loading.gif"></img><font color=#003>正在处理您的请求……</font>';
        var loginsuccess_html = '<img src="../img/loading.gif"></img><font color=#003>登录成功，请稍候……</font>';
        jQuery(document).ready(function () {
            jQuery('#user_username').focus();
            var tmp = getCookie("site");
            if (tmp != null) {
                jQuery('#site').val(tmp);
            }
            ;
            jQuery("#check_code").focus(function () {
                jQuery("#img_checkcode").attr({
                    src: "../DaemonCode?timestamp=" + new Date().getTime(),
                    title: "验证码",
                    alt: "生成新的验证码"
                });
            });
            jQuery("#img_checkcode").click(function () {
                jQuery("#img_checkcode").attr({
                    src: "../DaemonCode?timestamp=" + new Date().getTime(),
                    title: "验证码",
                    alt: "生成新的验证码"
                });
            }).Attr("src", "../DaemonCode?timestamp=" + new Date().getTime());
        });
        function login() {
            var site = jQuery("#site").val();
            var username = jQuery("#user_username").val();
            var password = jQuery('#user_password').val();
            var code = jQuery('#check_code').val();
            var url = "../DaemonLogon?site=" + site + "&action=logon&logonid="
                    + username + "&password="
                    + password
                    + "&checkcode=" + code;
            jQuery("#loading").css("display", "block");
            jQuery("#loading").html(loading_html);
            var table = new AW.XML.Table;
            table.setURL(url);
            table.request();//发送url请求
            table.response = function (text) {
                table.setXML(text);
                if (table.getErrCode() != '0') {//处理xml中的错误消息
                    alert(table.getErrNote());
                    window.location.reload();
                    return;
                }
                setCookie("site", site);
                jQuery("#loading").html(loginsuccess_html);
                location.replace("main.htm");
            };
        }
        ;

        function checkcode_up(ctrl) {
            ctrl.value = ctrl.value.toUpperCase();
        }
        ;
        function RequiredField() {
            var uname = jQuery("user_username").val();
            var pwd = jQuery("user_password").val();
            var code = jQuery("check_code").val();
            if (uname != "" && pwd != "" && code != "")
                document.getElementById("signin_submit").disabled = false;
            else
                document.getElementById("signin_submit").disabled = true;
        }
        ;
        function setCookie(name, value) {
            var Days = 360; //此cookie将被保存360天
            var exp = new Date();
            exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
            document.cookie = name + "=" + escape(value) + ";expires="
                    + exp.toGMTString();
        }
        ;
        function getCookie(name) {
            var arr, reg = new RegExp("(^|)" + name + "=([^;]*)(;|$)");
            if (arr = document.cookie.match(reg))
                return unescape(arr[2]);
            else
                return null;
        }
        ;
        function delCookie(name) {
            var exp = new Date();
            exp.setTime(exp.getTime() - 1);
            var cval = getCookie(name);
            if (cval != null) {
                document.cookie = name + "=" + cval + ";expires="
                        + exp.toGMTString();
            }
        }
        ;
    </script>

<style type="text/css">
body {
	background-image: url(../resources/images/page_bg.jpg);
}

.main {
	background-image: url(../img/dw.gif);
	width: 99%;
	margin-left: 10px;
	margin-top: 10%;
}

.main-content {
	border: 2px solid white;
	width: 400px;
	padding: 20px;
	margin: 5px;
}

.content-wrapper {
	margin-top: 40px;
	text-align: center;
}

#loading {
	float: left;
	background-color: #fff;
	font-size: 12px;
}
</style>
</head>
<body>
	<div class="row main">
		<div class="col-md-5 col-md-offset-1">
			<div class="content-wrapper">
				<div>
					<h2>海航商业供应商服务系统</h2>
					<br> <img src="../resources/images/logo.png" alt="">

					<h3>零售商用户登陆</h3>
				</div>
			</div>
		</div>
		<div class="col-md-4 ">
			<div class="main-content">
				<form class="form-horizontal">
					<div class="form-group">
						<label class="col-lg-4  control-label" for="user_username"><i
							class="glyphicon glyphicon-user"></i> 登录代码: </label>

						<div class="col-lg-8">
							<input class="form-control" type="text" id="user_username"
								placeholder="UserName" onchange="RequiredField();" />
						</div>

					</div>
					<div class="form-group">
						<label class="col-lg-4  control-label" for="user_password"><i
							class="glyphicon glyphicon-lock"></i> 登录密码:</label>

						<div class="col-lg-8">
							<input type="password" class="form-control" id="user_password"
								placeholder="Password" onchange="RequiredField();" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-lg-4  control-label" for="check_code"><i
							class="glyphicon glyphicon-picture"></i>
							验&nbsp;&nbsp;证&nbsp;&nbsp;码:</label>

						<div class="col-lg-4">
							<input class="form-control" id="check_code" type="text"
								name="check_code" placeholder="Code"
								onkeyup="RequiredField();checkcode_up(this);if(event.keyCode==13) login();" />
						</div>
						<div class="col-lg-4">
							<img class="form-control" id="img_checkcode" src="../DaemonCode"
								alt="验证码" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-lg-4 control-label" for="inputPassword"><i
							class="glyphicon glyphicon-hand-right"></i> 选择区域:</label>

						<div class=" col-lg-8">
							<select class="form-control" id="site"><%=VSSConfig.getInstance().optHTML%>
							</select>
						</div>
					</div>
					<div class="form-group">
						<div class="col-lg-4  col-md-offset-2">
							<input type="button" id="signin_submit" value="登&nbsp;&nbsp;录"
								class="btn  btn-primary" onclick="login()" />
						</div>
						<div class="col-lg-6">
							<input type="reset" value="重&nbsp;&nbsp;置"
								class="btn  btn-primary" />
						</div>

					</div>


					<div id="loading" style="display: none;"></div>
				</form>
			</div>
		</div>
	</div>
	<div id="footer" class="navbar-fixed-bottom"
		style="text-align: center;">
		<p>琼ICP备11000390-2号</p>
	</div>
	<div style="clear: none;"></div>
	<!-- Latest compiled and minified JavaScript -->
	<script src="../resources/bootstrap-3.0.2-dist/js/bootstrap.min.js"
		type="text/javascript"></script>
</body>
</html>
