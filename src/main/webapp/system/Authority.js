
function AuthorityList()
{
	this.permission_list = new Array(0);

	this.toElement = function( doc ){
		var elm_lst = doc.createElement( "authority_list" );
		
		for( var i=0; i< this.permission_list.length; i++ ) {
			elm_lst.appendChild( this.permission_list[i].toElement( doc ) );
		}
		return elm_lst;
	};
}

function parseAuthorityList( elm_lst )
{
	var lst = new AuthorityList();
	var grp = new Array(0);
		
	var len = elm_lst.childNodes.length;

	for( var i = 0; i<len; i++ ){
		var elm_info = elm_lst.childNodes.item(i);
		var moduleid 	= elm_info.getAttribute ( "moduleid" );
		var roleid 	= elm_info.getAttribute ( "roleid" );

		if( elm_info.nodeName == "authority" ){
			var au = parseAuthority( elm_info );
			grp[i] = au;

		} else {
			alert( "ERROR: Not a authority!" );
		}
	}
	
	lst.permission_list = grp;
	return lst;
}


function Authority( roleid, moduleid )
{
	this.roleid 		= roleid;
	this.moduleid 		= moduleid;
	this.att_read 		= "";
	this.att_edit 		= "";
	this.att_insert 	= "";
	this.att_delete 	= "";
	this.att_print 		= "";
	this.att_check 		= "";
	this.att_confirm 	= "";
	this.att_verify 	= "";
	
	this.toElement		= _toElement_Authority;
	this.setAttribute	= _setAttribute_Authority;
	this.toString = function() { return "roleid:" + this.roleid + "; moduleid:" + this.moduleid; }
}

function _toElement_Authority( doc )
{
	var elm_au = doc.createElement( "authority" );
	
	elm_au.setAttribute( "roleid", 	this.roleid );
	elm_au.setAttribute( "moduleid", this.moduleid );
	
	if( this.att_read != null ) 	elm_au.setAttribute( "read", 	this.att_read );
	if( this.att_edit != null ) 	elm_au.setAttribute( "edit", 	this.att_edit );
	if( this.att_insert != null ) 	elm_au.setAttribute( "insert",  this.att_insert );
	if( this.att_delete != null ) 	elm_au.setAttribute( "delete",  this.att_delete );
	if( this.att_print != null ) 	elm_au.setAttribute( "print",   this.att_print );
	if( this.att_check != null ) 	elm_au.setAttribute( "check",   this.att_check );
	if( this.att_confirm != null ) 	elm_au.setAttribute( "confirm",   this.att_confirm );
	if( this.att_verify != null ) 	elm_au.setAttribute( "verify",   this.att_verify );

	return elm_au;
}


function _setAttribute_Authority( name, value )
{
	if( "read" == name ) 	this.att_read = value;
	if( "edit" == name ) 	this.att_edit = value;
	if( "insert" == name ) 	this.att_insert = value;
	if( "delete" == name ) 	this.att_delete = value;
	if( "print" == name ) 	this.att_print = value;
	if( "check" == name ) 	this.att_check = value;
	if( "confirm" == name ) this.att_confirm = value;
	if( "verify" == name ) 	this.att_verify = value;
}


function parseAuthority( elm )
{
	var moduleid 	= elm.getAttribute ( "moduleid" );
	var roleid 	= elm.getAttribute ( "roleid" );
	
	var var_read 	= elm.getAttribute ( "read" );
	var var_edit 	= elm.getAttribute ( "edit" );
	var var_insert 	= elm.getAttribute ( "insert" );
	var var_delete 	= elm.getAttribute ( "delete" );
	var var_print 	= elm.getAttribute ( "print" );
	var var_check 	= elm.getAttribute ( "check" );
	var var_confirm = elm.getAttribute ( "confirm" );
	var var_verify 	= elm.getAttribute ( "verify" );
	
	var au 		= new Authority( roleid, moduleid );
	if( var_read 	!= null ) au.att_read 	= var_read;
	if( var_edit 	!= null ) au.att_edit 	= var_edit;
	if( var_insert 	!= null ) au.att_insert = var_insert;
	if( var_delete 	!= null ) au.att_delete = var_delete;
	if( var_print 	!= null ) au.att_print 	= var_print;
	if( var_check 	!= null ) au.att_check 	= var_check;
	if( var_confirm != null ) au.att_confirm = var_confirm;
	if( var_verify 	!= null ) au.att_verify = var_verify;

	return au;
}
