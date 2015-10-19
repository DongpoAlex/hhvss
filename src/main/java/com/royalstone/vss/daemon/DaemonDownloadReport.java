package com.royalstone.vss.daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.Day;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;
import com.royalstone.util.aw.CMServiceFactory;
import com.royalstone.util.aw.ICMQueryService;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.catalogue.SearchDeduction;
import com.royalstone.vss.catalogue.SearchInmonth;
import com.royalstone.vss.catalogue.SearchPromShare;
import com.royalstone.vss.report.CheckWorkInfo;
import com.royalstone.vss.report.CurrtentInventory;
import com.royalstone.vss.report.SaleVenderShopDaily;
import com.royalstone.vss.report.VenderGoodsShop;

/**
 * 此模块用于下载报表.
 * @param	peportname	单据名称
 * @param	releasedate_min	上传日期-起始日
 * @param	releasedate_max	上传日期-截止日
 * @author bai
 */
public class DaemonDownloadReport extends XDaemon
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
    	request.setCharacterEncoding("UTF-8");
	    Element elm_doc = new Element( "xdoc" );
    
        File fdown = File.createTempFile( "rpt", "xls" );
	    Connection conn = null;
	    try {
	    	if( ! isSessionActive( request ) ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
            Token token = this.getToken( request );
	    	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );

            String reportname = request.getParameter("reportname");
            if( reportname == null || reportname.length()==0 ) throw new InvalidDataException(" reportname is null ");
                      
            HashMap<String, String[]> parms = new HashMap<String, String[]>(request.getParameterMap());
            String venderid = null ;
        	if( token.isVender ) {
        		parms.remove("venderid");
            	String[] arr_vender = new String[1];
            	arr_vender[0] = token.getBusinessid();
           		parms.put( "venderid", arr_vender );
           		venderid = token.getBusinessid();
        	}else{
        		venderid = request.getParameter("venderid");
        	}
        	
            conn = openDataSource(token.site.getDbSrcName() );

            
            //如果打开数据链接成功，生成文件下载的临时文件，用以减小内存压力。
            String filename = "report.xls";
            
			/**
        	 * 下载最新库存
        	 */
        	if( reportname.equals("ci") ){
        		filename = "current_inventory_" + new Day().toString() + ".xls";
        		CurrtentInventory ci = new CurrtentInventory( conn );
        		//ci.cookExcelFile( parms, fdown );
        		ci.getJxlExcel( parms, fdown );
        		
			} else if ("cmexcel".equals(reportname)) {
				String cmid = request.getParameter("cmid");
				String clazz = request.getParameter("class");
				ICMQueryService service = CMServiceFactory.factory(clazz,  conn,token);
				
				service.excel(fdown, cmid, parms);
				
        	/*
        	 * 销售日报
        	 */
        	}else if( reportname.equals("sdaily") ){
        		filename = "sale_daily.xls";
        		SaleVenderShopDaily sd = new SaleVenderShopDaily( conn, parms );
        		if(token.site.getSid()==2){
        			if(com.royalstone.vss.basic.Vender.getVenderContracttype(conn,venderid)==1){
        				sd.cookExcelNullFile4OLE(fdown);
        			}else{
        				sd.cookExcelFile4OLE( fdown );
        			}
        		}else{
        			sd.cookExcelFile( fdown );
        		}
        		
        		
        	/*
        	 * 门店商品清单
        	 */
        	}else if( reportname.equals("gshop")){
        		filename = "vender_goods_shop.xls";
        		VenderGoodsShop gs = new VenderGoodsShop( conn );
        		gs.cookExcelFile( parms, fdown );
        		
        	/*
        	 * 促销明细
        	 */
        	}else if( reportname.equals("prom")){
//        		String month = "_";
//            	String sheetid = request.getParameter("sheetid");
//            	if(sheetid!=null && sheetid.length()>12){
//            		month += sheetid.substring(4, 10);
//            	}else{
//            		String sdateMin = request.getParameter("sdate_min");
//                	String[] ss = sdateMin.split("-");
//                	if(ss==null || ss.length !=3){
//                		throw new InvalidDataException("date is wrong:"+sdateMin);
//                	}
//                	month += ss[0]+ss[1];
//            	}
            	
        		filename = "prom_report.xls";
    			SearchPromShare sp = new SearchPromShare( conn, parms,"" );
    			sp.cookExcelFile( parms, fdown );
    		/*
    		 * 考勤信息
    		 */
        	}else if( reportname.equals("checkwork")){
        		filename = "checkwork.xls";
        		{
        			CheckWorkInfo info = new CheckWorkInfo( conn );
        			com.royalstone.workbook.Workbook book = info.getWorkBook(parms ,new FileOutputStream( fdown ));
        			book.write();
        		}
        	}else if( reportname.equals("deduction")){
        		filename = "deduction.xls";
        		SearchDeduction search = new SearchDeduction( conn, parms );
        		search.toBook(fdown);
        	}else if( reportname.equals("inmonth")){
        		filename = "inmonth.xls";
        		SearchInmonth search = new SearchInmonth( conn, parms );
        		search.toBook(fdown);
        	}
        	
        	// 把文件传到前台
        	outputFile( response, fdown , filename );
        	
		} catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
	    	output( response, elm_doc );
        } catch (Exception e) {	
        	//e.printStackTrace();
        	elm_doc.addContent(new XErr( -1, e.toString() ).toElement()) ; 
	    	output( response, elm_doc );
        } finally {
        	//关闭数据库，删除临时文件
	    	closeDataSource(conn);
        	fdown.delete();
        }
	}
    
    private static final long serialVersionUID = 20060813L;
}

