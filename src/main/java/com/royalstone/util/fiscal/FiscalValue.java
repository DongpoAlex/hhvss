/*
 * Created on 2005-12-15
 *
 */
package com.royalstone.util.fiscal;

import java.text.DecimalFormat;

/**
 * @author meng
 *
 */
public class FiscalValue {
	
	public FiscalValue ( int value )
	{
		this.value_yuan = (long) value;
		this.value_fen  = 0;
		analyse();
		
	}
	
	public FiscalValue( long value )
	{
		this.value_yuan = value;
		this.value_fen  = 0;
		analyse();
	}
	
	public FiscalValue( long val_yuan, int val_fen )
	{
		this.value_yuan = val_yuan;
		this.value_fen  = val_fen;
	}
	
	public FiscalValue( double value )
	{
		if( value > MAX_VALUE )throw new IllegalArgumentException( "Too Large! " );
		
		/**
		 * NOTE: 取元以下的部分此处需要作特殊处理.
		 */
		long vc = (long) Math.round( value * 100.0D );
		this.value_yuan = vc / 100L ;
		this.value_fen  = (int) (vc % 100L) ;
//		double x = (value - (double)value_yuan) * 100.0;
//		this.value_fen  = (int) Math.round ( x );
		analyse();
	}

	public FiscalValue( float value )
	{
		if( value > MAX_VALUE )throw new IllegalArgumentException( "Too Large! " );
//		this.value_yuan = (long) value;

		/**
		 * NOTE: 取元以下的部分此处需要作特殊处理.
		 */
		long vc = (long) Math.round( value * 100.0D );
		this.value_yuan = vc / 100L ;
		this.value_fen  = (int) (vc % 100L) ;
//		double x = ( (double)value - (double)value_yuan) * 100.0;
//		this.value_fen  = (int) Math.round ( x );

		analyse();
	}
	
	private void analyse()
	{
		if( value_yuan > MAX_VALUE )throw new IllegalArgumentException( "Too Large! " );
		long x = value_yuan;
		for( int i=0; i<digits.length; i++ ){
			digits[i] = x % 10L;
			x /= 10L;
		}
		
		if( digits[3] >0 && digits[4] >0 ) wall_bc = ""; 
		else wall_bc = "零";
		if( digits[7] >0 && digits[8] >0 ) wall_ab = ""; 
		else wall_ab = "零";
		
		if( x > 0 ) throw new IllegalArgumentException( "Value greater than MAX_VALUE! " );
		long sec_value = 0L;
		sec_value = this.value_yuan % 10000L;
		
		C = new DigitClause( (int) sec_value, 0 );
		
		sec_value = ( this.value_yuan / 10000L ) % 10000L;
		B = new DigitClause( (int) sec_value, 10000 );
		
		sec_value = ( this.value_yuan / 100000000L ) % 10000L;
		A = new DigitClause( (int) sec_value, 100000000 );
	}
	
	public String toChinese()
	{
		String str = "";
		if( this.value_yuan == 0 ) str = "零";
		if( A.value == 0 && B.value == 0 && C.value >0 ) str = C.toChinese();
		
		if( A.value == 0 && B.value  > 0 && C.value ==0 ) str = B.toChinese();
		if( A.value == 0 && B.value  > 0 && C.value  >0 ) str = B.toChinese() + wall_bc + C.toChinese();
		
		if( A.value  > 0 && B.value == 0 && C.value == 0 ) str = A.toChinese();
		if( A.value  > 0 && B.value == 0 && C.value  > 0 ) str = A.toChinese() + "零" + C.toChinese();
		if( A.value  > 0 && B.value  > 0 && C.value == 0 ) str = A.toChinese() + wall_ab + C.toChinese();
		if( A.value  > 0 && B.value  > 0 && C.value  > 0 ) str = A.toChinese() + wall_ab + B.toChinese() + wall_bc + C.toChinese();
		
		str += "元";
		str += ( new Cent(value_fen) ).toChinese();
		return str;
	}
	
	public void print ()
	{
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		System.out.println( df.format( (double)this.value_yuan + 0.01 * (double)this.value_fen )  + " -> " + this.toChinese() );
	}
	
	final static public long MAX_VALUE = 100000000000L;
	final private long value_yuan;
	final private int value_fen;
	private long[] digits = new long[13];
	private DigitClause A,B,C;
	private String wall_ab="", wall_bc="";
}
