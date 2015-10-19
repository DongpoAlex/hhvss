<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.email.*" errorPage="../errorpage/errorpage.jsp"%>

<%
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>demo</title>
<link rel="stylesheet" type="text/css"
	href="../ext/resources/css/ext-all.css" />
<script type="text/javascript" src="../ext/ext-base.js"></script>
<script type="text/javascript" src="../ext/ext-all.js"></script>

<script language="javascript" type="text/javascript">

var dataurl = "../DaemonMail?operation=browse&type=102";
//var dataurl = "data.xml";

Ext.onReady(function(){
	
var store1 = new Ext.data.XmlStore({
    //autoDestroy: true,
    storeId: 'mailStore',
    url: dataurl,
    record: 'mail', 
    idPath: 'mailid',
	//remoteSort:true,//按照数据的顺序排列，否则根据定义的顺序排列，true时，sortInfo无效
	sortInfo: {field:'mailid', direction:'ASC'},
//	remoteSort: true,
    fields: [
		{name: 'mailid',type: 'int'},
		{name: 'sender'},
		{name: 'title'},
		{name:'sendtime'},
		{name: 'receiptor01'}
	]
});






var store2 = new Ext.data.Store({
	storeId: 'mailStore2',
	//sortInfo: {field:'mailid', direction:'ASC'},
	proxy: new Ext.data.HttpProxy({
			url:dataurl,
			//success: function(response, options) {alert(response.responseText)},
			method: 'GET'
		}),
	reader: new Ext.data.XmlReader(
		{record: 'mail'}, 
		[
			{name: 'mailid',type: 'int'},
			{name: 'sender'},
			'title','sendtime','receiptor01'
		]
	)
});
//store2.setDefaultSort('mailid', 'asc');

var colModel = new Ext.grid.ColumnModel({
    defaults: {width: 80,sortable: true},
	columns: [
		{id:'mialid',header: "邮件ID", dataIndex: 'mailid',renderer:change,align: 'right'},
		{header: "发送者", dataIndex: 'sender', sortable: false,hidden: true},
	    {header: "标题",   dataIndex: 'title', width: 180},
		{header:"发送时间", dataIndex:'sendtime',width: 110,renderer: renderLast},
		{header: "收件人", dataIndex: 'receiptor01', sortable: false,hidden: true}
	]
});

var grid = new Ext.grid.GridPanel({
	store: store2,
	colModel: colModel,
	loadMask: {msg:'正在加载数据，请稍后……'},//是否有加载提示图标
    renderTo:'example-grid',
	//autoExpandColumn: 'title',
    //width:960,
    height:400,
	//stripeRows: true,//隔行色差
	frame:true,
	//iconCls:'icon-grid',
	title: '发件箱',
	
	bbar: new Ext.PagingToolbar({
		pageSize: 10,
		store: store2,
		displayInfo: true,
		//displayMsg: '显示 {0} - {1} of {2}',
		emptyMsg: "没有需要显示的信息"
	})
});

store2.load();

});


function renderLast(value, p, r){
        return String.format('{0}<br/>发送到: {1}', value, r.data['receiptor01']);
}

function change(val){
	if(val > 10000){
		return '<span style="color:green;">' + val + '</span>';
	}else if(val < 10000){
		return '<span style="color:red;">' + val + '</span>';
	}
	return val;
}

function load(){alert(Ext.get('example-grid'));}
</script>

</head>
<body>
	<input type="button" onclick="load()" value="读取数据">
	<div id="example-grid"></div>
</body>
</html>
