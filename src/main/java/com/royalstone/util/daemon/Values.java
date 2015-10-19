/*
 * Created on 2004-10-20
 *
 */
package com.royalstone.util.daemon;

import java.util.Vector;

/**	
 * Values 主要用于SQL 查询中过滤条件有多个可选值的情况.
 * 其成员函数 toString  和 toString4String 用于生成 IN 子句中的字串.
 * 字段类型为INT 时使用toString, 字段类型如果为 CHAR(n) 则使用toString4String .
 * @author Mengluoyi
 *
 */
public class Values
{

    /**
     * Constructs an instance of Values.
     */
    public Values(){}
    
    /**
     * Constructs an instance of Values.
     * @param value_lst
     */
    public Values( int[] value_lst )
    {
        if( value_lst == null )return;
        if ( value_lst.length == 0 )return;
        arr_values = new String[ value_lst.length ];
        for( int i=0; i<value_lst.length; i++ ) arr_values[i] = "" + value_lst[i];
    }

    /**Constructs an instance of Values.
     * @param value_lst
     */
    public Values( String[] value_lst )
    {
        if( value_lst == null )return;
        if ( value_lst.length == 0 )return;
        int len =0;
        for( int i=0; i<value_lst.length; i++ ) if( value_lst[i] != null && value_lst[i].length()>0 ) len++ ;
        arr_values = new String[ len ];
        for( int i=0, j=0; i<value_lst.length; i++ ) 
        	if( value_lst[i] != null && value_lst[i].length()>0 ) arr_values[j++] = value_lst[i];
    }
    
    public Values( Vector value )
    {
        if( value == null || value.size() == 0 ) return;
        arr_values = new String[ value.size() ];
        for( int i=0; i<value.size(); i++ ) arr_values[i] = (String) value.get(i);
    }
    
    /**	从字符串中解析可选值
     * @param value_str	包含可选值的字符串
     */
    public Values( String value_str )
    {
        if( value_str == null )return;
        String[] arr = value_str.trim().split( "," );
        if ( arr != null ) {
            Vector v = new Vector();
            for( int i=0; i<arr.length; i++ )if( arr[i].trim().length()>0 ) v.add( arr[i].trim() );
            String[] tmp = new String[0];
            String[] slist = (String[]) v.toArray( tmp ); 
            if( slist != null ) arr_values = slist;
        }
    }
    
    /**	该函数用于合并操作. 执行此函数后, 对象v 中的可选值将合并到调用者的可选值中.
     * @param v	an instance of Values.
     */
    public void merge( Values v )
    {
        String[] arr_str = new String[ arr_values.length + v.arr_values.length ];
        for( int i=0; i<arr_values.length; i++ ) arr_str[i] = arr_values[i];
        for( int i=arr_values.length, j=0; i< (arr_values.length + v.arr_values.length ); i++, j++ ) 
            arr_str[i] = v.arr_values[j];
        arr_values = arr_str;
    }
    
    public String join( String seperator )
    {
        if( arr_values.length == 0 ) return "";
        else {
            String str = arr_values[0];
            for( int i=1; i<arr_values.length; i++ ) str += seperator + arr_values[i];
            return str;
        }
    }
    
    /**	数据库中字段类型如果为INT,则用此函数生成IN 子句的字串.
     * @return IN子句中用的字串
     */
    public String toString()
    {
        String str = "";
        if( arr_values.length>0 ) str += arr_values[0];
        for( int i=1; i<arr_values.length; i++ ) str += "," + arr_values[i];
        return str;
    }
    
    /**	数据库中字段类型如果为CHAR(n),则用此函数生成IN 子句的字串.
     * @return IN子句中用的字串	
     */
    public String toString4String()
    {
        String str = "";
        if( arr_values.length>0 ) 
        	str += "'" + arr_values[0].replaceAll( "'", "''" ) + "'";
        for( int i=1; i<arr_values.length; i++ ) 
        	str += "," + "'" + arr_values[i].replaceAll( "'", "''" ) + "'";
        return str;
    }
    
    static public String toString4in(String[] arr_values)
    {
        String str = "";
        if( arr_values.length>0 ) 
        	str +=  "'" + arr_values[0].replaceAll( ",", "','" ) + "'";
        for( int i=1; i<arr_values.length; i++ ) 
        	str += "," + "'" + arr_values[i].replaceAll( "'", "''" ) + "'";
        return str;
    }
    
    public static String toString4String( String ss )
    {
    	return "'" + ss.replaceAll( "'", "''" ) + "'";
    }
    
    public static String toString4Like( String ss )
    {
    	return "'%" + ss.replaceAll( "'", "''" ) + "%'";
    }
    
    public static String toString4LikeL( String ss )
    {
    	return "'%" + ss.replaceAll( "'", "''" ) + "'";
    }
    
    public static String toString4LikeR( String ss )
    {
    	return "'" + ss.replaceAll( "'", "''" ) + "%'";
    }
    
    public String toString4like(String column)
    {
    	String str = "";
    	
    	if( column == null || column.length()==0 )
    		return "";
    	if( arr_values.length>0 )
    		str += column +" LIKE '%"+arr_values[0].replaceAll("'","''" )+"%'";
    	for( int i=1; i<arr_values.length; i++ ) 
        	str += " OR " + column +" LIKE '%" + arr_values[i].replaceAll( "'", "''" ) + "%'";
    	return str;
    }
    
    /**
     * @return	可选值的数目.
     */
    public int length()
    {
        return arr_values.length;
    }
    
    public int count()
    {
        return arr_values.length;
    }

    /**
     *  <code>arr_values</code>该变量用于存放多个可选值.
     */
    private String[] arr_values = new String[0];
}
