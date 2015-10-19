package com.royalstone.util.daemon;

import com.royalstone.util.Day;
import com.royalstone.util.InvalidDataException;

public class ValueAdapter {

    public static String toString4String( String ss )
    {
    	return "'" + ss.replaceAll( "'", "''" ) + "'";
    }
    public static String toSafeString( String ss )
    {
    	return  ss.replaceAll( "'", "''" );
    }
    
    public static String toString4String(String[] arr_values)
    {
        String str = "";
        if( arr_values.length>0 ) 
        	str += "'" + arr_values[0].replaceAll( "'", "''" ) + "'";
        for( int i=1; i<arr_values.length; i++ ) 
        	str += "," + "'" + arr_values[i].replaceAll( "'", "''" ) + "'";
        return str;
    }
    
    /**
     * @param yyyymmdd
     * @return
     * @throws InvalidDataException
     */
    public static Day std2Day( String yyyymmdd ) throws InvalidDataException
    {
        int y,m,d;
        Day day;
        if( yyyymmdd == null || yyyymmdd.length() < 8 )throw new InvalidDataException( "Invalid data:" + yyyymmdd );
        String [] str=yyyymmdd.split("-");
        if ( str.length != 3 ) throw new InvalidDataException( "Invalid data:" + yyyymmdd );

        try{
            y = Integer.parseInt(str[0]);
            m = Integer.parseInt(str[1]);
            d = Integer.parseInt(str[2]);
        }catch( NumberFormatException e){
            throw new InvalidDataException(e);
        }

        if( y < 1900 ) throw new InvalidDataException( "year before 1900 is not accepted." );
        if( y > 9001 ) throw new InvalidDataException( "year after 9000 is not accepted." );
        try{
            day = new Day(y,m,d);
        }catch(IllegalArgumentException e){
            throw new InvalidDataException(e);
        }
        return day;
    }
    
    /**
     * @param yyyy-mm-dd
     * @return
     * @throws InvalidDataException
     */
    public static String std2mdy( String yyyymmdd ) throws InvalidDataException
    {
        if( yyyymmdd == null ||yyyymmdd.length() < 8)throw new InvalidDataException( "Invalid data:" + yyyymmdd );
        return "to_date('"+yyyymmdd+"','yyyy-mm-dd')";
    }
    
	/**
	 * @param value	string for value.
	 * @return
	 * @throws InvalidDataException
	 */
	public static double parseDouble( String value ) throws InvalidDataException
	{
		if( value == null || value.length() == 0 ) return 0D;
		String str = "";
		for( int i=0; i<value.length(); i++ ) {
			char c = value.charAt(i);
			if( c == ',' )continue;
			else if ( c == '.' || c == '-' || c == '+' ) str += c;
			else if ( c>= '0' && c<= '9' ) str += c;
			else throw new InvalidDataException ( "Illegal string for value:" + str );
		}
		return Double.parseDouble( str );
	}
	
	/**
	 * @param s
	 * @return	true 传入参数s是有效的整数.
	 */
	public static boolean isInteger( String s )
	{
		boolean valid = false;
		if( s == null || s.length() == 0 ) return false;
        try{
    		Integer.parseInt(s);
    		valid = true;
        }catch( NumberFormatException e){
        	valid = false;
        }

		return valid;
	}
	/**
	 * 判断 s 是否是纯数字，负数小数都返回 false
	 * @param s
	 * @return
	 */
	public static boolean isNumber( String s )
	{
		boolean valid = true;
		if( s == null || s.length() == 0 ) return false;
		char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			char d = c[i];
			if ( d<48 || d>57 )
			{
				valid = false;
				break;
			}
		}
		
		return valid;
		
	}
	public static boolean isDecimal( String s )
	{
		boolean valid = false;
		if( s == null || s.length() == 0 ) return false;
        try{
    		parseDouble(s);
    		valid = true;
        }catch( NumberFormatException e){
        	valid = false;
        } catch (InvalidDataException e) {
        	valid = false;
		}

		return valid;
	}
    
	public static boolean isDate(String s) 
	{
		boolean valid = true;
		try {
			std2Day( s );
		} catch (InvalidDataException e) {
			valid = false;
		}
		return valid;
	}
}
