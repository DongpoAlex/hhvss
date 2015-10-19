package com.royalstone.vss.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.TokenException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
public class DaemonAddUsers extends XDaemon{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Connection conn = null;
		try {
			if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
			if( !this.isSessionActive( request) )throw new TokenException( PermissionException.LOGIN_PROMPT );    	
			conn = openDataSource(token.site.getDbSrcName());
			this.setDirtyRead(conn);
			Document doc = this.getParamDoc(request);
			Element elm_root = doc.getRootElement();
			Element elm_out = elm_root.getChild("xout");
			Element elm_list=elm_out.getChild("List");
			AddUsers au=new AddUsers(conn,elm_list);
			au.operation();
			elm_doc.addContent(new Element("xout"));
			elm_doc.addContent(new XErr(0, "ok").toElement());
		} catch (SQLException e) {
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage())
					.toElement());
			e.printStackTrace();
		} catch (Exception e) {
			elm_doc.addContent(new XErr(-1, e.getMessage()).toElement());
			e.printStackTrace();
		} finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}
    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
    	doPost( request, response );
	}
}

class AddUsers{
	public AddUsers( Connection conn,Element elm_list) throws SQLException{		
		this.conn=conn;
		this.elm_list=elm_list;
		this.roleid=getRoleid();
	}
	public void operation() throws SQLException{
		try{			
			conn.setAutoCommit(false);		
			List list = elm_list.getChildren("Vender");
			for (int k = 0; k < list.size(); k++) {
				this.new_userid=getMaxUserid()+1;
				Element record = (Element) list.get(k);
				String venderid=record.getChildText("venderid");
				String status=record.getChildText("status");
				int int_status=Status2Int(status);
				user_list(venderid,int_status);
				user_role();
				user_environment(venderid);
			}			
			conn.commit();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit( true );
		}
	}
	private void user_list(String venderid,int status) throws SQLException{
		
		String sql = "INSERT INTO user_list( userid, loginid, shopid, username, password ) " +
					 "values('"+this.new_userid+"','"+venderid+"','AAAA','"+venderid+"','"+venderid+"')";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();	
	}
	private void user_role() throws SQLException{
		String sql = "INSERT INTO user_role(userid,roleid) " +
					 "values("+this.new_userid+","+this.roleid+")";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private void user_environment(String venderid) throws SQLException{
		String sql = "INSERT INTO user_environment(userid,dataflag,datavalue) " 
					+ "values("
					+ this.new_userid + ",'venderid','"+venderid+"')";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private int Status2Int(String status){
		int int_status=0;
		if(status.equals("正常"))
			int_status=0;
		else if(status.equals("季节性"))
			int_status=1;
		else if(status.equals("拟淘汰"))
			int_status=2;
		else if(status.equals("淘汰"))
			int_status=3;
		return int_status;
	}
	private int getMaxUserid() throws SQLException{
		String sql = " SELECT MAX(userid) FROM user_list ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql );
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		stmt.close();
		return id;
	}
	private int getRoleid() throws SQLException{
		String sql = "SELECT roleid FROM role_list WHERE rolename='VENDER'";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery( sql );
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		stmt.close();
		return id;
	}
	private Element elm_list = new Element("List");
	final private Connection conn ;
	private int new_userid=0;
	private int roleid=0;	
}
