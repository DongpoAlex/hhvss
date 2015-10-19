var Print = function(){
	var clazz;
	var cmid;
	this.init = function(){
		var sheetid = window.location.href.getQuery("sheetid");
		clazz   = window.location.href.getQuery("clazz");
		cmid   = window.location.href.getQuery("cmid");
		var arr_sheetid = sheetid.split(",");
		for( var i=0; i<arr_sheetid.length;i++ ){
			load(arr_sheetid[i]);
		}
	};
	
	var load = function(sheetid){
		var url = "../DaemonSheet?cmid="+cmid+"&operation=show&clazz="+clazz+"&sheetid=" + sheetid;
		var table4detail = new AW.XML.Table;
		table4detail.setURL( url );
		table4detail.request();
		table4detail.response = function(text){
			setLoading(false);
			table4detail.setXML(text);
			var html = "<div id='div_print'><a href='javascript:doPrint()'>[打印本页]</a></div>";
			if( table4detail.getErrCode() != 0 ){	//处理xml中的错误消息
				html = table4detail.getErrNote() ;
			}else{
				var xsl = table4detail.getXMLNode("/xdoc/xout/sheet").getAttribute("xslprint");
				var title = table4detail.getXMLNode("/xdoc/xout/sheet").getAttribute("title");
				window.document.title = title;
				html += table4detail.transformNode( xsl );
			}
			var obj = $new("div");
			obj.innerHTML = html;
			document.body.appendChild( obj );
		};
		setLoading(true);
	};
};

var doPrint = function(){
	$("div_print").style.display="none";
	window.print();
};