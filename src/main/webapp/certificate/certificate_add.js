var g_sheetid;
var g_type;
var currentItemID=0;
window.onload = function(){
	//取得供应商的扩展信息，联系人信息
	setLoading(true);
	var parms = new Array();
	parms.push( "action=getVenderExt" );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var ajax = new AW.XML.Table;
	ajax.setURL(url);
	ajax.request();
	ajax.response = function(text){
		setLoading(false);
		ajax.setXML(text);
		var errCode= ajax.getErrCode();
		if(errCode==0){
			var contact = ajax.getXMLText("/xdoc/xout/vender/row/contact");
			var contacttel = ajax.getXMLText("/xdoc/xout/vender/row/contacttel");
			if(contact==''){
				jQuery('#btn_editVenderExt').click();
			}else{
				$('txt_contact').value = contact;
				$('txt_contacttel').value = contacttel;
			}
		}else{
			alert(ajax.getErrNote());
		}
	};
	
	jQuery( "#dialog:ui-dialog" ).dialog( "destroy" );
	
	jQuery('#btn_editVenderExt').click(function() {
		jQuery( "#txt_newContact" ).val(jQuery( "#txt_contact" ).val());
		jQuery( "#txt_newContactTel" ).val(jQuery( "#txt_contacttel" ).val());
		jQuery( "#dialog-form" ).dialog( "open" );
	});
	
	jQuery( "#dialog-form" ).dialog({
		autoOpen: false,
		height: 350,
		width: 350,
		modal: true,
		buttons: {
			"确定": function() {
				var table = new AW.XML.Table;
				table.setXML("");
				var doc = table.getXML();
				var elm_xdoc = doc.createElement("xdoc");
				var elm_xparms = doc.createElement("xparms");
				var elm_contact=doc.createElement("contact");	
				elm_contact.appendChild( doc.createTextNode(jQuery( "#txt_newContact" ).val()));	
				elm_xparms.appendChild( elm_contact );
				var elm_contacttel=doc.createElement("contacttel");	
				elm_contacttel.appendChild( doc.createTextNode(jQuery( "#txt_newContactTel" ).val()));	
				elm_xparms.appendChild( elm_contacttel );
				var elm_contactmotel=doc.createElement("contactmotel");	
				elm_contactmotel.appendChild( doc.createTextNode(jQuery( "#txt_newContactTel" ).val()));	
				elm_xparms.appendChild( elm_contactmotel );
				
				elm_xdoc.appendChild( elm_xparms );
				doc.appendChild(elm_xdoc);
				
				table.setParameter("action","setVenderExt");
				table.setURL("../DaemonCertificate?action=setVenderExt");
				table.setRequestMethod('POST');
				table.setRequestData(table.getXMLContent());
				table.request();
				table.response = function(text){
					setLoading(false);
					table.setXML(text);
					var errCode= table.getErrCode();
					if(errCode==0){
						jQuery( "#txt_contact" ).val(jQuery( "#txt_newContact" ).val());
						jQuery( "#txt_contacttel" ).val(jQuery( "#txt_newContactTel" ).val());
						jQuery( "#dialog-form" ).dialog( "close" );
					}else{
						alert(table.getErrNote());
					}
				};
			},
			"取消": function() {
				jQuery( this ).dialog( "close" );
			}
		},
		close: function() {
			
		}
	});
};

/**
 * 提供已录入同类型的单据，指导供应商有计划的新建单据
 * @return
 */
function checkSheet(type){
	var contact = $('txt_contact').value;
	var ccid = $("txt_ccid")==null?"":$("txt_ccid").value;
	var venderTypeName = $('txt_venderTypeName')==null?"":$('txt_venderTypeName').value;
	var venderType="";
	if(type==1){
		var vt1 = $('voteoption1');
		if(vt1!=null ){
			if(vt1.checked){
				venderType="1";
			}else{
				if(venderTypeName=='') {alert("请填写您代理产品的生产商名称!");return;}
				venderType="2";
			}
		}else{
			venderType="-1";
		}
	}else if(type==2){
		if(ccid==""){alert("请选择录入的证照品类");return;}
		var vt1 = $('voteoption1');
		if(vt1!=null ){
			if(vt1.checked){
				venderType="1";
			}else{
				if(venderTypeName=='') {alert("请填写您代理产品的生产商名称!");return;}
				venderType="2";
			}
		}else{
			venderType="-1";
		}
	}else if(type==3){
	}else if(type==4){
	}else{
		alert("未定义的证照类型:"+type);
	}
	
	setLoading( true );
	var parms = new Array();
	parms.push("action=getList");
	parms.push("ccid="+ccid);
	parms.push("type="+type);
	parms.push("venderType="+venderType);
	parms.push("venderTypeName="+encodeURIComponent(venderTypeName));
	var url  = ("../DaemonCertificate?"+parms.join('&'));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.setRows("row");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			var row_count = table.getCount();
			if( row_count > 0 ){	
				$("div_bulidhelp").style.display="block";
				$("div_list").innerHTML = getGrid(table);
			}else{
				saveHead(type);
			}
		}
		setLoading( false );
	};
}

function isexist(type)
{
	var venderType="";
	var ccid = $("txt_ccid")==null?"":$("txt_ccid").value;
	var venderTypeName = $('txt_venderTypeName')==null?"":$('txt_venderTypeName').value;
	var vt1 = $('voteoption1');
	if(vt1!=null ){
		if(vt1.checked){
			venderType="1";
		}else{
			if(venderTypeName=='') {alert("请填写您代理产品的生产商名称!");return;}
			venderType="2";
		}
	}else{
		venderType="-1";
	}
	setLoading( true );
	var parms = new Array();
	parms.push("action=getplcount");
	parms.push("ccid="+ccid);
	parms.push("type="+type);
	parms.push("venderType="+venderType);
	parms.push( "venderTypeName="+encodeURIComponent(venderTypeName) );
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/getplcount");
	table.setRows("row");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			var count = table.getXMLText( "/xdoc/xout/getplcount/row/count" );
			if( count > 0 ){
				alert("此品类的供应商有未审核的单据，请点击修改，将证照录入此份单据中，以避免重复新建单据！");
			}else{
				saveHead(type);
			}
		}
	};
	setLoading( false );
	
}


function getGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;
//	table.setColumns(["aaaa","bbbb","flag","sheetid","type","venderid","vendertype","vendertypename","contact","ccname","checker","checktime","note"]);	
//	var columnNames = ["查看明细","修改","状态","单号","证照类型","供应商ID","供应商类型","代理厂家名称","联系人","证照品类","审核人","审核时间","备注"];	
	
	table.setColumns(["aaaa","bbbb","flag","sheetid","type","venderid","vendertype","vendertypename","contact","ccname","note"]);	
	var columnNames = ["查看明细","修改","状态","单号","证照类型","供应商ID","供应商类型","代理厂家名称","联系人","证照品类","备注"];	
	grid.setId( "grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var sheetid = grid.getCellValue(3,row);
		var type =grid.getCellValue(4,row);
		if(column==0){
				window.open("./certificate_show.jsp?sheetid="+sheetid+"&type="+type);
		}
		
		if(column==1){
			var flag = grid.getCellValue(2,row);
			if(flag==0 || flag==99){
				//window.local("./certificate_edit.jsp?sheetid="+sheetid+"&type="+type);
				window.location.href = "./certificate_edit.jsp?sheetid="+sheetid+"&type="+type;
			}
		}
		
	};
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	grid.setCellText('查看',0);
	
	
	for(var i=0; i<row_count; i++){
		var flag = grid.getCellValue(2,i);
		var type = grid.getCellValue(4,i);
		var venderType = grid.getCellValue(6,i);
		if(flag==0 || flag==99){
			grid.setCellText('修改',1,i);
		}
		grid.setCellText(parseHeadFlag(flag),2,i);
		grid.setCellText(parseType(type),4,i);
		grid.setCellText(parseVenderType(venderType),6,i);
		
	}
	return grid.toString();
}


/**
 * 新建一个单，返回sheetid
 */
function saveHead(type){
	$("div_bulidhelp").style.display="none";
	var contact = $('txt_contact').value;
	var ccid = $("txt_ccid")==null?"":$("txt_ccid").value;
	var venderTypeName = $('txt_venderTypeName')==null?"":$('txt_venderTypeName').value;
	var note = $('txt_note')==null?'':$('txt_note').value;
	var venderType="";
	if(type==1){
		var vt1 = $('voteoption1');
		if(vt1!=null ){
			if(vt1.checked){
				venderType="1";
			}else{
				if(venderTypeName=='') {alert("请填写您代理产品的生产商名称!");return;}
				venderType="2";
			}
		}else{
			venderType="-1";
		}
	}else if(type==2){
		if(ccid==""){alert("请选择录入的证照品类");return;}
		var vt1 = $('voteoption1');
		if(vt1!=null ){
			if(vt1.checked){
				venderType="1";
			}else{
				if(venderTypeName=='') {alert("请填写您代理产品的生产商名称!");return;}
				venderType="2";
			}
		}else{
			venderType="-1";
		}
	}else if(type==3){
	}else if(type==4){
	}else{
		alert("未定义的证照类型:"+type);
	}
	
	setLoading(true);
	var parms = new Array();
	parms.push( "action=addHead" );
	parms.push( "type="+type );
	parms.push( "venderType="+venderType );
	parms.push( "venderTypeName="+encodeURIComponent(venderTypeName) );
	parms.push( "contact="+encodeURIComponent(contact) );
	parms.push( "ccid="+ccid );
	parms.push( "note="+encodeURIComponent(note) );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	//alert(url)
	var ajax = new AW.XML.Table;
	ajax.setURL(url);
	ajax.request();
	ajax.response = function(text){
		setLoading(false);
		ajax.setXML(text);
		var errCode= ajax.getErrCode();
		if(errCode==0){
			var sheetid = ajax.getXMLText("xdoc/xout");
			
			if(sheetid==null || sheetid=='') alert('错误！无法获取新生成的单据ID');
			g_sheetid = sheetid;
			$('btn_bulid').style.display = "none";
			$('txt_sheetid').value=sheetid;
			$('txt_flag').value="新建"+parseType(type);
			$('itemList').style.display="block";
			$('add_btn').style.display="";
			$('submit_btn').style.display="";
			if(type==1){
				$("voteoption1").disabled='disabled';
				$("voteoption2").disabled='disabled';
				$("voteoption3").disabled='disabled';
				$("voteoption4").disabled='disabled';
				$('txt_venderTypeName').disabled='disabled';
				
				setLoading(true);
				var parms = new Array();
				parms.push( "action=selct" );
				parms.push( "flag=0" );
				var url = ("../DaemonCertificate?"+parms.join( '&' ));
				var table = new AW.XML.Table;
				table.setURL(url);
				table.request();
				table.response = function(text){
					setLoading(false);
					table.setXML(text);
					if(table.getErrCode()==0){
						var nodes = table.getXML().selectNodes("/xdoc/xout/select/option");
						for(var index=0; index<nodes.length; index++) {
							var ctid = nodes[index].getAttribute('value');
							ccuploadcreate(i,type,ctid);
						}
					}else{
						alert(table.getErrNote());
					}
				};
			}else if(type==2){
				$("voteoption1").disabled='disabled';
				$("voteoption2").disabled='disabled';
				$("voteoption3").disabled='disabled';
				$("voteoption4").disabled='disabled';
				$('txt_venderTypeName').disabled='disabled';
				$("txt_ccid").disabled='disabled';
				setLoading(true);
				var parms = new Array();
				parms.push( "action=selctc" );
				parms.push( "ccid="+$("txt_ccid").value );
				var url = ("../DaemonCertificate?"+parms.join( '&' ));
				var table = new AW.XML.Table;
				table.setURL(url);
				table.request();
				table.response = function(text){
					setLoading(false);
					table.setXML(text);
					if(table.getErrCode()==0){
						var nodes = table.getXML().selectNodes("/xdoc/xout/select/optgroup/option");
						for(var index=0; index<nodes.length; index++) {
							var ctid = nodes[index].getAttribute('value');
							var flag = nodes[index].getAttribute('flag');
							if(flag==0){
								ccuploadcreate(i,type,ctid);
							}
						}
					}else{
						alert(table.getErrNote());
					}
				};
			}else{
				for(var i=1;i<=3;i++)
				{
				    uploadcreate(i,type);
				}
			}
		}else{
			alert(ajax.getErrNote());
		}
	};
}

function venderSubmit(){
	if(!confirm("是否确认需要提交审核")){
		return;
	}
	
	setLoading( true );
	var parms =new Array();
	parms.push("action=checkimageList");
	parms.push("sheetid="+g_sheetid);
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/checkimageList");
	table.setRows("row");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			var count = table.getXMLText( "/xdoc/xout/checkimageList/row/count" );
			if( count > 0 ){
					var parmsd = new Array();
					parmsd.push("action=venderSubmit");
					parmsd.push("sheetid="+g_sheetid);
					var url  = "../DaemonCertificate?"+parmsd.join('&');
					var ajax = new AW.XML.Table;
					ajax.setURL((url));
					ajax.request();
					ajax.response = function(tx){
						ajax.setXML(tx);
						var xcode = ajax.getErrCode();
						if( xcode != '0' ){//处理xml中的错误消息
							alert( xcode+ajax.getErrNote());
						}else{
							alert("提交成功");
							window.location.reload('./certificate_show.jsp?sheetid='+g_sheetid+'&type='+g_type);
						}
					};
			}else{
				alert("请先上传未上传的图片！");
			}
		}
		setLoading( false );
	};

}

function venderTypeName(e){
	if(e.id=='voteoption1'){
		$('txt_venderTypeName').value='';
		$('venderTypeExt').style.display='none';
	}else{
		$('venderTypeExt').style.display='block';
		$('voteoption4').checked=true;
	}
}

function changeVenderTypeName(e){
	if(e.id=='voteoption3'){
		$('txt_venderTypeName').value=e.value;
		$('txt_venderTypeName').disabled='disabled';
	}else{
		$('txt_venderTypeName').value='';
		$('txt_venderTypeName').disabled='';
	}
	
}