/*
 * Created on 2005-12-15
 *
 */
package com.royalstone.util.fiscal;

/**
 * @author meng
 *
 */
public class Cent {

	public Cent( int cent )
	{
		this.cent = cent;
	}
	
	public String toChinese()
	{
		int j = cent / 10;
		int f = cent % 10;
		
		String str = "整";
		
		if( j == 0 && f == 0 ) str = "整";
		else
		if( j >0 && f == 0 ) str = DigitLetter.toChinese(j) + "角整";
		else
		if( j == 0 && f >0 ) str = "零" + DigitLetter.toChinese(f) + "分" ;
		else {
			str = DigitLetter.toChinese(j) + "角" + DigitLetter.toChinese(f) + "分" ;
		}
		
		return str;
	}
	
	final private int cent;
}
