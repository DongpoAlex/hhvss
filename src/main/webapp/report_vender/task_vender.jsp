<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" import="java.util.*" import="java.sql.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.util.security.DES_Encrypt"
	errorPage="../errorpage/errorpage.jsp"%>

<%
request.setCharacterEncoding( "UTF-8" );
final int moduleid = 3010101;

HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );

// 查询用户的权限.
Permission perm = token.getPermission( moduleid );
String msg = "您未获得使用此模块的授权,请与管理员联系. 模块号: " + moduleid;
if( !perm.include( Permission.READ ) ) throw new PermissionException( msg );
%>

<%
String[] venders = token.getEnv( "venderid" );
String   venderid = venders[0];
%>

<%
Connection conn = XDaemon.openDataSource( token.site.getDbSrcName() );
Vender     vender = Vender.getVender( conn, venderid );
XDaemon.closeDataSource( conn );
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
body {
	font-size: 14px;
	color: navy;
	margin-left: 8px;
	margin-right: 8px;
	margin-top: 0px;
	scrollbar-base-color: #F9F8F4;
	scrollbar-arrow-color: #ECEBEB;
}

.vender {
	color: #006600;
	font-weight: bold;
}

.wellcome {
	float: none;
	border-bottom-width: 4px;
	border-bottom-color: #CbCbCb;
	padding-left: 16px;
	background-color: #efefef;
	padding-top: 5px;
	border-bottom-style: solid;
	margin-bottom: 8px;
	height: 30px;
}

.note {
	font-size: 12px;
	margin-top: 10px;
	color: #888;
	text-align: right;
}

.task_title {
	font-weight: bold;
	font-size: 14px;
	color: #F79A30;
	background-color: #E9FBE3;
	border-bottom: 4px solid #8AB933;
	padding-top: 4px;
	padding-bottom: 4px;
	padding-left: 14px;
}

.task {
	width: 99%;
	border: 1px solid #D7EAB3;
}

.warning {
	margin-top: 5px;
	width: 99%;
	border: 1px solid #FFCCCC;
}

.warning_title {
	font-weight: bold;
	font-size: 14px;
	color: #FF6666;
	background-color: #FFEBEB;
	border-bottom-width: 4px;
	border-bottom-style: solid;
	border-bottom-color: #CC3366;
	padding-top: 4px;
	padding-bottom: 4px;
	padding-left: 14px;
}

.mid {
	height: 400px;
	width: 99%;
}

#div_ad {
	cursor: pointer;
	display: none;
	background: url("../img/crb_ad.jpg") no-repeat;
	height: 257;
	width: 697;
}

#div_left {
	float: left;
	width: 26%;
}

#div_right {
	float: right;
	width: 73%;
}

#vg_logo {
	background-image: url(<%= token.site.getLogo ()%>);
	height: 90px;
	width: 166px;
}

#div_noteboard {
	height: auto;
	margin-bottom: 8px;
}

#div_noteboard_title {
	font-weight: bold;
	font-size: 14px;
	color: #F79A30;
	background-color: #FEF2E0;
	padding-top: 4px;
	padding-bottom: 4px;
	padding-left: 14px;
	border-top: 1px solid #F79A30;
	border-right: 1px solid #F79A30;
	border-left: 1px solid #F79A30;
}

.warning_catalogue a {
	margin: 8px;
	line-height: 180%;
	color: red;
}

UL {
	margin: 4px;
	margin-left: 0px;
}

LI {
	line-height: 200%;
	list-style-type: none;
	background-image: url(../img/green.gif);
	background-repeat: no-repeat;
	padding-left: 22px;
	background-position: 2px;
}

.foot {
	float: left;
	width: 99%;
	text-align: center;
	padding: 5px;
	background-color: #CEE3EF;
	border-bottom-width: 6px;
	border-bottom-color: #0084AD;
	border-bottom-style: solid;
}

.noteboard_list {
	font-size: 12px;
	background-color: #333;
	width: 100%;
}

.noteboard_list td {
	background-color: #fff;
	border-top: 1px solid #e3e3e3;
	cursor: pointer;
}

.noteboard_list th {
	background-color: #e9e9e9;
}
</style>

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/jquery.xslt.js"></script>
<script type="text/javascript" src="../js/jquery.timers.js"></script>
<script language="javascript">
		var sid = <%=token.site.getSid()%>;	
		var a1=7;
		var a2=21;
		if(sid==10){
			a1=14;
			a2=28;
		}
		
		jQuery(document).ready(function(){
			display_retwarning();
		});
		
		//退货预警
		function display_retwarning(){
			var url =  "../DaemonTaskPending?operation=hasRetWarning";
			jQuery.get(url,{},
			function(xml){
				var elm = jQuery(xml).find('retwarning');
				if(elm!=null){
					var awoke = Number(elm.attr('awoke'));
					var warning = Number(elm.attr('warning'));
					var siteName =elm.attr('siteName');
					if( awoke+warning > 0 ){
						jQuery('#div_readwarning').fadeIn();
						var messageHTML = "";
						if(awoke>0){
							messageHTML  = "<a href='javascript:load_module(3010131,\"g_status=90\")'>您目前有 "+awoke+" 份预警状态退货通知单，请尽快执行退货！</a><div>根据合同约定，贵司需在退货通知单发出之后的"+a1+"天内完成退货，逾期未退，需向"+siteName+"交纳逾期储位占用费！</div><br>";
						}
						if(warning>0){
							messageHTML += "<a href='javascript:load_module(3010131,\"g_status=91\")'>您目前有 "+warning+" 份过期状态退货通知单，请尽快执行退货！</a><div>根据合同约定，贵司需在退货通知单发出之后的"+a2+"天内完成退货,逾期未退，则视为贵司自动放弃本单所列商品及数量的所有权和处置权并承担相应法律责任！</div><br>"
						}
						jQuery('#div_warning_message').html(messageHTML);
					}else{
						read_warning();
					}
				}
				jQuery('.foot').fadeIn();
			});
		}
		
		function read_warning(){
			jQuery('#div_readwarning').hide();
			jQuery('#div_cat').show();
			
			display_catalogue();
		}
		
		function display_ad(){
			return;
			//if(sid==1){
				jQuery('#div_ad').slideDown(3000);
				
				//15秒后缩小
				jQuery('body').oneTime(15000,'D',function(){
					jQuery("#div_ad").animate({height:"50px",speed:3000});
				});
			//}
		}
		
		function display_catalogue( )
		{
			var url =  "../DaemonTaskPending";
			var xslPath = "../xsl/task_vender.xsl";
			jQuery.get(url,{},function(xml){
				var xmlstr;
				if(jQuery.browser.msie){
					xmlstr = xml.xml;
				}else{
					xmlstr = (new XMLSerializer()).serializeToString(xml);
				}
				//display_newTaskList();
				//需要通过中间元素承转一下
				jQuery('#div_left_task').hide();
				jQuery('#cat_temp').xslt(xmlstr,xslPath,function(){jQuery('#div_left_task').html(jQuery('#cat_temp').html());jQuery('#div_left_task').slideDown(function(){display_noteboard();});});
			});
		}
		
		function display_newTaskList(){
			var url="../DaemonMain?clazz=VenderTask&operation=getTask";
			var xslPath = "../xsl/venderTaskList.xsl";
			jQuery.get(url,{},function(xml){
				var xmlstr;
				if(jQuery.browser.msie){
					xmlstr = xml.xml;
				}else{
					xmlstr = (new XMLSerializer()).serializeToString(xml);
				}
				//需要通过中间元素承转一下
				jQuery('#div_left_tasklsit').hide();
				jQuery('#cat_temp').xslt(xmlstr,xslPath,function(){jQuery('#div_left_tasklsit').html(jQuery('#cat_temp').html());jQuery('#div_left_tasklsit').slideDown();});
			});
			
		}
		
		function display_noteboard()
		{
			var url = "../DaemonBoardManager?operation=catalogue_vender";
			var xslPath = "../xsl/noteboard.xsl";
			jQuery.get(url,{},function(xml){
				var xmlstr;
				if(jQuery.browser.msie){
					xmlstr = xml.xml;
				}else{
					xmlstr = (new XMLSerializer()).serializeToString(xml);
				}
				var obj = jQuery(xml);
				obj.find("row").each(
					function(i){
						var editor = jQuery(this).children("editor");
					}
				);
				
				jQuery('#div_noteboard').hide();
				jQuery('#cat_temp').xslt(xmlstr,xslPath,function(){jQuery('#div_noteboard').html(jQuery('#cat_temp').html());
				jQuery('#div_noteboard').slideDown(
						//function(){display_ad();}
				);
				});
			});
		}
		
		function load_module ( moduleid, operation )
		{
			var url = "../logon/navigator.jsp?moduleid=" + moduleid +"&operation="+operation;
			frmMenu = parent.frames[ 0 ];
			frmMenu.location.href = url ;
		}
		
		function open_notebord(note_id)
		{
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,width=600,height=800,top=0";
			var url = " ../noteboard/noteboard_vender_open.jsp?note_id="+note_id ;
			window.open(url,note_id,attributeOfNewWnd);
		}

		function openvedio(){
			var attributeOfNewWnd = "location=no,titlebar=no,status=yes,toolbar=no,menubar=no,resizable=1,alwaysRaised=1,scrollbars=no,width=460,height=400,top=0";
			var url = "http://museum.crvanguard.com.cn/video/" ;
			window.open(url,"供应商网上结算培训视频",attributeOfNewWnd);
		}

		function toCRB(){
			/*window.open ('crblink.jsp', 'crb', '');*/
		}
		
		</script>

</head>

<body>
	<div id="div_cat" style="display: none">
		<div class="wellcome">
			<span style="float: left"> 您 好：<span class="vender"><%=token.username%>
			</span>， 欢迎访问供应商服务系统！
			</span>
			<div class="note">
				您目前代表以下供应商：
				<%=vender.vendername%>
				。
			</div>
		</div>
		<div class="mid">
			<div id="div_left">
				<div id="div_left_tasklsit"></div>
				<div id="div_left_task"></div>
			</div>
			<div id="div_right">
				<div id="div_ad"></div>
				<div style="padding: 4px; margin: 4px; border: 1px dotted #123123">
					1、供应商服务系统使用培训网址： <a href="http://113.140.27.50/vender/vss.zip">http://113.140.27.50/vender/vss.zip</a>
					<br /> 2、供应商索证管理系统使用教材下载地址： <br /> 3、商业移动供应链系统程序及操作手册：

					<script type="text/javascript">
						if(sid==1 || sid==2 || sid==8){
							document.write("<br/>");
							document.write("4、<a href=\"javascript:openvedio()\">供应商网上结算培训视频</a>");
						}
					</script>
				</div>
				<div id="div_noteboard_title">有效公文列表&nbsp;&nbsp;&nbsp;&nbsp;</div>
				<div id="div_noteboard"></div>
			</div>
		</div>

	</div>

	<div id="div_readwarning" style="display: none; height: 275px;">
		<div>
			<div class="warning">
				<div class="warning_title">系统预警提醒</div>
				<div id="div_warning_message" class="warning_catalogue"
					style="padding: 10px;"></div>
				<div style="text-align: center; margin: 10px;">
					<input type="button" value="确定" onclick="read_warning()" />
				</div>
			</div>
		</div>
	</div>

	<div class="foot">
		<div class="foot_top">
			&copy;海航实业控股（集团）有限公司 版权所有 <a href="#" onclick="toCRB()">&copy;</a>
		</div>
		<div>CopyRight &copy; 2013 China Resource Vanguard
			·最佳分辨率1024×768·</div>
	</div>

	<span id="cat_temp" style="display: none;"></span>
</body>
</html>
