package com.royalstone.workbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * @author (版权归原作者) 用于读取excel
 */
public class ReadExcelTest {
	public static void main(String[] args) {
		jxl.Workbook rwb=null;
		try {
			InputStream is = new FileInputStream("x:/a.xlsx");
			// 声名一个工作薄
			rwb = Workbook.getWorkbook(is);
			// 获得工作薄的个数
			int sheetCount = rwb.getNumberOfSheets();
			if(sheetCount==0){
				throw new Exception("工作表数量为0");
			}
			// 在Excel文档中，第一张工作表的缺省索引是0
			Sheet st = rwb.getSheet(0);
			// 通用的获取cell值的方式,getCell(int column, int row) 行和列
			int rows = st.getRows();
			int cols = st.getColumns();
			System.out.println("当前工作表的名字:" + st.getName());
			System.out.println("总行数:" + rows);
			System.out.println("总列数:" + cols);
			for (int i = 1; i < rows; i++) {
				Cell c1 = st.getCell(0, i);
				Cell c2 = st.getCell(1, i);
				Cell c3 = st.getCell(2, i);
				System.out.println(MessageFormat.format("卡号：{0}、密码：{1}、日期：{2}", c1.getContents(),c2.getContents(),c3.getContents()));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("出错了");
		}
		finally {
			if(rwb!=null)
				rwb.close();
		}
	}
}
