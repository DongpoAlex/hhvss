extendDate();
function showYearDate(e){
	var options = e.options;
	for(var i=0; i<options.length; i++) {
		var o = options[i];
		if(o.selected){
			var yearflag=o.yearflag;
			if(yearflag==1){
				$("input_yeardate").style.display="block";
			}else{
				$("input_yeardate").style.display="none";
			}
			//针对进口批文的管理
			if(o.appflag==1){
				$("input_papprovalnum").style.display="block";
				$("input_approvalnum").style.display="block";
			}else if(o.appflag==0 && o.whflag==1) {
				$("input_papprovalnum").style.display="block";
				$("input_approvalnum").style.display="none";
			}else{
				$("input_papprovalnum").style.display="none";
				$("input_approvalnum").style.display="none";
			}
		}
	}
}


function changeCtype(e){
	showYearDate(e);
	var options = e.options;
	for(var i=0; i<options.length; i++) {
		var o = options[i];
		if(o.selected){
			var flag=o.flag;
			if(flag==0){
				$("txt_ctype").innerText="必备证照";
			}else{
				$("txt_ctype").innerText="选择性证照";
			}
		}
	}
}


function uploadcreate(itemID,type){
	g_type=type;
	currentItemID ++;
	if(itemID==null || itemID=='' || itemID==-1){
		itemID = currentItemID;
	}
	var ccid = $("txt_ccid")==null?"":$("txt_ccid").value;
		src="./certificateitem_edit.jsp?&sheetid="+g_sheetid+"&type="+type+"&id="+itemID+"&ccid="+ccid;
	var strContent = "<iframe src='"+(src)+"' id='itemEdit' width='100%' name='frame_upload' frameborder='0' scrolling='no'></iframe>";
	
	var item = document.createElement("item");
	item.innerHTML=strContent;
	$("itemList").appendChild(item);
}

function ccuploadcreate(itemID,type,ctid){
	g_type=type;
	currentItemID ++;
	if(itemID==null || itemID=='' || itemID==-1){
		itemID = currentItemID;
	}
	var ccid = $("txt_ccid")==null?"":$("txt_ccid").value;
		src="./certificateitem_edit.jsp?&sheetid="+g_sheetid+"&type="+type+"&id="+itemID+"&ccid="+ccid+"&ctid="+ctid;
	var strContent = "<iframe src='"+(src)+"' id='itemEdit' width='100%' name='frame_upload' frameborder='0' scrolling='no'></iframe>";
	
	var item = document.createElement("item");
	item.innerHTML=strContent;
	$("itemList").appendChild(item);
}

function parseItemFlag(flag){
	if(flag==0){
		return "未提交";
	}else if(flag==1){
		return "已提交等待审核";
	}else if(flag==100){
		return "审核通过";
	}else if(flag==-1){
		return "审核返回";
	}else if(flag==-10){
		return "年审预警";
	}else if(flag==-11){
		return "过期预警";
	}else if(flag==-100){
		return "过期作废";
	}else{
		return "未定义类型";
	}
}
function parseHeadFlag(flag){
	if(flag==0){
		return "未提交";
	}else if(flag==1){
		return "已提交等待审核";
	}else if(flag==99){
		return "部分审核";
	}else if(flag==100){
		return "审核通过";
	}else{
		return "未定义类型";
	}
}

function parseType(type){
	if(type==1){
		return "基本证照";
	}else if(type==2){
		return "品类证照";
	}else if(type==3){
		return "旧品证照";
	}else if(type==4){
		return "新品证照";
	}else{
		return "未定义类型";
	}
}

function parseVenderType(type){
	if(type==1){
		return "生产型";
	}else if(type==2){
		return "代理或贸易型";
	}else{
		return "";
	}
}
//判断是否数字
function isNum(val) {
    var intStr = /^\d+$/;
    if (!intStr.test(val)) {
        return (false);
    } else {
        return (true);
    }
}

function setWinHeight(obj)  { 
	var win=obj;
	if (document.getElementById) { 
		if (win && !window.opera)  { 
			if (win.contentDocument && win.contentDocument.body.offsetHeight) 
				win.height = win.contentDocument.body.offsetHeight+20; 
			else if(win.contentWindow.document && win.contentWindow.document.body.scrollHeight) 
				win.height = win.contentWindow.document.body.scrollHeight +20;
		}
	} 
} 