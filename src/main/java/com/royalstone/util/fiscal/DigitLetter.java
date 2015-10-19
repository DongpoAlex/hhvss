/*
 * Created on 2005-12-15
 *
 */
package com.royalstone.util.fiscal;

/**
 * @author meng
 *
 */
public class DigitLetter {

	public static String toChinese( int n )
	{
		switch( n ){
			case 0: 	return "零";
			case 1: 	return "壹";
			case 2: 	return "贰";
			case 3: 	return "叁";
			case 4: 	return "肆";
			case 5: 	return "伍";
			case 6: 	return "陆";
			case 7: 	return "柒";
			case 8: 	return "捌";
			case 9: 	return "玖";
			default : 			return "";
		}
	}
}
