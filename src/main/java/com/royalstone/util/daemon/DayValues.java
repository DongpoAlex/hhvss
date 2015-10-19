/*
 * Created on 2004-10-26
 *
 */
package com.royalstone.util.daemon;

import com.royalstone.util.Day;
import com.royalstone.util.InvalidDataException;

/**
 * @author Mengluoyi
 *
 */
public class DayValues
{

    public DayValues( Day startday, Day endday ) throws InvalidDataException
    {
        int days = endday.daysBetween(startday);
        if( days <0 )throw new InvalidDataException( "endday must be after startday." ); 
        start = new Day( startday );
        end   = new Day( endday );
    }
    
    public int getDays()
    {
        return end.daysBetween(start);
    }
    
    public String toString()
    {
        return " BETWEEN " + day2mdy(start) + " AND " + day2mdy( end ) + " ";
    }
    
    public String toString4MDY () throws InvalidDataException
    {
        int days = end.daysBetween(start);
        if( days >500 )throw new InvalidDataException( "Days between startday and endday must be less than 500." ); 
        Day d = new Day(start);
        String s = day2mdy(d);
        while ( end.daysBetween( d ) >0 ){
            d.advance(1);
            s += "," + day2mdy(d);
        }
        return s;
    }
    
    public static String day2mdy( Day d )
    {
        return " mdy(" + d.getMonth() + "," + d.getDay() + "," + d.getYear() + ") ";
    }
    
    private Day start, end;
}
