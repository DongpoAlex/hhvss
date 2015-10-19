<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );

//业态
SelShopform sel_shopform = new SelShopform(token);
sel_shopform.setAttribute("onchange","setSelCharge();");
//区域
SelRegion sel_region = new SelRegion(token);

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>供应商协议申请</title>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"
	type="text/javascript"></script>
<script language="javascript" src="../js/common.js"
	type="text/javascript"> </script>
<script language="javascript" src="../js/EditGrid.js"> </script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>


<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<style>
.aw-grid-control {
	height: 70%;
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
	width: 120px;
}

.aw-column-11 {
	width: 0px;
}

.aw-column-10 {
	width: 200px;
}

.disabled {
	border: none;
	border-bottom: 1px solid #000;
}

.abled {
	background-color: #efe;
}
</style>
<script language="javascript" type="text/javascript">
		var grid = new AW.Grid.Editor;
		window.onload= function(){
			setSelCharge();
		};

		function setSelCharge(){
			var table = new AW.XML.Table;
			table.setURL("../DaemonDBLookup?type=oicharge&showAll=true&name=txt_chargecodeid&shopform="+$F('txt_shopform'));
			table.request();
			table.response = function (xml) {
				$("div_chargecodeid").innerHTML = table.getXMLContent(xml);
			}
		}
		
		function search(){
			setLoading(true);
			var table = new AW.XML.Table;
			var parms = new Array();
			parms.push("operation=search");
			parms.push("chargename="+$("txt_chargecodeid").value);
			parms.push("shopform="+$("txt_shopform").value);
			parms.push("regionid="+$("txt_regionid").value);
			
			var url = "../DaemonOI?"+parms.join('&');
			table.setURL(url);
			table.setTable("xdoc/xout/rowset");
			table.setRows("row");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					table.setColumns([ "sheetid","aaa","flagname","shopformname","regionname","categoryname","chargename","editdate","checker","checkdate","note","flag" ] );
					var columnNames = [ "单号","打印","状态","业态", "区域", "课类", "扣项", "编辑日期", "审核人", "审核日期", "备注","" ];
					
					var row_count = table.getCount();
					if(row_count==0){
						$('div_list').innerHTML="没有记录";
					}
					grid.setId( "grid_cat" );
					grid.setColumnCount( columnNames.length );
					grid.setRowCount( row_count );	
					grid.setHeaderText( columnNames );
					
					grid.setSelectorVisible(true);
					grid.setSelectorWidth(30);
					grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
					
					var grid_link = new AW.Templates.Link;
					grid_link.setEvent( "onclick",
						function(){
							var sheetid = grid.getCellValue( 0, grid.getCurrentRow() );
							open_sheet_detail( sheetid );
						}
					);
					grid.setCellTemplate( grid_link, 0 );
					//打印
					grid_link2 = new AW.Templates.Link;
					grid_link2.setEvent( "onclick",
						function(){
							var flag = grid.getCellValue( 11, grid.getCurrentRow() );
							var sheetid = grid.getCellValue( 0, grid.getCurrentRow() );
							if(flag>=2){
								print(sheetid);
							}else{
								alert("仅采购确认后单据可打印!");
							}
						}
					);
					grid.setCellTemplate( grid_link2, 1 );
					grid.setCellModel(table);
					grid.setCellText('打印',1);
					grid.refresh();
					var sumcount = table.getXMLNode("/xdoc/xout/rowset").getAttribute("rows");
					$('div_list').innerHTML= "查询结果：总计："+sumcount+"行， 显示："+row_count+"行"+grid.toString();
				}
			};
		}
		function open_sheet_detail(sheetid){
			var url = "oi_input.jsp?sheetid=" + sheetid;
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		 	window.open( url, "",attributeOfNewWnd);
		}

		function print(sheetid){
			var url = "print.jsp?sheetid=" + sheetid;
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		 	window.open( url, "",attributeOfNewWnd);
		}

		function newsheet(){
			var url = "oi_input.jsp";
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
			window.open( url, "",attributeOfNewWnd);
		}
		</script>

</head>


<body>
	<form action="" id="form_head">
		<table width="98%" border="1" cellpadding="4px" cellspacing="0">
			<caption>查询条件</caption>
			<tr>
				<td>业态名称: <%=sel_shopform %>
				</td>
				<td>区域名称: <%=sel_region %>
				</td>
				<td>扣项名称:
					<div id="div_chargecodeid"></div>
				</td>
			</tr>
		</table>
		<div style="margin: 9px;">
			<input type="button" value="查询" id="btn_search" onclick="search()">
			<input type="button" value="新建" id="btn_search" onclick="newsheet()">
		</div>
		<div id="div_list"></div>
	</form>
</body>
</html>
