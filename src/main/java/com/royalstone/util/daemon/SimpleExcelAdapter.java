package com.royalstone.util.daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import com.royalstone.util.Day;
import com.royalstone.util.SolarCalendar;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于输出一个EXCEL2002可以识别的XML文件.
 * @author mengluoyi	2008-03-29
 *
 */
public class SimpleExcelAdapter
{
	
	public SimpleExcelAdapter( )
	{
	}

	public int cookExcelFile( File file, ResultSet rs, String[] title, String sheet_name ) throws SQLException, IOException 
	{
		FileOutputStream fout = new FileOutputStream( file );
		PrintStream printer = new PrintStream( fout, false, code_set );
		
		printBookHead( printer );
		printSheetHead( printer, sheet_name );
		printTitle( printer, title );
		int rows = printRows( printer, rs );
		printSheetTail( printer );
		printBookTail( printer );
		
		fout.close();
		return rows;
	}
	
	private void printTitle( PrintStream printer, String[] title )
	{
		printer.println( "<Row>" );
		for( int i=0; i<title.length; i++ ) {
			printCell( printer, "String", title[i] );
		}
		printer.println( "</Row>" );
	}
	
	
	private int printRows( PrintStream printer, ResultSet rs ) throws SQLException, IOException
	{
		ResultSetMetaData metadata = rs.getMetaData();
		
        int rows = 0;
		while ( rs.next() ) {
			++ rows;
			
			printer.println( "<Row>" );
	        for( int i=1; i<= metadata.getColumnCount(); i++ ) {
	        	
	        	String value = rs.getString( i );
	        	String type  = "String";

	        	if( value == null || rs.wasNull() ) value = "";
	        	
	        	if( value.length()>0 )
	        	switch( metadata.getColumnType(i) ){
	        	
	        	/**
	        	 * EXCEL要求在String类型前面加一个单引号, 否则作为数字处理.
	        	 */
	        	case Types.CHAR:
	        	case Types.VARCHAR:
		        	value = SqlUtil.fromLocal( value ).trim();
		        	
					break;
	        	
				/**
				 * 类型为数字,则直接使用JDBC转换后的字串,不再另作处理.
				 */
				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.DECIMAL:
				case Types.FLOAT:
				case Types.DOUBLE:
				case Types.NUMERIC:
				case Types.REAL:
					type = "Number";
					break;
					
				// 类型:日期
				case Types.DATE:
					Date date = rs.getDate( i );
					SolarCalendar solar_date = new SolarCalendar();
					solar_date.setTimeInMillis( date.getTime() );
					value = ( new Day( solar_date ) ).toString();
					break;
					
				// 类型: 日期-时间
				case Types.TIME:
				case Types.TIMESTAMP:
					Timestamp stamp = rs.getTimestamp( i );
					SolarCalendar solar_time = new SolarCalendar();
					solar_time.setTimeInMillis( stamp.getTime() );
					value = solar_time.toString();

					break;
					
				default:
					value = value.trim();
				}
	        	
	        	printCell( printer, type, value );
	        	
	        }
			printer.println( "</Row>" );
			
		}
		return rows;
	}
	
	private void printBookHead( PrintStream printer )
	{
		printer.println( "<?xml version='1.0' encoding='" + code_set + "' ?>" );
		printer.println( "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\" >" );
		printer.println( "<OfficeDocumentSettings xmlns='urn:schemas-microsoft-com:office:office' />" );
		printer.println( "<ExcelWorkbook xmlns='urn:schemas-microsoft-com:office:excel' />" );
		printer.println( "<Styles><Style ss:ID='Default' ss:Name='Normal' /></Styles>" );
		printer.println( "" );
	}
	
	private void printBookTail( PrintStream printer )
	{
		printer.println( "</Workbook>" );
	}
	
	private void printSheetHead( PrintStream printer, String sheet_name )
	{
		printer.println( "<Worksheet ss:Name='" + sheet_name + "'>" );
		printer.println( "<Table>" );
	}
	
	private void printSheetTail( PrintStream printer )
	{
		printer.println( "</Table>" );
		printer.println( "</Worksheet>" );
	}
	
	private void printCell( PrintStream printer, String type, String value )
	{
		printer.print( "<Cell>" );
		printer.print( "<Data ss:Type=\"" + type + "\">" );
		value = value.replaceAll( "&", "&amp;" );
		value = value.replaceAll( "\"", "&quot;" );
		value = value.replaceAll( "<", "&lt;" );
		value = value.replaceAll( ">", "&gt;" );
		
		printer.print( value );
		printer.print( "</Data>" );
		printer.print( "</Cell>" );
	}

	static final public int ROWS_LIMIT = 30000;
    static private final String code_set = "utf-8";
}
