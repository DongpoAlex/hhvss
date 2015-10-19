package com.royalstone.vss.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Map;

import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;
import com.royalstone.workbook.Workbook;

/**
 * KPI报表
 * 
 * @author baibai
 * 
 */
public class KPI {
	final private Connection	conn;
	final private Map			map;
	final private String		sql_sel,sql_count;
	final private String sql_order=" order by y.item ";
	final private String monthid;
	public KPI(Connection conn, Map map) throws InvalidDataException {
		this.conn = conn;
		this.map = map;
		String[]ss = (String[]) map.get("monthid");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			this.monthid=ss[0];
		}else{
			throw new InvalidDataException("monthid is null");
		}
		ss = (String[]) map.get("sgroupid");
		if (ss != null && ss.length > 0 && ss[0].length() > 0 && !ss[0].toLowerCase().equals("all")) {
			sql_count =" select count(*) from t_vendersgroupid_scorecard_yb y";
			
			sql_sel =" select y.monthid,y.venderid,y.vendername,y.sgroupid,substr(y.item,4,length(y.item)) item,substr(y.item,1,2) auxitem, " +
			" y.month_lj,y.year_lj,y.month_lj_ly,y.year_lj_ly, " +
			" nvl(b.budget,0) budget,nvl(b.budget_year,0) budget_year " +
			" from t_vendersgroupid_scorecard_yb y " +
			" left join t_vendersgroupid_budget b on (b.monthid=y.monthid and b.venderid=y.venderid and b.item=y.item) ";
		} else {
			sql_count =" select count(*) from t_vender_scorecard_yb y";
			
			sql_sel =" select y.monthid,y.venderid,y.vendername,'ALL' sgroupid,substr(y.item,4,length(y.item)) item,substr(y.item,1,2) auxitem, " +
			" y.month_lj,y.year_lj,y.month_lj_ly,y.year_lj_ly, " +
			" nvl(b.budget,0) budget,nvl(b.budget_year,0) budget_year " +
			" from t_vender_scorecard_yb y " +
			" left join t_vender_budget b on (b.monthid=y.monthid and b.venderid=y.venderid and b.item=y.item) ";
		}

	}

	/**
	 * 获得符合查询条件的xml格式文档
	 * 
	 * @param map
	 *            查询条件
	 * @return xml格式文档
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public Element getKPI() throws SQLException, InvalidDataException {
		Element elm_data = null;

		Filter filter = cookFilter(map);
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		int count = getCount(sql_where);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = sql_sel + sql_where + sql_order;
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_data = adapter.getRowSetElement("report", "row", 1, VSSConfig.getInstance().getRowsLimitSoft());
		rs.close();
		pstmt.close();

		int rows = adapter.rows();
		elm_data.setAttribute("rows", "" + rows);
		elm_data.setAttribute("reportname", "kpi");

		return elm_data;
	}

	/**
	 * 查询符合查询条件的数目
	 * 
	 * @param sql_where
	 *            查询条件
	 * @return 符合查询条件的数目
	 * @throws SQLException
	 */
	private int getCount(String sql_where) throws SQLException {
		int count = 0;

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_count  + sql_where);
		if (rs.next()) count = rs.getInt(1);
		rs.close();
		stmt.close();

		return count;
	}

	/**
	 * @param map
	 * @return
	 */
	private Filter cookFilter(Map map) {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			filter.add(" y.venderid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("monthid");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			filter.add(" y.monthid = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("sgroupid");
		if (ss != null && ss.length > 0 && ss[0].length()>0) {
			filter.add(" y.sgroupid = " + Values.toString4String(ss[0]));
		}

		/*20101220 不展示项目
		16_OI金额(元)
		18_投资回报周期(月)
		05_毛利率(%)
		17_OI率(%)
		*/
		filter.add(" substr(y.item,1,2) not in ('05','16','17','18') " );
		return filter;
	}


	/**
	 * 此方法把查询结果输出到一个EXCEL兼容的XML文件中
	 * 
	 * @author mengluoyi 2008-03-29
	 * @param map
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public void toExcel(File file) throws SQLException, InvalidDataException, IOException, RowsExceededException, WriteException {
		Filter filter = cookFilter(map);
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		int count = getCount(sql_where);
		if (count > VSSConfig.getInstance().getRowsLimitHard()) throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = sql_sel + sql_where + sql_order;

		String year = monthid.substring(0, 4)+"年";
		String month = monthid.substring(4, 6)+"月";
		String upyear = (Integer.parseInt(monthid.substring(0, 4))-1)+"年";
		
		String[] title = { "项目", upyear+month+"数据", year+month+"数据", year+month+"预算", year+month+"与"+upyear+month+"对比", year+month+"预算达成率", upyear+"01至"+month+"数据", year+"01至"+month+"数据",year+"01至"+month+"预算",year+"与"+upyear+"01至"+month+"对比",year+"01至"+month+"预算达成率" };

		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		WritableCellFormat totalx1Format = new WritableCellFormat(new WritableFont(WritableFont.COURIER,14,WritableFont.BOLD));
		totalx1Format.setAlignment(Alignment.LEFT); 
		totalx1Format.setBackground(Colour.LIGHT_GREEN);
		WritableCellFormat totalx2Format = new WritableCellFormat(new WritableFont(WritableFont.COURIER,16,WritableFont.BOLD));
		totalx2Format.setAlignment(Alignment.CENTRE); 
		
		FileOutputStream fout = new FileOutputStream( file );
		Workbook book = new Workbook(fout);
		WritableSheet sheet = book.makeSheet(monthid+"月度供应商业绩表现卡", 1);
		
		for (int i = 0; i < title.length; i++) {
			Label label=new Label(i,5,title[i]);
			sheet.addCell(label);
		}
		
		int i = 6;
		DecimalFormat df=new DecimalFormat("#.00");
		while(rs.next()){
			if(i==6){
				String major = rs.getString("sgroupid");
				String venderid= rs.getString("venderid");
				String vendername = rs.getString("vendername");
				sheet.mergeCells(0, 0, 10, 0);
				sheet.mergeCells(0, 1, 10, 1);
				sheet.mergeCells(0, 2, 10, 2);
				sheet.mergeCells(0, 3, 10, 3);
				sheet.addCell( new Label(0,0,"课类:"+major,totalx2Format));
				sheet.addCell( new Label(0,1,"供应商编码:"+venderid,totalx2Format));
				sheet.addCell( new Label(0,2,"供应商名称:"+vendername,totalx2Format));
				sheet.addCell( new Label(0,3,"数据时间段:"+monthid,totalx2Format));
			}
			String auxitem = rs.getString("auxitem");
			if(auxitem.equals("01")){
				sheet.mergeCells(0, i, 10, i);
				sheet.addCell(new Label(0,i,"销售",totalx1Format));
				i++;
			}
			if(auxitem.equals("08")){
				sheet.mergeCells(0, i, 10, i);
				sheet.addCell(new Label(0,i,"库存",totalx1Format));
				i++;
			}
			if(auxitem.equals("11")){
				sheet.mergeCells(0, i, 10, i);
				sheet.addCell(new Label(0,i,"送货",totalx1Format));
				i++;
			}
			if(auxitem.equals("19")){
				sheet.mergeCells(0, i, 10, i);
				sheet.addCell(new Label(0,i,"其它",totalx1Format));
				i++;
			}
			
			String item = rs.getString("item");
			double month_lj = rs.getDouble("month_lj");
			double year_lj = rs.getDouble("year_lj");
			double month_lj_ly = rs.getDouble("month_lj_ly");
			double year_lj_ly = rs.getDouble("year_lj_ly");
			double budget = rs.getDouble("budget");
			double budget_year = rs.getDouble("budget_year");
			sheet.addCell(new Label(0,i,item));
//			if(auxitem.equals("01")||auxitem.equals("02")||auxitem.equals("03")||auxitem.equals("04")
//					||auxitem.equals("06")||auxitem.equals("08")||auxitem.equals("09")||auxitem.equals("11")
//					||auxitem.equals("13")||auxitem.equals("14")||auxitem.equals("16")||auxitem.equals("18")
//					||auxitem.equals("20")){
				sheet.addCell(new Label(1,i,df.format(month_lj_ly),Workbook.formatNumber));
				sheet.addCell(new Label(2,i,df.format(month_lj),Workbook.formatNumber));
				sheet.addCell(new Label(3,i,df.format(budget),Workbook.formatNumber));
				String tmp = month_lj_ly!=0?df.format((month_lj/month_lj_ly-1)*100)+"%":"";
				sheet.addCell(new Label(4,i,tmp,Workbook.formatNumber));
				tmp = budget!=0?df.format((month_lj/budget)*100)+"%":"";
				sheet.addCell(new Label(5,i,tmp,Workbook.formatNumber));
				sheet.addCell(new Label(6,i,df.format(year_lj_ly),Workbook.formatNumber));
				sheet.addCell(new Label(7,i,df.format(year_lj),Workbook.formatNumber));
				sheet.addCell(new Label(8,i,df.format(budget_year),Workbook.formatNumber));
				tmp = year_lj_ly!=0?df.format((year_lj/year_lj_ly-1)*100)+"%":"";
				sheet.addCell(new Label(9,i,tmp,Workbook.formatNumber));
				tmp = budget_year!=0?df.format((year_lj/budget_year)*100)+"%":"";
				sheet.addCell(new Label(10,i,tmp,Workbook.formatNumber));
//			}else{
//				if(auxitem.equals("05")||auxitem.equals("07")||auxitem.equals("10")||auxitem.equals("12")
//						||auxitem.equals("15")||auxitem.equals("17")||auxitem.equals("19")){
//					
//				}
				
//				sheet.addCell(new Label(1,i,month_lj_ly+""));
//				sheet.addCell(new Label(2,i,month_lj+""));
//				sheet.addCell(new Label(3,i,budget+""));
//				String tmp = month_lj_ly!=0?(month_lj/month_lj_ly-1)*100+"%":"";
//				sheet.addCell(new Label(4,i,tmp));
//				tmp = budget!=0?(month_lj/budget)*100+"%":"";
//				sheet.addCell(new Label(5,i,tmp));
//				sheet.addCell(new Label(6,i,year_lj_ly+""));
//				sheet.addCell(new Label(7,i,year_lj+""));
//				sheet.addCell(new Label(8,i,budget_year+""));
//				tmp = year_lj_ly!=0?(year_lj/year_lj_ly-1)*100+"%":"";
//				sheet.addCell(new Label(9,i,tmp));
//				tmp = budget_year!=0?(year_lj/budget_year)*100+"%":"";
//				sheet.addCell(new Label(10,i,tmp));
//			}
				
			i++;
		}
		
		
		book.write();
		fout.close();
		
		rs.close();
		stmt.close();
	}
}
