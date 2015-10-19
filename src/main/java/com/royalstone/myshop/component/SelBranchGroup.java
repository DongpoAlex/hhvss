/*
 * 创建日期 2007-03-12
 *
 */
package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

/**
 * 本类参考SelBranchAll类，将
 * @param	shoptype=0	公司总部
 * @param	shoptype=10	城市公司
 * @param	shoptype=20	配送公司
 * @param	shoptype=11	门店
 * @param	shoptype=22	配送中心
 * @author bai
 *
 */
public class SelBranchGroup extends XComponent
{
	
    public SelBranchGroup( Token token,int[] shoptype ) throws Exception 
    {
    	super(token);
    	this.shoptype = shoptype;
    	init( "" );
    }
	
    public SelBranchGroup(Token token, int[] shoptype, String note ) throws Exception 
    {
    	super(token);
    	this.shoptype = shoptype;
    	init( note );
    }

    private void init( String note ) throws Exception
    {
    	this.elm_ctrl = new Element( "select" );
        Element elm_opt  = new Element("option");
        elm_opt.setAttribute( "value", "" );
        
        this.elm_ctrl.addContent( elm_opt.addContent( note ) );
        
        try{
            conn = openDataSource( token.site.getDbSrcName() );
        	if( this.isTypeSelected( 0) ) addCompany( 0 );
        	if( this.isTypeSelected(10) ) addCompany( 10 );
        	if( this.isTypeSelected(20) ) addCompany( 20 );
        	
        	if( this.isTypeSelected(11) ) addShop( 11 );
        	if( this.isTypeSelected(22) ) addShop( 22 );
        }catch(Exception e){
            throw e;
        }finally{
            closeDataSource( conn );
        }
    }
    
    
    /**
     * 如果机构是公司或者配送公司(0/10/20), 则调用此方法添加记录.
     * @param typeid
     * @throws SQLException
     */
    private void addCompany( int typeid ) throws SQLException
	{
        String sql       = " SELECT shopid, shopname FROM shop "
        	+ " WHERE shoptype = " + typeid
        	+ " ORDER BY shopid ";
        Statement st     = conn.createStatement();
        ResultSet rs     = st.executeQuery(sql);
        
        Element elm_opt;
        while(rs.next()){
            String id                      = rs.getString("shopid");
            String shopname                = rs.getString("shopname");
            id 		 = ( id==null )?	   "" : SqlUtil.fromLocal(id).trim();
            shopname = ( shopname==null )? "" : SqlUtil.fromLocal(shopname).trim();
            shopname = id + " " + shopname;

	        vec_shopid.add( id );
            elm_opt                        = new Element("option");
            elm_opt.setAttribute( "value", id );
            elm_opt.addContent( shopname );
            this.elm_ctrl.addContent(elm_opt);
        }
        rs.close();
        st.close();
    }

    public String codeString()
    {
    	String str = ( vec_shopid.size()==0 ) ? "" : (String) vec_shopid.get(0);
    	for( int i=1; i<vec_shopid.size(); i++ ) str += "," + (String)vec_shopid.get(i);
    	return str;
    }

    /**	
     * 查询数据库,生成一个包含门店信息的XML元素.
     * 如果机构是门店或者配送中心(11/22), 则调用此方法添加记录.
     * @throws SQLException
     * @throws NamingException
     */
    private void addShop( int typeid ) throws NamingException, SQLException
    {
    	Element elm_opt;

        String sql_grp = " SELECT c.shopid, c.shopname " +
        		" FROM shop c JOIN shop s ON (c.shopid=s.headshopid) " +
        		" WHERE s.shoptype = " + typeid +
        		" GROUP BY c.shopid,c.shopname ORDER BY 1 ";
        
        PreparedStatement stmt_grp = conn.prepareStatement( sql_grp );
        ResultSet rs = stmt_grp.executeQuery();
        while( rs.next() ){
            String groupid = rs.getString(1);
            String groupname = rs.getString(2);
            Element elm_grp = new Element( "option" );
            elm_grp.setAttribute( "style", "color:blue" );
            elm_grp.addContent( SqlUtil.fromLocal( groupname ) );
            
            
            String sql_shop_count=" SELECT COUNT(*) FROM shop "
            	+ " WHERE headshopid = ? "
    			+ " AND shoptype = " + typeid
				+ " ORDER BY 1 " ;
            PreparedStatement pstmt_count = conn.prepareStatement( sql_shop_count );
            pstmt_count.setString(1, groupid );
            ResultSet rs_count = pstmt_count.executeQuery();
            rs_count.next();
            int rows = rs_count.getInt(1);
            rs_count.close();
            pstmt_count.close();
            
            if( rows>0 ) {
            	elm_ctrl.addContent(elm_grp);
            }
            
            String sql_shop = " SELECT shopid, shopname, bookno FROM shop "
            	+ " WHERE headshopid = ? "
    			+ " AND shoptype = " + typeid
				+ " ORDER BY 1 " ;
            
            PreparedStatement pstmt = conn.prepareStatement( sql_shop );
            pstmt.setString(1, groupid );
            ResultSet rs_shop = pstmt.executeQuery();
            
            StringBuffer grpValue = new StringBuffer();
            StringBuffer grpNames = new StringBuffer();
            rows=0;
            while( rs_shop.next() ){
            	++ rows;
    	        elm_opt = new Element( "option" );

    	        String shopid = rs_shop.getString( "shopid" );
    	        String shopname = rs_shop.getString( "shopname" );
    	        String bookno   = rs_shop.getString( "bookno" );
    	        
    	        shopid 	= ( shopid==null )? 	"" : SqlUtil.fromLocal( shopid ).trim();
    	        shopname = ( shopname==null )? 	"" : SqlUtil.fromLocal( shopname ).trim();
    	        bookno 	= ( bookno==null )? 	"" : SqlUtil.fromLocal( bookno ).trim();
    	        vec_shopid.add( shopid );
    	        
    	        if( rows==1 ){
    	        	grpValue.append(shopid);
    	        	grpNames.append(shopname);
    	        }else{
    	        	grpValue.append(","+shopid);
    	        	grpNames.append(","+shopname);
    	        }
    	        
    	        elm_opt.setAttribute( "value", shopid );
    	        elm_opt.setAttribute( "display", shopname );
    	        elm_opt.setAttribute( "bookno", bookno );
    	        elm_opt.setAttribute("style","padding-left:6px;");
    	        shopname = "　"+shopid + " " + shopname; 
    	        elm_opt.addContent( shopname );
    	        elm_ctrl.addContent(elm_opt);
    	        
    	        
            }
            rs_shop.close();
            pstmt.close();
            if( rows>0 ) {
            	elm_grp.setAttribute("value",grpValue.toString());
            	elm_grp.setAttribute("display",grpNames.toString());
            }
        }
        rs.close();
        stmt_grp.close();
    }
    
    private boolean isTypeSelected( int typeid )
    {
    	for ( int i=0; i< shoptype.length; i++ ) if( shoptype[i] == typeid ) return true;
    	return false;
    }
    

	private Connection conn = null;
    private int[] shoptype;
    private Vector vec_shopid = new Vector(0);
}
