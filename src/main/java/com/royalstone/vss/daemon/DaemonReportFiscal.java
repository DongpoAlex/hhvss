/*
 * Created on 2006-08-17
 *
 */

package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.report.CurrentPayAdvice;
import com.royalstone.vss.report.PayAdvice;

/**
 * 此模块用于查询以下单据的目录: 订单, 验收单, 退货通知单, 退货单.
 * @param  reportname  单据名称. 可以取以下值: payadvice.
 * @author mengluoyi
 */
public class DaemonReportFiscal extends XDaemon
{
    
	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
		doPost(request, response);
	}

    public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	this.check4Gzip(request);
    	request.setCharacterEncoding( "UTF-8" );
	    Element elm_doc = new Element( "xdoc" );
    	Element elm_out = new Element( "xout" );
    	
    	Connection conn = null;
	    try {
	       	if (!isSessionActive(request))
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	        Token token = this.getToken(request);
	        if (token == null)
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	       
            conn = openDataSource( token.site.getDbSrcName() );
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED );
            HashMap parms = new HashMap(request.getParameterMap());

            /**
             * 从 session 取venderid
             */
            
            if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
        	}
            
            String focus = request.getParameter( "focus" );
            if( focus == null || focus.length() == 0 ) throw new InvalidDataException( "focus is invalid!" );

            /**
             * 历史结算限额
             */
            if( focus.equalsIgnoreCase( "payadvice_history" ) ){
            	/*
            	Day sysdate = new Day();
            	int accmonth_min = 100 * sysdate.getYear() + 1;
            	int accmonth_max = 100 * sysdate.getYear() + sysdate.getMonth();
            	String[] arr_min = { "" + accmonth_min };
            	String[] arr_max = { "" + accmonth_max };
            	parms.put( "accmonth_min", arr_min );
            	parms.put( "accmonth_max", arr_max );
            	*/
            	PayAdvice advice = new PayAdvice( conn, parms );
                Element elm_rpt = advice.toElement();
                elm_out.addContent( elm_rpt );
            	
            }else if( focus.equalsIgnoreCase( "payadvice_current" ) ) {
            	
            	/**
            	 * 当月结算限额
            	 */
            	CurrentPayAdvice advice = new CurrentPayAdvice();
                Element elm_rpt = advice.getCurrentPayAdvice(conn, parms );
                elm_out.addContent( elm_rpt );
            }
            
			elm_doc.addContent( elm_out );
			elm_doc.addContent( new XErr( 0, "OK" ).toElement() );
			
		} catch (SQLException e) {
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
