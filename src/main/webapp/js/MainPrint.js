var Print = function(){
	this.service;
	this.cmid;
	this.operation;
	this.arr_sheetid;
	_self = this;
	this.init = function(){
		var sheetid = window.location.href.getQuery("sheetid");
		this.service   = window.location.href.getQuery("service");
		this.cmid   = window.location.href.getQuery("cmid");
		this.operation = window.location.href.getQuery("operation");
		this.operation = this.operation==null?"print":this.operation;
		this.arr_sheetid = sheetid.split(",");
		for( var i=0; i<this.arr_sheetid.length;i++ ){
			this.load(this.arr_sheetid[i]);
		}
	};
	
	this.load = function(sheetid){
		var url = "../DaemonMain?cmid="+this.cmid+"&operation="+this.operation+"&service="+this.service+"&sheetid=" + sheetid;
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