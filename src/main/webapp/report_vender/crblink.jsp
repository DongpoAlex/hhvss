<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" import="java.util.*" import="java.sql.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.util.security.DES_Encrypt"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException("您尚未登录,或已超时.");

	String[] venders = token.getEnv("venderid");
	String venderid = venders[0];
	//加密
	String descode = DES_Encrypt.encrypt(venderid);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>华润银行-携手借贷</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript">
jQuery(document).ready(function(){
	//jQuery("form").first().submit();
	
	//检查是否同意协议
	var url ="../DaemonVenderAdmin?operation=getCrbLicStatus&focus=venderdiy";
	jQuery.get(url,{},
			function(xml){
				var elmXerr = jQuery(xml).find('xerr');
				var code = elmXerr.children('code').text();
				if(code!=0){
					alert(elmXerr.find('note').text());
					return;
				}else{
					var status = jQuery(xml).find('xout').text();
					if(status=='N'){
						//显示协议
						jQuery('#div_lic').slideDown(4000);
					}else{
						jQuery("form").first().submit();
					}
				}
			},'xml');
});

function argee(){
	var url ="../DaemonVenderAdmin?operation=agreeCrbLic&focus=venderdiy";
	jQuery.get(url,{},
	function(xml){
		var elmXerr = jQuery(xml).find('xerr');
		var code = elmXerr.children('code').text();
		if(code!=0){
			alert(elmXerr.find('note').text());
			return;
		}else{
			jQuery("form").first().submit();
		}
	},'xml');
}

</script>
</head>
<body>
	<form action="https://ebanking.crbank.com.cn/corbank/loanIndex.do"
		method="post" id="crbform" name="crbform">
		<input type="hidden" value="<%=descode%>" id="crbdescode"
			name="suppliNo">
	</form>

	<div id="div_lic"
		style="display: none; width: 100%; text-align: center;">
		<div style="">
			<h3>交易信息使用授权</h3>
			<div
				style="text-align: left; padding-left: 20%; padding-right: 20%; line-height: 150%;">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				请供应商在使用本软件/链接前仔细阅读本授权。包括免除或者限制商业（包括商业有限公司及其关联公司，下同）责任的免责申明。供应商的安装使用行为将视为对本《授权》的接受，并同意接受本授权约束。<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				供应商同意商业将其与本公司的合作/交易信息（包括但不限于销售额、已对账金额、库存等）提供给珠海华润银行股份有限公司（以下简称“华润银行”），用于申请办理华润银行“E润通
				”业务及其它金融业务。华润银行使用该等信息产生的任何责任与商业无关。
				<hr>
			</div>
			<input type="button" value="同意" onclick="argee()">&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" value="拒绝" onclick="window.close();">
		</div>
	</div>
</body>
</html>