/*
 * Created on 2006-08-18
 *
 */

package com.royalstone.vss.daemon;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.util.excel.Workbook;
import com.royalstone.vss.detail.DownloadPaySheet;
import com.royalstone.vss.detail.DownloadPaymentSheet;

/**
 * 此模块用于导出付款单/付款申请单的明细. 包括已付款/未付款的付款单都可以查询.
 * 输出内容为一个EXCEL可以识别的XML文件. 要求EXCEL版本为EXCEL2002以上.
 * @author mengluoyi
 * @param	sheetname	单据名称
 * @param	sheetid		单据号
 * @param	venderid	供应商编码(从session取用户环境)
 *
 */
public class DaemonDownloadPayment extends XDaemon
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
	    File fdown = File.createTempFile( "rpt", "xls" );
    	Connection conn = null;
	    try {
	    	
	    	if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           

            conn = openDataSource( token.site.getDbSrcName());
            
            /**
             * 查询单据明细必须设置 ISOLATION: COMMITTED_READ.
             */
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );
            
            String sheetname = request.getParameter( "sheetname" );
            if( sheetname == null || sheetname.length() == 0 ) throw new InvalidDataException( "sheetname not set! " );

            String sheetid = request.getParameter( "sheetid" );
            if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid not set! " );
            
            /**
             * 获得供应商id。设置查询条件
             */
            String venderid = token.getBusinessid();
             
            /**
             * sheetname 为paymentnote, 表示向后台索取对象为付款单信息.
             */
            if( sheetname.equalsIgnoreCase( "paymentnote" ) ){
            	DownloadPaySheet downloader = new DownloadPaySheet( conn, sheetid,token );
            	
            	/**
            	 * 为了保障供应商业务数据的安全性, 核对安全令牌中的供应商编码与单据的供应商编码是否一致.
            	 */
            	String venderid_pm = downloader.getPaymentnoteVenderid(sheetid).trim();
            	if ( ! venderid_pm.equals(venderid) )  throw new InvalidDataException( "供应商只可以下载属于自己的数据." );
            	
            	Workbook book = downloader.getWorkbook();
            	book.toFile(fdown);
            	/**
            	 * 向前台输出内容为 EXCEL 兼容的XML文档.
            	 */
            	outputFile( response, fdown , sheetid + ".xls" );
            }
            if( sheetname.equalsIgnoreCase( "paymentsheet" ) ){
            	DownloadPaymentSheet downloader = new DownloadPaymentSheet( conn, sheetid );
            	
            	/**
            	 * 为了保障供应商业务数据的安全性, 核对安全令牌中的供应商编码与单据的供应商编码是否一致.
            	 */
            	String venderid_pm = downloader.getPaymentnoteVenderid(sheetid).trim();
            	if ( ! venderid_pm.equals(venderid) )  throw new InvalidDataException( "供应商只可以下载属于自己的数据." );
            	
            	Workbook book = downloader.getWorkbook();
            	book.toFile(fdown);
            	/**
            	 * 向前台输出内容为 EXCEL 兼容的XML文档.
            	 */
            	outputFile( response, fdown , sheetid + ".xls" );
            }    
            

		} catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
	    	output( response, elm_doc );
        } catch (Exception e) {
        	//e.printStackTrace();
        	elm_doc.addContent( new XErr( -1, e.toString() ).toElement() ) ; 
	    	output( response, elm_doc );
        } finally {
	    	closeDataSource(conn);
	    	fdown.delete();
        }
	}

    private static final long serialVersionUID = 20061124L;
}
