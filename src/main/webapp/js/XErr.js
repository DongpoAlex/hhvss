// This file should be in UTF-8 code_set.

function XErr( code, note, time )
{
	this.code = code;
	this.note = note;
	this.time = time;
	
	this.toString 		= _toString_XErr;
	this.parseElement 	= parseXErr;
}

function _toString_XErr()
{
	return "" + this.code + ":" + this.note ;
}

function parseXErr( elm_err )
{
	var elm = elm_err.selectSingleNode("code");
	if( elm == null ) throw "Invalid data for xerr!";
	var code = elm.text;

	var elm = elm_err.selectSingleNode("note");
	if( elm == null ) throw "Invalid data for xerr!";
	note = elm.text;
	
	var elm = elm_err.selectSingleNode("time");
	if( elm == null ) throw "Invalid data for xerr!";
	var time = elm.text;

	return new XErr ( code, note, time );	
}
