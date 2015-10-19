var HTML = function(service){
	var _self = this;
	this.toHTML = function(elmid,params){
		var p = params?"&"+params.join("&"):"";
		var url = "../DaemonMain?&operation=toHTML&service="+service+p;
		var table4detail = new AW.XML.Table;
		table4detail.setURL( url );
		table4detail.request();
		table4detail.response = function(text){
			setLoading(false);
			table4detail.setXML(text);
			var html = "No Tags!";
			if( table4detail.getErrCode() != 0 ){	//处理xml中的错误消息
				html = table4detail.getErrNote() ;
			}else{
				var node = table4detail.getXMLNode("/xdoc/xout/HTML");
				if(node!=null)
					html = table4detail.getXMLContent(node);
			}
			$(elmid).innerHTML = html;
			//alert(html)
			_self.onload();
		};
		setLoading(true);
	};
	
	this.onload=function(){};
};