/*
 * 创建日期 2005-9-15
 */
package com.royalstone.myshop.basic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;

/**
 * @author lizhenghan
 */
public class DaemonSGroup extends XDaemon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost( HttpServletRequest request, HttpServletResponse response )throws ServletException, IOException
	{
		doGet( request, response );
	}
	
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		this.check4Gzip( request );
		Element elm_doc = new Element( "xdoc" );
		Connection conn = null;
		
		try {
			if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            
            conn = openDataSource( token.site.getDbSrcName() );
            this.setDirtyRead(conn);
            
            String majorid = request.getParameter( "majorid" );
            String isOld= request.getParameter( "isOld" );
            if( majorid == null || majorid.length() == 0 ) throw new InvalidDataException( "majorid not set! " );
            Element elm;
			if(isOld!=null && isOld.equalsIgnoreCase("true")){
            	OldMajorid info = new OldMajorid( conn, majorid );
            	elm = info.toElement();
            }else{
            	Majorid info = new Majorid( conn, majorid );
            	elm = info.toElement();
            }
            
	    	elm_doc.addContent( new Element( "xout" ).addContent(elm) );
			elm_doc.addContent( new XErr( 0, "OK" ).toElement() );

		} catch (SQLException e) {
			elm_doc.addContent( new XErr( e.getErrorCode(), e.getMessage() ).toElement() ); 
        } catch (Exception e) {
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
        } finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}
}

class Majorid
{
	public Majorid( Connection conn, String majorid ) throws SQLException
	{
		String sql = " SELECT categoryid id, categoryname name, deptlevelid, taxrate, note "
			+ " FROM category WHERE categoryid ="+majorid;
		
		Statement st = conn.createStatement();
		ResultSet rs 	= st.executeQuery( sql );
        if( rs.next() ){	
        	XResultAdapter adapter = new XResultAdapter( rs );
        	elm_majorid = adapter.getElement4CurrentRow("sgroup" );
        }
        rs.close();
        st.close();
        if( elm_majorid == null ) throw new SQLException( "无此课类部门:" + majorid, "NOT FOUND", 100 );
    }
    
    public Element toElement()
    {
        return elm_majorid;
    }
    
    private Element elm_majorid = null;
}

class OldMajorid
{
	public OldMajorid( Connection conn, String majorid ) throws SQLException
	{
		String sql = " SELECT categoryid id, categoryname name, deptlevelid, taxrate, note "
			+ " FROM category_bak WHERE categoryid ="+majorid;
		
		Statement st = conn.createStatement();
		ResultSet rs 	= st.executeQuery( sql );
        if( rs.next() ){	
        	XResultAdapter adapter = new XResultAdapter( rs );
        	elm_majorid = adapter.getElement4CurrentRow("sgroup" );
        }
        rs.close();
        st.close();
        if( elm_majorid == null ) throw new SQLException( "无此课类部门:" + majorid, "NOT FOUND", 100 );
    }
    
    public Element toElement()
    {
        return elm_majorid;
    }
    
    private Element elm_majorid = null;
}