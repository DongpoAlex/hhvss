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
public class Demo {

	public static void main(String[] args) {
		
		test( 0.0 );

		test( 0.01 );
		test( 0.02 );
		test( 0.03 );
		test( 0.04 );
		test( 0.05 );
		test( 0.06 );
		test( 0.07 );
		test( 0.08 );
		test( 0.09 );
		System.out.println();

		test( 0.10 );
		test( 0.20 );
		test( 0.30 );
		test( 0.40 );
		test( 0.50 );
		test( 0.60 );
		test( 0.70 );
		test( 0.80 );
		test( 0.90 );
		System.out.println();

		test( 0.11 );
		test( 0.19 );
		test( 0.91 );
		test( 0.99 );
		System.out.println();

		test( 1.00 );
		test( 1.01 );
		test( 1.91 );
		test( 1.99 );
		test( 9.99 );
		test( 99.99 );
		test( 999.99 );
		test( 9999.99 );
		test( 99999.99 );
		test( 9999999999.99 );
		test( 99999999999.99 );
		test( FiscalValue.MAX_VALUE );
		System.out.println();

		
		test( 10 );
		test( 20 );
		test( 30 );
		test( 40 );
		test( 50 );
		test( 60 );
		test( 70 );
		test( 80 );
		test( 90 );
		test( 99 );
		test( 100 );
		test( 200 );
		test( 3000 );
		test( 40000 );
		test( 500000 );
		test( 6000000 );
		test( 70000000 );
		test( 800000000 );
		test( 9000000000L );
		test( 90000000000L );
		System.out.println();
		
		test( 1.10 );
		test( 20.30 );
		test( 10.01 );
		test( 100.01 );
		test( 1000.01 );
		test( 10000.01 );
		test( 10000.10 );
		test( 10000.50 );
		test( 10000.66 );
		
		test( 11 );
		test( 101 );
		test( 1001 );
		test( 10001 );
		test( 100001 );
		test( 1000001 );
		test( 10000001 );
		test( 100000001 );
		test( 1000000001 );
		test( 10000000001L );
		test( 10000000001.03D );
		test( 10000000001.89D );
		System.out.println();
		
		test( 110 );
		test( 1010 );
		test( 10010 );
		test( 100010 );
		test( 1000010 );
		test( 10000010 );
		test( 100000010 );
		test( 1000000010 );
		test( 10000000010L );
		test( 10000000010.01D );
		test( 10000000010.47D );
		System.out.println();
		
		test( 10000000.99D );
		test( 10000001.99D );
		test( 10000009.99D );
		test( 90000000.99D );
		test( 90000001.99D );
		test( 90000009.99D );
		test( 99999999.99D );
		System.out.println();
		
		test( 100000000.99D );
		test( 100000001.99D );
		test( 100000009.99D );
		System.out.println();
		
		test( 1000000000.99D );
		test( 1000000001.99D );
		test( 1000000009.99D );
		System.out.println();

		test( 10000000000.99D );
		test( 10000000001.99D );
		test( 10000000009.99D );
		System.out.println();

		test( 88888888888.99D );
		test( 88888888888.98D );
		test( 80808080808.99D );
		test( 80808080808.98D );
		test( 80080080080.99D );
		test( 80080080080.98D );
		test( 80008000888.99D );
		test( 80008000888.98D );
		test( 88008008008.99D );
		test( 88800080008.98D );
		System.out.println();
		
		test( 1234567 );
		test( 1234007 );
		test( 1230067 );
		test( 1200567 );
		test( 1230567 );
		test( 1204567 );
		test( 1030567 );
		test( 1034007 );
		
		test( 0.0045 );
		test( 0.0049 );
		test( 0.005 );
		test( 0.0455 );
		test( 0.055 );
		test( 0.555 );
		test( 5.555 );
		test( 99.999 );
	}
	
	public static void test ( double v )
	{
		FiscalValue f = new FiscalValue( v );
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		System.out.println( df.format( v )  + " -> " + f.toChinese() );
	}
	
	public static void test ( float v )
	{
		FiscalValue f = new FiscalValue( v );
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		System.out.println( df.format( v )  + " -> " + f.toChinese() );
	}
	
	public static void test ( long v )
	{
		FiscalValue f = new FiscalValue( v );
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		System.out.println( df.format( v )  + " -> " + f.toChinese() );
	}
	

}
