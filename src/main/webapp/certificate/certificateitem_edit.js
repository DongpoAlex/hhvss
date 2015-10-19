function init(){
	if(g_ctid>0){
		$(txt_ctid).value=g_ctid;
		$(txt_ctid).disabled="disabled";
	}
	if(g_type==1){
		showYearDate($(txt_ctid));
		$('txt_ctype').innerText='必备证照';
	}
	if(g_type==2 ){
		changeCtype($(txt_ctid));
		showYearDate($(txt_ctid));
	}
	if(g_type==4){
		$("txt_barcodeid").onchange=function(){};
	}
	//证照编码
	var certificateID = $('txt_certificateid');
	if(certificateID.value!=""){
		lockInput();
		if(g_flag<1){
			$("save_btn").style.display="none";
			$("edit_btn").style.display="";
		}
	}else{
		$("save_btn").style.display="";
		$("edit_btn").style.display="none";
		var html = "<img border='1' width='100' height='100' id='uploadIMG' src='./images/noimg.gif'>";
		$('divuploadIMG').innerHTML = html;
		$("images_btn").value="上传图片";
		return;
	}
	
	//读取图片信息
	var parms = new Array();
	parms.push("action=getImageList");
	parms.push("sheetid="+g_sheetid);
	parms.push("seqno="+g_seqno);
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			//判断图片个数
			var node_row 	= table.getXML().selectNodes( "/xdoc/xout/img/row" );
			var rows = node_row.length;
			var imgsrc = "./images/noimg.gif";
			if(rows==0){
			//没有图片
				//如果状态为非审核状态，才可以上传图片
				if(g_flag<1){
					$("images_btn").value="上传图片";
				}
			}else if(rows==1){
				imgsrc = "../DaemonImgDownLoad?venderid="+g_venderid+"&filename="+node_row[0].selectSingleNode("imgfile").text;
			}else{
			//多图
				imgsrc = "./images/imgmore.jpg";
			}
			var html = "<img border='1' width='100' height='100' id='uploadIMG' src='"+imgsrc+"' onclick='openImg(this)'>";
			$('divuploadIMG').innerHTML = html;
		}
	};
}

function getGoods(e){
	var barcode=e.value;
	if(barcode=='')return;
	
	var parms = new Array();
	parms.push("barcode="+barcode);
	var url  = "../DaemonGoods?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
			e.value='';
			$("txt_goodsname").value='';
		}else{
			var goodsname = table.getXMLText( "/xdoc/xout/goods/goodsname" );
			$("txt_goodsname").value=goodsname;
		}
	};
}

function openImg(e){
	window.showModalDialog("imgshow.html",e.src,"help:no;dialogHeight=600px;dialogWidth=800px;resizable=yes"); 
}

function maxYear(e){
	if(e.checked){
		$('txt_expirydate').value="2099-12-31";
		$('txt_expirydate').disabled ="disabled";
	}else{
		$('txt_expirydate').value = "";
		$('txt_expirydate').disabled ="";
	}
}


function saveItem(){
	//证照种类
	var ctid = $('txt_ctid');
	ctid = ctid==null?"":ctid.value;
	//证照名称
	var cname = $('txt_cname').value;
	//证照编码
	var certificateID = $('txt_certificateid').value;
	//有效期
	var expiryDate = $('txt_expirydate').value;
	var yearDate = $('txt_yeardate').value;
	//商品条码
	var barcodeid = $('txt_barcodeid').value;
	//商品名称
	var goodsname = $('txt_goodsname').value;
	//批文号
	var approvalnum = $('txt_approvalnum').value;
	//生产日期
	var papprovalnum = $('txt_papprovalnum').value;
	//供应商编码
	var editor = $('txt_vender').value;
	//备注
	var note = $('txt_note').value;
	
	if(g_type==1){
		if(ctid==''){alert("请选择证照类型");return false;}
		if(certificateID==''){alert("请填写证照编码");return false;}
		if(expiryDate==''){alert("请填写有效期终止日");return false;}
		var tmp = $('txt_yeardate');
		var tmps = $('input_yeardate');
		if(tmps.style.display!="none" && tmp.value==""){alert("请填写下次年审日期");return false;}
	}else if(g_type==2){
		if(ctid==''){alert("请选择证照类型");return false;}
		if(certificateID==''){alert("请填写证照编码");return false;}
		if(expiryDate==''){alert("请填写有效期终止日");return false;}
		var tmp = $('txt_yeardate');
		var tmps = $('input_yeardate');
		if(tmps.style.display!="none" && tmp.value==""){alert("请填写下次年审日期");return false;}
	}else if(g_type==3){//3- 旧品证照
		if(ctid==''){alert("请选择证照类型");return false;}
		if(certificateID==''){alert("请填写证照编码");return false;}
		if(expiryDate==''){alert("请填写有效期终止日");return false;}
		if(barcodeid==''){alert("请填写对应商品条码");return false;}
		if(goodsname==''){alert("商品名称未正确获取");return false;}
		if(!isNum(barcodeid)){alert("商品条码必须是数字");return;}
	}else if(g_type==4){//4-新品证照
		if(ctid==''){alert("请选择证照类型");return false;}
		if(certificateID==''){alert("请填写证照编码");return false;}
		if(expiryDate==''){alert("请填写有效期终止日");return false;}
		if(barcodeid==''){alert("请填写对应商品条码");return false;}
		if(goodsname==''){alert("请填写对应商品名称");return false;}
		if(!isNum(barcodeid)){alert("商品条码必须是数字");return;}
		
	}else{
		alert("未定义的证照类型:"+g_type);return false;
	}
	if ((g_type==3) || (g_type==4)){
		var parms = new Array();
		parms.push("action=checkitemList");
		parms.push("certificateID="+certificateID);
		parms.push("papprovalnum="+papprovalnum);
		parms.push("expiryDate="+expiryDate);
		parms.push("barcodeid="+barcodeid);
		var url  = ("../DaemonCertificate?"+parms.join('&'));
		var table = new AW.XML.Table;
		table.setURL(url);
		table.setTable("xdoc/xout/checkitemlist");
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
					var sheetid =  table.getXMLText( "/xdoc/xout/checkitemlist/row/sheetid" );
					var papproval =  table.getXMLText( "/xdoc/xout/checkitemlist/row/papprovalnum" );
					var expirydate =  table.getXMLText( "/xdoc/xout/checkitemlist/row/expirydate" );
					var barcode =  table.getXMLText( "/xdoc/xout/checkitemlist/row/barcodeid" );
					var seqno = table.getXMLText( "/xdoc/xout/checkitemlist/row/seqno" );
						alert("生产日期："+papproval+" ;截止日期："+expirydate+";商品条码："+barcode+"；相同的证照已经存在！单号："+sheetid);
//						$('txt_ctid').value = "";
//						$('txt_certificateid').value = "";
//						$('txt_expirydate').value = "";
//						$('txt_yeardate').value = "";
//						$('txt_barcodeid').value = "";
//						$('txt_goodsname').value = "";
//						$('txt_approvalnum').value = "";
//						$('txt_papprovalnum').value = "";
						return false;
				}else{
					 save(g_sheetid,g_seqno,goodsname,cname,barcodeid,certificateID,note,ctid,expiryDate,yearDate,approvalnum,papprovalnum,editor);
				}
			}
		};
	}else {
		 save(g_sheetid,g_seqno,goodsname,cname,barcodeid,certificateID,note,ctid,expiryDate,yearDate,approvalnum,papprovalnum,editor);	
	}	
}

function save(g_sheetid,g_seqno,goodsname,cname,barcodeid,certificateID,note,ctid,expiryDate,yearDate,approvalnum,papprovalnum,editor){
			var parms = new Array();
			parms.push( "action=editItem" );
			parms.push( "sheetid="+g_sheetid );
			parms.push( "seqno="+g_seqno );
			parms.push( "goodsName="+encodeURIComponent(goodsname) );
			parms.push( "certificateName="+encodeURIComponent(cname) );
			parms.push( "barcodeid="+barcodeid );
			parms.push( "certificateID="+certificateID );
			parms.push( "note="+encodeURIComponent(note) );
			parms.push( "ctid="+ctid);
			parms.push( "expiryDate="+expiryDate );
			parms.push( "yearDate="+yearDate );
			parms.push( "approvalnum="+approvalnum );
			parms.push( "papprovalnum="+papprovalnum );
			parms.push( "editor="+editor);
			var url  = ("../DaemonCertificate?"+parms.join('&'));
			var ajax = new AW.XML.Table;
			ajax.setURL(url);
			ajax.request();
			ajax.response = function(text){
				ajax.setXML(text);
				var errCode= ajax.getErrCode();
				if(errCode==0){
					alert('保存成功');
					lockInput();
					$("save_btn").style.display="none";
					$("edit_btn").style.display="";
					$("isMdf").value=true;
					
					//检查图片。如果未录入图片且相同证照号已有图片，则自动设置图片。
					var imgfile = ajax.getXMLText( "/xdoc/xout/img/row/imgfile" );
					if(imgfile!='' && $("uploadIMG").src!="./images/noimg.gif"){
						imgsrc = "../DaemonImgDownLoad?venderid="+g_venderid+"&filename="+imgfile;
						$("uploadIMG").src = imgsrc;
					}
				}else{
					alert(ajax.getErrNote());
				}
			};
}

function delItem(){
	if(!confirm("该操作将删除文字信息吗，确认删除？")){
		return;
	}
	
	setLoading( true );
	var parms = new Array();
	parms.push("action=delSheetItem");
	parms.push("sheetid="+g_sheetid);
	parms.push("seqno="+g_seqno);
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
			alert("本条证照："+g_sheetid+","+g_seqno+"已删除");
			//$('txt_ctid').value = "";
			$('txt_certificateid').value = "";
			$('txt_expirydate').value = "";
			$('txt_yeardate').value = "";
			$('txt_barcodeid').value = "";
			$('txt_goodsname').value = "";
			$('txt_approvalnum').value = "";
			$('txt_papprovalnum').value = "";
			$('uploadIMG').src='./images/noimg.gif';
			$("isMdf").value=true;
//			window.close();
		}
	};
}
function initEditItem(){
	
	var ctid = $('txt_ctid');
	//证照名称
	var cname = $('txt_cname');
	//证照编码
	var certificateID = $('txt_certificateid');
	//有效期
	var expiryDate = $('txt_expirydate');
	var yearDate = $('txt_yeardate');
//    var whDate = $('txt_whdate');
	//商品条码
	var barcodeid = $('txt_barcodeid');
	//商品名称
	var goodsname = $('txt_goodsname');
	//批文号
	var approvalnum = $('txt_approvalnum');
	//生产日期
	var papprovalnum = $('txt_papprovalnum');
	if(ctid!=null) ctid.disabled=false;
	if(cname!=null) cname.disabled=false;
	if(certificateID!=null) certificateID.disabled=false;
	if(expiryDate!=null) expiryDate.disabled=false;
	if(yearDate!=null) yearDate.disabled=false;
//	if(whDate!=null) whDate.disabled=false;
	if(barcodeid!=null) barcodeid.disabled=false;
	//新品不检测barcode
	if(g_type==4){
		if(goodsname!=null) goodsname.disabled=false;
		barcodeid.onchange=function(){};
	}
	if(approvalnum!=null) approvalnum.disabled=false;
	if(papprovalnum!=null) papprovalnum.disabled=false;
	
	$("save_btn").style.display="";
	$("edit_btn").style.display="none";
}


function imgmore(){
	//先检查是否保存
	var certificateID = $('txt_certificateid');
	if($("edit_btn").style.display=="none"){
		alert('请先填写文字信息，保存后再维护图证照图片');
		return;
	}
	var url = "uploadmore.jsp?sheetid="+g_sheetid+"&seqno="+g_seqno+"&type="+g_type;
	var newwin = window.showModalDialog(url,window,"help:no;dialogHeight=600px;dialogWidth=600px;resizable=yes");
	$("isMdf").value=true;
	//window.open(url);
}


function lockInput(){
	//
	var ctid = $('txt_ctid');
	//证照名称
	var cname = $('txt_cname');
	//证照编码
	var certificateID = $('txt_certificateid');
	//有效期
	var expiryDate = $('txt_expirydate');
	var yearDate = $('txt_yeardate');
//	var whDate=$('txt_whdate');
	//商品条码
	var barcodeid = $('txt_barcodeid');
	//商品名称
	var goodsname = $('txt_goodsname');
	//批文号
	var approvalnum = $('txt_approvalnum');
	//生产日期
	var papprovalnum = $('txt_papprovalnum');
	if(ctid!=null) ctid.disabled="disabled";
	if(cname!=null) cname.disabled="disabled";
	if(certificateID!=null) certificateID.disabled="disabled";
	if(expiryDate!=null) expiryDate.disabled="disabled";
	if(yearDate!=null) yearDate.disabled="disabled";
//	if(whDate!=null) whDate.disabled="disabled";
	if(barcodeid!=null) barcodeid.disabled="disabled";
	if(goodsname!=null) goodsname.disabled="disabled";
	if(approvalnum!=null) approvalnum.disabled="disabled";
	if(papprovalnum!=null) papprovalnum.disabled="disabled";
}