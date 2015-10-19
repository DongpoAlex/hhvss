var init = function(sheetid,seqno){
	setLoading( true );
	var parms = new Array();
	parms.push("action=showDetail");
	parms.push("sheetid="+sheetid);
	parms.push("seqno="+seqno);
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
			$('txt_flag').innerText=parseItemFlag(table.getXMLText( "/xdoc/xout/detail/row/flag" ));
			$('txt_venderid').innerText=table.getXMLText( "/xdoc/xout/detail/row/venderid" );
			$('txt_vendername').innerText=table.getXMLText( "/xdoc/xout/detail/row/vendername" );
			$('txt_addr').innerText=table.getXMLText( "/xdoc/xout/detail/row/address" );
			$('txt_tel').innerText=table.getXMLText( "/xdoc/xout/detail/row/telno" );
			$('txt_contact').innerText=table.getXMLText( "/xdoc/xout/detail/row/contact" );
			var type= table.getXMLText( "/xdoc/xout/detail/row/type" );
			if(type==1){
				var venderType=table.getXMLText( "/xdoc/xout/detail/row/vendertype" );
				var venderTypeName=table.getXMLText( "/xdoc/xout/detail/row/vendertypename" ); 
				$('tool').innerHTML = (venderType==1)?"供应商类型：生产型":"供应商类型：代理或贸易型—"+venderTypeName;
				$('txt_ctype').value="必备证照";
			}else if(type==2){
				$('tool').innerHTML = table.getXMLText( "/xdoc/xout/detail/row/ccname" );
				var ctcflag = table.getXMLText( "/xdoc/xout/detail/row/ctcflag" );
				$('txt_ctype').value = ctcflag==0?"必备证照":"选择性证照";
				var venderType=table.getXMLText( "/xdoc/xout/detail/row/vendertype" );
				var venderTypeName=table.getXMLText( "/xdoc/xout/detail/row/vendertypename" ); 
				$('tool').innerHTML = (venderType==1)?"供应商类型：生产型":"供应商类型：代理或贸易型—"+venderTypeName;
			}
			$('txt_ctname').value=table.getXMLText( "/xdoc/xout/detail/row/ctname" );
			$('txt_cname').value=table.getXMLText( "/xdoc/xout/detail/row/certificatename" );
			$('txt_certificateid').value=table.getXMLText( "/xdoc/xout/detail/row/certificateid" );
			$('txt_expirydate').value=table.getXMLText( "/xdoc/xout/detail/row/expirydate" );
			$('txt_yeardate').value=table.getXMLText( "/xdoc/xout/detail/row/yeardate" );
			$('txt_goodsname').value=table.getXMLText( "/xdoc/xout/detail/row/goodsname" );
			$('txt_barcodeid').value=table.getXMLText( "/xdoc/xout/detail/row/barcodeid" );
			$('txt_note').value=table.getXMLText( "/xdoc/xout/detail/row/note" );
			$('txt_approvalnum').value=table.getXMLText( "/xdoc/xout/detail/row/approvalnum" );
			$('txt_papprovalnum').value=table.getXMLText( "/xdoc/xout/detail/row/papprovalnum" );
			var yearflag=table.getXMLText( "/xdoc/xout/detail/row/yearflag" );
			if(yearflag==1){
				$('input_yeardate').style.display='block';
			}
			var appflag=table.getXMLText( "/xdoc/xout/detail/row/appflag" );
			if(appflag==1){
				$('input_approvalnum').style.display='block';
				$('input_papprovalnum').style.display='block';
			}
			
			var venderid = table.getXMLText( "/xdoc/xout/detail/row/venderid" );
			initImages(sheetid,seqno,venderid);
		}
		setLoading( false );
	};
};


function initImages(sheetid,seqno,venderid){
	var parms = new Array();
	parms.push("action=getImageList");
	parms.push("sheetid="+sheetid);
	parms.push("seqno="+seqno);
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
			}else if(rows==1){
				imgsrc = "../DaemonImgDownLoad?venderid="+venderid+"&filename="+node_row[0].selectSingleNode("imgfile").text;
			}else{
			//多图，自动展示第一张
				imgsrc = "../DaemonImgDownLoad?venderid="+venderid+"&filename="+node_row[0].selectSingleNode("imgfile").text;
				var html = "图片列表：";
				for ( var i = 0; i < node_row.length; i++) {
					var array_element = node_row[i];
					var tempSrc = node_row[i].selectSingleNode("imgfile").text;
					html += "<a class='imgNav' href=\"javascript:changeImg("+Number(i+1)+",'"+venderid+"','"+tempSrc+"')\">"+Number(i+1)+"</a>";
				}
				html +="当前第<span id='imgcount'>1</span>张,共"+rows+"张";
				$('imagesList').style.display="";
				$('imagesList').innerHTML=html;
			}
			$('spanImgShow').innerHTML = "<img alt=\"证照图片\" id=\"imgShow\" src=\""+imgsrc+"\">";
			$('imghref').href = imgsrc;
			$('imgShow').height=350;
		}
	}
}


function imgDel(){
	if($('imgShow').height>500){
		$('imgShow').style.height=$('imgShow').height*0.7;
		$('imgShow').style.width=$('imgShow').width*0.7;
	}
}

function imgAdd(){
	$('imgShow').style.height=$('imgShow').height*1.3;
	$('imgShow').style.width=$('imgShow').width*1.3;
}

function imgTrunLeft(){
	rotate($('imgShow'),'left',0);
}

function imgTrunRight(){
	rotate($('imgShow'),'right',0);
}

function changeImg(seqno,venderid,src){
	var imgsrc = "../DaemonImgDownLoad?venderid="+venderid+"&filename="+src;
	$('spanImgShow').innerHTML = "<img alt=\"证照图片\" id=\"imgShow\" src=\""+imgsrc+"\">";
	$('imgShow').height=350;
	$('imgcount').innerHTML=seqno;
}

// 图片旋转
// 方案修改自：http://byzuo.com/
function rotate(e, name, maxWidth) {
	var img = e,
		  step = img.getAttribute('step'),
		  imgWidth,imgHeight;

	if (!imgWidth ) {
		imgWidth = img.width;
		imgHeight = img.height;
	};

	if (step == null) step = 0;
	if (name === 'left') {
		(step == 3) ? step = 0 : step++;
	} else if (name === 'right') {
		(step == 0) ? step = 3 : step--;
	};
	img.setAttribute('step', step);
	var show_width = imgWidth,
		show_height = imgHeight;
	if ((step == 1 || step == 3) && imgWidth < imgHeight && imgHeight > maxWidth) {
		show_height = maxWidth;
		show_width = imgWidth * maxWidth / imgHeight;
	}
	// IE浏览器使用滤镜旋转
	if (document.all) {
		img.style.filter = 'progid:DXImageTransform.Microsoft.BasicImage(rotation=' + step + ')';
		img.width = show_width;
		img.height = show_height;
		// IE8高度设置
		if ('ie8' == 8) {
			switch (step) {
			case 0:
				document.body.height='';
				break;
			case 1:
				document.body.height(imgWidth + 10);
				break;
			case 2:
				document.body.height='';
				break;
			case 3:
				document.body.height(imgWidth + 10);
				break;
			};
		};
		// 对现代浏览器写入HTML5的元素进行旋转： canvas
	} else {
		alert('该浏览器不支持');
	}
}