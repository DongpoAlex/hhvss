/**
 * loading 	读取框
*/
document.write("<div id=\"divloading\" class=\"loading\"></div>");
function $(id){
	return document.getElementById(id);
}
function setLoading( on ,text )
{
	$("divloading").style.display = on ? "block":"none";
	$("divloading").innerHTML = ( text==null )? "数据读取中,请稍候……":text+",请等候……";
	$("divloading").style.left = (window.screen.width) / 2-200 ;
	$("divloading").style.top = (window.screen.height) / 2-200 ;
}
