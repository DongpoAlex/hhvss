<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession( false );
	if (session == null)
		throw new PermissionException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException("您尚未登录,或已超时.");
	%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<style>
</style>
<script type="text/javascript">
			
			function initTask(){
				setLoading(true,'正在检查需要处理的证照');
				var url 		= "../DaemonCertificate?action=getTask";
				var table = new AW.XML.Table;
				table.setURL(url);
				table.setTable("xdoc/xout/group/task");
				table.request();
				table.response = function(text){
					setLoading(false);
					table.setXML(text);
					var xcode = table.getErrCode();
					if( xcode != '0' ){//处理xml中的错误消息
						alert( xcode+table.getErrNote());
					}else{
						var htmlOut = table.transformNode("taskList.xsl");
						$('taskList').innerHTML = htmlOut;
					}
				};

			}
		
		function toSheet(sheetid,type){
			window.open("certificate_edit.jsp?sheetid="+sheetid+"&type="+type);
		}

		function newSheet(type){
			window.location.href="certificate_add.jsp?type="+type;
		}
		</script>
</head>
<body>
	<!-- 
		<table class="navtable2" cellpadding="0" cellspacing="0" border="0" style="">
			<thead>
				<th colspan="4">您需要添加的证照提示如下：</th>
			</thead>
			<tbody>
				<tr>
					<td><div id="taskList"></div></td>
				</tr>
			</tbody>
			<tfoot>
			</tfoot>		
		</table>
	 -->
	<table class="navtable" cellpadding="0" cellspacing="0" border="0">
		<thead>
			<th colspan="4">开始录入新证照</th>
		</thead>
		<tbody>
			<tr>
				<td width="100"><a href="certificate_add.jsp?type=1"
					title="新增基本证照"><img alt="新增基本证照" src="images/main1.gif"></a></td>
				<td width="370"><div class="note">基本证照：合作供应商经营必备证照，主要包括：营业执照（副本）、组织机构代码、税务登记证国、地税（副本）等。</div></td>
				<td width="100"><a href="certificate_add.jsp?type=2"
					title="新增品类证照"><img alt="新增品类证照" src="images/main2.gif"></a></td>
				<td width="370"><div class="note">品类证照：依据合作供应商合同签约品类部门，提供该品类部门所需证照资料。</div></td>
			</tr>
			<tr>
				<td width="100"><a href="certificate_add.jsp?type=3"
					title="新增旧品证照"><img alt="新增旧品证照" src="images/main3.gif"></a></td>
				<td width="370"><div class="note">旧品证照：合作供应商已正常销售单品的质检报告、3C证书、进出口检验检疫证明\报关单等资料录入</div></td>
				<td width="100"><a href="certificate_add.jsp?type=4"
					title="新增新品证照"><img alt="新增新品证照" src="images/main4.gif"></a></td>
				<td width="370"><div class="note">新品证照:
						合作供应商新立项商品的质检报告、3C证书、进出口检验检疫证明\报关单等资料录入。</div></td>
			</tr>
		</tbody>
		<tfoot>
		</tfoot>
	</table>

	<table class="navtable2" cellpadding="0" cellspacing="0" border="0">
		<thead>
			<th colspan="2">查看需要处理的证照</th>
			<th colspan="2">审核通过的正式证照</th>
		</thead>
		<tbody>
			<tr>
				<td width="100"><a href="certificate_vender_list.jsp"
					title="待提交证照"><img alt="待提交证照" src="images/main5.gif"></a></td>
				<td width="370"><div class="note">只有将证照提交，零售商才能看到审核。对于即将过期和已过期的证照也能在这里找到，请将它们重新编辑后提交。</div></td>
				<td width="100"><a href="certificate_vender_report.jsp"
					title="已核准证照"><img alt="已核准证照" src="images/main6.gif"></a></td>
				<td width="370"><div class="note">这里可以看到所有核准通过，正式生效的证照。</div></td>
			</tr>
		</tbody>
		<tfoot>
		</tfoot>
	</table>
</body>
</html>