<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=5010103;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<script language="javascript">
extendDate();
extendNumber();
</script>

<!-- grid format -->
<style>
.aw-grid-control {
	width: 100%;
	border: none;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	width: 80px;
	cursor: point;
}

#myGrid .aw-column-3 {
	width: 60px;
	cursor: point;
}

#myGridDetail {
	background-color: #F9F8F4
}

#myGridDetail .aw-column-0 {
	width: 80px;
}

#myGridDetail .aw-column-1 {
	width: 80px;
}
</style>
<xml id="island4cat" />
<xml id="island4reply" />
<xml id="island4member" />
<xml id="island4detail" />
<xml id="islanddata" />

<script language="javascript">
var obj = new AW.UI.Grid;
var obj_detail = new AW.UI.Grid;
var g_groupname="";
var g_groupid="";
var row_selected="";
/**
 *查询现有的群组信息
 */
function set_init(){
	setLoading(true);
	set_visible("div_groupcat");
	var url2servlet  = "../DaemonMail?operation=mailgrouplist";
	var courier = new AjaxCourier( url2servlet );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}
/**
 *响应群组信息查询结果
 */
function analyse_catalogue(text){
	setLoading(false);
	island4cat.loadXML( text );	
	var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code == "0" ) {    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/MailGroupInfo" );
	  	var row_count   = elm_row.childNodes.length;
		display();		
		div_addgroup.style.display='block';	
	}
	else {	
		alert( xerr.toString() );	
		setLoading(false);	
	}
}
/**
 *显示群组列表
 */
function display(){	
	var table     = new AW.XML.Table;	
	var node_list = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/MailGroupInfo" );
	var row_count = node_list.childNodes.length;	
	islanddata.loadXML( table.getXMLContent(node_list) );
	var xml, node = document.getElementById( "islanddata" );
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument("","", null);
		xml.appendChild(node.selectSingleNode("*"));
	}		
	table.setXML( xml );		
	var columnNames = ["邮件组ID","邮件组名称","邮件组类型","删除"];	
	var columnOrder = [0,1,2,3];	
	obj.setId( "myGrid" );	
	obj.setColumnCount(4);
	obj.setRowCount( row_count );	
	obj.setHeaderText(columnNames);
	obj.setColumnIndices( columnOrder );	
	var str = new AW.Formats.String;			
	var obj_link = new AW.Templates.Link;
	obj_link.setEvent( "onclick",
	function(){
		var current_row = obj.getCurrentRow();
		var groupid = obj.getCellValue( 0, current_row );
		g_groupid=groupid;
		g_groupname=obj.getCellValue(1,current_row);
		load_groupMember( groupid );
	}
	);
	var obj_del = new AW.Templates.Link;
	obj_del.setEvent( "onclick",
	function(){
		var current_row = obj.getCurrentRow();
		var groupid = obj.getCellValue( 0, current_row );
		del_group( groupid );
	}
	);
	obj_del.setContent("删除");				
	obj.setCellTemplate( obj_link, 0 );
	obj.setCellTemplate( obj_del, 3 ); 	 	
	obj.setCellModel(table);					
	div_groupcat.innerHTML = obj.toString();	
	islanddata.loadXML  ( "" );
}
/**
 *添加群组
 */
function add_group(){
	if( txt_groupid.value=='' || txt_groupname.value=='' ){
		alert("请填写完整信息!");
		return;
	}
	
	var url2servlet  = "../DaemonMail?operation=addmailgroup";
	url2servlet+="&groupId="+txt_groupid.value;
	url2servlet+="&groupName="+(txt_groupname.value);
	var courier = new AjaxCourier( encodeURI(url2servlet) );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_addreply;
	courier.call();	
}
/**
 *响应添加结果
 */
function analyse_addreply(text){
	island4reply.loadXML("");	
	island4reply.loadXML( text );
	var elm_err 	= island4reply.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );	
	if( xerr.code == "0" ){ 	
	setLoading(true,"操作成功");
	set_init();
	}
	else
	alert(xerr.toString());		
}
/**
 *删除群组
 */
function del_group(groupid){
	var con=confirm("您确实要删除这条记录吗？")
	if(con){
	var url2servlet  = "../DaemonMail?operation=delmailgroup";
	url2servlet+="&groupId="+groupid;
	var courier = new AjaxCourier( encodeURI(url2servlet) );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_delreply;
	courier.call();	
	}
	else
	return;
}
/**
 *响应删除结果
 */
function analyse_delreply(text){
	island4reply.loadXML("");	
	island4reply.loadXML( text );
	var elm_err 	= island4reply.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );	
	if( xerr.code == "0" ){ 	
	setLoading(true,"操作成功");
	set_init();
	}
	else
	alert(xerr.toString());	
}
/**
 *设置显示的DIV层
 */
function set_visible(div_id){
	div_groupcat.style.display='none';
	div_groupdetail.style.display='none';
	div_addgroup.style.display='none';
	div_addmember.style.display='none';
	var division = document.getElementById(div_id);
	division.style.display='block';
}

/************************************邮件组明细***************************************************************/
/**
 *获取邮件组成员ID
 */
function load_groupMember(groupid){
	setLoading(true);
	var url2servlet  = "../DaemonMail?operation=mailgroupitemlist&groupId="+groupid;	
	var courier = new AjaxCourier( url2servlet );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_memberreply;
	courier.call();	
}

/**
 *响应明细结果
 */
function analyse_memberreply(text){
	div_groupdetail.innerHTML='邮件组名称：<b>'+g_groupname+'</b>'
	+"<br/><br/><a onclick='javascript:set_init()' href='#'><<返回邮件组列表</a>"
	+'<br/><br/>';
	set_visible("div_groupdetail");
	island4detail.loadXML("");
	island4detail.loadXML(text);
	var elm_err 	= island4detail.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code == "0" ) {    	 
		var elm_row 	= island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/MailGroupItemInfo" );
	  	var row_count   = elm_row.childNodes.length;	  	
	  	if( row_count== 0 ){
	  		alert("该邮件组没有成员信息，现在转到邮件组成员添加。");	  		
	  		window.location.href="mailgroup_upload.jsp";
	  	}else{			
			btnDelMember.disabled=false;
		}
		display_detail();
		div_addmember.style.display='block';	
	}
	else {	
		alert( xerr.toString() );	
	}
	setLoading(false);
}
/**
 *显示明细列表
 */
function display_detail(){	
	var table     = new AW.XML.Table;	
	var node_list = island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/MailGroupItemInfo" );	
	var row_count = node_list.childNodes.length;	
	islanddata.loadXML( table.getXMLContent(node_list) );	
	var xml, node = document.getElementById( "islanddata" );
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument("","", null);
		xml.appendChild(node.selectSingleNode("*"));
	}		
	table.setXML( xml );		
	var columnNames = ["用户ID","用户名称"];	
	var columnOrder = [0,1];
	obj_detail.setId( "myGridDetail" );
	obj_detail.setSelectionMode("multi-row-marker");
	obj_detail.onSelectedRowsChanged = function(arrayOfRowIndices){        	
        	row_selected=arrayOfRowIndices;	
        	return true;
    	}	
	obj_detail.setColumnCount(2);
	obj_detail.setRowCount( row_count );	
	obj_detail.setHeaderText(columnNames);
	obj_detail.setColumnIndices( columnOrder );	
	obj_detail.setCellModel(table);						
	div_groupdetail.innerHTML+= obj_detail.toString();	
	islanddata.loadXML  ( "" );
}
/**
 *删除邮件组成员
 */
function del_Member(){	
	var con=confirm("您确实要删除这条记录吗？")
	if(con){
	var array_selected = new Array();
	for(var i=0;i<row_selected.length;i++)	
	array_selected.push( obj_detail.getCellValue(1,row_selected[i]) );	
	if( array_selected.length == 0 ){
		setLoading(true,"请选择要删除记录");
		return;
	}
	var url2servlet="../DaemonMail?operation=delmailgroupitem&groupId="+g_groupid;
	url2servlet+="&loginId="+array_selected.join(",");
	var courier = new AjaxCourier( url2servlet );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_delMemberreply;
	courier.call();	
	}
}
/**
 *响应删除结果
 */
function analyse_delMemberreply(text){
	island4reply.loadXML("");	
	island4reply.loadXML( text );
	var elm_err 	= island4reply.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );	
	if( xerr.code == "0" ){
		obj_detail = new AW.UI.Grid;	
		load_groupMember(g_groupid);
	}
	else
		alert(xerr.toString());	
}


/************************************Detail End***************************************************************/
</script>
</head>
<body onLoad="set_init()">
	<br />
	<div id="div_groupcat" style="display: none;"></div>
	<div id="div_groupdetail" style="display: none;"></div>
	<br />
	<div id="div_addgroup" style="display: none;">
		添加邮件组 <a href="mailgroup_upload.jsp">批量添加邮件组成员</a> <br /> <span>邮件组ID</span><input
			type=text id="txt_groupid" /> &nbsp;&nbsp;<span>邮件组名称</span><input
			type=text id="txt_groupname" /> <br /> <br /> <input type=button
			name="btnAdd" value="添加" onclick="add_group()" />&nbsp;<input
			type=button name="btnCancel" value="取消" />
	</div>
	<div id="div_addmember" style="display: none;">
		&nbsp;&nbsp;&nbsp;&nbsp;删除选中用户&nbsp;<input type=button
			id="btnDelMember" value="删除" onclick="del_Member()" /> <br /> <br />
		<a href="mailgroup_upload.jsp">批量添加邮件组成员</a>
	</div>
</body>
</html>