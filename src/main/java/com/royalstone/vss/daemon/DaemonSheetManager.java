/*
 * Created on 2006-07-31
 *
 */

package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.detail.ShowElecInstall;
import com.royalstone.vss.detail.ShowSalePick;
import com.royalstone.vss.manager.OrderPurchaseManager;
import com.royalstone.vss.manager.PaymentnoteManager;
import com.royalstone.vss.manager.PaymentsheetManager;
import com.royalstone.vss.manager.PurchaseManager;
import com.royalstone.vss.manager.RetnoticeManager;

/**
 * 此模块用于确认新订单.
 * @param  sheetname  单据名称. 可以取以下值: purchase,receipt,retnotice,ret.
 * @author mengluoyi
 *
 */
public class DaemonSheetManager extends XDaemon
{
    
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
	    doGet(request, response);
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	this.check4Gzip(request);
    	request.setCharacterEncoding( "UTF-8" );
	    Element elm_doc = new Element( "xdoc" );
    	
    	Connection conn = null;
	    try {
	       	if (!isSessionActive(request))
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	        Token token = this.getToken(request);
	        if (token == null)
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	       
            
            conn = openDataSource( token.site.getDbSrcName() );
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED );
            
            String sheetname = request.getParameter( "sheetname" );
            if( sheetname == null || sheetname.length() == 0 ) throw new InvalidDataException( "sheetname not set! " );

            String operation = request.getParameter( "operation" );
            if( operation == null || operation.length() == 0 ) throw new InvalidDataException( "operation not set! " );

            Map map = request.getParameterMap();
            
            HashMap parms = new HashMap( map );
            
            /**
             * 供应商只可以查看自己的单据.
             * 如果访问者以供应商身份登录, 应添加针对供应商ID的过滤条件.
             */
            if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
        	}
            
            /**
             * 订单确认
             */
            if( sheetname.equalsIgnoreCase( "purchase" ) && operation.equalsIgnoreCase( "confirm" ) ){
        		PurchaseManager manager = new PurchaseManager( conn );
        		int rows = manager.confirm( parms );
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            } 
            /**
             * 订单拒绝
             */
            if( sheetname.equalsIgnoreCase( "purchase" ) && operation.equalsIgnoreCase( "refuse" ) ){
        		PurchaseManager manager = new PurchaseManager( conn );
        		int rows = manager.refuse( parms );
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            } 
            /**
             * 订单已被阅读
             */
            if( sheetname.equalsIgnoreCase( "orderpurchase" ) && operation.equalsIgnoreCase( "read" ) ){
            	OrderPurchaseManager manager = new OrderPurchaseManager( conn );
        		int rows = manager.setSheetRead( parms );
        		//if( rows == 0 ) throw new SQLException( "请检查单据号及单据的状态" , "", 100 );
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            } 
            if( sheetname.equalsIgnoreCase( "retnotice" ) && operation.equalsIgnoreCase( "read" ) ){
            	RetnoticeManager manager = new RetnoticeManager( conn );
        		int rows = manager.setSheetRead( parms );
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            }
            if( sheetname.equalsIgnoreCase( "retnotice" ) && operation.equalsIgnoreCase( "confirm" ) ){
            	RetnoticeManager manager = new RetnoticeManager( conn );
        		int rows = manager.setSheetConfirm( parms );
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            }
            if( sheetname.equalsIgnoreCase( "orderpurchase" ) && operation.equalsIgnoreCase( "confirm" ) ){
            	OrderPurchaseManager manager = new OrderPurchaseManager( conn );
        		int rows = manager.confirmSheet( parms );
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            } 
            if( sheetname.equalsIgnoreCase( "orderpurchase" ) && operation.equalsIgnoreCase( "refuse" ) ){
            	OrderPurchaseManager manager = new OrderPurchaseManager( conn );
        		int rows = manager.refuseSheet( parms );
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            } 
            
            //带货通知单阅读
            if( sheetname.equalsIgnoreCase( "salepick" ) && operation.equalsIgnoreCase( "read" ) ){
            	String[] ss = (String [] ) map.get( "sheetid" );
            	if( ss != null && ss.length >0 ) {
            		String sheetid = ss[0];
            		ShowSalePick manager = new ShowSalePick(conn,sheetid);
            		manager.updateStatus(0, 1, sheetid);
            	}
            }
            //电器通知单阅读
            if( sheetname.equalsIgnoreCase( "elecinstall" ) && operation.equalsIgnoreCase( "read" ) ){
            	String[] ss = (String [] ) map.get( "sheetid" );
            	if( ss != null && ss.length >0 ) {
            		String sheetid = ss[0];
            		ShowElecInstall manager = new ShowElecInstall(conn,sheetid);
            		conn.setAutoCommit(false);
            		if(manager.updateStatus(0, 1, sheetid)==1){
            			//写日志
            			manager.insertLog(sheetid);
            		}
            		conn.commit();
            		conn.setAutoCommit(true);
            	}
            }
            
          //电器取消单阅读
            if( sheetname.equalsIgnoreCase( "elecinstallcancel" ) && operation.equalsIgnoreCase( "read" ) ){
            	String[] ss = (String [] ) map.get( "sheetid" );
            	if( ss != null && ss.length >0 ) {
            		String sheetid = ss[0];
            		ShowElecInstall manager = new ShowElecInstall(conn,sheetid);
            		conn.setAutoCommit(false);
            		if(manager.updateCancelStatus(0, 1, sheetid)==1){
            			//写日志
            			manager.insertCancelLog(sheetid);
            		}
            		conn.commit();
            		conn.setAutoCommit(true);
            	}
            }
            /**
             * 付款单开票申请
             */
            if( sheetname.equalsIgnoreCase( "paymentnote" ) && operation.equalsIgnoreCase( "req" ) ){
            	String venderid=token.getBusinessid();
            	PaymentnoteManager manager = new PaymentnoteManager( conn );
            	conn.setAutoCommit(false);
        		int rows = manager.confirmSheet( parms,venderid );
        		conn.commit();
        		conn.setAutoCommit(true);
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            } 
            if( sheetname.equalsIgnoreCase( "paymentsheet" ) && operation.equalsIgnoreCase( "req" ) ){
            	String venderid=token.getBusinessid();
            	PaymentsheetManager manager = new PaymentsheetManager( conn );
            	conn.setAutoCommit(false);
        		int rows = manager.confirmSheet( parms,venderid );
        		conn.commit();
        		conn.setAutoCommit(true);
    			elm_doc.addContent( new XErr( 0, "此项操作修改的单据数量:" + rows ).toElement() );
            } 
            
            
		} catch (SQLException e) {
			try {
				conn.rollback();
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}
			//e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
        } catch (Exception e) {
        	//e.printStackTrace();
        	elm_doc.addContent( new XErr( -1, e.toString() ).toElement() ) ; 
        } finally {
	    	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}
    
    private static final long serialVersionUID = 20060731L;
}
