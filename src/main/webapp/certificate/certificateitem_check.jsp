<%@ page pageEncoding="utf-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*" import="java.sql.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException("您尚未登录,或已超时.");
	//查询用户的权限.
	final int moduleid = 8000003;
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);
	
	String type = request.getParameter("type");
	String sheetid = request.getParameter("sheetid");
	String id = request.getParameter("id");
	String ccid = request.getParameter("ccid");
	String flag = request.getParameter("flag");
	String filename = request.getParameter("filename");
	String btn_none = request.getParameter("btn_none");
	String venderid = request.getParameter("venderid");
	String imgsrc = "../img/a.gif";
	if (filename != null && filename.length() > 0) {
		imgsrc = "../DaemonImgDownLoad?venderid=" + venderid
				+ "&filename=" + filename;
	}
	//初始值
	String ctid = request.getParameter("ctid");
	String cname = new String(request.getParameter("cname").getBytes(
			"ISO8859-1"), "UTF-8");
	String certificateid = request.getParameter("certificateid");
	String ctype = new String(request.getParameter("ctype").getBytes(
			"ISO8859-1"), "UTF-8");
	String yeardate = request.getParameter("yeardate");
	String expirydate = request.getParameter("expirydate");
	String goodsname = new String(request.getParameter("goodsname")
			.getBytes("ISO8859-1"), "UTF-8");
	goodsname = goodsname.replace("'", "’");
	String barcodeid = request.getParameter("barcodeid");
	String approvalnum = request.getParameter("approvalnum");
	String papprovalnum = request.getParameter("papprovalnum");
	String note = request.getParameter("note");
	String checker = request.getParameter("checker");
	String checktime = request.getParameter("checktime");
	String editor = request.getParameter("editor");
	String edittime = request.getParameter("edittime");
	ctid = ctid == null ? "" : ctid;
	cname = cname == null ? "" : cname;
	certificateid = certificateid == null ? "" : certificateid;
	ctype = ctype == null ? "" : ctype;
	yeardate = yeardate == null ? "" : yeardate;
	expirydate = expirydate == null ? "" : expirydate;
	goodsname = goodsname == null ? "" : goodsname;
	barcodeid = barcodeid == null ? "" : barcodeid;
	approvalnum = approvalnum == null ? "" : approvalnum;
	papprovalnum = papprovalnum == null ? "" : papprovalnum;
	note = note == null ? "" : note;
	checker = checker == null ? "" : checker;
	checktime = checktime == null ? "" : checktime;
	editor = editor == null ? "" : editor;
	edittime = edittime == null ? "" : edittime;

	//控件显示
	String dis_cname = "block";
	String dis_certificateid = "block";
	String dis_ctype = "none";
	String dis_yeardate = "none";
	String dis_expirydate = "none";
	String dis_goodsname = "none";
	String dis_barcodeid = "none";
	String dis_note = "block";
	String dis_approvalnum = "none";
	String dis_papprovalnum = "none";
	if ("1".equals(request.getParameter("yearflag"))) {
		dis_yeardate = "block";
	}
	if ("1".equals(request.getParameter("appflag"))) {
		dis_approvalnum = "block";
		dis_papprovalnum = "block";
	}

	if ("1".equals(type)) {
		dis_ctype = dis_expirydate = "block";
	} else if ("2".equals(type)) {
		dis_ctype = dis_expirydate = "block";
	} else if ("3".equals(type)) {
		dis_expirydate = dis_barcodeid = dis_goodsname = "block";
	} else if ("4".equals(type)) {
		dis_expirydate = dis_barcodeid = dis_goodsname = "block";
	}
%>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"/>
<link href="./css.css" rel="stylesheet" />
<script src="../js/Date.js"> </script>
<script src="../AW/runtime/lib/aw.js"> </script>
<script src="./common.js"> </script>
<style>
</style>
<script type="text/javascript">
		function init(sheetid,seqno){
			var parms = new Array();
			parms.push("action=getImageList");
			parms.push("sheetid="+sheetid);
			parms.push("seqno="+seqno);
			var url  = "../DaemonCertificate?"+parms.join('&');
			var table = new AW.XML.Table;
			table.setURL(url);
			table.request();
			table.response = function(text){
				table.setXML(text);
				var xcode = table.getErrCode();
				if( xcode != '0' ){//处理xml中的错误消息
					alert( xcode+table.getErrNote());
				}else{
					//判断图片个数
					var node_row 	= table.getXML().selectNodes( "/xdoc/xout/img/row" );
					var rows = node_row.length;
					var imgsrc = "./images/noimg.gif";
					if(rows==0){
					//没有图片
					}else if(rows==1){
						imgsrc = "../DaemonImgDownLoad?venderid=<%=venderid%>&filename="+node_row[0].selectSingleNode("imgfile").text;
					}else{
					//多图
						imgsrc = "./images/imgmore.jpg";
					}
					$('uploadIMG').src = imgsrc;
					$('a_uploadIMG').href = "certificate_detail.jsp?sheetid=<%=sheetid%>&seqno=<%=id%>&type=<%=type%>";
				}
			};
		}
		
		function checkItemOK(){
			if(<%=type%>==2 && window.parent.g_cnum - window.parent.g_checked == 1){
				if(!confirm("本证照为该单最后一张证照，是否审核通过？")){
					return;
				}
			}
			var parms = new Array();
			parms.push("action=checkitemOK");
			parms.push("sheetid=<%=sheetid%>");
			parms.push("note="+encodeURIComponent($('txt_note').value));
			parms.push("seqno=<%=id%>");
			var url  = "../DaemonCertificate?"+parms.join('&');
			var table = new AW.XML.Table;
			table.setURL((url));
			table.request();
			table.response = function(text){
				table.setXML(text);
				var xcode = table.getErrCode();
				if( xcode != '0' ){//处理xml中的错误消息
					alert( xcode+table.getErrNote());
				}else{
					alert("操作成功");
					$('checkNO_btn').disabled = 'disabled';
					$('checkOK_btn').disabled = 'disabled';
					$('checkBox_btn').disabled = 'disabled';
					$('txt_note').disabled = 'disabled';
					$('checkBox_btn').checked = false;
					window.parent.g_checked++;
					if(window.parent.g_cnum == window.parent.g_checked){
						alert("本单证照已全部审核通过！");
						window.parent.document.getElementById('btn_checkAll').disabled='disabled';
						window.parent.document.getElementById('btn_checkChecked').disabled='disabled';
					}
				}
			};
		}

		function checkItemOK2(){
			var parms = new Array();
			parms.push("action=checkitemOK");
			parms.push("sheetid=<%=sheetid%>");
			parms.push("note="+encodeURIComponent($('txt_note').value));
			parms.push("seqno=<%=id%>");
			var url  = "../DaemonCertificate?"+parms.join('&');
			var table = new AW.XML.Table;
			table.setURL((url));
			table.request();
			table.response = function(text){
				table.setXML(text);
				var xcode = table.getErrCode();
				if( xcode != '0' ){//处理xml中的错误消息
					alert( xcode+table.getErrNote());
				}else{
					$('checkNO_btn').disabled = 'disabled';
					$('checkOK_btn').disabled = 'disabled';
					$('checkBox_btn').disabled = 'disabled';
					$('txt_note').disabled = 'disabled';
					$('checkBox_btn').checked = false;
					window.parent.g_checked++;
					if(window.parent.g_cnum == window.parent.g_checked){
						window.parent.document.getElementById('btn_checkAll').disabled='disabled';
						window.parent.document.getElementById('btn_checkChecked').disabled='disabled';
					}
				}
			};
		}
		
		function checkItemNO(){
			var note =$('txt_note').value;
			if(note==''){
				alert('请填写备注，说明返回原因');
				return ;
			}
			var parms = new Array();
			parms.push("action=checkitemNO");
			parms.push("sheetid=<%=sheetid%>");
			parms.push("note="+encodeURIComponent(note));
			parms.push("seqno=<%=id%>");
			var url  = "../DaemonCertificate?"+parms.join('&');
			//alert(url)
			var table = new AW.XML.Table;
			table.setURL((url));
			table.request();
			table.response = function(text){
				table.setXML(text);
				var xcode = table.getErrCode();
				if( xcode != '0' ){//处理xml中的错误消息
					alert( xcode+table.getErrNote());
				}else{
					alert("操作成功");
					$('checkNO_btn').disabled = 'disabled';
					$('checkOK_btn').disabled = 'disabled';
					$('checkBox_btn').disabled = 'disabled';
					$('checkBox_btn').checked = false;
					$('txt_note').disabled = 'disabled';
					//有返回单，则不能整单通过
					window.parent.document.getElementById('btn_checkAll').disabled='disabled';
				}
			};
		}
		</script>
</head>
<body onload="init('<%=sheetid%>',<%=id%>)">
	<div class='detail'>
		<table>
			<tr>
				<td>
					<div style='cursor: pointer;' id="divuploadIMG">
						<a id="a_uploadIMG" href="./images/loading.gif" target="_blank">
							<img border="1" width='100' height='100' id='uploadIMG'
							src='./images/loading.gif'>
						</a>
					</div>
				</td>
				<td>
					<div style='width: 100%; margin-left: 6px; margin-bottom: 8px;'>
						<div style='width: 100%'>
							<div id='input_cname'
								style="margin-right:20px;float:left;display: <%=dis_cname%>">
								证照名称： <input class="lineinput" class="lineinput"
									readonly="readonly" id='txt_cname' type='text' size='20'
									maxlength='64' value='<%=cname%>' />
							</div>
							<div id='input_certificateid'
								style="margin-right:20px;float:left;display: <%=dis_certificateid%>">
								证照编码： <input class="lineinput" readonly="readonly"
									id='txt_certificateid' type='text' size='20' maxlength='64'
									value='<%=certificateid%>' />
							</div>
							<div id='input_ctype'
								style="margin-right:20px;float:left;display: <%=dis_ctype%>">
								证照类型： <span id='txt_ctype' class='xiahuaxian'><%=ctype%></span>
							</div>
						</div>
						<div style='width: 100%; margin-top: 4px;'>
							<div id='input_expirydate'
								style="margin-right:20px;float:left;display: <%=dis_expirydate%>">
								有效期至： <input class="lineinput" readonly="readonly"
									id='txt_expirydate' type='text' size='8'
									onblur='checkDate(this)' value='<%=expirydate%>' />
							</div>
							<div id='input_yeardate'
								style="margin-right:20px;float:left;display: <%=dis_yeardate%>">
								年审日期： <input class="lineinput" readonly="readonly"
									id='txt_yeardate' type='text' size='8' onblur='checkDate(this)'
									value='<%=yeardate%>' />
							</div>
							<div id='input_goodsname'
								style="margin-right:20px;float:left;display: <%=dis_goodsname%>">
								商品名称： <input class="lineinput" readonly="readonly"
									readonly="readonly" id='txt_goodsname' type='text' size='20'
									maxlength='64' value='<%=goodsname%>' />
							</div>
							<div id='input_barcodeid'
								style="margin-right:20px;float:left;display: <%=dis_barcodeid%>">
								商品条码： <input class="lineinput" readonly="readonly"
									id='txt_barcodeid' type='text' size='20' maxlength='64'
									value='<%=barcodeid%>' />
							</div>
							<div id='input_approvalnum'
								style="margin-right:20px;float:left;display: <%=dis_approvalnum%>">
								批文号： <input class="lineinput" id='txt_approvalnum' type='text'
									size='16' maxlength='64' value='<%=approvalnum%>' />
							</div>
							<div id='input_papprovalnum'
								style="margin-right:20px;float:left;display: <%=dis_papprovalnum%>">
								生产日期： <input class="lineinput" id='txt_papprovalnum' type='text'
									size='16' maxlength='64' value='<%=papprovalnum%>' />
							</div>
						</div>
						<div style='width: 100%; margin-left: 6px; margin-bottom: 8px;'>
							<div style="margin-right: 20px; float: left">
								编辑人： <input class="lineinput" readonly="readonly" type='text'
									size='8' maxlength='64' value='<%=editor%>' />
							</div>
							<div style="margin-right: 20px; float: left">
								编辑时间： <input class="lineinput" type='text' size='7'
									onblur='checkDate(this)' value='<%=edittime%>' />
							</div>
							<div style="margin-right: 20px; float: left">
								审核人： <input class="lineinput" readonly="readonly" type='text'
									size='8' maxlength='64' value='<%=checker%>' />
							</div>
							<div style="margin-right: 20px; float: left">
								审核时间： <input class="lineinput" type='text' size='7'
									onblur='checkDate(this)' value='<%=checktime%>' />
							</div>
							<span id='input_note' style="float:left;display: <%=dis_note%>">
								备注： <input class="lineinput" id='txt_note' type='text' size='30'
								maxlength='128' value='' />
							</span>
						</div>
					</div>
					<div class="line"></div>
					<div style='width: 100%; margin-left: 6px; margin-top: 8px;'>
						<a style='float: left; margin-right: 20px;' target='_blank'
							href='certificate_detail.jsp?sheetid=<%=sheetid%>&seqno=<%=id%>&type=<%=type%>'><img
							src='images/detail.gif'></a>
						<%
							String message = "";
							if ("1".equals(flag) || "2".equals(flag)) {
								if (!perm.include(Permission.CONFIRM)) {
						%>
						<input type="checkbox" id="checkBox_btn"> <br /> <input
							class="button" onclick="checkItemOK()" type="button"
							value="一审核通过" id="checkOK_btn"> <input class="buttonred"
							onclick="checkItemNO()" type="button" value="返回供应商"
							id="checkNO_btn">
						<%} else {%>
						<input type="checkbox" id="checkBox_btn"> <br /> <input
							class="button" onclick="checkItemOK()" type="button"
							value="二审核通过" id="checkOK_btn"> <input class="buttonred"
							onclick="checkItemNO()" type="button" value="返回供应商"
							id="checkNO_btn">
						<%}
							} else {
								if (flag.equals("0")) {
									out.print("<span style='color: yellow'>未提交</span>");
								} else if (flag.equals("1")) {
									out.print("<span style='color: olive'>已提交等待审核</span>");
								}else if(flag.equals("2")){
									out.print("<span style='color: blue'>一审核通过</span>");
								}else if (flag.equals("100")) {
									out.print("<span style='color: blue'>二审核通过</span>");
								} else if (flag.equals("-1")) {
									out.print("<span style='color: red'>审核返回</span>");
								} else if (flag.equals("-10")) {
									out.print("状态：<span style='color: red'>年审预警</span>");
								} else if (flag.equals("-11")) {
									out.print("状态：<span style='color: red'>过期预警</span>");
								} else if (flag.equals("-100")) {
									out.print("<span style='color: red'>过期作废</span>");
								} else {
									out.print("未定义的状态");
								}
						%>
						<script type="text/javascript">
						window.parent.g_checked++;
						</script>
						<%
							}
						%>
					</div>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>