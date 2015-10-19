var Show = function(){
	var service;
	var cmid;
	var operation;
	this.init = function(){
		var sheetid = window.location.href.getQuery("sheetid");
		service   = window.location.href.getQuery("service");
		cmid   = window.location.href.getQuery("cmid");
		operation = window.location.href.getQuery("operation");
		operation = operation==null?"show":operation;
		load(sheetid);
	};
	
	var load = function(sheetid){
		var url = "../DaemonMain?cmid="+cmid+"&operation="+operation+"&service="+service+"&sheetid=" + sheetid;
		var table4detail = new AW.XML.Table;
		table4detail.setURL( url );
		table4detail.request();
		table4detail.response = function(text){
			setLoading(false);
			table4detail.setXML(text);
			var html;
			if( table4detail.getErrCode() != 0 ){	//处理xml中的错误消息
				html = table4detail.getErrNote() ;
			}else{
				var xsl = table4detail.getXMLNode("/xdoc/xout/sheet").getAttribute("xsl");
				var title = table4detail.getXMLNode("/xdoc/xout/sheet").getAttribute("title");
				window.document.title = title;
				html = table4detail.transformNode( xsl );
			}
			var obj = $new("div");
			obj.innerHTML = html;
			document.body.appendChild( obj );
		};
		setLoading(true);
	};
};