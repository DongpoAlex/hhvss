/*
 * Created on 2005-12-15
 *
 */
package com.royalstone.util.fiscal;

/**
 * @author meng
 *
 */
public class Power {
	
	public Power( int x )
	{
		this.p = x;
	}
	
	public String toChinese( )
	{
		switch( p ){
			case 10: 			return "拾";
			case 100: 			return "佰";
			case 1000: 			return "仟";
			case 10000: 		return "万";
			case 100000000: 	return "亿";
			default : 			return "";
		}
	}
	
	final private int p;
}
