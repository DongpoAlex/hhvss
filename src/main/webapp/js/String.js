/**
*@author:yzn
*Create on 2006-03-21
*/

/**
*对JS的String对象扩展,增加trim()方法,去掉字符串的前后空格
*/
String.prototype.trim = function(){    
    return this.replace(/(^\s*)|(\s*$)/g, "");
};
/**
*对JS的String对象扩展,增加toFsical()方法,把一个金额转为财务格式
*/    
String.prototype.toFiscal = function(){
   extendNumber();  
   var n=parseFiscalNumber(this);   
   return n.toFiscalString();
};
/**
*对JS的String对象扩展,增加toArray()方法,把一个以|分隔的String转为JS的数组并返回
*/     
String.prototype.toArray=function() {		
	var str=new Array();
	var return_array=new Array();
	var tmp="";
	str=this.split('');	
	for(var i=0,j=0;i<str.length;i++){	
	if(str[i]!="|") tmp+=str[i];	
	else
	{return_array[j++]=tmp;
	 tmp="";
	 continue;}
	}	
    return return_array;
  };
/**
*对JS的String对象扩展,增加reverse()方法,把一个字符串逆转并返回
*/   
String.prototype.reverse=function() {			
	var tmp_a=new Array();
	var tmp_b=new Array();
	tmp_a=this.split('');
	tmp_b=tmp_a.reverse();
	var return_str=tmp_b.toString();
	var re = /,/g;		
    return return_str.replace(re, "");
  }
  
/**
*对JS的String对象扩展,增加inspect()方法,把一个String用单引号括起来
*/    
String.prototype.inspect=function() {
    return "'" + this.replace('\\', '\\\\').replace("'", '\\\'') + "'";
  };
  

/**
 * 扩展几个方法
 * add bai
 * 2006-11-8
 */
/**
 *  获得地址栏制定的参数
 */
String.prototype.getQuery = function(name)
{
var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
var r = this.substr(this.indexOf("\?")+1).match(reg);
if (r!=null) return unescape(r[2]); return null;
};

