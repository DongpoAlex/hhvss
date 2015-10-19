/*
 * Created on 2005-1-17
 *
 */
package com.royalstone.util.component;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.royalstone.security.Token;

/**
 * @author Mengluoyi
 *
 */
public class XComponent 
{
	
	/**
	 * @param token
	 */
	public XComponent(Token token) {
		super();
		this.token = token;
	}

	public static Connection openDataSource ( String dbname ) throws NamingException, SQLException
	{
	    Context initCtx = new javax.naming.InitialContext();
	    javax.sql.DataSource ds = (javax.sql.DataSource)initCtx.lookup("java:comp/env/" + dbname );
        Connection conn = ds.getConnection();	  
        
        /**
         * 2008-03-29	mengluoyi
         * 数据库封锁级置为CR, 适应大多数应用的需要,避免因为RR而降低访问效率.
         * SET ISOLATION TO COMMITTED READ
         */
        // conn.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );

        return conn;
	}

	public static void closeDataSource( Connection conn )
	{
		if ( conn != null )
		try {
			conn.close();
		} catch (SQLException e) {
			// do nothing.
		}
	}

    public void setAttribute( String name, String value )
    {
        elm_ctrl.setAttribute(name, value);
    }
    
    public Element toElement(){
    	return this.elm_ctrl;
    }
    
    public String toString()
    {
    	XMLOutputter outputter = new XMLOutputter( "  ", true, "UTF-8" );
    	outputter.setTextTrim(true);
    	return outputter.outputString( elm_ctrl ) ;
    }
    
    final static public String toString(Element elm)
    {
    	XMLOutputter outputter = new XMLOutputter( " ", true, "UTF-8" );
    	outputter.setTextTrim(true);
    	return outputter.outputString( elm ) ;
    }
	
    protected Element 	elm_ctrl = null;
    final protected Token token;
}
