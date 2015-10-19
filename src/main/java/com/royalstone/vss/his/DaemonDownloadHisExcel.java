package com.royalstone.vss.his;

import java.io.File;
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

/**
 * 公共模块，提供下载EXCEL文档支持
 * 
 * @author baibai
 *
 */
public class DaemonDownloadHisExcel extends XDaemon {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.check4Gzip(request);
        request.setCharacterEncoding( "UTF-8" );
        Connection conn = null;
        File fdown = File.createTempFile( "rpt", "xls" );
        try {
            if (!isSessionActive(request))
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
            Token token = this.getToken(request);
            if (token == null)
                throw new PermissionException(PermissionException.LOGIN_PROMPT);
           
            String operation = request.getParameter("operation");
            if (operation == null)
                throw new InvalidDataException("operation is null");
            
            
            conn = openDataSource(token.site.getDbSrcName());
            
            HashMap parms = new HashMap(request.getParameterMap());
            if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
        	}
            
            
            String fileName = "down.xls";

            /**
             * 导出订货审批单明细
             */
            if( operation.equalsIgnoreCase("order_sheet") ){
            	String sheetid = request.getParameter( "sheetid" );
            	if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid not set! " );
            	ShowPurchsechkSheet downloader = new ShowPurchsechkSheet(conn,sheetid);
            	if( token.isVender ){
            		String venderid = token.getBusinessid();
                	
                     /**
                 	 * 为了保障供应商业务数据的安全性, 核对安全令牌中的供应商编码与单据的供应商编码是否一致.
                 	 */
                 	String venderid_pm = downloader.getVenderId().trim();
                 	if ( ! venderid_pm.equals(venderid) )  throw new InvalidDataException( "供应商只可以下载属于自己的数据." );
                 	
            	}
                
             	downloader.makeExcel(fdown);
            	fileName = sheetid + ".xls" ;
            /**
             * 导出供应商审批单目录
             */
            }else if( operation.equalsIgnoreCase("order_retail") ){
            	SearchPurchaseChkSheet search = new SearchPurchaseChkSheet(conn,parms);
            	search.cookExcelFile(fdown);
            	fileName = "order.xls";
            /**
             * 导出供应商退货通知单目录
             */
            }else if( operation.equalsIgnoreCase("retnotice") ){
            	SearchRetNotice search = new SearchRetNotice(conn,parms);
            	search.cookExcelFile(fdown);
            	fileName = "retnoice.xls";
            /**
             * 导出库存调进价单明细
             */
            }else if( operation.equalsIgnoreCase("stk") ){
            	String sheetid=request.getParameter("sheetid");
            	if(sheetid==null || sheetid.length()==0)
            		throw new InvalidDataException("sheetid is null");
            	
            	ShowStkCostAdj search = new ShowStkCostAdj(conn,sheetid);
            	search.cookExcelFile(fdown);
            	fileName = "stk.xls";
            }else if( operation.equalsIgnoreCase("sdaily") ){
        		SaleVenderShopDaily sd = new SaleVenderShopDaily( conn, parms );
        		if(token.site.getSid()==2){
        			sd.cookExcelFile4OLE( fdown );
        		}else{
        			sd.cookExcelFile( fdown );
        		}
        		fileName = "sdaily.xls";
            }else if( operation.equalsIgnoreCase("promshare") ){
            	SearchPromShare search = new SearchPromShare(conn, parms, "_cdept");
            	search.cookExcelFile(parms, fdown);
            	fileName = "promshare.xls";
            }
            // 把文件传到前台
            outputFile( response, fdown , fileName );
            
        } catch (SQLException e) {
        	e.printStackTrace();
        	Element elm_doc = new Element( "xdoc" );
        	elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() );
        	output( response, elm_doc );
		} catch (Exception e) {
			e.printStackTrace();
        	Element elm_doc = new Element( "xdoc" );
        	elm_doc.addContent(new XErr( 0, e.toString() ).toElement()) ; 
        	output( response, elm_doc );
		} finally {
            closeDataSource(conn);
            fdown.delete();
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    private static final long serialVersionUID = 20061109L;

}
