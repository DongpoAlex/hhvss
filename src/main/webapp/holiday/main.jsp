<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.sql.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.basic.Vender"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	String venderid = token.getBusinessid();

	Connection conn = XDaemon.openDataSource(token.site.getDbSrcName());
	Vender vender = Vender.getVender(conn, venderid);
	XDaemon.closeDataSource(conn);

	String vendername = vender.vendername;
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>供应商节假日维护</title>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="../js/EditGrid.js"> </script>


<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<style>
.aw-grid-control {
	height: 60%;
	width: 100%;
	border: 1px solid #333;
}

.aw-grid-row {
	height: 20px;
	border-bottom: 1px dashed #ccc;
	height: 24px;
}

.aw-grid-cell {
	border-right: 1px solid #eee;
}

.editline {
	border: none;
	border-bottom: 1px solid #000;
}

.abled {
	background-color: #efe;
}

TH {
	text-align: left;
	background-color: #eee;
	color: blue;
	font-size: 16px;
}

input[readonly] {
	background-color: #dcdcdc;
	border: #3532ff 1px solid;
	color: #000000;
	cursor: default;
}
</style>
<script language="javascript" type="text/javascript">
		var startDay = new Array();
		window.onload= function(){
			show();
		};
		
		function initExp(){
			var table = new AW.XML.Table;
			table.setURL("../DaemonHoliday?operation=getexpVender");
			table.setTable("xdoc/xout/list");
			table.request();
			table.response = function (text) {
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					var str = table.transformNode( "../xsl/holidayexp.xsl" );
					$("div_explist").innerHTML=str;
					var cols = table.getXML().selectNodes("xdoc/xout/list/row");
					if(cols==null){return;}
					for ( var i = 0; i < cols.length; i++) {
						var col = cols[i];
						var status = col.selectSingleNode("status").text;
						var startdate = col.selectSingleNode("startdate").text;
						startDay.push(startdate);
						if(status=='已过填写日期' || status=='未到填写日期'){
							var form = $("item"+(i+1));
							var inputs = form.getElementsByTagName("input");
							inputs[0].value=status;
							inputs[0].disabled="false";
							inputs[1].disabled="false";
						}
					}
				}
			};
		}
		
		function save(){
			if(!checkData()){
				return;
			}

			setLoading(true);
			var table = new AW.XML.Table;
			var reqData = table.getXMLContent(cookRequestData());
			table.setURL("../DaemonHoliday?operation=save");
			table.setRequestMethod('POST');
			table.setRequestData(reqData);
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					alert("保存成功！");
					window.location.reload();
				}
			};
		}

		function cookRequestData(){
			var table = new AW.XML.Table;
			table.setXML("");
			var doc = table.getXML();
			var elmSet = doc.createElement("xdoc");
			
			//添加表头信息
			var nodeName =["vendercon","vendertel"];
			var elmHead = $XML(doc,"head",nodeName);
			elmSet.appendChild(elmHead);
			//添加表体信息
			var elmRowSet = doc.createElement("rowset");
			var forms = document.getElementsByTagName("form");
			for(var i=0;i<forms.length;i++){
				var elmRow = doc.createElement("row");
				var inputs = forms[i].getElementsByTagName("input");
				if(inputs[inputs.length-1].value=="true"){
					for(var j=1;j<inputs.length;j++){
						var tmp = doc.createElement(inputs[j].name);
						tmp.appendChild(doc.createTextNode(inputs[j].value));
						elmRow.appendChild(tmp);
					}
					elmRowSet.appendChild(elmRow);
				}
			}
			elmSet.appendChild(elmRowSet);
			//alert(elmSet.xml);
			return elmSet;
		}

		function checkData(){
			var forms = document.getElementsByTagName("form");
			var row =0;
			for(var i=0;i<forms.length;i++){
				var inputs = forms[i].getElementsByTagName("input");
				if(inputs[inputs.length-1].value=="true"){
					for(var j=2;j<4;j++){
						if(inputs[j].value==""){
							alert(inputs[j].alt+"必须填写");
							inputs[j].focus();
							return false;
						}
					}
					row++;
				}
			}

			if(row==0){
				alert("没有数据需要修改");
				return false;
			}

			return true;
		}
		function show(){
			setLoading(true);
			var table = new AW.XML.Table;
			table.setURL("../DaemonHoliday?operation=show");
			table.setTable("xdoc/xout/list");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					$('title').innerHTML=table.getXMLText("xdoc/xout/list/row/year")+'年度'+$('title').innerHTML;
					$('txt_vendertel').value=table.getXMLText("xdoc/xout/list/row/vendertel");
					$('txt_vendercon').value=table.getXMLText("xdoc/xout/list/row/vendercon");
					//$('txt_venderid').value=table.getXMLText("xdoc/xout/list/row/venderid");
					//$('txt_vendername').value=table.getXMLText("xdoc/xout/list/row/vendername");
					//循环赋值明细
					var rows = table.getCount();
					for ( var i = 0; i < rows; i++) {
						
						var htype=table.getData(5,i);
						var s1 = table.getData(7,i);
						var s2 = table.getData(8,i);
						var s3 = table.getData(9,i);
						var s4 = table.getData(10,i);
						var note = table.getData(11,i);
						var s5 = table.getData(12,i);

						var inputs = $("item"+htype).getElementsByTagName("input");

						inputs[2].value=s1;
						inputs[3].value=s2;
						inputs[4].value=note;
						inputs[5].value=s5;
					}
					
					//
					initExp();
				}
			};

		}

		function enEdit(e){
			var id = e.seq;
			var form = $("item"+id);
			var inputs = form.getElementsByTagName("input");
			inputs[0].value="修改中";
			inputs[0].disabled="false";
			inputs[inputs.length-1].value="true";
			for(var i=2;i<7;i++){
				inputs[i].disabled="";
				inputs[i].className="abled";
			}
		}

		function noHoliday(e){
			var id = e.seq;
			var form = $("item"+id);
			var inputs = form.getElementsByTagName("input");
			inputs[0].value="修改中";
			inputs[0].disabled="false";
			inputs[0].disabled="false";
			inputs[inputs.length-1].value="true";
			for(var i=2;i<5;i++){
				inputs[i].disabled="disabled";
				inputs[i].className="";
				if(i<4){
					inputs[i].value = startDay[(id-1)];
				}
			}
			
			inputs[4].value=inputs[inputs.length-2].value+" 不休假！";
		}
		</script>

</head>

<body>
	<table width="100%" border="1" cellpadding="4px" cellspacing="0">
		<caption id="title">供应商节假日维护</caption>
		<tr>
			<td>供应商编码:<input value="<%=venderid%>" id="txt_venderid"
				readonly="readonly" class="editline" /></td>
			<td>供应商名称:<input value="<%=vendername%>" id="txt_vendername"
				readonly="readonly" class="editline" /></td>
			<td>供应商联系人:<input value="" id="txt_vendercon" class="abled" /></td>
			<td>供应商电话:<input value="" id="txt_vendertel" class="abled" /></td>
		</tr>
	</table>
	<div
		style="padding: 6px; background-color: #eef; font-weight: bold; font-size: 16px; color: red; font-family: '黑体'">
		1）停止收单日指休假前停止收单的时间，在此之前收到的订单必须在休假前送达；恢复收单日为休假结束后恢复收单的时间，恢复收单日后收到的订单须及时送货。<br></br>
		2）请按照实际休假情况最迟在休假前21天完成填写，过期后将不能录入，请联系全国商品支持部供应链管理组。VSS系统邮件联系：gylglz。<br></br>
		3）没有录入的话视为不休假，系统不会提前备货，订单有效期也不会自动延长。
	</div>
	<div id="div_explist"></div>
	<br />

	<input type="button" value="保存" onclick="save()">
	<span style="font-size: 16px;">☜ 修改数据后记得点击这里保存修改</span>
	<form action="" name="item1">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<tr>
				<th colspan="4">元旦 - <input type="button" value="修改"
					onclick="enEdit(this)" seq="1" name="editbutton"> <input
					type="button" value="不休假" onclick="noHoliday(this)" seq="1"
					name="editbutton"></th>
			</tr>
			<tr>
				<td>停止收单日:<input value="" name="startdate" id="sd1" size="14"
					onFocus="WdatePicker({onpicked:function(){ed1.focus();}})"
					disabled="disabled" alt="停止收单日" /></td>
				<td>恢复收单日:<input value="" name="enddate" id="ed1" size="14"
					onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd1\',{d:0});}',maxDate:'#F{$dp.$D(\'sd1\',{d:5});}'})"
					disabled="disabled" alt="恢复收单日" /></td>
				<td>
					<!--停止送货日:<input value="" name="startdate2" id="sds1" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd1\',{d:0});}',onpicked:function(){eds1.focus();}})"
			disabled="disabled" alt="停止送货日" />
		-->
				</td>
				<td>
					<!--恢复送货日:<input value="" name="enddate2" id="eds1" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'ed1\',{d:0});}'})"
			disabled="disabled" alt="恢复送货日" />
		-->
				</td>
			</tr>
			<tr>
				<td colspan="3">备注:<input value="" name="note" size="100"
					disabled="disabled" /></td>
				<td>更新时间:<input value="" name="txt_editdate"
					readonly="readonly" class="editline" /> <input name="htype"
					value="1" type="hidden"> <input name="holiday" value="元旦"
					type="hidden"> <input name="isupdate" value="false"
					type="hidden"></td>
			</tr>
		</table>
	</form>

	<form action="" name="item2">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<tr>
				<th colspan="4">春节 - <input type="button" value="修改"
					onclick="enEdit(this)" seq="2" name="editbutton"> <input
					type="button" value="不休假" onclick="noHoliday(this)" seq="2"
					name="editbutton"></th>
			</tr>
			<tr>
				<td>停止收单日:<input value="" name="startdate" id="sd2" size="14"
					onFocus="WdatePicker({onpicked:function(){ed2.focus();}})"
					disabled="disabled" alt="停止收单日" /></td>
				<td>恢复收单日:<input value="" name="enddate" id="ed2" size="14"
					onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd2\',{d:0});}',maxDate:'#F{$dp.$D(\'sd2\',{d:21});}'})"
					disabled="disabled" alt="恢复收单日" /></td>
				<td>
					<!--停止送货日:<input value="" name="startdate2" id="sds2" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd2\',{d:0});}',onpicked:function(){eds2.focus();}})"
			disabled="disabled" alt="停止送货日" />
		-->
				</td>
				<td>
					<!--恢复送货日:<input value="" name="enddate2" id="eds2" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'ed2\',{d:0});}'})"
			disabled="disabled" alt="恢复送货日" />
		-->
				</td>
			</tr>
			<tr>
				<td colspan="3">备注:<input value="" name="note" size="100"
					disabled="disabled" /></td>
				<td>更新时间:<input value="" name="txt_editdate"
					readonly="readonly" class="editline" /> <input name="htype"
					value="2" type="hidden"> <input name="holiday" value="春节"
					type="hidden"> <input name="isupdate" value="false"
					type="hidden"></td>
			</tr>
		</table>
	</form>

	<form action="" name="item3">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<tr>
				<th colspan="4">清明 - <input type="button" value="修改"
					onclick="enEdit(this)" seq="3" name="editbutton"> <input
					type="button" value="不休假" onclick="noHoliday(this)" seq="3"
					name="editbutton"></th>
			</tr>
			<tr>
				<td>停止收单日:<input value="" name="startdate" id="sd3" size="14"
					onFocus="WdatePicker({onpicked:function(){ed3.focus();}})"
					disabled="disabled" alt="停止收单日" /></td>
				<td>恢复收单日:<input value="" name="enddate" id="ed3" size="14"
					onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd3\',{d:0});}',maxDate:'#F{$dp.$D(\'sd3\',{d:5});}'})"
					disabled="disabled" alt="恢复收单日" /></td>
				<td>
					<!--停止送货日:<input value="" name="startdate2" id="sds3" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd3\',{d:0});}',onpicked:function(){eds3.focus();}})"
			disabled="disabled" alt="停止送货日" />
		-->
				</td>
				<td>
					<!--恢复送货日:<input value="" name="enddate2" id="eds3" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'ed3\',{d:0});}'})"
			disabled="disabled" alt="恢复送货日" />
		-->
				</td>
			</tr>
			<tr>
				<td colspan="3">备注:<input value="" name="note" size="100"
					disabled="disabled" /></td>
				<td>更新时间:<input value="" name="txt_editdate"
					readonly="readonly" class="editline" /> <input name="htype"
					value="3" type="hidden"> <input name="holiday" value="清明 "
					type="hidden"> <input name="isupdate" value="false"
					type="hidden"></td>
			</tr>
		</table>
	</form>

	<form action="" name="item4">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<tr>
				<th colspan="4">劳动 - <input type="button" value="修改"
					onclick="enEdit(this)" seq="4" name="editbutton"> <input
					type="button" value="不休假" onclick="noHoliday(this)" seq="4"
					name="editbutton"></th>
			</tr>
			<tr>
				<td>停止收单日:<input value="" name="startdate" id="sd4" size="14"
					onFocus="WdatePicker({onpicked:function(){ed4.focus();}})"
					disabled="disabled" alt="停止收单日" /></td>
				<td>恢复收单日:<input value="" name="enddate" id="ed4" size="14"
					onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd4\',{d:0});}',maxDate:'#F{$dp.$D(\'sd4\',{d:7});}'})"
					disabled="disabled" alt="恢复收单日" /></td>
				<td>
					<!--停止送货日:<input value="" name="startdate2" id="sds4" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd4\',{d:0});}',onpicked:function(){eds4.focus();}})"
			disabled="disabled" alt="停止送货日" />
		-->
				</td>
				<td>
					<!--恢复送货日:<input value="" name="enddate2" id="eds4" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'ed4\',{d:0});}'})"
			disabled="disabled" alt="恢复送货日" />
		-->
				</td>
			</tr>
			<tr>
				<td colspan="3">备注:<input value="" name="note" size="100"
					disabled="disabled" /></td>
				<td>更新时间:<input value="" name="txt_editdate"
					readonly="readonly" class="editline" /> <input name="htype"
					value="4" type="hidden"> <input name="holiday" value="劳动 "
					type="hidden"> <input name="isupdate" value="false"
					type="hidden"></td>
			</tr>
		</table>
	</form>

	<form action="" name="item5">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<tr>
				<th colspan="4">端午 - <input type="button" value="修改"
					onclick="enEdit(this)" seq="5" name="editbutton"> <input
					type="button" value="不休假" onclick="noHoliday(this)" seq="5"
					name="editbutton"></th>
			</tr>
			<tr>
				<td>停止收单日:<input value="" name="startdate" id="sd5" size="14"
					onFocus="WdatePicker({onpicked:function(){ed5.focus();}})"
					disabled="disabled" alt="停止收单日" /></td>
				<td>恢复收单日:<input value="" name="enddate" id="ed5" size="14"
					onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd5\',{d:0});}',maxDate:'#F{$dp.$D(\'sd5\',{d:5});}'})"
					disabled="disabled" alt="恢复收单日" /></td>
				<td>
					<!--停止送货日:<input value="" name="startdate2" id="sds5" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd5\',{d:0});}',onpicked:function(){eds5.focus();}})"
			disabled="disabled" alt="停止送货日" />
		-->
				</td>
				<td>
					<!--恢复送货日:<input value="" name="enddate2" id="eds5" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'ed5\',{d:0});}'})"
			disabled="disabled" alt="恢复送货日" />
		-->
				</td>
			</tr>
			<tr>
				<td colspan="3">备注:<input value="" name="note" size="100"
					disabled="disabled" /></td>
				<td>更新时间:<input value="" name="txt_editdate"
					readonly="readonly" class="editline" /> <input name="htype"
					value="5" type="hidden"> <input name="holiday" value="端午 "
					type="hidden"> <input name="isupdate" value="false"
					type="hidden"></td>
			</tr>
		</table>
	</form>
	<form action="" name="item6">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<tr>
				<th colspan="4">中秋 - <input type="button" value="修改"
					onclick="enEdit(this)" seq="6" name="editbutton"> <input
					type="button" value="不休假" onclick="noHoliday(this)" seq="6"
					name="editbutton"></th>
			</tr>
			<tr>
				<td>停止收单日:<input value="" name="startdate" id="sd6" size="14"
					onFocus="WdatePicker({onpicked:function(){ed6.focus();}})"
					disabled="disabled" alt="停止收单日" /></td>
				<td>恢复收单日:<input value="" name="enddate" id="ed6" size="14"
					onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd6\',{d:0});}',maxDate:'#F{$dp.$D(\'sd6\',{d:5});}'})"
					disabled="disabled" alt="恢复收单日" /></td>
				<td>
					<!--停止送货日:<input value="" name="startdate2" id="sds6" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd6\',{d:0});}',onpicked:function(){eds6.focus();}})"
			disabled="disabled" alt="停止送货日" />
		-->
				</td>
				<td>
					<!--恢复送货日:<input value="" name="enddate2" id="eds6" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'ed6\',{d:0});}'})"
			disabled="disabled" alt="恢复送货日" />
		-->
				</td>
			</tr>
			<tr>
				<td colspan="3">备注:<input value="" name="note" size="100"
					disabled="disabled" /></td>
				<td>更新时间:<input value="" name="txt_editdate"
					readonly="readonly" class="editline" /> <input name="htype"
					value="6" type="hidden"> <input name="holiday" value="中秋 "
					type="hidden"> <input name="isupdate" value="false"
					type="hidden"></td>
			</tr>
		</table>
	</form>

	<form action="" name="item7">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<tr>
				<th colspan="4">国庆 - <input type="button" value="修改"
					onclick="enEdit(this)" seq="7" name="editbutton"> <input
					type="button" value="不休假" onclick="noHoliday(this)" seq="7"
					name="editbutton"></th>
			</tr>
			<tr>
				<td>停止收单日:<input value="" name="startdate" id="sd7" size="14"
					onFocus="WdatePicker({onpicked:function(){ed7.focus();}})"
					disabled="disabled" alt="停止收单日" /></td>
				<td>恢复收单日:<input value="" name="enddate" id="ed7" size="14"
					onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd7\',{d:0});}',maxDate:'#F{$dp.$D(\'sd7\',{d:21});}'})"
					disabled="disabled" alt="恢复收单日" /></td>
				<td>
					<!--停止送货日:<input value="" name="startdate2" id="sds7" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sd7\',{d:0});}',onpicked:function(){eds7.focus();}})"
			disabled="disabled" alt="停止送货日" />
		-->
				</td>
				<td>
					<!--恢复送货日:<input value="" name="enddate2" id="eds7" size="14"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'ed7\',{d:0});}'})"
			disabled="disabled" alt="恢复送货日" />
		-->
				</td>
			</tr>
			<tr>
				<td colspan="3">备注:<input value="" name="note" size="100"
					disabled="disabled" /></td>
				<td>更新时间:<input value="" name="txt_editdate"
					readonly="readonly" class="editline" /> <input name="htype"
					value="7" type="hidden"> <input name="holiday" value="国庆 "
					type="hidden"> <input name="isupdate" value="false"
					type="hidden"></td>
			</tr>
		</table>
	</form>

</body>
</html>
