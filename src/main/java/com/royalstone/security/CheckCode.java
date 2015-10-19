package com.royalstone.security;


/**
 * CheckCode 用于生成校验码. 调用此类, 可以把一个整数转换成一个4位的String.
 */
public class CheckCode 
{

	public CheckCode( int code )
	{
		this.code = code;
	}
	
	/**
	 * @return 4 位的String. 
	 */
	public String toString()
	{
		byte[] b = new byte[4];
		int n = ( code>=0 ) ? code : -code;
		for( int i=0; i< b.length; i++ ) {
			b[i] = map[n%32];
			n /= 32;
		}
		return new String(b);
	}
	
	/**
	 * 判断两个CheckCode 对象是否相等. 只要其4位的表达式相同, 不论数字是否相等,都认为是一样的CheckCode.
	 * @param c
	 * @return
	 */
	public boolean equals( CheckCode c )
	{
		String s1 = this.toString();
		String s2 = c.toString();
		return ( s1.equals(s2) );
	}
	
	public static void main(String[] args){
		
	}
	
	static public String getCode(){
		byte[] b = new byte[4];
		for( int i=0; i< b.length; i++ ) {
			int n = 1;
			b[i] = map[n%32];
		}
		return new String(b);
	}
	final private int code;
	final static private byte[] map = {
			'A', 'B', 'C', 'D', 'E', '#', 'C', 'H', 'J', 'K', 
			'L', 'M', 'N', 'P', 'Q', 'R', '4', 'T', 'Q', 'V', 
			'M', 'X', 'Y', 'Z', '2', '3', '4', 'X', '6', '7', 
			'8', '9' };
}

