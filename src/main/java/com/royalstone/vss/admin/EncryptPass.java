package com.royalstone.vss.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import com.jivesoftware.util.StringUtils;

public class EncryptPass {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args)  
	{
		if( args.length != 1 ) {
			System.err.println( "Usage: EncryptPass " + " passwd_file" );
			return;
		}
		
		String pass_file = args[0];
		
		File file = new File ( pass_file );
		if( ! file.exists() ) {
			System.err.println( "File not exists, please check your file path." );
			return;
		}
		
		try {
			FileReader freader = new FileReader( file );
			LineNumberReader lreader = new LineNumberReader( freader );
			while ( true ) {
				String line = lreader.readLine();
				if( line == null ) break;
				
				String[] arr_str = line.split( "\t", 2 );
				if( arr_str != null && arr_str.length == 2){
					String id = arr_str[0];
					String pass = arr_str[1];
					System.out.println( id + "\t" + StringUtils.hash( pass ) );
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println( e.toString() );
		} catch (IOException e) {
			System.err.println( e.toString() );
		}
	}

}
