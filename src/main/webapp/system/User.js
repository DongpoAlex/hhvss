
function User( )
{
	this.userid		= "";
	this.loginid 	= "";
	this.username 	= "";
	this.shopid 	= "AAAA";
	this.password 	= "";
	this.status		= "";
	this.menuroot	= "";
	
	this.toString = function() { return "loginid:" + this.loginid + "; username:" + this.username; }
	this.toElement = _toElement_User;
}


function parseUser( elm_user )
{
	var elm = elm_user.selectSingleNode( "userid" );
	if( elm == null ) throw "Invalid data for userid!";
	var userid = elm.text;

	var elm = elm_user.selectSingleNode( "loginid" );
	if( elm == null ) throw "Invalid data for loginid!";
	var loginid = elm.text;

	var elm = elm_user.selectSingleNode( "username" );
	if( elm == null ) throw "Invalid data for username!";
	var username = elm.text;

	var elm = elm_user.selectSingleNode( "shopid" );
	if( elm == null ) throw "Invalid data for shopid!";
	var shopid = elm.text;

	var elm = elm_user.selectSingleNode( "menuroot" );
	if( elm == null ) throw "Invalid data for menuroot!";
	var menuroot = elm.text;

	var elm = elm_user.selectSingleNode( "status" );
	if( elm == null ) throw "Invalid data for menuroot!";
	var status = elm.text;
	
	
	var user 	= new User();
	user.userid 	= userid;
	user.loginid 	= loginid;
	user.username 	= username;
	user.shopid 	= shopid;
	user.menuroot	= menuroot;
	user.status		= status;
	return user;
}


function _toElement_User( doc )
{
	var elm_user = doc.createElement( "user" );

	var elm = doc.createElement( "userid" );
	elm.appendChild( doc.createTextNode( this.userid ) );
	elm_user.appendChild( elm );

	var elm = doc.createElement( "username" );
	elm.appendChild( doc.createTextNode( this.username ) );
	elm_user.appendChild( elm );

	var elm = doc.createElement( "loginid" );
	elm.appendChild( doc.createTextNode( this.loginid ) );
	elm_user.appendChild( elm );

	var elm = doc.createElement( "shopid" );
	elm.appendChild( doc.createTextNode( this.shopid ) );
	elm_user.appendChild( elm );

	var elm = doc.createElement( "password" );
	elm.appendChild( doc.createTextNode( this.password ) );
	elm_user.appendChild( elm );
	
	var elm = doc.createElement( "status" );
	elm.appendChild( doc.createTextNode( this.status) );
	elm_user.appendChild( elm );
	
	var elm = doc.createElement( "menuroot" );
	elm.appendChild( doc.createTextNode( this.menuroot) );
	elm_user.appendChild( elm );

	return elm_user;
}
