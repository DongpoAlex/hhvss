/**
 * DATE: 	2004-11-23
 * DATE:	2004-11-28
 * DATE:	2006-03-29
 * AUTHOR:	Mengluoyi
 * USAGE:	日期处理的常用函数.
 */

/**
 * 此函数对 JavaScript 内嵌的对象进行扩展.
 * Date 对象的方法 toString 将替换为自定义的函数 stdString4Date.
 * Date 对象新增一个方法 advance.
 * 执行此函数后,调用新的方法 Date.toString 可以得到标准的日期字串: YYYY-MM-DD.
 */
function extendJS()
{
	Date.prototype.toString 	= stdString4Date;
	Date.prototype.advance 		= advanceDate;
	Date.prototype.daysBetween 	= daysBetween;
}

function extendDate()
{
	Date.prototype.toString 	= stdString4Date;
	Date.prototype.advance 		= advanceDate;
	Date.prototype.daysBetween 	= daysBetween;
}

/**
 * 调用此函数将返回一个新的 Date 对象. 该对象表示的日期比调用的对象要超前 n 天.
 * 函数 extendJS() 又将此函数设置为Date 对象的一个新方法,名为advance.
 * 例如: var tomorrow = new Date().advance(1);
 * 执行后, tomorrow 为明天的日期.
 * NOTE: 调用此方法后, 调用者的值不变.
 */
function advanceDate( n )
{
	var milliseconds = this.valueOf();
	milliseconds += (24*3600*1000) * n;
	var newdate = new Date(milliseconds);
	return newdate;
}


/**
 * 调用此函数将返回在日期 this 与 b 之间相差的天数.
 * 如果日期 b 与日期 this 在同一天, 返回值为0;
 * 如果日期 b 在日期 this 之前, 返回值为正;
 * 如果日期 b 在日期 this 之后, 返回值为负;
 * NOTE: JavaScript 中的Date 对象精度到毫秒. 但此函数计算过程中只考虑年/月/日.
 * 例如, 如果 b 表示 "2000-01-01 23:59:59", this 为 "2000-01-02 00:00:00", 则此函数计算结果为1.
 */
function daysBetween( b )
{
	var end   = new Date( this.getFullYear(), this.getMonth(), this.getDate() );
	var start = new Date( b.getFullYear(), b.getMonth(), b.getDate() );
	
	var days = ( end - start ) / (24*3600*1000);
	return days;
}

/**
 * 此函数把日期对象转换为以 '-' 分隔的标准格式: YYYY-MM-DD
 */
function stdString4Date()
{
	var y = this.getFullYear();
	var m = this.getMonth()+1;
	var d = this.getDate();
	var dstr = "" + ( 10000 * y + 100 * m + d );
	var s = dstr.substr( 0, 4 ) + "-" + dstr.substr( 4, 2 ) + "-" + dstr.substr( 6, 2 );

	return s ;
}

/**
 * 此函数对字串 s 进行处理:
 * 1) 可接受的分隔符将替换为标准分隔符 '.' ;
 * 2) 过滤重复的分隔符;
 * 3) 去掉前导分隔符;
 * @return	以句点 '.' 分隔的字串
 */
function cookStr4Date( s )
{
	var re = /[,;#_ \\\/\-]/g;

	var str = s;
	var newstr=str.replace(re, ".");
	
	re = /\.\./g;
	newstr=newstr.replace(re, ".");

	while( newstr != str ) {
		str = newstr;
		newstr=str.replace(re, ".");
	}
	
	if( newstr.charAt(0) == '.' ) newstr = newstr.substr(1);
	return newstr;
}


/**
 * 此函数将试图从字串 str 中解析出有效的日期值. 
 * 字串str 可以是中国人所习惯使用的多种格式, 如: 2001/3/8, 2004.10.1, 03\12\31, 2000-02-28, 10/31, 15.
 * 解析的具体规则如下:
 * 1) 可以接受以下三种格式: {年,月,日}{月,日}{日}. 
 *    如果只提供一个数字,则认为输入日期在当前月,所输入数字为日期; 
 *    如果提供两个数字,则认为输入日期在当前年, 所输入的两个数字分别为月和日;
 *    如果提供三个数字,则认为所输入数字分别为:年,月,日.
 * 2) 三个数字之间必须有分隔符. 分隔符可以是空格,也可以是以下字符: .-_/\,;
 * 3) 重复的分隔符将被过滤掉. 但分隔符之间不得有非数字字符.
 * 4) 如果提供多于三个数字,则只取前三组,多余的数字将被忽略.
 * 5) 字串中的年份部分必须在0到9000之间. 如果字串中的年份小于1000, 则程序认为是2000年后的年份简写,并自动作加2000的处理.
 * 6) 月份部分必须在 1 至 12 之间. 
 * 7) 对于"日",将按相应的年和月份进行检查. 
 * 8) 快捷方式: 昨天/明天/前天/后天/大前天/大后天用以下符号表示: <, >, <<, >>, <<<, >>>.
 * 9) 快捷方式: ( 表示一周前, ) 表示一周后.
 * 10) 快捷方式: [ ] 分别表示一个月前和一个月后.
 * 11) 快捷方式: { } 分别表示一年前和一年后.
 * 12) 快捷方式: -n +n 分别表示today-n/today+n. 
 * **) 如果参数 str 不能按以上规则解析出有效的日期,则函数将抛出一个 Exception, 并说明出错的原因.
 * @param str	代表日期的字串.
 * @return 	字串 str 中所包含的日期信息.
 * Last modified: 2006.03.29 Mengluoyi
 */
function parseDate( str )
{
	var today = new Date();
	var y = today.getYear();
	var m = today.getMonth()+1;
	var d = today.getDate();

	var retday = new Date();
	
	if( str == "." ) 	return retday;
	if( str == "<" ) 	return retday.advance(-1);
	if( str == "<<" ) 	return retday.advance(-2);
	if( str == "<<<" ) 	return retday.advance(-3);
	if( str == ">" ) 	return retday.advance(1);
	if( str == ">>" ) 	return retday.advance(2);
	if( str == ">>>" ) 	return retday.advance(3);
	
	/* 一周前/一周后. */
	if( str == "(" ) return retday.advance(-7);
	if( str == ")" ) return retday.advance(7);
	
	/* 一个月前/一个月后 */
	if( str == "[" ) {
		retday = new Date( y, m-2, d );
		if( retday.getDate() != d ) {
			retday = new Date( y, m-1, 0 );
		}
		return retday;
	}
	if( str == "]" ) {
		retday = new Date( y, m, d );
		if( retday.getDate() != d ) {
			retday = new Date( y, m+1, 0 );
		}
		return retday;
	}
	
	/* 一年前/一年后. */
	if( str == "{" ) {
		retday =  ( m == 2 && d == 29 ) ? new Date( y-1, m-1, d ) : new Date( y-1, m-1, d-1 );
		return retday;
	}
	if( str == "}" ) {
		retday =  ( m == 2 && d == 29 ) ? new Date( y+1, m-1, d ) : new Date( y+1, m-1, d-1 );
		return retday;
	}
	
	/*  today+n/today-n */
	if( str.substr(0,1) == "+" || str.substr(0,1) == "-" ) {
		var ss = str.substr( 1, str.length-1 );
		var re = new RegExp ( "[0-9]" );
		var rx = new RegExp ( "[^0-9]" );
		if( re.test( ss ) && !rx.test( ss ) ) {
			var n = parseInt( str );
			if ( !isNaN(n) && n < 1000000 && n > -1000000 ) {
				return new Date( y, m-1, d+n );
			} 
		}
	}

	var s = cookStr4Date( str );

	var arrDate = s.split( "." );
	
	if( arrDate.length == 0 ) {
		throw "Date Format should be: YYYY-MM-DD";
	}
	
	if( arrDate.length == 1 ) {
		d = parseInt( arrDate[0], 10 );
	}
	
	if( arrDate.length == 2 ) {
		m = parseInt( arrDate[0], 10 );
		d = parseInt( arrDate[1], 10 );
	}
	
	if( arrDate.length >= 3 ) {
		y = parseInt( arrDate[0], 10 );
		m = parseInt( arrDate[1], 10 );
		d = parseInt( arrDate[2], 10 );
	}

	if( isNaN( y ) || isNaN( m ) || isNaN( d ) ) throw "Date Format should be: YYYY-MM-DD";
	if( y >=0 && y < 1000 ) y += 2000;
	if( y<1000 || y> 9000 ) throw "无效年!" ;
	if( m< 1 || m > 12 ) throw "无效月!" ;
	
	var month_end = new Date( y, m, 0 );
	if( d > month_end.getDate() )throw "" + y + "年" + m + "月的最后一天为" + month_end.getDate() + "日!" ;
	if( d < 1 )  throw "无效日!" ;
	retday = new Date( y, m-1, d );
	 
	return retday;
}


/**
 * 此函数检查控件ctrl 输入的日期值.
 * @param ctrl	参数 ctrl 通常是一个文本输入控件. 
 * 1) 如果 ctrl.value 不是有效的日期值,则报错.
 * 2) 如果控件 ctrl 设置了属性min/max, 则进行相应检查. 如果输入值超出了设置的范围,则报错.
 */
function checkDate( ctrl )
{
	var min, max, d;

	try{
		if( ctrl.value.length == 0 )return false;
		d = parseDate( ctrl.value );
		
		if( ctrl.min != null && ctrl.min.length>0 ){
			min = parseDate( ctrl.min );
			if( d < min ) {
				alert ( "不得早于" + min.toString() + "!" );
				ctrl.value = min.toString();
				ctrl.select();
				ctrl.focus();
				return false;
			}
		}

		if( ctrl.max != null && ctrl.max.length>0 ){
			max = parseDate( ctrl.max );
			if( d > max ) {
				alert ( "不得晚于" + max.toString() + "!" );
				ctrl.value = max.toString();
				ctrl.select();
				ctrl.focus();
				return false;
			}
		}
		
		ctrl.value = d.toString();
		return true;
	} catch (e) {
	  	alert(e);
		ctrl.select();
		ctrl.focus();
	  	return false;
	}
}

/**************************************** FILE OVER ***************************************/
extendDate();