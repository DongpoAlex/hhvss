var g_sheetid;
var currentItemID=0;
var init = function(type,sheetid){
	g_sheetid = sheetid;
	setLoading( true );
	var parms = new Array();
	parms.push("action=show");
	parms.push("sheetid="+sheetid);
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
//		alert(text)
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$('txt_flag').innerText=parseHeadFlag(table.getXMLText( "/xdoc/xout/detail/head/row/flag" ));
			$('txt_venderid').innerText=table.getXMLText( "/xdoc/xout/detail/head/row/venderid" );
			$('txt_vendername').innerText=table.getXMLText( "/xdoc/xout/detail/head/row/vendername" );
			$('txt_addr').innerText=table.getXMLText( "/xdoc/xout/detail/head/row/address" );
			$('txt_tel').innerText=table.getXMLText( "/xdoc/xout/detail/head/row/telno" );
			$('txt_contact').innerText=table.getXMLText( "/xdoc/xout/detail/head/row/contact" );			
			
			
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
		}
		setLoading( false );
	};
};

function initData(type,table){
	$('txt_contact').value = table.getXMLText( "/xdoc/xout/detail/head/row/contact" );
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
		
		var editor = next_node.selectSingleNode("editor").text;
		var edittime = next_node.selectSingleNode("edittime").text;
		var checker = next_node.selectSingleNode("checker").text;
		var checktime = next_node.selectSingleNode("checktime").text;
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
		var src = "./certificateitem_show.jsp?&sheetid="+g_sheetid+"&type="+type+"&id="+seqno+"&ccid="+ccid+"&yearflag="+yearflag+
		"&editor="+editor+"&edittime="+edittime+"&checker="+checker+"&checktime="+checktime+
		"&whflag="+whflag+"&flag="+flag+"&venderid="+venderid+"&ctid="+ctid+"&cname="+encodeURIComponent(ctname)+"&certificateid="+
		certificateid+"&ctype="+ctype+"&yeardate="+yeardate+"&expirydate="+expirydate+
		"&barcodeid="+barcodeid+"&note="+encodeURIComponent(note)+"&approvalnum="+approvalnum+
		"&papprovalnum="+papprovalnum+"&appflag="+appflag+"&goodsname="+encodeURIComponent(goodsname);
		
		next_node = node_row.nextNode();
		html += "<div><iframe src='"+src+"'  style='width:100%;margin-bottom:5px;' frameborder='no' onload='setWinHeight(this)' scrolling='no' /></div>";
	}
	$("itemList").innerHTML = html;
}
