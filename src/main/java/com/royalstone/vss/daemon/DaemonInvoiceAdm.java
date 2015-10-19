
package com.royalstone.vss.daemon;

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
import com.royalstone.vss.detail.ShowVenderInvoice;
import com.royalstone.vss.liquidation.VenderInvoiceEditor;

/**
 * 此模块用于发票处理.包括发票的添加，显示，删除，修改。
 * 在每此调用方法前都会对令牌中的venderid 与 发票对应的付款申请单中的 venderid 匹配。
 * @param	sheetname	单据名称
 * @author mengluoyi
 * @author baibai
 */
public class DaemonInvoiceAdm extends XDaemon
{
    
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
	    doGet(request, response);
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	request.setCharacterEncoding( "UTF-8" );
    	this.check4Gzip(request);
	    Element elm_doc = new Element( "xdoc" );
        Element elm_out = new Element( "xout" );
    	
    	Connection conn = null;
	    try {
	    	if( ! isSessionActive( request ) ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
            Token token = this.getToken( request );
	    	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
            if ( !token.isVender ) throw new PermissionException( "本模块仅可由供应商调用!" );
            
            conn = openDataSource( token.site.getDbSrcName() );
            
            String operation = request.getParameter( "operation" );
           
            if( operation == null || operation.length() == 0 ) throw new InvalidDataException( "operation is invalid." );

            String venderid_current = token.getBusinessid();
            String venderid_db ="";     //数据库中表对应的venderid
            
                if( operation.equalsIgnoreCase( "confirm" ) ) {
                    /**
                     * 发票输入完毕，提交
                     */
                    String sheetid_payment = request.getParameter( "refsheetid" );
                    if( sheetid_payment == null || sheetid_payment.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
                    String contacttel =  request.getParameter( "contacttel" );
                    String contact =  request.getParameter( "contact" );
                    if(contact==null || contact.length()==0) contact="";
                    if(contacttel==null || contacttel.length()==0) throw new InvalidDataException("联系电话为空");
                    
                    VenderInvoiceEditor editor = new VenderInvoiceEditor( conn, token );
                    /**
                     * 验证venderid 
                     */
                    venderid_db = editor.getPaymentVenderid(sheetid_payment);
                    if ( !venderid_current.equals( venderid_db ) ) throw new InvalidDataException( "请不要尝试查看他人信息！" );                      
                    
                    editor.confirm( sheetid_payment,contacttel,contact );
                }else if( operation.equalsIgnoreCase( "additem" ) ) {
                    /**
                     * 手工输入发票
                     */
                    String sheetid_invoice = request.getParameter( "sheetid" );
                    if( sheetid_invoice == null || sheetid_invoice.length() == 0 ) throw new InvalidDataException( "sheetid is invalid!" );
                    VenderInvoiceEditor editor = new VenderInvoiceEditor( conn, token );
                    /**
                     * 验证venderid 
                     */
                    venderid_db = editor.getInvoiceVenderid(sheetid_invoice);
                    if ( !venderid_current.equals( venderid_db ) ) throw new InvalidDataException( "请不要尝试修改他人信息！" );  
                    
                    editor.addItem( sheetid_invoice, request );
                    
                    ShowVenderInvoice view = new ShowVenderInvoice( conn, sheetid_invoice,token );
                    elm_out.addContent( view.toElement() );
                }else if( operation.equalsIgnoreCase( "replaceitem" ) ) {
                    /**
                     * 修改一条发票信息
                     */
                    String sheetid_invoice = request.getParameter( "sheetid" );
                    if( sheetid_invoice == null || sheetid_invoice.length() == 0 ) throw new InvalidDataException( "sheetid is invalid!" );
                    VenderInvoiceEditor editor = new VenderInvoiceEditor( conn, token );
                    /**
                     * 验证venderid 
                     */
                    venderid_db = editor.getInvoiceVenderid(sheetid_invoice);
                    if ( !venderid_current.equals( venderid_db ) ) throw new InvalidDataException( "请不要尝试修改他人信息！" );                      
                     
                    editor.replaceItem( sheetid_invoice, request );
                    
                    ShowVenderInvoice view = new ShowVenderInvoice( conn, sheetid_invoice,token );
                    elm_out.addContent( view.toElement() );
                }else if( operation.equalsIgnoreCase( "del" ) ) {
                    /**
                     * 删除一条发票
                     */
                    String sheetid_invoice = request.getParameter( "sheetid" );
                    if( sheetid_invoice == null || sheetid_invoice.length() == 0 ) throw new InvalidDataException( "sheetid is invalid!" );
                    String seqno = request.getParameter( "seqno" );
                    if( seqno == null || seqno.length() == 0 ) throw new InvalidDataException( "seqno is invalid!" );
                    int iseqno = Integer.parseInt( seqno );
                    
                    VenderInvoiceEditor editor = new VenderInvoiceEditor( conn, token );
                    /**
                     * 验证venderid 
                     */
                    venderid_db = editor.getInvoiceVenderid(sheetid_invoice);
                    if ( !venderid_current.equals( venderid_db ) ) throw new InvalidDataException( "请不要尝试修改他人信息！" );                    
                    
                    editor.deleteItem(sheetid_invoice, iseqno);
                    
                    ShowVenderInvoice view = new ShowVenderInvoice( conn, sheetid_invoice,token );
                    elm_out.addContent( view.toElement() );
                }else if( operation.equalsIgnoreCase( "opensheet" ) ) {
                    /**
                     * "打开" 发票接收单. 要求前台提供付款单的单号.
                     * 如果对应的发票接收单已经存在, 则查询其内容; 如果发票接收单尚不存在, 则建立发票接收单.
                     */
                    String sheetid_payment = request.getParameter( "refsheetid" );
                    if( sheetid_payment == null || sheetid_payment.length() == 0 ) throw new InvalidDataException( "refsheetid is invalid." );

                    VenderInvoiceEditor editor = new VenderInvoiceEditor( conn, token );
                   
                    /**
                     * 验证venderid 
                     */
                    venderid_db = editor.getPaymentVenderid(sheetid_payment);
                    if ( !venderid_current.equals( venderid_db ) ) throw new InvalidDataException( "请不要尝试查看他人信息！" );                   
                    
                    String sheet_invoice = editor.openSheet( sheetid_payment );
                    
                    ShowVenderInvoice search = new ShowVenderInvoice( conn, sheet_invoice,token );
                    Element elm_sheet = search.toElement();
                    elm_out.addContent( elm_sheet );
                } else if( operation.equalsIgnoreCase( "showdetail" ) ) {
                    /**
                     *  返回发票申请单信息.前台必须指定sheetid。
                     */
                    String sheetid_invoice = request.getParameter( "sheetid" );
                    if( sheetid_invoice == null || sheetid_invoice.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
                    
                   /**
                    * 验证venderid 
                    */
                    VenderInvoiceEditor editor = new VenderInvoiceEditor( conn, token );
                    venderid_db = editor.getInvoiceVenderid( sheetid_invoice );
                    if ( !venderid_current.equals( venderid_db ) ) throw new InvalidDataException( "请不要尝试查看他人信息！" );
                    
                    ShowVenderInvoice search = new ShowVenderInvoice( conn, sheetid_invoice,token );
                    Element elm_sheet = search.toElement();
                    elm_out.addContent( elm_sheet );
                }

			elm_doc.addContent( elm_out );
			elm_doc.addContent( ( new XErr( 0, "OK" ) ).toElement() );
        } catch (InvalidDataException e) {
        	//e.printStackTrace();
        	elm_doc.addContent(new XErr( -1, e.getMessage() ).toElement()) ; 
		} catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
        } catch (Exception e) {
        	//e.printStackTrace();
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
        } finally {
        	output( response, elm_doc );
	    	closeDataSource(conn);
        }
	}
    
    private static final long serialVersionUID = 20061017L;
}

