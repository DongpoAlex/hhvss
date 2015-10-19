/*
 * Created on 2005-12-28
 *
 */
package com.royalstone.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import com.royalstone.util.InvalidDataException;

/**
 * @author meng
 *
 */
public class Workbook {

	public Workbook()
	{
		elm = new Element( "Workbook" );
		elm.setNamespace( namespace );
		elm.addNamespaceDeclaration( space_office );
		elm.addNamespaceDeclaration( space_excel );
		elm.addNamespaceDeclaration( space_style );
		
		Styles ss = new Styles();
		elm.addContent( ss.toElement() );
	}
	
	public void addSheet( Worksheet sheet )
	{
		elm.addContent( sheet.toElement() );
	}
	
	/**
	 * 此方法向Excel
	 * @param rs
	 * @param sheet_name
	 * @param title
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public void addSheet( ResultSet rs, String sheet_name, String[] title ) throws SQLException, InvalidDataException
	{
		Worksheet sheet = new Worksheet( sheet_name );
		Table table = new Table( rs, title );
		sheet.addTable( table );
		addSheet( sheet );
	}
	
	public void addSheet( ResultSet rs, String sheet_name ) throws SQLException, InvalidDataException
	{
		Worksheet sheet = new Worksheet( sheet_name );
		Table table = new Table( rs );
		sheet.addTable( table );
		addSheet( sheet );
	}
	
	public Element toElement()
	{
		return elm;
	}
	
	/**
	 * 此方法把WorkBook中的内容送入文件中
	 * @param file	一个已经建好的文件对象
	 * @throws IOException
	 */
	public void toFile( File file ) throws IOException
	{
		FileOutputStream ostream = new FileOutputStream( file );
		XMLOutputter putter = new XMLOutputter( "", true, "UTF-8" );
		putter.output( new Document(elm), ostream );
		ostream.close();
	}
	
	private Element elm = new Element( "Workbook" );
	
	final public static Namespace namespace = Namespace.getNamespace( "urn:schemas-microsoft-com:office:spreadsheet" );
	final public static Namespace space_office = Namespace.getNamespace( "o", "urn:schemas-microsoft-com:office:office" );
	final public static Namespace space_excel = Namespace.getNamespace( "x", "urn:schemas-microsoft-com:office:excel" );
	final public static Namespace space_style = Namespace.getNamespace( "ss", "urn:schemas-microsoft-com:office:spreadsheet" );
}
