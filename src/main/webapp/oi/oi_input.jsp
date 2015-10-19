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


	String sheetid = request.getParameter("sheetid");
	sheetid=sheetid==null?"":sheetid;
	String venderid = token.getBusinessid();
	Connection conn = XDaemon.openDataSource( token.site.getDbSrcName() );
	Vender     vender = Vender.getVender( conn, venderid );
	XDaemon.closeDataSource( conn );
	String vendername = vender.vendername;
	String vendertel = vender.telno;
	String venderfax = vender.faxno;
	String venderaddr = vender.address;
	
	//业态
	SelShopform sel_shopform = new SelShopform(token);
	sel_shopform.setAttribute("onchange","setSelCharge();");
	//区域
	SelRegion sel_region = new SelRegion(token);
	sel_region.setAttribute("name","dochead");
	sel_region.setAttribute("alt","区域");
%>


<%@page%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>供应商协议申请</title>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="../js/EditGrid-1.1.js"> </script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>

<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<style>
.aw-grid-control {
	height: 200px;
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

.aw-column-0 {
	text-align: center;
	width: 45px;
	color: #060;
	cursor: hand;
}

.aw-column-1 {
	text-align: center;
	width: 45px;
	color: #f00;
	cursor: hand;
}

.aw-column-13 {
	width: 0px;
}

.aw-column-14 {
	width: 0px;
}

.editline {
	border: none;
	border-bottom: 1px solid #000;
}

.abled {
	background-color: #efe;
}
</style>
<script language="javascript" type="text/javascript">
		var grid = new AW.Grid.Editor;
		var sheetid = "<%=sheetid%>";
		var current_row=0;
		var currentMode = "NEW";
		
		function setSelCharge(chargecode){
			var table = new AW.XML.Table;
			table.setURL("../DaemonDBLookup?type=oicharge&name=txt_chargecodeid&shopform="+$F('txt_shopform'));
			table.request();
			table.response = function (xml) {
				$("div_chargecodeid").innerHTML = table.getXMLContent(xml);
				if(chargecode){
					$('txt_chargecodeid').value=chargecode;
				}
			};
		}
		
			//列名数组
			var columnNames = ["","","门店编码","门店名称","数量","管理费","培训费","合计金额","促销员类型","入职类型","促销开始日期","促销结束日期","陈列方式","",""];
			var nodeNames = ["edit","del","shopid","shopname","promqty","managecharge","traincharge","chargevalue","promtypename","onboardtypename","begindate","enddate","shelfid","promtype","onboardtype"];
		
			window.onload= function(){
			grid.setId('grid');
			grid.setHeaderText( columnNames );
			grid.setNodeNames( nodeNames );
			grid.setColumnCount(columnNames.length);
			grid.setSelector();
			
			//确认中validating (Enter)一般用于数据检测,return true 则不能确认此数据,无法编辑其它数据
			grid.onCellValidating = function(text, col, row){
				//alert(parseDate(text));
				//检查日期
				if(col==10 || col==11){
					var d = parseDate(text);
					grid.setCellData(d.toString(),col,row);
				}
				//检查数量列
				if(col==4){
					if(!isNum(text)){
						alert('必须填入整数');
						grid.setCellData('',col,row);
					}
				}

				if(col==0){
					getShopInfo(text,row);
				}
			};
			grid.setCellEditable(false);
			grid.setCellEditable(true,4);
			grid.setCellEditable(true,5);
			grid.setCellData('修改',0);
			grid.setCellData('删除',1);
			grid.onCellClicked = function(event, column, row){
				// 点击第1列, 表示要作修改.
				if( column == 0 ){
					currentMode = "EDIT";
					$('btnitem').value="修改";
					var a = getPromType(grid.getCellValue(8,row));
					var b = getOnBoardType(grid.getCellValue(9,row));
					
					$('txt_shopid').value = grid.getCellValue(2,row);
					$('txt_display').value = grid.getCellValue(3,row);
					$('txt_promqty').value = grid.getCellValue(4,row);
					$('txt_managecharge').value = grid.getCellValue(5,row);
					$('txt_traincharge').value = grid.getCellValue(6,row);
					$('txt_promtype').value = grid.getCellValue(13,row);
					$('txt_onboardtype').value = grid.getCellValue(14,row);
					$('txt_begindate').value = grid.getCellValue(10,row);
					$('txt_enddate').value = grid.getCellValue(11,row);
					$('txt_shelfid').value = grid.getCellValue(12,row);
				}else if( column == 1 ){
				//删除
					if(confirm("您确认删除么?")){
						grid.delRow();
					}
				}
			};
			$("div_detail").innerHTML = grid.toString();
			if(sheetid!=''){
				show();
			}else{
				setSelCharge();
			}
		};

		//生成提交信息
		function cookRequestData(){
			var table = new AW.XML.Table;
			table.setXML("");
			var doc = table.getXML();
			var elmSet = doc.createElement("xdoc");
			
			//添加表头信息
			var nodeName =["sheetid","shopform","regionid","majorid","depart","chargecodeid","settlemode","sdate","vendercon","vendertel","venderfax","venderaddr","note"];
			var elmHead = $XML(doc,"head",nodeName);
			elmSet.appendChild(elmHead);
			//添加表体信息
			elmSet.appendChild(grid.toXML());
			return elmSet;
		}
		
		function save(){
			if(!checkNull("dochead")){return;}
			setLoading(true);
			var table = new AW.XML.Table;
			var reqData = table.getXMLContent(cookRequestData());
			table.setURL("../DaemonOI?operation=save");
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
					var sheetid = table.getXMLText("xdoc/xout");
					$('txt_sheetid').value = sheetid;
					show();
				}
			};
		}

		function show(){
			setLoading(true);
			var table = new AW.XML.Table;
			table.setURL("../DaemonOI?operation=show&sheetid="+$F('txt_sheetid'));
			table.setTable("xdoc/xout/sheet/body");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					var sheetid = table.getXMLText("xdoc/xout/sheet/head/row/sheetid");
					if(sheetid==""){
						alert("该单据不存在！");
						return;
					}
					$('txt_sheetid').value=table.getXMLText("xdoc/xout/sheet/head/row/sheetid");
					$('txt_flag').value=table.getXMLText("xdoc/xout/sheet/head/row/flagname");
					var flag = table.getXMLText("xdoc/xout/sheet/head/row/flag");
					$('txt_venderid').value=table.getXMLText("xdoc/xout/sheet/head/row/venderid");
					$('txt_vendername').value=table.getXMLText("xdoc/xout/sheet/head/row/vendername");
					$('txt_shopform').value=table.getXMLText("xdoc/xout/sheet/head/row/shopform");
					$('txt_regionid').value=table.getXMLText("xdoc/xout/sheet/head/row/regionid");
					$('txt_majorid').value=table.getXMLText("xdoc/xout/sheet/head/row/majorid");
					$('txt_majorname').value=table.getXMLText("xdoc/xout/sheet/head/row/categoryname");
					
					$('txt_depart').value=table.getXMLText("xdoc/xout/sheet/head/row/departname");
					$('txt_editer').value=table.getXMLText("xdoc/xout/sheet/head/row/editer");
					$('txt_editdate').value=table.getXMLText("xdoc/xout/sheet/head/row/editdate");
					$('txt_checker').value=table.getXMLText("xdoc/xout/sheet/head/row/checker");
					$('txt_checkdate').value=table.getXMLText("xdoc/xout/sheet/head/row/checkdate");
					
					$('txt_vendertel').value=table.getXMLText("xdoc/xout/sheet/head/row/vendertel");
					$('txt_venderfax').value=table.getXMLText("xdoc/xout/sheet/head/row/venderfax");
					$('txt_venderaddr').value=table.getXMLText("xdoc/xout/sheet/head/row/venderaddr");
					$('txt_vendercon').value=table.getXMLText("xdoc/xout/sheet/head/row/vendercon");
					$('txt_note').value=table.getXMLText("xdoc/xout/sheet/head/row/note");
					$('txt_sdate').value=table.getXMLText("xdoc/xout/sheet/head/row/sdate");
					$('txt_settlemode').value=table.getXMLText("xdoc/xout/sheet/head/row/settlemode");
					setSelCharge(table.getXMLText("xdoc/xout/sheet/head/row/chargecodeid"));
					
					//禁止编辑
					if(flag!=0) {
						setForm("form_head","no");
						grid.setCellEditable(false);
					}
					grid.setRowCount(table.getCount());
					table.setColumns(grid.getNodeNames());
					grid.setCellModel(table);
					grid.setCellText("修改",0);
					grid.setCellText("删除",1);
					grid.refresh();
				}
			};

		}
		function comf(){
			if(!checkNull("dochead")){return;}
			setLoading(true);
			var table = new AW.XML.Table;
			var reqData = table.getXMLContent(cookRequestData());
			table.setURL("../DaemonOI?operation=save");
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
					var sheetid = table.getXMLText("xdoc/xout");
					$('txt_sheetid').value = sheetid;
					setLoading(true);
					var table2 = new AW.XML.Table;
					table2.setURL("../DaemonOI?operation=comf&sheetid="+$F('txt_sheetid'));
					table2.request();
					table2.response = function (text) {
						setLoading(false);
						table2.setXML(text);
						var xcode = table2.getXMLText("xdoc/xerr/code");
						if (xcode != 0) {
							alert(xcode + table2.getXMLText("xdoc/xerr/note"));
						} else {
							alert("提交成功！");
							setForm("form_head","no");
							grid.setCellEditable(false);
							$('txt_flag').value="提交";
						}
					};
				}
				
			};
		}

		function del(){
			setLoading(true);
			var table = new AW.XML.Table;
			var reqData = table.getXMLContent(cookRequestData());
			table.setURL("../DaemonOI?operation=delete&sheetid="+$F('txt_sheetid'));
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					alert("删除成功！");
					window.close();
				}
			};
		}
		
	    function getMajorInfo(v){
	    	var url = "../DaemonOI?operation=getMajorInfo&majorid="+v;
			var table = new AW.XML.Table;
			table.setURL( url );
			table.request();
			table.response = function( text ){
				table.setXML(text);
				if(table.getXMLNode("xdoc/xout/majorInfo/row")!=null){
					$('txt_majorname').value=table.getXMLText("xdoc/xout/majorInfo/row/categoryname");
					$('txt_depart').value=table.getXMLText("xdoc/xout/majorInfo/row/depart");
				}else{
					alert("该课类不存在！请重新输入");
					$('txt_majorid').value='';
					
				}
			};
		}

	    function getShopInfo(v,row){
	    	var url = "../DaemonOI?operation=getShopInfo&shopid="+v;
			var table = new AW.XML.Table;
			table.setURL( url );
			table.request();
			table.response = function( text ){
				table.setXML(text);
				if(table.getXMLNode("xdoc/xout/shopInfo/row")!=null){
					var shopname = table.getXMLText("xdoc/xout/shopInfo/row/shopname");
					grid.setCellData(shopname,1,row);
				}else{
					alert("该门店不存在！请重新输入");
					grid.setCellData('',1,row);
				}
			};
		}

		function to_grid(){
			if(!checkNull("item")){return;}
			//数字检查
			var pro = $('txt_promqty').value;
			var managecharge = $('txt_managecharge').value;
			var traincharge = $('txt_traincharge').value;
			if(isNaN(managecharge)){ alert("管理费必须是数字");return; }
			if(isNaN(traincharge)){ alert("促销费必须是数字");return; }
			if(isNaN(pro)){ alert("数量必须是数字");return; }
			managecharge = Number(managecharge);
			traincharge = Number(traincharge);
			var v = $('txt_shopid').value;
			var v2 = $('txt_display').value;
			var vs = v.split(',');
			var vs2 = v2.split(',');
			for ( var i = 0; i < vs.length; i++) {
				//强制4字符门店
				var shopid= vs[i];
				if(shopid.length>4){
					shopid = shopid.substring(0,4);
				}
				if(currentMode=="NEW"){
					grid.newRow();
					
					//门店重复检查
					if(hasShop(shopid)){
						alert('门店'+shopid+'在本单已录入，如需再次添加该本店，请在新单中录入。');
						break;
					}
					
				}else{
					currentMode="NEW";
					$('btnitem').value="新增";
				}
				
				var shopname = vs2[i];
				var a = getPromType($('txt_promtype').value);
				var b = getOnBoardType($('txt_onboardtype').value);
				current_row = grid.getCurrentRow();
				grid.setCellData(shopid,2,current_row);
				grid.setCellData(shopname,3,current_row);
				grid.setCellData($('txt_promqty').value,4,current_row);
				grid.setCellData(managecharge,5,current_row);
				grid.setCellData(traincharge,6,current_row);
				grid.setCellData(managecharge+traincharge,7,current_row);
				grid.setCellData(a,8,current_row);
				grid.setCellData(b,9,current_row);
				grid.setCellData($('txt_begindate').value,10,current_row);
				grid.setCellData($('txt_enddate').value,11,current_row);
				grid.setCellData($('txt_shelfid').value,12,current_row);
				grid.setCellData($('txt_promtype').value,13,current_row);
				grid.setCellData($('txt_onboardtype').value,14,current_row);
				grid.setCellData('修改',0,current_row);
				grid.setCellData('删除',1,current_row);
			}
			
			//clear("item");
			$('txt_shopid').value="";
		}
		
		function hasShop(shopid){
			for(var j=0;j<=grid.getRowCount();j++){
				var tmp = grid.getCellValue(2,j);
				if(tmp == shopid){
					return true;
				}
			}
			return false;
		}

		//将选择的门店输入到门店input
		function onSelBranchGroupChange(objid){
			var n = 0;
			var tmpshopid = new Array();
			var tmpshopname = new Array();
			var opt = $(objid+"_groupid").options;
			for(var i=0; i<opt.length; i++){
				if(opt[i].selected)		
				{
					tmpshopid[n] = opt[i].value;
					tmpshopname[n] = opt[i].getAttribute("display");
					n++;
				}
			}
			$(objid).value = tmpshopid.join(",");
			$("txt_display").value = tmpshopname.join(",");
		}

		function checkNull(name){
			var objs = document.getElementsByName(name);

			for ( var i = 0; i < objs.length; i++) {
				if(objs[i].value==""){
					alert(objs[i].alt+"不能为空！");
					return false;
				}
			}
			return true;
		}

		function clear(name){
			var objs = document.getElementsByName(name);

			for ( var i = 0; i < objs.length; i++) {
				objs[i].value="";
			}
		}

		function checkShop(){
			var shopid = $('txt_shopid').value;
			//强制门店为4字符门店
			if(shopid.length >4 ){
				shopid = shopid.substring(0,4);
				$('txt_shopid').value = shopid;
			}
			
	    	var url = "../DaemonOI?operation=getShopInfo&shopid="+shopid;
			var table = new AW.XML.Table;
			table.setURL( url );
			table.request();
			table.response = function( text ){
				table.setXML(text);
				if(table.getXMLNode("xdoc/xout/shopInfo/row")!=null){
					var shopname = table.getXMLText("xdoc/xout/shopInfo/row/shopname");
					$('txt_display').value=shopname;
				}else{
					alert("该门店不存在！请重新输入");
					$('txt_shopid').value="";
				}
			};
		}

		function getPromType(type){
			switch (Number(type)) {
			case 1:
				return "长期";
				break;
			case 2:
				return "短期";
				break;
			case 3:
				return "专职顶岗";
				break;
			case 4:
				return "理货";
				break;
			case 5:
				return "其他";
				break;

			default:
				return "";
				break;
			}
		}
		function getOnBoardType(type){
			switch (Number(type)) {
			case 1:
				return "新入职";
				break;
			case 2:
				return "续签";
				break;
			case 3:
				return "换人";
				break;
			case 4:
				return "其他";
				break;
			default:
				return "";
				break;
			}
		}
		
		</script>

</head>


<body>
	<form action="" id="form_head">
		<table width="100%" border="1" cellpadding="4px" cellspacing="0">
			<caption>供应商协议申请</caption>
			<tr>
				<td>单据编号:<input value="<%=sheetid%>" id="txt_sheetid"
					readonly="readonly" class="editline" /></td>
				<td>单据状态:<input value="新建" id="txt_flag" readonly="readonly"
					class="editline" /></td>
				<td>供应商编码:<input value="<%=venderid%>" id="txt_venderid"
					readonly="readonly" class="editline" /></td>
				<td>供应商名称:<input value="<%=vendername%>" id="txt_vendername"
					readonly="readonly" class="editline" /></td>
			</tr>
			<tr>
				<td>业态:<%=sel_shopform %>
				</td>
				<td>区域:<%=sel_region %>
				</td>
				<td>扣项:
					<div id="div_chargecodeid"></div>
				</td>
				<td>合作课类:<input value="" id="txt_majorid" class="abled"
					size="6" onchange="getMajorInfo(this.value)" name="dochead"
					alt="合作课类" /> 课类名称:<input value="" id="txt_majorname"
					readonly="readonly" class="editline" size="10" />
				</td>
			</tr>
			<tr>
				<td>计划收费日期:<input value="" id="txt_sdate" size="12"
					class="abled" onFocus="WdatePicker({minDate:'%y-%M-%d'})"
					name="dochead" alt="计划收费日期" /></td>
				<td>付款方式:<select id="txt_settlemode">
						<option value="1" selected="selected">帐扣</option>
						<option value="0">交现</option>
				</select></td>
				<td>部门:<input value="" id="txt_depart" readonly="readonly"
					class="editline" size="10" /></td>
				<td>供应商联系人:<input value="" id="txt_vendercon" class="abled"
					name="dochead" alt="供应商联系人" /></td>
			</tr>
			<tr>
				<td>供应商电话:<input value="<%=vendertel%>" id="txt_vendertel"
					class="abled" /></td>
				<td><span style="display: none;">供应商传真:<input
						value="<%=venderfax%>" id="txt_venderfax" class="abled" /></span></td>
				<td colspan="2"><span style="display: none;">供应商地址:<input
						value="<%=venderaddr%>" id="txt_venderaddr" size="50"
						class="abled" /></span></td>
			</tr>
			<tr>
				<td>制单人:<input value="" id="txt_editer" readonly="readonly"
					class="editline" /></td>
				<td>制单日期:<input value="" id="txt_editdate" readonly="readonly"
					class="editline" /></td>
				<td>确认人:<input value="" id="txt_checker" readonly="readonly"
					class="editline" /></td>
				<td>确认日期:<input value="" id="txt_checkdate" readonly="readonly"
					class="editline" /></td>
			</tr>
			<tr>
				<td colspan="4">备注:<input value="" id="txt_note" size="170"
					class="abled" /></td>
			</tr>
		</table>
		<div style="margin: 9px;">
			<input type="button" value="保存" id="btn_save" onclick="save()">
			<input type="button" value="保存并送审" id="btn_comf" onclick="comf()">
			<input type="button" value="删除整单" id="btn_deleteRow" onclick="del()">
		</div>
	</form>

	<div>
		门店：<input type="text" name="item" id="txt_shopid" size="12" alt="门店"
			onchange="checkShop()" /><a
			href="javascript:showShopListUnclose('txt_shopid')">选择门店</a> <input
			type="hidden" id="txt_display"></input> 数量：<input type="text"
			name="item" id="txt_promqty" size="12" alt="数量" /> 管理费：<input
			type="text" name="item" id="txt_managecharge" size="12" alt="管理费"
			value="0" /> 培训费：<input type="text" name="item" id="txt_traincharge"
			size="12" alt="培训费" value="0" /> 促销员类型： <select name="item"
			id="txt_promtype" alt="促销员类型">
			<option value="1">长期</option>
			<option value="2">短期</option>
			<option value="3">专职顶岗</option>
			<option value="4">理货</option>
			<option value="5">其他</option>
		</select> 入职类型： <select name="item" id="txt_onboardtype" alt="入职类型">
			<option value="1">新入职</option>
			<option value="2">续签</option>
			<option value="3">换人</option>
			<option value="4">其他</option>
		</select> <br></br> 促销起始日期： <input type="text" name="item" id="txt_begindate"
			onFocus="WdatePicker({minDate:'%y-%M-%d',onpicked:function(){txt_enddate.focus();},maxDate:'#F{$dp.$D(\'txt_enddate\')}'})"
			size="12" alt="促销起始日期" /> - <input type="text" name="item"
			id="txt_enddate"
			onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_begindate\')}'})"
			size="12" alt="促销起始日期" /> 陈列方式：<input type="text" name="item"
			id="txt_shelfid" size="20" alt="陈列方式" value=" " maxlength="64" /> <input
			type="button" value="新增" onclick="to_grid()" id="btnitem">
	</div>
	<div id="editmsg"></div>
	<div id="div_detail"></div>
</body>
</html>
