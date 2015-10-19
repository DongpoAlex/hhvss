/**
 * 
 */
package com.royalstone.workbook;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Element;

import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;

/**
 * @author BaiJian 
 * Excel 导出测试
 */
public class Demo extends XDaemon {

	private static final long serialVersionUID = -2579146824819331430L;
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
	    doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
    	this.check4Gzip(request);
	    Element elm_doc = new Element( "xdoc" );
    
	    Connection conn = null;
	    try {
	    	/**
	    	 * 为了和原来的类尽可能的写法相似，采用了与原来同样的命名Workbok
	    	 * 而且调用方法几乎一样，只是在构造该类是必须将response.getOutputStream() 传进去
	    	 * 
	    	 * 下面是我写的一个简单的例子。
	    	 */
	    	//通过一个类得到 Workbook对象
	    	Workbook book = Baijian.getWorkbook(conn,response.getOutputStream());
	    	
	    	//excl输出到前台，和原来有所区别。
			book.output(response, "test.xls");
			
		} catch (SQLException e) {
			elm_doc.addContent( new XErr( e.getErrorCode(), e.toString() ).toElement() ); 
	    	output( response, elm_doc );
        } catch (Exception e) {	
        	elm_doc.addContent(new XErr( 0, e.toString() ).toElement()) ; 
	    	output( response, elm_doc );
        } finally {
	    	closeDataSource(conn);
        }
	}
	
	
}
class Baijian{
	static public Workbook getWorkbook(Connection conn,OutputStream os) throws SQLException, IOException, RowsExceededException, WriteException{
		String sql="select shopid,shopname from shop";
		String[] title ={"门店ID","门店名称"};
		
		//new 一个book 和原来不一样的是，需要传入输出流
		Workbook book = new Workbook(os);
		
		
		//添加一个工作簿这里和原来一致
		book.addSheet(conn.createStatement().executeQuery(sql), "第一页", title);
		
		return book;
	}
	
}