/*
 * Created on 2005-12-15
 *
 */
package com.royalstone.util.fiscal;

/**
 * @author meng
 *
 */
public class DigitClause {

	
	public DigitClause( int value, int power )
	{
		this.value = value;
		this.power = new Power( power );
		
		int x = value;
		words[0] = new DigitWord( x%10, 0 );
		x /= 10;
		words[1] = new DigitWord( x%10, 10 );
		x /= 10;
		words[2] = new DigitWord( x%10, 100 );
		x /= 10;
		words[3] = new DigitWord( x%10, 1000 );
		
		int head=3, tail=0;
		while( head >0 && words[head].number == 0 ) head-- ;
		while( tail <4 && words[tail].number == 0 ) tail++ ;
		
		for( int i=0; i<4; i++ ) words[i].silent = !( tail <= i && i <= head );
		if( words[1].number == 0 && words[2].number == 0 )words[1].silent = true;
	}
	
	public String toChinese()
	{
		String str = "";
		for( int i=3; i>=0; i-- ) if( !words[i].silent ) str += words[i].toChinese();
		if( value>0 ) str += power.toChinese();
		return str;
	}
	
	final public int value;
	final private Power power;
	private DigitWord[] words = new DigitWord[4];
	public boolean silent = true;
}
