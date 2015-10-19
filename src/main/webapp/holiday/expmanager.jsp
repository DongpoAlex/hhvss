<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.sql.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.basic.Vender"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );

%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>节假日维护</title>
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
</style>
<script language="javascript" type="text/javascript">
		window.onload= function(){
			show();
		};
		
		function save(){
			if(!checkData()){
				return;
			}

			
			setLoading(true);
			var table = new AW.XML.Table;
			var reqData = table.getXMLContent(cookRequestData());
			table.setURL("../DaemonHoliday?operation=setexp");
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
					for(var j=1;j<3;j++){
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
			table.setURL("../DaemonHoliday?operation=getexp");
			table.setTable("xdoc/xout/list");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				//alert(text.xml);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					//循环赋值明细
					var rows = table.getCount();
					for ( var i = 0; i < rows; i++) {

						var htype=table.getData(0,i);
						var s1 = table.getData(2,i);
						var s2 = table.getData(3,i);
						var s3 = table.getData(4,i);
						var s4 = table.getData(5,i);

						var inputs = $("item"+htype).getElementsByTagName("input");

						inputs[1].value=s1;
						inputs[2].value=s2;
						inputs[3].value=s3;
						inputs[4].value=s4;
								
					}
				}
			};

		}

		function enEdit(e){
			var id = e.id;
			var form = $("item"+id);
			var inputs = form.getElementsByTagName("input");
			inputs[0].value="修改中";
			inputs[0].disabled="false";
			inputs[inputs.length-1].value="true";
			for(var i=1;i<inputs.length;i++){
				inputs[i].disabled="";
				inputs[i].className="abled";
			}
		}
		
		</script>

</head>

<body>
	<div style="width: 400px;">
		<input type="button" value="保存" onclick="save()"> <span
			style="font-size: 16px;">☜ 修改数据后记得点击这里保存修改</span>
		<form action="" name="item1">
			<table width="100%" border="1" cellpadding="4px" cellspacing="0">
				<tr>
					<th colspan="2">元旦 - <input type="button" value="修改"
						onclick="enEdit(this)" id="1" name="editbutton"></th>
				</tr>
				<tr>
					<td>开始日期:<input value="" name="startdate2" id="sds1" size="8"
						onFocus="WdatePicker({onpicked:function(){eds1.focus();}})"
						disabled="disabled" alt="开始日期" />
					</td>
					<td>结束日期:<input value="" name="enddate2" id="eds1" size="8"
						onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sds1\',{d:0});}'})"
						disabled="disabled" alt="结束日期" />
					</td>
				</tr>
				<tr>
					<td colspan="">备注:<input value="" name="note" size="30"
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
					<th colspan="2">春节 - <input type="button" value="修改"
						onclick="enEdit(this)" id="2" name="editbutton"></th>
				</tr>
				<tr>
					<td>开始日期:<input value="" name="startdate2" id="sds1" size="8"
						onFocus="WdatePicker({onpicked:function(){eds1.focus();}})"
						disabled="disabled" alt="开始日期" />
					</td>
					<td>结束日期:<input value="" name="enddate2" id="eds1" size="8"
						onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sds1\',{d:0});}'})"
						disabled="disabled" alt="结束日期" />
					</td>
				</tr>
				<tr>
					<td colspan="">备注:<input value="" name="note" size="30"
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
					<th colspan="2">清明 - <input type="button" value="修改"
						onclick="enEdit(this)" id="3" name="editbutton"></th>
				</tr>
				<tr>
					<td>开始日期:<input value="" name="startdate2" id="sds1" size="8"
						onFocus="WdatePicker({onpicked:function(){eds1.focus();}})"
						disabled="disabled" alt="开始日期" />
					</td>
					<td>结束日期:<input value="" name="enddate2" id="eds1" size="8"
						onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sds1\',{d:0});}'})"
						disabled="disabled" alt="结束日期" />
					</td>
				</tr>
				<tr>
					<td colspan="">备注:<input value="" name="note" size="30"
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
					<th colspan="2">劳动 - <input type="button" value="修改"
						onclick="enEdit(this)" id="4" name="editbutton"></th>
				</tr>
				<tr>
					<td>开始日期:<input value="" name="startdate2" id="sds1" size="8"
						onFocus="WdatePicker({onpicked:function(){eds1.focus();}})"
						disabled="disabled" alt="开始日期" />
					</td>
					<td>结束日期:<input value="" name="enddate2" id="eds1" size="8"
						onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sds1\',{d:0});}'})"
						disabled="disabled" alt="结束日期" />
					</td>
				</tr>
				<tr>
					<td colspan="">备注:<input value="" name="note" size="30"
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
					<th colspan="2">端午 - <input type="button" value="修改"
						onclick="enEdit(this)" id="5" name="editbutton"></th>
				</tr>
				<tr>
					<td>开始日期:<input value="" name="startdate2" id="sds1" size="8"
						onFocus="WdatePicker({onpicked:function(){eds1.focus();}})"
						disabled="disabled" alt="开始日期" />
					</td>
					<td>结束日期:<input value="" name="enddate2" id="eds1" size="8"
						onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sds1\',{d:0});}'})"
						disabled="disabled" alt="结束日期" />
					</td>
				</tr>
				<tr>
					<td colspan="">备注:<input value="" name="note" size="30"
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
					<th colspan="2">中秋 - <input type="button" value="修改"
						onclick="enEdit(this)" id="6" name="editbutton"></th>
				</tr>
				<tr>
					<td>开始日期:<input value="" name="startdate2" id="sds1" size="8"
						onFocus="WdatePicker({onpicked:function(){eds1.focus();}})"
						disabled="disabled" alt="开始日期" />
					</td>
					<td>结束日期:<input value="" name="enddate2" id="eds1" size="8"
						onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sds1\',{d:0});}'})"
						disabled="disabled" alt="结束日期" />
					</td>
				</tr>
				<tr>
					<td colspan="">备注:<input value="" name="note" size="30"
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
					<th colspan="2">国庆 - <input type="button" value="修改"
						onclick="enEdit(this)" id="7" name="editbutton"></th>
				</tr>
				<tr>
					<td>开始日期:<input value="" name="startdate2" id="sds1" size="8"
						onFocus="WdatePicker({onpicked:function(){eds1.focus();}})"
						disabled="disabled" alt="开始日期" />
					</td>
					<td>结束日期:<input value="" name="enddate2" id="eds1" size="8"
						onFocus="WdatePicker({minDate:'#F{$dp.$D(\'sds1\',{d:0});}'})"
						disabled="disabled" alt="结束日期" />
					</td>
				</tr>
				<tr>
					<td colspan="">备注:<input value="" name="note" size="30"
						disabled="disabled" /></td>
					<td>更新时间:<input value="" name="txt_editdate"
						readonly="readonly" class="editline" /> <input name="htype"
						value="7" type="hidden"> <input name="holiday" value="国庆 "
						type="hidden"> <input name="isupdate" value="false"
						type="hidden"></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>
