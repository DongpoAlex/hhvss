package com.royalstone.util.daemon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.GZIPOutputStream;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.royalstone.util.SolarCalendar;

/**
 * XDaemonBase 是VSS系统中大多数后台　Servlet 的父类. 为其子类提供两组方法: <br>
 * A) 打开/关闭数据库连接的方法: openDataSource, closeDataSource. <br>
 * B) 向浏览器端输出内容的方法: output
 * 
 * @author Mengluoyi
 */
public class XDaemonBase extends HttpServlet {

	private static final long	serialVersionUID	= 200712031124L;

	/**
	 * 打开数据库连接.
	 * 
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	final static public Connection openDataSource(String src_name) throws NamingException, SQLException {
		//System.out.println("打开数据源："+src_name);
		Context initCtx = new javax.naming.InitialContext();
		DataSource ds = (DataSource) initCtx.lookup("java:comp/env/" + src_name);
		Connection conn = ds.getConnection();
		/**
		 * 2008-03-29 mengluoyi 数据库封锁级置为CR, 适应大多数应用的需要,避免因为RR而降低访问效率. SET
		 * ISOLATION TO COMMITTED READ
		 */
		// conn.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED
		// );
		return conn;
	}

	/**
	 * SET ISOLATION TO DIRTY READ;
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	final protected void setDirtyRead(Connection conn) throws SQLException {
	// conn.setTransactionIsolation( Connection.TRANSACTION_READ_UNCOMMITTED);
	}

	/**
	 * SET ISOLATION TO COMMITTED READ;
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	final protected void setCommittedRead(Connection conn) throws SQLException {
	// conn.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED);
	}

	/**
	 * SET LOCK MODE TO WAIT n seconds
	 * 
	 * @param conn
	 * @param seconds
	 * @throws SQLException
	 */
	final protected void setWaitTime(Connection conn, int seconds) throws SQLException {
		String sql = " SET LOCK MODE TO WAIT ";
		if (seconds > 0) sql += seconds;
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		stmt.close();
	}

	/**
	 * SET LOCK MODE TO NOT WAIT
	 * 
	 * @param conn
	 * @param seconds
	 * @throws SQLException
	 */
	final protected void setNotWait(Connection conn, int seconds) throws SQLException {
		String sql = " SET LOCK MODE TO NOT WAIT ";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		stmt.close();
	}

	/**
	 * 关闭数据库连接. 关闭过程中的Exception 将被抑制.
	 */
	final static public void closeDataSource(Connection conn) {
		if (conn != null) try {
			conn.close();
			System.out.println("关闭数据源："+conn.getCatalog());
		}
		catch (SQLException e) {
			// do nothing.
		}
	}

	/**
	 * 此函数将一个XML 节点送回前台浏览器.
	 * 
	 * @param response
	 * @param elm
	 *            送回前台的XML节点.
	 * @throws IOException
	 */
	final protected void output(HttpServletResponse response, Element elm) throws IOException {
		this.output(response, new Document(elm));
	}

	/**
	 * 此函数将一个XML 文档送回前台浏览器.
	 * 
	 * @param response
	 * @param doc
	 *            送回前台的XML文档.
	 * @throws IOException
	 */
	final protected void output(HttpServletResponse response, Document doc) throws IOException {
		long currentTime = System.currentTimeMillis();
		response.setDateHeader("Expires", currentTime + 1000);
//		response.setHeader("Pragma", "No-cache");
//		response.setHeader("Cache-Control", "no-cache");
//		response.setDateHeader("Expires", 0);

		XMLOutputter outputter = new XMLOutputter("  ", true, "UTF-8");
		if (useGzip) {
			response.setHeader("Content-Encoding", "gzip");
			response.setContentType("text/xml;charset=UTF-8");
			GZIPOutputStream gzip = new GZIPOutputStream(response.getOutputStream());
			outputter.output(doc, gzip);
			gzip.flush();
			gzip.close();
		} else {
			response.setContentType("text/xml;charset=UTF-8");
			ServletOutputStream out = response.getOutputStream();
			outputter.output(doc, out);
			out.flush();
			out.close();
		}

		/**
		 * 2008-03-29 mengluoyi 每次调用都打印内存信息 使用变量output_times累计调用次数.
		 * 每过100次打印内存使用情况; 每调1000次则进行一次垃圾回收.
		 */
		++output_times;
		if (output_times > 1000000) output_times = 0;
		if (output_times % 100 == 1) printMemUse();
		if (output_times % 1000 == 1) collectGarbage();

	}

	final protected void export(HttpServletResponse response, Element elm_doc, String filename)
			throws IOException {
		Document doc = new Document(elm_doc);
		export(response, doc, filename);
	}

	/**
	 * 此方法把一个XML文档发送到前台, 同时指定前台保存的文件名. 每次调用此方法都要打印内存占用情况
	 * 
	 * @param response
	 * @param doc
	 * @param filename
	 * @throws IOException
	 */
	final protected void export(HttpServletResponse response, Document doc, String filename)
			throws IOException {
		long currentTime = System.currentTimeMillis();
		response.setDateHeader("Expires", currentTime + 1000);

		XMLOutputter outputter = new XMLOutputter("  ", true, "UTF-8");
		if (useGzip) {
			response.setHeader("Content-Encoding", "gzip");
			response.setHeader("Content-disposition", "attachment; filename=" + filename);
			GZIPOutputStream gzip = new GZIPOutputStream(response.getOutputStream());
			outputter.output(doc, gzip);
			gzip.flush();
			gzip.close();
		} else {
			response.setHeader("Content-disposition", "attachment; filename=" + filename);
			ServletOutputStream out = response.getOutputStream();
			outputter.output(doc, out);
			out.flush();
			out.close();
		}

		/**
		 * 2008-03-26 mengluoyi 每次调用此方法都要打印内存占用情况
		 */
		printMemUse();
	}

	final static public void output(Element elm, Writer writer) throws IOException {
		XMLOutputter outputter = new XMLOutputter(" ", true, "UTF-8");
		outputter.output(elm, writer);
	}

	/**
	 * 此函数将一个字串送回前台浏览器.
	 * 
	 * @param response
	 * @param str
	 *            送回前台的字串.
	 * @throws IOException
	 */
	final protected void output(HttpServletResponse response, String str) throws IOException {
		OutputStreamWriter outwriter = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
		PrintWriter out = new PrintWriter(outwriter);
		out.print(str);
		out.flush();
		out.close();
	}

	/**
	 * 此方法用于把文件下载到客户端, 并可以用来下载二进制文件. 每次调用此方法都要打印内存占用情况
	 * 
	 * @param response
	 * @param file
	 * @param fileName
	 * @throws IOException
	 */
	final protected void outputFile(HttpServletResponse response, File file, String filename)
			throws IOException {
		FileInputStream fis = new FileInputStream(file);
		OutputStream ostream = response.getOutputStream();

		response.setContentType("application/ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=" + filename);

		byte[] b = new byte[1024];
		try {
			int len = fis.read(b);
			while (len > 0) {
				ostream.write(b, 0, len);
				len = fis.read(b);
			}
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			fis.close();
			ostream.close();
		}

		/**
		 * 2008-03-26 mengluoyi 每次调用此方法都要打印内存占用情况
		 */
		printMemUse();

	}

	/**
	 * 此函数用于获取客户端发来的 XML文档.
	 * 
	 * @param request
	 * @return 客户端发来的 XML文档.
	 * @throws JDOMException
	 * @throws IOException
	 */
	final protected Document getParamDoc(HttpServletRequest request) throws JDOMException, IOException {
		InputStreamReader reader = new InputStreamReader(request.getInputStream(), "UTF-8");
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(reader);
		reader.close();
		return doc;
	}

	final protected void check4Gzip(HttpServletRequest request) {
		String encodings = request.getHeader("Accept-Encoding");
		useGzip = (encodings != null && encodings.indexOf("gzip") != -1);
	}

	/**
	 * 2007-12-03 mengluoyi 为了防止堆内存溢出而引入该方法
	 */

	/**
	 * 此方法用于打印后台程序占用内存的情况. 在Websphere的日志文件SystemOut.log中可以看到打印的信息.
	 */
	private void printMemUse() {
		long mem_total0 = Runtime.getRuntime().totalMemory();
		long mem_free0 = Runtime.getRuntime().freeMemory();
		long mem_used0 = mem_total0 - mem_free0;

		SolarCalendar now = new SolarCalendar();
		System.out.println(now.toString() + " MemoryUsage(MB){T|F|U}: " + "{" + mem_total0 / 0x100000 + "|"
				+ mem_free0 / 0x100000 + "|" + mem_used0 / 0x100000 + "}");
	}

	/**
	 * 2008-03-29 mengluoyi 此方法要求JVM进行垃圾回收, 以避免后台程序占用内存过多.
	 */
	private void collectGarbage() {
		printMemUse();

		long t0 = System.currentTimeMillis();
		// 强制性垃圾回收
		System.gc();
		long t1 = System.currentTimeMillis();

		SolarCalendar now = new SolarCalendar();
		System.out.println(now.toString() + " garbage_collected: " + (t1 - t0) + "ms");

		printMemUse();
	}

	final protected boolean useGzip() {
		return useGzip;
	}

	private boolean		useGzip			= false;

	/**
	 * 2007-12-03 mengluoyi 引入变量output_times, Daemon.output 调用次数达到一定值, 就打印内存信息,
	 * 适当时候可以要求JVM进行垃圾回收.
	 */
	static private int	output_times	= 0;
}