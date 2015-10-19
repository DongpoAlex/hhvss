
function CyberMenu( )
{
	this.menuid	= "";
	this.menulabel	= "";
	this.menuroot	= "";
	this.menulabel	= "";
	this.headmenuid	= "";
	this.moduleid	= "";
	this.action	= "";
	this.roletype="";
	
	this.toString = function() { return "menuid:" + this.menuid + "; menulabel:" + this.menulabel; }
	this.toElement = _toElement_Menu;
}


function parseMenu( elm_menu )
{
	var obj_menu 	= new CyberMenu();
	
	obj_menu.menuid 	= elm_menu.getAttribute ( "menuid" );
	obj_menu.menulabel 	= elm_menu.getAttribute ( "menulabel" );
	obj_menu.menulevel 	= elm_menu.getAttribute ( "menulevel" );
	obj_menu.menuroot 	= elm_menu.getAttribute ( "menuroot" );
	obj_menu.headmenuid 	= elm_menu.getAttribute ( "headmenuid" );
	obj_menu.moduleid 	= elm_menu.getAttribute ( "moduleid" );
	obj_menu.action 	= elm_menu.getAttribute ( "action" );
	obj_menu.roletype 	= elm_menu.getAttribute ( "roletype" );
	
	if( obj_menu.menulevel == null ) throw "Invalid data for menulevel!";
	
	if( obj_menu.moduleid == null ) obj_menu.moduleid = "0";
	if( obj_menu.action == null ) obj_menu.action = "";

	return obj_menu;
}


function _toElement_Menu( doc )
{
	var elm_menu = doc.createElement( "menu" );
	
	if( this.menuid != null ) 	elm_menu.setAttribute( "menuid", 	this.menuid );
	if( this.menulabel != null ) 	elm_menu.setAttribute( "menulabel", 	this.menulabel );
	if( this.menulevel != null ) 	elm_menu.setAttribute( "menulevel", 	this.menulevel );
	if( this.menuroot != null ) 	elm_menu.setAttribute( "menuroot", 	this.menuroot );
	if( this.headmenuid != null ) 	elm_menu.setAttribute( "headmenuid", 	this.headmenuid );
	if( this.moduleid != null ) 	elm_menu.setAttribute( "moduleid", 	this.moduleid );
	if( this.action != null ) 	elm_menu.setAttribute( "action", 	this.action );
	if( this.roletype != null ) 	elm_menu.setAttribute( "roletype", 	this.roletype );
	
	return elm_menu;
}
