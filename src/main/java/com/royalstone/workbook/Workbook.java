/**
 * 
 */
package com.royalstone.workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.royalstone.util.aw.ColModelDetail;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author BaiJian
 * 为兼容以前的excel导入而写的替代类
 */
public class Workbook {
	
	public static void main(String[] args){
		
		
	}
	public static void writeToFile(File file, ResultSet rs, String[] title, String sheet_name) throws IOException, RowsExceededException, WriteException, SQLException{
		FileOutputStream fout = new FileOutputStream( file );
		Workbook book = new Workbook(fout);
		
		book.addSheet(rs, sheet_name, title);
		
		book.write();
		fout.close();
	}
	
	public static void writeToFile(File file, ResultSet rs, List<ColModelDetail> cmdList, String sheet_name) throws IOException, RowsExceededException, WriteException, SQLException{
		FileOutputStream fout = new FileOutputStream( file );
		Workbook book = new Workbook(fout);
		
		book.addSheet(rs, sheet_name, cmdList);
		
		book.write();
		fout.close();
	}
	
	public Workbook(OutputStream os) throws IOException{
		book = jxl.Workbook.createWorkbook(os);
	}
	
	public WritableSheet makeSheet(String sheetName,int idx){
		WritableSheet sheet = book.createSheet(sheetName, sheetCount++);
		return sheet;
	}
	public void addSheet(ResultSet rs, String sheetName, String[] title) throws RowsExceededException, WriteException, SQLException{
		WritableSheet sheet = book.createSheet(sheetName, sheetCount++);
		addTitle(title, sheet);
		addBody(rs, sheet);
	}
	public void addSheet(Connection conn,String sql, String sheetName, String[] title) throws RowsExceededException, WriteException, SQLException{
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		addSheet(rs, sheetName, title);
		SqlUtil.close(rs);
	}
	
	public void addSheet(Connection conn,String sql,Object[] params, String sheetName, String[] title) throws RowsExceededException, WriteException, SQLException{
		ResultSet rs = SqlUtil.queryPS(conn, sql,params);
		addSheet(rs, sheetName, title);
		SqlUtil.close(rs);
	}
	
	public void addSheet(ResultSet rs, String sheetName, List<ColModelDetail> cmdList) throws RowsExceededException, WriteException, SQLException{
		WritableSheet sheet = book.createSheet(sheetName, sheetCount++);
		String title[] = new String[cmdList.size()];
		for (int i = 0; i < title.length; i++) {
			title[i] = ((ColModelDetail)cmdList.get(i)).getName();
		}
		addTitle(title, sheet);
		addBody(cmdList,rs, sheet);
	}
	
	public void output(HttpServletResponse response,String fileName) throws IOException, WriteException{
		response.reset();
	    response.setContentType( "application/vnd.ms-excel" );
    	response.setHeader( "Content-disposition", "attachment; filename=" + fileName );
    	book.write();
    	book.close();
	}
	
	public void addTitle(String[] title,WritableSheet sheet) throws RowsExceededException, WriteException{
		for (int i = 0; i < title.length; i++) {
			Label label=new Label(i,0,title[i],formatTitle);
			sheet.addCell(label);
		}
	}
	
	public void addBody(ResultSet rs,WritableSheet sheet) throws SQLException, RowsExceededException, WriteException{
		ResultSetMetaData meta = rs.getMetaData();
		int row = 1;
		while(rs.next()){
			for(int i=1;i<=meta.getColumnCount();i++){
				sheet.addCell((WritableCell) parseMetaData(rs, meta, i, row));
			}
			row++;
		}
	}
	
	private void addBody(List<ColModelDetail> cmdList,ResultSet rs,WritableSheet sheet)throws SQLException, RowsExceededException, WriteException{
		int row = 1;
		while(rs.next()){
			for(int i=0;i<cmdList.size();i++){
				String field = ((ColModelDetail)cmdList.get(i)).getField();
				String type = ((ColModelDetail)cmdList.get(i)).getVtype();
				String value;
				try{
					value = rs.getString(field);
				}catch (Exception e) {
					continue;
				}
				if("1".equals(type) || "2".equals(type)){
					try{
						sheet.addCell(new jxl.write.Number(i,row,Double.parseDouble(value)));
					}catch (Exception e) {
						sheet.addCell(new Label(i,row,SqlUtil.fromLocal(value),formatNumber));
					}
				}else{
					sheet.addCell(new Label(i,row,SqlUtil.fromLocal(value)));
				}
			}
			row++;
		}
	}
	
	private Object parseMetaData(ResultSet rs,ResultSetMetaData meta,int col,int row) throws SQLException{
		Object o = null;
		//空值检查
		String tmp = rs.getString(col);
		if(tmp==null || tmp.length()==0){
			return new Label(col-1,row,"");
		}
		
		switch(meta.getColumnType(col)){
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			o = new DateTime(col-1,row,rs.getDate(col),formatDate);
			break;
		case Types.DECIMAL:
		case Types.DOUBLE:
			o = new jxl.write.Number(col-1,row,rs.getDouble(col),formatNumber);
			break;
		case Types.SMALLINT:
		case Types.INTEGER:
			o = new jxl.write.Number(col-1,row,rs.getDouble(col),formatNumber);
			break;
		case Types.NUMERIC:
			o = new jxl.write.Number(col-1,row,rs.getDouble(col),formatNumber);
			break;
		default:
			o = new Label(col-1,row,SqlUtil.fromLocal(rs.getString(col)));
		}
		return o;
	}
	
	public void write() throws WriteException, IOException{
		book.write();
		book.close();
	}
	private int sheetCount=0;
	private WritableWorkbook book ;
	
	public final static WritableCellFormat formatTitle=new WritableCellFormat(new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.WHITE));
	public final static WritableCellFormat formatNumber=new WritableCellFormat(new WritableFont(WritableFont.TAHOMA,10));
	public final static WritableCellFormat formatDate=new WritableCellFormat(new WritableFont(WritableFont.TIMES,10));
	static {
		try {
			formatTitle.setBackground(Colour.DARK_BLUE);
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
}
