var g_sheetid;
var g_type;
var g_ccid;
var g_cts = new Array();
var currentItemID=0;
var init = function(type,sheetid){
	g_type=type;
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
	
			if(headFlag==0){
				
			}else if(headFlag==1 || headFlag==100){
				$('add_btn').style.display="none";
				$('submit_btn').style.display="none";
				$('del_btn').style.display="none";
			}else if(headFlag=99){
				
			}
			$("txt_sheetid").value=sheetid;
			$("txt_flag").value =parseHeadFlag(headFlag);
			$('txt_contact').value = table.getXMLText( "/xdoc/xout/detail/head/row/contact" );
			$('txt_contacttel').value = table.getXMLText( "/xdoc/xout/detail/head/row/telno" );
			//根据证照类型初始化类型栏
			if(type==1){
				var venderType = table.getXMLText( "/xdoc/xout/detail/head/row/vendertype" );
				if(venderType==2){
					$('voteoption2').checked='checked';
					$('txt_venderTypeName').value=table.getXMLText( "/xdoc/xout/detail/head/row/vendertypename" );
					$('btn_venderTypeName').disabled="";
				}
				$('voteoption1').disabled="disabled";
				$('voteoption2').disabled="disabled";
				
			}else if(type==2){
				var venderType = table.getXMLText( "/xdoc/xout/detail/head/row/vendertype" );
				if(venderType==2){
					$('voteoption2').checked='checked';
					$('txt_venderTypeName').value=table.getXMLText( "/xdoc/xout/detail/head/row/vendertypename" );
					$('btn_venderTypeName').disabled="";
				}
				$('voteoption1').disabled="disabled";
				$('voteoption2').disabled="disabled";
				g_ccid = table.getXMLText( "/xdoc/xout/detail/head/row/ccid" );
				$('txt_ccid').value= g_ccid;
				$('txt_ccid').disabled='disabled';
			}
			
			initData(type,table);
			
			if(type==2){
				var cts = new Array();
				setLoading(true);
				var parms = new Array();
				parms.push( "action=selctc" );
				parms.push( "ccid="+g_ccid );
				var url = ("../DaemonCertificate?"+parms.join( '&' ));
				var table2 = new AW.XML.Table;
				table2.setURL(url);
				table2.request();
				table2.response = function(text){
					setLoading(false);
					table2.setXML(text);
					if(table2.getErrCode()==0){
						var nodes = table2.getXML().selectNodes("/xdoc/xout/select/optgroup/option");
						for(var index=0; index<nodes.length; index++) {
							var ctid = nodes[index].getAttribute('value');
							var flag = nodes[index].getAttribute('flag');
							if(flag==0){
								cts.push(ctid);
							}
						}
						
						for(var i=0; i<cts.length; i++) {
							for(var j=0; j<g_cts.length; j++) {
								if(cts[i]==g_cts[j]){
									cts.splice(i,1);
								}
							}
						}
						for(var i=0; i<cts.length; i++) {
							ccuploadcreate(-1,type,cts[i]);
						}
					}else{
						alert(table.getErrNote());
					}
				};
			}
			if(type==1){
				var cts = new Array();
				setLoading(true);
				var parms = new Array();
				parms.push( "action=selct" );
				parms.push( "flag=0" );
				var url = ("../DaemonCertificate?"+parms.join( '&' ));
				var table2 = new AW.XML.Table;
				table2.setURL(url);
				table2.request();
				table2.response = function(text){
					setLoading(false);
					table2.setXML(text);
					if(table2.getErrCode()==0){
						var nodes = table2.getXML().selectNodes("/xdoc/xout/select/option");
						for(var index=0; index<nodes.length; index++) {
							var ctid = nodes[index].getAttribute('value');
								cts.push(ctid);
						}
						
						for(var i=0; i<cts.length; i++) {
							for(var j=0; j<g_cts.length; j++) {
								if(cts[i]==g_cts[j]){
									cts.splice(i,1);
								}
							}
						}
						//取消生成
					}else{
						alert(table.getErrNote());
					}
				};
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
				var doc = table._xml;
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

function initData(type,table){
	var venderid = table.getXMLText( "/xdoc/xout/detail/head/row/venderid" );
	$("itemList").style.display="block";
	var node_row 	= table.getXML().selectNodes( "/xdoc/xout/detail/body/row" );	
	var next_node = node_row.nextNode();
	var html = "";
	while( next_node != null ) {
		var sheetid=next_node.selectSingleNode("sheetid").text;
		var seqno=next_node.selectSingleNode("seqno").text;
		var flag=next_node.selectSingleNode("flag").text;
		var certificateid=next_node.selectSingleNode("certificateid").text;
		var certificatename=next_node.selectSingleNode("certificatename").text;
		var ctid=next_node.selectSingleNode("ctid").text;
		var expirydate=next_node.selectSingleNode("expirydate").text;
		var yeardate = next_node.selectSingleNode("yeardate").text;
//		var whdate = next_node.selectSingleNode("whdate").text;
		var goodsname=next_node.selectSingleNode("goodsname").text;
		var barcodeid=next_node.selectSingleNode("barcodeid").text;
		var note=next_node.selectSingleNode("note").text;
		var ctname=next_node.selectSingleNode("ctname").text;
		var yearflag=next_node.selectSingleNode("yearflag").text;
		var whflag=next_node.selectSingleNode("whflag").text;
		var approvalnum=next_node.selectSingleNode("approvalnum").text;
		var papprovalnum=next_node.selectSingleNode("papprovalnum").text;
		var appflag=next_node.selectSingleNode("appflag").text;
		var ctype="必备证照";
		var ccid = $("txt_ccid")==null?"":$("txt_ccid").value;
		var src = "./certificateitem_edit.jsp?&sheetid="+g_sheetid+"&type="+type+"&id="+seqno+"&ccid="+ccid+"&yearflag="+yearflag+
			"&whflag="+whflag+"&venderid="+venderid+"&flag="+flag+"&ctid="+ctid+"&cname="+encodeURIComponent(certificatename)+"&certificateid="+
			certificateid+"&ctype="+ctype+"&yeardate="+yeardate+"&expirydate="+expirydate+"&goodsname="+encodeURIComponent(goodsname)+
			"&barcodeid="+barcodeid+"&note="+note+"&approvalnum="+approvalnum+"&papprovalnum="+papprovalnum+"&appflag="+appflag;
		
		html += "<div><iframe src='"+src+"'  style='width:100%;margin-bottom:5px;' frameborder='no' onload='setWinHeight(this)' scrolling='no' /></div>";
		
		next_node = node_row.nextNode();
		g_cts.push(ctid);
		
		currentItemID ++;
		if(seqno>currentItemID){
			currentItemID=Number(seqno);
		}
	}
	$("itemList").innerHTML = html;
}

function venderSubmit(){
	//检查是否修改
	var bol = false;
	var ifrs = document.getElementsByTagName('iframe');
	for ( var i = 0; i < ifrs.length; i++) {
		var btn = ifrs[i].contentWindow.document.getElementById('isMdf');
		if(btn!=null && btn.value=='true'){
			bol = true;
			break;
		}
	}
	if(!bol){
		alert("尚未做任何修改");
		return;
	}
	
	
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
					var parms = new Array();
					parms.push("action=venderSubmit");
					parms.push("sheetid="+g_sheetid);
					var url  = "../DaemonCertificate?"+parms.join('&');
					var ajax = new AW.XML.Table;
					ajax.setURL((url));
					ajax.request();
					ajax.response = function(tx){
						ajax.setXML(tx);
						var xcode = ajax.getErrCode();
						if( xcode != '0' ){//处理xml中的错误消息
							alert( ajax.getErrNote());
						}else{
							alert("提交成功");
							window.location.reload('./certificate_show.jsp?sheetid='+g_sheetid+'&type='+g_type);
						}
					};
			}else{
				alert("请先上传未上传的图片！");
			}
		}
	};
	setLoading( false );
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
			history.go(-1);
			window.close();
		}
	};
}

function updateVenderType(){
	if($('txt_venderTypeName').value==''){
		alert("供应商类型名称不能为空，请填写！");
		return;
	}
	setLoading( true );
	var parms = new Array();
	parms.push("action=updateVenderTypeName");
	parms.push("sheetid="+g_sheetid);
	parms.push("venderTypeName="+encodeURIComponent($('txt_venderTypeName').value));
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
			alert("已更新");
		}
	};
}