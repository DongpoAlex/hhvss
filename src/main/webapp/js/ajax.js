/**
 * DATE:	2005-08-08
 * DATE:	2006-08-17
 * DATE:	2006-08-21
 * AUTHOR:	Mengluoyi
 * USAGE:	AJAX前后台通信用的常用函数.
 * 修改：	姚仲南 2011-03-24 14:18
 */

/*********************************************************************/
/**
 * creates a XMLHttpRequest.
 */
function createXMLHttpRequest()
{
	var xmlhttp=false;
	try {
		xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (E) {
			xmlhttp = false;
		}
	}
	
	if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
		xmlhttp = new XMLHttpRequest();
	}
	return xmlhttp;
}


/**
 * 以下函数用于封装 AJAX 调用.
 * AjaxReader 用于传递处理函数到 AjaxCourier 中. 使用时一般需要重新定义其处理函数 read.
 */
function AjaxReader( )
{
	this.read = function( text ) { alert( text ); };
}

/**
 * 此对象用于 AJAX 前台与后台之间异步通信. AjaxCourier 把一个 XML 文档发送到后台, 并接收后台发回的XML应答.
 * 后台发回的 XML 应答到来时, AjaxCourier 将调用处理函数 reader.read.
 * 函数 reader.read 需要根据具体的应用环境重新定义.
 * @param url	代表后台服务的 URL 字串.
 */
function AjaxCourier( url )
{
	url +="&timestamp="+ new Date().getTime();
	this.url  	= url;
	this.island4req	= null;
	this.xmlhttp 	= createXMLHttpRequest();
	this.reader	= new AjaxReader();

	this.call	= function () {
		var xmlhttp 	= createXMLHttpRequest();
		var reader 	= this.reader ;
		xmlhttp.open( "POST", url );
		xmlhttp.onreadystatechange = function() {
			if ( xmlhttp.readyState==4 ) {	
				reader.read( xmlhttp.responseText );
			}
		};
	 
		var str_param = "";
		var str_head  = '<?xml version="1.0" encoding="UTF-8" ?>\n';
		if( this.island4req != null ) str_param = str_head + this.island4req.xml;
		xmlhttp.send( str_param );
	};
}
/*********************************************************************/

/**
 * 此函数把前台的TEXT打印成XML文档并上传前台; 不同的field 打包成XML内的不同的属性(attribute).
 *
 */

function AjaxUploader( url, field_name )
{
	this.url  	= url;
	this.text  	= "";
	this.island4req	= null;
	this.xmlhttp 	= createXMLHttpRequest();
	this.reader	= new AjaxReader();
	
	this.field_name = field_name;
	this.field_number = field_name.length;
	this.rows_limit	= 1000;
	this.row_name   = "row";
	this.set_name   = "row_set";

	this.call	= function () {
	
		var matrix = _upload_txt_to_matrix( this.text, this.field_number, this.rows_limit );
		_upload_matrix_to_island( this.island4req, matrix, this.field_name );
	
		var xmlhttp 	= createXMLHttpRequest();
		var reader 	= this.reader ;
		xmlhttp.open( "POST", url );
		xmlhttp.onreadystatechange = function() {
			if ( xmlhttp.readyState==4 ) {	
				reader.read( xmlhttp.responseText );
			}
		};
	 
		var str_param = "";
		var str_head  = '<?xml version="1.0" encoding="UTF-8" ?>\n';
		if( this.island4req != null ) str_param = str_head + this.island4req.xml;
		xmlhttp.send( str_param );
	};
}

function _upload_txt_to_matrix ( text, field_number, lines_limit )
{
		var str4err = "";
		var arr_lines = text.split( "\n", lines_limit+1 );
		
		if(arr_lines.length >lines_limit )  throw "导入的数据不能超过" + lines_limit + "条!" ;

		// windows 内换行符为"\r\n", 需要去掉最后一位.
		for( var i=0; i<arr_lines.length; i++ ) {
			var n = arr_lines[i].length;
			arr_lines[i] = arr_lines[i].substring(0, n-1);
		}
		
		var arr_matrix = new Array( );
		for( var i=0; i<arr_lines.length; i++ ) {
			var line = new String ( arr_lines[i] );
			
			// 忽略空白行.
			if( line == "" ) continue;
			var arr = line.split( "\t" );
			arr_matrix[ i ] = arr;
			if( arr.length < field_number ) 
			{
			   str4err += "第"+(i+1)+"行仅有 " + arr.length + " 列: " + line+"\n";	
			}
			
			for( var k=0; k<arr.length; k++)
			{
			   if (arr[k]=="") str4err += "第"+(i+1)+"行应第"+(k+1)+"列无值: " + line+"\n";
			}

		}
		if( str4err != "" ) throw str4err;
		return arr_matrix;
}

function _upload_matrix_to_island( island, matrix, field_name )
{
		island.loadXML( "" );
		var doc      	= island.XMLDocument;
		var elm_root 	= doc.selectSingleNode("/");
		
		var elm_xdoc   = doc.createElement( "xdoc" );
		var elm_set = doc.createElement( "row_set" );
		elm_root.appendChild( elm_xdoc );
		elm_xdoc.appendChild( elm_set );

		for( var i=0; i<matrix.length; i++ ) {
			var elm_row = doc.createElement( "row" );
			elm_set.appendChild( elm_row );
			for( var j=0; j<field_name.length && j<matrix[i].length; j++ ) {
				elm_row.setAttribute( field_name[j], matrix[i][j] );
			}
		}
		
//		alert( island.XMLDocument.selectSingleNode( "/" ).xml );

}

/*********************************************************************/
/**
 * 此函数把前台的TEXT打印成XML文档并上传前台; 不同的field 打包成XML内的不同的元素(element).
 * 修改：姚仲南 2011-03-24 14:18
 * 修正文本框内容没有赋值的问题
 */

function AjaxMatrix( url, field_name,field_value )
{	
	this.url  	= url;
	this.text  	= field_value+"\r\n";
	this.island4req	= null;
	this.xmlhttp 	= createXMLHttpRequest();
	this.reader	= new AjaxReader();
	
	this.field_name = field_name;
	this.field_number = field_name.length;
	this.rows_limit	= 1000;
	this.row_name   = "row";
	this.set_name   = "row_set";

	this.call	= function () {
	
		var matrix = _ajxmatrix_txt_to_matrix( this.text, this.field_number, this.rows_limit );
		_ajxmatrix_matrix_to_island( this.island4req, matrix, this.field_name, this.set_name, this.row_name );		
		var xmlhttp 	= createXMLHttpRequest();
		var reader 	= this.reader ;
		xmlhttp.open( "POST", url );
		xmlhttp.onreadystatechange = function() {
			if ( xmlhttp.readyState==4 ) {	
				reader.read( xmlhttp.responseText );
			}
		};
	 
		var str_param = "";
		var str_head  = '<?xml version="1.0" encoding="UTF-8" ?>\n';
		if( this.island4req != null ) str_param = str_head + this.island4req.xml;
		xmlhttp.send( str_param );
	};
}

function _ajxmatrix_txt_to_matrix ( text, field_number, lines_limit )
{
		var str4err = "";
		var arr_lines = text.split( "\n", lines_limit+1 );
		
		if(arr_lines.length >lines_limit )  throw "导入的数据不能超过" + lines_limit + "条!" ;

		// windows 内换行符为"\r\n", 需要去掉最后一位.
		for( var i=0; i<arr_lines.length; i++ ) {
			var n = arr_lines[i].length;
			if(i==arr_lines[i].length-1)
			arr_lines[i] = arr_lines[i].substring(0, n);
			else
			arr_lines[i] = arr_lines[i].substring(0, n-1);
		}
		
		var arr_matrix = new Array( );
		for( var i=0; i<arr_lines.length; i++ ) {
			var line = new String ( arr_lines[i] );
			
			// 忽略空白行.
			if( line == "" ) continue;
			var arr = line.split( "\t" );
			arr_matrix[ i ] = arr;
			if( arr.length < field_number ) 
			{
			   str4err += "第"+(i+1)+"行仅有 " + arr.length + " 列: " + line+"\n";	
			}
			
			for( var k=0; k<arr.length; k++)
			{
			   if (arr[k]=="") str4err += "第"+(i+1)+"行应第"+(k+1)+"列无值: " + line+"\n";
			}

		}
		if( str4err != "" ) throw str4err;
		return arr_matrix;
}

function _ajxmatrix_matrix_to_island( island, matrix, field_name, set_name, row_name )
{
		island.loadXML( "" );
		var doc      	= island.XMLDocument;
		var elm_root 	= doc.selectSingleNode("/");
		
		var elm_xdoc   = doc.createElement( "xdoc" );
		var elm_set = doc.createElement( set_name );
		elm_root.appendChild( elm_xdoc );
		elm_xdoc.appendChild( elm_set );

		for( var i=0; i<matrix.length; i++ ) {
			var elm_row = doc.createElement( row_name );
			elm_set.appendChild( elm_row );
			for( var j=0; j<field_name.length && j<matrix[i].length; j++ ) {
				var elm = doc.createElement( field_name[j] );
				elm.appendChild( doc.createTextNode( matrix[i][j] ) );
				elm_row.appendChild( elm );
			}
		}
		
		//alert( island.XMLDocument.selectSingleNode( "/" ).xml );

}


/******************************** file ajax.js over *************************************/

