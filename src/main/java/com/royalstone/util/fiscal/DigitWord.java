/*
 * Created on 2005-12-15
 *
 */
package com.royalstone.util.fiscal;

/**
 * @author meng
 *
 */
public class DigitWord {

	public DigitWord( int number, int power )
	{
		this.number = number;
		this.power = new Power(power);
	}
	
	public String toChinese()
	{
		String str = "";
		if( !silent ){
			if( number == 0 ) str = DigitLetter.toChinese( 0 );
			if( number >0 ) str = DigitLetter.toChinese( number ) + power.toChinese();
		}
		return str;
	}
	
	public boolean silent = true;
	private Power power;
	public int number  = 0;
}
