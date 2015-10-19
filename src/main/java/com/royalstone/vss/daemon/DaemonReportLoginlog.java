package com.royalstone.vss.daemon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.vss.report.ReportLoginlog;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
public class DaemonReportLoginlog extends XDaemon {	
	
	private static final long serialVersionUID = -44217486789327690L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		this.check4Gzip(request);
		Element elm_doc = new Element("xdoc");
		Element elm_out = new Element("xout");

		Connection conn = null;
		try {
			if (!isSessionActive(request))
				throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null)
				throw new PermissionException(PermissionException.LOGIN_PROMPT);
			if (token.isVender)
				throw new PermissionException("本模块仅可由零售商用户调用!");

			conn = openDataSource(token.site.getDbSrcName());

			String operation = request.getParameter("operation");
			Map<?, ?> map = request.getParameterMap();

			if (operation.equals("browse")) {
				ReportLoginlog syslog = new ReportLoginlog(conn, map);
				Element elm_report = syslog.toElement();
				elm_out.addContent(elm_report);
                elm_doc.addContent(elm_out);
                elm_doc.addContent((new XErr(0, "OK")).toElement());
                output(response, elm_doc);
			} else {
				ReportLoginlog syslog = new ReportLoginlog(conn, map);
				List<List<String>> filtrate = syslog.toJSON();
				response.setContentType("application/ms-excel");
				response.setHeader("Content-disposition", "attachment; filename=gyssfmx.xls");
				CreateXL(filtrate,"gyssfmx.xls",response.getOutputStream());
                
			}


		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		} catch (SQLException e) {
			// e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.toString())
					.toElement());
		} catch (Exception e) {
			// e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		} finally {
            closeDataSource(conn);
		}
	}
	
	public void  CreateXL(List<List<String>> xlJson,String filePath,OutputStream stream){
		try {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("供应商收费明细");		
		HSSFCellStyle style = workbook.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 指定单元格居中对齐       
        HSSFRow row = sheet.createRow(0);   
        for (int j = 0; j < 5; j++) {  
            HSSFCell cell = row.createCell(j);  
            cell.setCellValue(getStr( j,""));  
            cell.setCellStyle(style);  
        }  
		for (int i = 0; i < xlJson.size(); i++) {  
			row = sheet.createRow(i+1);  
            List<String> list = xlJson.get(i);  
            for (int j = 0; j < list.size(); j++) {  
                HSSFCell cell = row.createCell(j);  
                cell.setCellValue(list.get(j));  
                cell.setCellStyle(style);  
            }  
        }
	    	// 导出文件  
        	workbook.write(stream);
        	stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException ex){
			ex.printStackTrace();
		}
       
	}
	
	private String getStr(int j, String str) {  
        switch (j) {  
            case 0:  
                str = "经营公司";  
                break;  
            case 1:  
                str = "登陆ID";  
                break;  
            case 2:  
                str = "供应商名称";  
                break;  
            case 3:  
                str = "收费标准";  
                break;  
            case 4:  
                str = "收费金额";  
                break;  
        }  
        return str;  
    }  
}
