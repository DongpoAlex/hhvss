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
import com.royalstone.vss.catalogue.SearchChargeShopItem;
import com.royalstone.vss.catalogue.SearchChargeshop;
import com.royalstone.vss.catalogue.SearchChargesum;
import com.royalstone.vss.catalogue.SearchDeduction;
import com.royalstone.vss.catalogue.SearchDept;
import com.royalstone.vss.catalogue.SearchElecInstall;
import com.royalstone.vss.catalogue.SearchFresh4retail;
import com.royalstone.vss.catalogue.SearchGoods;
import com.royalstone.vss.catalogue.SearchInmonth;
import com.royalstone.vss.catalogue.SearchLiquidation;
import com.royalstone.vss.catalogue.SearchPaySheet;
import com.royalstone.vss.catalogue.SearchPaymentSheet4Vi;
import com.royalstone.vss.catalogue.SearchPaymentnote;
import com.royalstone.vss.catalogue.SearchPaytype;
import com.royalstone.vss.catalogue.SearchPn4Vi;
import com.royalstone.vss.catalogue.SearchPromShare;
import com.royalstone.vss.catalogue.SearchPurchaseChkSheet;
import com.royalstone.vss.catalogue.SearchPurchseSheet;
import com.royalstone.vss.catalogue.SearchPurmadj;
import com.royalstone.vss.catalogue.SearchReceipt;
import com.royalstone.vss.catalogue.SearchRentNote;
import com.royalstone.vss.catalogue.SearchRet;
import com.royalstone.vss.catalogue.SearchRetNotice;
import com.royalstone.vss.catalogue.SearchSalePick;
import com.royalstone.vss.catalogue.SearchShop;
import com.royalstone.vss.catalogue.SearchStkCostAdj;
import com.royalstone.vss.catalogue.SearchVenderInvoice;
import com.royalstone.vss.report.cm.VenderPaystatus;

/**
 * 此模块用于查询以下单据的目录: 订单, 验收单, 退货通知单, 退货单.
 * 联营租赁费用通知单
 * @param  sheetname  单据名称. 可以取以下值: purchase,receipt,retnotice,ret.
 * @author mengluoyi
 *
 */
public class DaemonSearchSheet extends XDaemon
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
    	Element elm_out = new Element( "xout" );
    	
    	Connection conn = null;
	    try {
	       	if (!isSessionActive(request))
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	        Token token = this.getToken(request);
	        if (token == null)
	            throw new PermissionException(PermissionException.LOGIN_PROMPT);
	       

            conn = openDataSource( token.site.getDbSrcName());
            // conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED );
            
            String sheetname = request.getParameter( "sheetname" );
            if( sheetname == null || sheetname.length() == 0 ) throw new InvalidDataException( "sheetname not set! " );

            Map map = request.getParameterMap();
            
            HashMap parms = new HashMap(map);
            
            /**
             * 供应商只可以查看自己的单据.
             * 如果访问者以供应商身份登录, 应添加针对供应商ID的过滤条件.
             */
            if( token.isVender ) {
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
        	}

        	String month=request.getParameter("isbak");
			if(month!=null && month.toLowerCase().equals("isbak")){
				month="_bak";
			}else{
				month="";
			}
            /**
             * 在全部订货审批单中查询
             */
            if( sheetname.equalsIgnoreCase( "ordersheet" ) ){
            	SearchPurchaseChkSheet search = new SearchPurchaseChkSheet( conn, parms,month );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            /**
             * 查询新订单的确认情况
             */
            else if( sheetname.equalsIgnoreCase( "fresh_confirm" ) ){
            	SearchFresh4retail search = new SearchFresh4retail( conn, parms );
                Element elm_cat = search.toElement();
                elm_out.addContent( elm_cat );
            }
            /**
             * 查询订货通知单
             */
            else if( sheetname.equalsIgnoreCase( "purchase" ) ){
            	SearchPurchseSheet search = new SearchPurchseSheet( conn, parms, month );
                Element elm_cat = search.toElement();
                elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "chargeshop" ) ){
            	SearchChargeshop search = new SearchChargeshop( conn, parms );
                Element elm_cat = search.toElement();
                elm_out.addContent( elm_cat );
            } 
            else if( sheetname.equalsIgnoreCase( "ChargeShopItem" ) ){
            	SearchChargeShopItem search = new SearchChargeShopItem( conn, parms );
                Element elm_cat = search.toElement();
                elm_out.addContent( elm_cat );
            } 
            /**
             * 验收单
             */
            else if( sheetname.equalsIgnoreCase( "receipt" ) ){
        	    SearchReceipt search = new SearchReceipt( conn, parms );
        	    Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            	
            } 
            /**
             * 退货通知单
             */
            else if( sheetname.equalsIgnoreCase( "retnotice" ) ){
            	SearchRetNotice search = new SearchRetNotice( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            } 
            /**
             * 退货单
             */
            else if( sheetname.equalsIgnoreCase( "ret" ) ){
            	SearchRet search = new SearchRet( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
           	
        	/**
        	 * 未结算扣项
        	 */
        	}else if( sheetname.equals("chargesum")){
        		SearchChargesum cs = new SearchChargesum( conn );
        		Element elm_cat = cs.getChargeSum( parms );
            	elm_out.addContent( elm_cat );
            }

            /**
             * 对帐申请单查询
             */
            else if( sheetname.equalsIgnoreCase( "liquidation" ) ){
            	SearchLiquidation search = new SearchLiquidation( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            /**
             * 供应商录入发票查询（历史数据查询）
             * add by bai
             */
            else if( sheetname.equalsIgnoreCase("venderinvoice")){
            	SearchVenderInvoice search = new SearchVenderInvoice(conn , parms,token);
            	Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            /**
             * 已制单审核付款单查询
             * add by bai
             */
            else if( sheetname.equalsIgnoreCase( "pn4vi" ) ){
            	Element elm_cat;
            	if(token.site.getSid()==11){
            		SearchPaymentSheet4Vi search = new SearchPaymentSheet4Vi( conn, parms );
            		elm_cat = search.toElement();
            	}else{
            		SearchPn4Vi search = new SearchPn4Vi( conn, parms );
            		elm_cat = search.toElement();
            	}
            	elm_out.addContent( elm_cat );
            }
            /**
             * 付款单/付款申请单查询
             */
            else if( sheetname.equalsIgnoreCase( "paymentnote" ) ){
            	SearchPaymentnote search = new SearchPaymentnote( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "paymentsheet" ) ){
            	SearchPaySheet search = new SearchPaySheet( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            /**
             * 联营费用通知单
             */
            else if( sheetname.equalsIgnoreCase( "rentnote" ) ){
            	SearchRentNote search = new SearchRentNote(conn,parms);
            	elm_out.addContent(search.getSheetList());
            }
            /**
             * 查询库存进价调整单
             */
            else if( sheetname.equalsIgnoreCase( "stkcostadj" ) ){
            	SearchStkCostAdj search = new SearchStkCostAdj( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            /**
             * 手工结算调整单
             */
            else if( sheetname.equalsIgnoreCase( "purmadj" ) ){
            	SearchPurmadj search = new SearchPurmadj( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            //带货按装
            else if( sheetname.equalsIgnoreCase( "salepick" ) ){
            	SearchSalePick search = new SearchSalePick(conn, parms);
            	Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            //电器按装
            else if( sheetname.equalsIgnoreCase( "elecinstall" ) ){
            	SearchElecInstall search = new SearchElecInstall(conn, parms);
            	Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "shop" ) ){
            	SearchShop search = new SearchShop( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "goods" ) ){
            	SearchGoods search = new SearchGoods( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "dept" ) ){
            	SearchDept search = new SearchDept( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "paytype" ) ){
            	SearchPaytype search = new SearchPaytype( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "promshare" ) ){
            	SearchPromShare search = new SearchPromShare( conn, parms,"" );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "deduction" ) ){
            	SearchDeduction search = new SearchDeduction( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            else if( sheetname.equalsIgnoreCase( "inmonth" ) ){
            	SearchInmonth search = new SearchInmonth( conn, parms );
                Element elm_cat = search.toElement();
            	elm_out.addContent( elm_cat );
            }
            
            else if( sheetname.equalsIgnoreCase( "VenderPaystatus" ) ){
            	VenderPaystatus search = new VenderPaystatus( conn, token );
            	if(token.isVender){
            		elm_out.addContent( search.list() );
            	}else{
            		String venderid = request.getParameter("venderid");
            		elm_out.addContent( search.list(venderid ) );
            	}
            }
            /*
             * 参数不存在
             */
            else
            	throw new InvalidDataException( "Undefined sheetname: " + sheetname );
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
