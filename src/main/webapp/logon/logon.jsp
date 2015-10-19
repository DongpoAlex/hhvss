<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.vss.VSSConfig"%>
<!DOCTYPE html>
<html>
<head>
<title>商业供应商服务系统</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="Keywords" content="商业,VSS,供应商,零售商,服务,富基融通,JAVA" />
<meta name="Description" content="商业供应商服务系统。北京富基融通科技有限公司维护。" />
<link rel="SHORTCUT ICON" href="favicon.ico" />
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="../resources/bootstrap-3.0.2-dist/css/bootstrap.min.css" />
<!-- Optional theme -->
<link rel="stylesheet"
	href="../resources/bootstrap-3.0.2-dist/css/bootstrap-theme.min.css" />
<script src="/resources/script/jquery-1.11.1.min.js"
	type="text/javascript"></script>
<!--[if lt IE 9]>
    <script type="text/javascript" src="../resources/script/html5shiv.js"></script>
    <script type="text/javascript" src="../resources/script/respond.min.js"></script>
    <![endif]-->
<!-- 版本确定 2013-11-22 21:38-->
<link href="../resources/css/site.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<!--[if lt IE 8]>
<div class="navbar navbar-default navbar-static-top" style="text-align: center; padding-top: 10px; color: red;">
    <p>浏览器版本过低，页面显示将存在异常。系统最低要求<span style="color: blue;">IE8以上</span>才可以访问！你的浏览器版本过低请升级你的浏览器为IE8！
        <a href="http://windows.microsoft.com/zh-cn/internet-explorer/download-ie">
            点击这里升级</a></p>
</div>
<![endif]-->
	<div class="navbar-wrapper">
		<div class="navbar">
			<div class="container">
				<div class="navbar-header navbar-left">
					<img src="../resources/images/login_title1.png"
						style="position: absolute; top 20px; left: 60px;" />
				</div>
				<div class="navbar-nav navbar-right">
					<a class="navbar-link" href="http://www.hnaholding.net">官方网站》</a>
				</div>
			</div>
		</div>
	</div>
	<!-- Carousel   ================================================== -->
	<div id="myCarousel" class="carousel slide" data-ride="carousel"
		data-interval="5000">
		<div class="marketing">
			<div class="blackbox">
				<form class="form-horizontal" id="target" action="#" method="post">
					<div class="form-group">
						<label class="col-xs-4  control-label" for="user_username">登录代码:</label>

						<div class="col-xs-7">
							<input class="form-control input-sm" type="text"
								id="user_username" placeholder="UserName" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-4  control-label" for="user_password">登录密码:</label>

						<div class="col-xs-7">
							<input type="password" class="form-control input-sm"
								id="user_password" placeholder="Password" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-4  control-label" for="check_code">验证密码:</label>

						<div class="col-xs-4">
							<input class="form-control input-sm" id="check_code" type="text"
								name="check_code" placeholder="Code" AUTOCOMPLETE="off" />
						</div>
						<div class="col-xs-3" style="padding-left: 0px;">
							<img class="form-control input-sm" id="img_checkcode" src=""
								alt="验证码" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-4 control-label" >选择区域:</label>

						<div class=" col-xs-7">
							<select class="form-control input-sm" id="site"><%=VSSConfig.getInstance().optHTML%>
							</select>
						</div>
					</div>
					<div class="form-group" style="margin-top: 20px;">
						<div class="col-xs-6" style="text-align: right;">
							<input type="button" id="signin_submit" value="登&nbsp;&nbsp;录"
								class="btn-ttc btn-logon" />
						</div>
						<div class="col-xs-6">
							<input type="reset" value="重&nbsp;&nbsp;置"
								class="btn-ttc btn-reset" />
						</div>
					</div>
				</form>
			</div>
		</div>

		<!-- Indicators -->
		<ol class="carousel-indicators" style="bottom: 5px;">
			<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
			<li data-target="#myCarousel" data-slide-to="1"></li>
			<li data-target="#myCarousel" data-slide-to="2"></li>
		</ol>
		<div class="carousel-inner">
			<div class="item active">
				<img src="../resources/images/logobg/bg01.jpg" />

				<div class="container">
					<div class="carousel-caption-ttc">
						<img src="../resources/images/xlh.png" />
					</div>
				</div>
			</div>
			<div class="item">
				<img src="../resources/images/logobg/bg02.png" />

				<div class="container">
					<div class="carousel-caption-ttc">
						<img src="../resources/images/sjyg.png" />
					</div>
				</div>
			</div>
			<div class="item">
				<img src="../resources/images/logobg/bg03.png" />

				<div class="container">
					<div class="carousel-caption-ttc">
						<img src="../resources/images/msjl.png" />
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- /.carousel -->


	<!-- Marketing messaging and featurettes
================================================== -->
	<!-- Wrap the rest of the page in another container to center all the content. -->
	<div class="container">
		<div
			style="line-height: 1.7; font-size: 14px; font-family: arial; padding-top: 20px;">
			<p align="center">
				<span><span lang="EN-US" style="color: rgb(117, 131, 157)">&nbsp;</span></span>
				<span><span lang="EN-US"
					style="font-size: 10.5pt; color: rgb(117, 131, 157)"><a
						href="http://www.hnagroup.com/" target="_blank"><span
							lang="EN-US" style="color: #75839d; text-decoration: none"><span
								lang="EN-US">海航集团</span></span></a>&nbsp;</span></span><span> <span
					style="font-size: 10.5pt; color: rgb(117, 131, 157)"> ┊<span
						lang="EN-US">&nbsp;<a href="http://www.hnadc.net/"
							target="_blank"><span lang="EN-US"
								style="color: #75839d; text-decoration: none"><span
									lang="EN-US">海航地产</span></span></a>&nbsp;
					</span> ┊<span lang="EN-US">&nbsp;<a
							href="http://www.cnminsheng.com/" target="_blank"><span
								lang="EN-US" style="color: #75839d; text-decoration: none"><span
									lang="EN-US">西安民生</span></span></a>&nbsp;
					</span> ┊<span lang="EN-US">&nbsp;<a href="http://www.hnsip.com/"
							target="_blank"><span lang="EN-US"
								style="color: #75839d; text-decoration: none"><span
									lang="EN-US">望海国际</span></span></a></span></span></span>
			</p>

			<p align="center">
				<span lang="EN-US" style="font-size: 10.0pt; color: darkgray">Copyright
					©2013 HNA Holding </span><span lang="EN-US"
					style="font-size: 10.0pt; color: #939393;">All Rights
					Reserved </span>
			</p>

			<p align="center">
				<span lang="EN-US" style="font-size: 10.0pt; color: #939393"></span><span
					style="font-size: 10.0pt; color: darkgray">海航实业控股（集团）有限公司<span
					lang="EN-US"> &nbsp;</span>版权所有<span lang="EN-US">&nbsp; </span></span><span
					lang="EN-US" style="font-size: 10.0pt; color: #333333"><a
					href="http://www.miit.gov.cn/n11293472/index.html" target="_blank"><span
						lang="EN-US" style="color: darkgray"><span lang="EN-US">琼ICP</span></span><span
						lang="EN-US" style="color: darkgray"><span lang="EN-US">备11000390-2</span></span><span
						lang="EN-US" style="color: darkgray"><span lang="EN-US">号</span></span></a>
				</span>
			</p>

			<p align="center">
				<span style="font-size: 10.0pt; color: darkgray">系统客服电话：<span
					lang="EN-US">010-84871788</span>（<span lang="EN-US">1007</span>）（百货）<span
					lang="EN-US">029-62654202 </span>（超市）<span lang="EN-US"></span></span>
			</p>
		</div>
	</div>
	<!-- Latest compiled and minified JavaScript -->
	<script src="../resources/bootstrap-3.0.2-dist/js/bootstrap.min.js"
		type="text/javascript"></script>

	<script>
var loading_html = '登录验证，请稍候……';
var loginsuccess_html = '登录成功，请稍候……';

$(document)
        .ready(
        function () {
            //初始化轮播
            $('.carousel').carousel({
                interval: 4000
            });

            //设置默认站点
            var tmp = getCookie("site");
            if (tmp != null) {
                $('#site').val(tmp);
            }

            //设置自动大写
            $('#user_username').blur(
                    function (event) {
                        event.target.value = event.target.value
                                .toUpperCase();
                    });
            $('#check_code').blur(
                    function (event) {
                        event.target.value = event.target.value
                                .toUpperCase();
                    });

            //获取验证码图片
            jQuery('#check_code').focus(function () {
                var obj = jQuery('#img_checkcode');
                var seldata = $('#site').val();
                if (seldata == 110) {
                    obj.attr('src', '../hhvssjrd/DaemonCode?timestamp=' + new Date().getTime());
                } else {
                    obj.attr('src', '../DaemonCode?timestamp=' + new Date().getTime());

                }
                obj.fadeIn(500);
            });
            jQuery('#img_checkcode').click(function (event) {
                var seldata = $('#site').val();
                var obj = jQuery(event.target);
                obj.hide();
                if (seldata == 110) {
                    obj.attr('src', '../hhvssjrd/DaemonCode?timestamp=' + new Date().getTime());
                } else {

                    obj.attr('src', '../DaemonCode?timestamp=' + new Date().getTime());

                }
                obj.fadeIn(1000);
            });
            //设置登录动作
            jQuery('#signin_submit')
                    .click(
                    function () {
                        var site = jQuery("#site")
                                .val();
                        var username = jQuery(
                                "#user_username").val();
                        var password = jQuery(
                                "#user_password").val();
                        var checkcode = jQuery(
                                "#check_code").val();

                        if (username == '') {
                            alert('登录代码必须填写');
                            return;
                        } else if (password == '') {
                            alert('密码必须填写');
                            return;
                        } else if (checkcode == '') {
                            alert('验证码必须填写');
                            return;
                        }
                        if (site == 1) {
                            jQuery.post("../hhvssjrd/DaemonLogonVender", {
                                'site': 0,
                                'action': 'logon',
                                'logonid': username,
                                'password': password,
                                'checkcode': checkcode
                            }, function (xml) {
                                var elmXerr = jQuery(xml).find('xerr');
                                var code = elmXerr.children('code').text();
                                if (code == 0) {
                                    setCookie("site", site);
                                    window.location = "../hhvssjrd/logon/main.htm";
                                } else {
                                    var note = elmXerr.children('note').text();
                                    alert("登陆失败,错误代码[" + code + "]-错误信息: " + note);
                                }
                            }, 'xml').error(function (ex) {
                                alert("请求错误,错误代码[" + ex.readyState + "],错误信息: " + ex.responseText);
                            });
                            return;
                        }
                        //如果是百货站点，转到百货网站
                        //ajax 登录例子
                        //url 对方的登录验证的地址
                        //对方的返回格式有要求，必须是:var ojb = {msg:'请求成功',code:100};  这种格式，其实就是一段js脚本
                        //同时还要求两边的字符集是一样的，否则会乱码，所以，可以只要对方提供 code，自己根据code属性来提示。
                        if (site == 101 || site == 102
                                || site == 103
                                || site == 104
                                || site == 105) {
                            $.get('../DaemonLogonVender',
                                    {
                                        site: site,
                                        password: password,
                                        logonid: username
                                    },
                                    function (data) {
                                        var obj = eval('(' + data + ')');
                                        var str1 = obj.SID;
                                        if (typeof(obj) == "undefined") {
                                            alert('远程验证服务器出错!稍后重试..');
                                            return;
                                        }
                                        if (str1 == -5) {
                                            alert('状态错误');
                                            return;
                                        } else if (str1 == -2) {
                                            alert('账号资费不足');
                                            return;
                                        } else if (str1 == 03) {
                                            alert('商场给名单开通');
                                            return;
                                        } else if (str1 == 04) {
                                            alert('暂停功能');
                                            return;
                                        } else if (str1 == 10) {
                                            alert('用户名为空');
                                            return;
                                        } else if (str1 == 11) {
                                            alert('密码为空');
                                            return;
                                        } else if (str1 == 12) {
                                            alert('用户名或密码含有非法字符');
                                            return;
                                        } else if (str1 == 13) {
                                            alert('服务器错误');
                                            return;
                                        } else if (str1 == 14) {
                                            alert('用户名或密码不正确,请重新输入');
                                            return;
                                        } else if (str1 == 15) {
                                            alert('无权登陆');
                                            return;
                                        } else if (str1 == 16) {
                                            alert('用户名或密码不存在请重新输入 ');
                                            return;
                                        } else if (str1 == 17) {
                                            alert('用户名或密码错误！请重新输入；');
                                            return;
                                        } else {
                                            window.location = "http://113.140.27.50:81/Web_login.aspx?sid="
                                                    + obj.SID
                                                    + "&username="
                                                    + obj.username;
                                        }

                                    });
                            return;
                        }

                        var url = "../DaemonLogonVender";

                        $('#loading').show();
                        $('#loading').find('span')
                                .text(loading_html);

                        $.post(url,
                                {
                                    'site': site,
                                    'action': 'logon',
                                    'logonid': escape(username),
                                    'password': escape(password),
                                    'checkcode': escape(checkcode)
                                },
                                function (xml) {
                                    jQuery('#loading').hide();
                                    var elmXerr = jQuery(xml).find('xerr');
                                    var code = elmXerr.children('code')
                                            .text();
                                    if (code != 0) {
                                        alert(elmXerr
                                                .find(
                                                'note')
                                                .text());
                                        jQuery(
                                                '#user_password')
                                                .val(
                                                "");
                                        jQuery(
                                                '#check_code')
                                                .val(
                                                "");
                                        jQuery(
                                                '#img_checkcode')
                                                .click();
                                        jQuery(
                                                '#user_username')
                                                .focus();
                                        return;
                                    } else {
                                        jQuery(
                                                '#loading')
                                                .show();
                                        jQuery(
                                                '#loading')
                                                .find(
                                                'span')
                                                .text(
                                                loginsuccess_html);
                                        setCookie(
                                                "site",
                                                site);
                                        location
                                                .replace("main.htm");
                                    }
                                }, 'xml');
                    });
        });

function RequiredField() {
    if (jQuery('#user_username').val() != ''
            && jQuery('#user_password').val() != ''
            && jQuery('#check_code').val() != '') {
        jQuery('#signin_submit').attr('disabled', false);
    } else {
        jQuery('#signin_submit').attr('disabled', true);
    }
}

function setCookie(name, value) {
    var Days = 360; //此cookie将被保存360天
    var exp = new Date();
    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
    document.cookie = name + "=" + escape(value) + ";expires="
            + exp.toGMTString();
}
function getCookie(name) {
    var arr, reg = new RegExp("(^|)" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
}
function delCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = getCookie(name);
    if (cval != null) {
        document.cookie = name + "=" + cval + ";expires="
                + exp.toGMTString();
    }
}
</script>
</body>
</html>
