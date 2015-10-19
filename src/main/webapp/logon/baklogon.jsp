<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*"
	import="com.royalstone.vss.daemon.DaemonLogonVender"%>
<%
String url	= "../DaemonLogonVender?action=load_code";
%>
<html>
<head>
<title>商业供应商服务系统</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="keywords" content="商业,VSS,供应商,零售商,服务,富基融通,JAVA" />
<link rel="SHORTCUT ICON" href="favicon.ico" />
<style type="text/css">
TD.tem {
	FONT-SIZE: 9pt;
	LINE-HEIGHT: 14pt;
	FONT-FAMILY: 宋体
}

NPUT {
	FONT-SIZE: 9pt;
}
</style>

<script language="javascript" src="../js/ajax.js" type="text/javascript"> </script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>
<script language="javascript" src="../js/Number.js"
	type="text/javascript"> </script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"> </script>
<xml id="doc4reply" />
<script language="javascript" type="text/javascript">
			var loading_html='<img src="../img/loading.gif"></img><font color=#003>正在处理您的请求……</font>';
			var loginsuccess_html='<img src="../img/loading.gif"></img><font color=#003>登录成功，请稍候……</font>';
			
			window.onload=function(){
				document.getElementById('user_username').focus();
				var tmp = getCookie("venderType");
				if(tmp!=null){
					if(tmp=="ole"){
						document.getElementById("radio2").checked = true;
					}
				}
			}
			function login(){
				var venderType;
				if(document.getElementById("radio1").checked){
					venderType="crv";
				}else{
					venderType="ole";
				}
				
				var url="../DaemonLogonVender?venderType="+venderType+"&action=logon&logonid="+document.getElementById('user_username').value+"&password="+document.getElementById('user_password').value+"&checkcode="+document.getElementById('check_code').value;      
				document.getElementById('loading').style.display="block";
				document.getElementById('loading').innerHTML=loading_html;
				var courier         = new AjaxCourier(url);
				courier.island4req  = null;
				courier.reader.read = function(text){
					document.getElementById('doc4reply').loadXML(text);	    	
					var elm_err 	= document.getElementById('doc4reply').XMLDocument.selectSingleNode( "/xdoc/xerr" );
					var xerr = parseXErr( elm_err );
					if ( xerr.code == "0" ){
							setCookie("venderType",venderType);
							document.getElementById('loading').innerHTML=loginsuccess_html;
							location.replace( "main.htm" );
					} else {
						alert(xerr.note);			
						window.location.reload();
					}
				};
				courier.call();           
			}
			
			function checkcode_up( ctrl ){
				ctrl.value = ctrl.value.toUpperCase();
			}
			
			function RequiredField(){
				if( document.getElementById('user_username').value != '' 
					&& document.getElementById('user_password').value != '' 
					&& document.getElementById('check_code').value != '' )
				{
					document.getElementById('signin_submit').disabled=false;
				}
				else
				{
					document.getElementById('signin_submit').disabled=true;
				}
			}
			
			function  setCookie(name,value)  
			{  
			     var Days = 360;   //此cookie将被保存360天  
			     var exp = new Date(); 
			     exp.setTime(exp.getTime() + Days*24*60*60*1000);  
			     document.cookie = name + "=" + escape(value) 
					+ ";expires=" + exp.toGMTString();  
			 }  
			 function getCookie(name)  
			 {  
			     var arr,reg=new RegExp("(^|)"+name+"=([^;]*)(;|$)");  
			     if(arr=document.cookie.match(reg))
				return unescape(arr[2]);  
			     else
				return null;  
			 }  
			 function delCookie(name)  
			 {  
			     var exp = new Date();  
			     exp.setTime(exp.getTime() - 1);  
			     var cval=getCookie(name);  
			     if(cval!=null) {
				document.cookie= name + "="+cval
					+";expires="+exp.toGMTString();
				}
			 }

			</script>
</head>
<body>
	<table height="100%" cellspacing="0" cellpadding="0" width="90%"
		align="center" bgcolor="#fffcdf" border="0">
		<tbody>
			<tr>
				<td valign="center" align="middle" height="218">
					<table height="150" cellspacing="0" cellpadding="0" width="539"
						align="center" background="../img/dw.gif" border="0">
						<tbody>
							<tr>
								<td height="65" colspan="2" align="center"><font
									style="LETTER-SPACING: 10px" color="#f99d33" size="5"><b>商业供应商服务系统</b></font></td>
							</tr>
							<tr>
								<td valign="center" align="right" width="190" rowspan="2"><img
									src="../img/LogoBig.jpg" alt=""></td>
								<td valign="center" align="middle" width="349"></td>
							</tr>
							<tr>
								<td valign="bottom" align="left" width="349">
									<form name="frmLogin" action="Login.do" method="post">
										<table cellspacing="4" cellpadding="0" width="100%"
											align="left" border="0">
											<tbody>
												<tr>
													<td class="tem" nowrap>登录代码&nbsp;</td>
													<td class="tem"><input type="text" id="user_username"
														size="12" name="user_username"
														onkeypress="if(event.keyCode<=123 && event.keyCode>=97)event.keyCode=event.keyCode-32"
														onkeydown="RequiredField()" onblur="checkcode_up(this)"></td>
													<td class="tem">&nbsp;密&nbsp;&nbsp;码&nbsp;</td>
													<td class="tem"><input id="user_password"
														type="password" size="12" name="Passwd"
														onkeydown="RequiredField()"></td>
												</tr>
												<tr>
													<td class="tem" nowrap>验证码:&nbsp;</td>
													<td class="tem"><input id="check_code" type="text"
														size="12" name="check_code"
														onkeypress="if(event.keyCode<=123 && event.keyCode>=97)event.keyCode=event.keyCode-32"
														onkeyup="checkcode_up(this);if(event.keyCode==13)login();"
														onkeydown="RequiredField()"></td>
													<td class="tem" colspan="2"><img id="img_checkcode"
														src="<%=url%>" alt="" /></td>
												</tr>
												<tr>
													<td colspan="3"><label for="radio1"> <input
															type="radio" vlaue="crv" name="radio1" id="radio1"
															checked="checked" /> 华南大超/标超
													</label> <label for="radio2"> <input type="radio"
															vlaue="ole" name="radio1" id="radio2" /> OLE
													</label></td>
													<td><input type="button" id="signin_submit"
														value="登&nbsp;&nbsp;录" onclick="login()" disabled="true"
														style="width: 70px; height: 24px; font-weight: bold;"></td>
												</tr>
											</tbody>
										</table>
									</form>
								</td>
							</tr>
							<tr>
								<td align="middle" colspan="3" height="43">
									<div id="loading" style="display: none;"></div>
								</td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td align="middle" colspan="3" height="43"><font size="2">粤ICP备05036369号</font></td>
			</tr>
		</tbody>
	</table>
</body>
</html>
