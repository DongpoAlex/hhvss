var g_sheetid;
var g_checked=0;//已审核通过数
var g_cnum=0;//证照总数
var init = function(type,sheetid){
	g_sheetid = sheetid;
	setLoading( true );
	var parms = new Array();
	parms.push("action=show");
	parms.push("sheetid="+sheetid);
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
			var headFlag = table.getXMLText( "/xdoc/xout/detail/head/row/flag" );
			$("txt_sheetid").value=sheetid;
			$("txt_flag").value =parseHeadFlag(headFlag);
			$('txt_contact').value = table.getXMLText( "/xdoc/xout/detail/head/row/contact" );
			$('txt_contacttel').value = table.getXMLText( "/xdoc/xout/detail/head/row/telno" );
			if(type==1){
				var venderType = table.getXMLText( "/xdoc/xout/detail/head/row/vendertype" );
				if(venderType==2){
					$('voteoption2').checked='checked';
					$('txt_venderTypeName').value=table.getXMLText( "/xdoc/xout/detail/head/row/vendertypename" );
					$('txt_venderTypeName').disabled='disabled';
				}
				$('voteoption1').disabled="disabled";
				$('voteoption2').disabled="disabled";
			}else if(type==2){
				var venderType = table.getXMLText( "/xdoc/xout/detail/head/row/vendertype" );
				if(venderType==2){
					$('voteoption2').checked='checked';
					$('txt_venderTypeName').value=table.getXMLText( "/xdoc/xout/detail/head/row/vendertypename" );
					$('txt_venderTypeName').disabled='disabled';
				}
				$('voteoption1').disabled="disabled";
				$('voteoption2').disabled="disabled";
				$('txt_ccid').value=table.getXMLText( "/xdoc/xout/detail/head/row/ccid" );
				$('txt_ccid').disabled='disabled';
			}
			initData(type,table);
			//有非审核通过单，则不能整单通过
			if( headFlag==100){
				document.getElementById('btn_checkAll').disabled='disabled';
			}else if(headFlag==99){
				document.getElementById('btn_checkAll').disabled='disabled';
			}
			
		}
		setLoading( false );
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
				
				var venderid = jQuery( "#txt_venderid" ).text();
				table.setParameter("action","setVenderExt");
				table.setURL("../DaemonCertificate?action=setVenderExt&venderid="+venderid);
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

function initData(type,table){
	$('txt_contact').value = table.getXMLText( "/xdoc/xout/detail/head/row/contact" );
	var venderid = table.getXMLText( "/xdoc/xout/detail/head/row/venderid" );
	$("itemList").style.display="block";
	var nodes = table.getXMLNodes( "/xdoc/xout/detail/body/row" );	
	var html = "";
	g_cnum = nodes.length;
	for ( var i = 0; i < nodes.length; i++) {
		var next_node = nodes[i];
		var sheetid=table.getXMLText("sheetid",next_node);
		var seqno=table.getXMLText("seqno",next_node);
		var flag=table.getXMLText("flag",next_node);
		var certificateid=table.getXMLText("certificateid",next_node);
		var certificatename=table.getXMLText("certificatename",next_node);
		var ctid=table.getXMLText("ctid",next_node);
		var expirydate=table.getXMLText("expirydate",next_node);
		var yeardate = table.getXMLText("yeardate",next_node);
		var goodsname=table.getXMLText("goodsname",next_node);
		var barcodeid=table.getXMLText("barcodeid",next_node);
		var note=table.getXMLText("note",next_node);
		var ctname=table.getXMLText("ctname",next_node);
		var yearflag=table.getXMLText("yearflag",next_node);
		var approvalnum=table.getXMLText("approvalnum",next_node);
		var papprovalnum=table.getXMLText("papprovalnum",next_node);
		var appflag=table.getXMLText("appflag",next_node);
		var checktime=table.getXMLText("checktime",next_node);
		var checker=table.getXMLText("checker",next_node);
		var edittime=table.getXMLText("edittime",next_node);
		var editor=table.getXMLText("editor",next_node);
		
		var ctype="必备证照";
		var ccid = $("txt_ccid")==null?"":$("txt_ccid").value;
		var src = "./certificateitem_check.jsp?&sheetid="+g_sheetid+"&type="+type+"&id="+seqno+"&ccid="+ccid+"&yearflag="+yearflag+
		"&flag="+flag+"&venderid="+venderid+"&ctid="+ctid+"&cname="+encodeURIComponent(ctname)+"&certificateid="+
		certificateid+"&ctype="+ctype+"&yeardate="+yeardate+"&expirydate="+expirydate+"&goodsname="+encodeURIComponent(goodsname)+
		"&barcodeid="+barcodeid+"&note="+note+"&approvalnum="+approvalnum+"&papprovalnum="+papprovalnum+"&appflag="+appflag
		+"&checktime="+checktime+"&checker="+checker+"&edittime="+edittime+"&editor="+editor;
		
		html += "<div><iframe src='"+src+"'  style='width:100%;margin-bottom:5px;' frameborder='no' onload='setWinHeight(this)' scrolling='no' /></div>";
	}
	
	$("itemList").innerHTML = html;
}

function checkAll(){
	if(!confirm("是否确认需要整单审核")){
		return;
	}
	setLoading( true );
	var parms = new Array();
	parms.push("action=checkAllOK");
	parms.push("sheetid="+g_sheetid);
	parms.push("note="+encodeURIComponent("整单审核通过"));
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
			window.location.reload();
		}
		setLoading( false );
	};
}

function delSheet(){
	if(!confirm("该操作将删除整张单据，确认删除？")){
		return;
	}
	
	setLoading( true );
	var parms = new Array();
	parms.push("action=delSheet");
	parms.push("sheetid="+g_sheetid);
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL((url));
	table.request();
	table.response = function(text){
		setLoading( false );
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			alert("单据："+g_sheetid+"已删除");
			window.close();
		}
	};
}

function checkChecked(){
	var ifrs = document.getElementsByTagName('iframe');
	for ( var i = 0; i < ifrs.length; i++) {
		var btn = ifrs[i].contentWindow.document.getElementById('checkBox_btn');
		if(btn!=null && btn.checked){
			ifrs[i].contentWindow.checkItemOK2();
		}
	}
	alert("操作成功");
}
