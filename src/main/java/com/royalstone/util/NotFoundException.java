/*
 * Created on 2005-6-24
 *
 */
package com.royalstone.util;

/**
 * @author meng
 *
 */
public class NotFoundException extends RuntimeException {

	/**
	 * @param msg
	 */
	public NotFoundException( String msg ) {
		super( msg ) ;
	}

	public static void main(String[] args) {
		NotFoundException e = new NotFoundException( "Task not found" );
		System.out.println( e.getMessage() );
	}

	private static final long serialVersionUID = 20060719L;
}
