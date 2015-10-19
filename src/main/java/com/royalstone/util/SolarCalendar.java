/*
 * Created on 2004-12-6
 *
 */
package com.royalstone.util;

import java.text.DecimalFormat;
import java.util.GregorianCalendar;

/**
 * SolarCalendar 表示阳历日期, 从GregorianCalendar 派生出来, 唯一的不同是覆盖了方法toString.
 * @author Mengluoyi
 *
 */
public class SolarCalendar extends GregorianCalendar
{
    /**
     * 此函数返回一个字串,与IDS中的DATETIME类型可以自动转换.
     * 格式: YYYY-MM-DD HH:MM:SS
     */
    public String toString()
    {
        int year 	= this.get( SolarCalendar.YEAR );
        int month 	= this.get( SolarCalendar.MONTH ) +1;
        int day 	= this.get( SolarCalendar.DAY_OF_MONTH );
        int hour 	= this.get( SolarCalendar.HOUR_OF_DAY );
        int minute 	= this.get( SolarCalendar.MINUTE );
        int second 	= this.get( SolarCalendar.SECOND );

    	DecimalFormat d4 = new DecimalFormat( "0000" );
    	DecimalFormat d2 = new DecimalFormat( "00" );
    	return d4.format( year ) + "-" + d2.format( month ) + "-" + d2.format(day)
    	+ " " + d2.format(hour) + ":" + d2.format(minute) + ":" + d2.format(second);
    }
    
	/**
	 * 把标准格式的日期时间解析为SolarCalendar对象
	 * 可以接受的日期格式包括: 
	 * 1) YYYY-MM-DD hh:mm:ss
	 * 2) YYYY-MM-DD hh:mm
	 * 3) YYYY-MM-DD hh
	 * 4) YYYY-MM-DD
	 * hour部分采用24小时制
	 * 该方法仅取参数的前19位进行解析,超出的部分将被忽略.
	 * @param datetime
	 * @return
	 * @throws InvalidDataException
	 */
	static public SolarCalendar parseSolarCalendar( String datetime ) throws InvalidDataException 
	{
		int year 	= 0;
		int month 	= 0;
		int day 	= 0;
		int hour 	= 0;
		int minute 	= 0;
		int second 	= 0;
		
		if( datetime == null ) throw new InvalidDataException( "NULL string!" );
		datetime = datetime.trim();
		int len = ( datetime.length() < 19 ) ? datetime.length() : 19;
		datetime = datetime.substring( 0, len );
		
		String[] ss = datetime.split( " " );
		
		String[] ss_date = ss[0].split( "-" );
		if( ss_date.length != 3 ) throw new InvalidDataException( "illegal datetime:" + datetime );
		
		try{
			year 	= Integer.parseInt( ss_date[0] );
			month 	= Integer.parseInt( ss_date[1] );
			day 	= Integer.parseInt( ss_date[2] );
			if( year <1000 || year >9000 ) throw new InvalidDataException( "year should be between 1000 and 9000:" + datetime );
			
			if( ss.length >1 ) {
				String[] ss_time = ss[1].split( ":" );
				if( ss_time.length >0 && ss_time[0] != null ) hour		= Integer.parseInt( ss_time[0] );
				if( ss_time.length >1 && ss_time[1] != null ) minute	= Integer.parseInt( ss_time[1] );
				if( ss_time.length >2 && ss_time[2] != null ) second	= Integer.parseInt( ss_time[2] );
			}
			
			SolarCalendar cal = new SolarCalendar();
			cal.set( year, month-1, day, hour, minute, second );
			
			/** 
			 * 2008-02-29	mengluoyi
			 * 毫秒位必须置为0,否则 MS-SQLSERVER 会出问题.
			 */
			cal.set( SolarCalendar.MILLISECOND, 0 );
			return cal;

		} catch ( NumberFormatException e ) {
			throw new InvalidDataException ( "illegal datetime:" + datetime );
		}
	}

	private static final long serialVersionUID = 200711211152L;
}
