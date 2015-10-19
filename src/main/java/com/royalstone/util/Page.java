/*
 * Created on 2005-6-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.util;

import org.jdom.Element;

/**
 * @author Snake Eyes
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Page {
	private int pageNo;

	private int pageSize;

	private int recordCount;

	private Page() {
	}

	public Page(int pageNo, int pageSize, int recordCount) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.recordCount = recordCount;
	}

	/**
	 * 输入总行数和每一页显示的行数，用来判断总共页数
	 * 暂时不用
	 * @param total_row
	 * @param page_size
	 * @return
	 */
	/*
	private String getTotalPage(int total_row, int page_size) {
		if (total_row == 0)
			return "0";
		else if (total_row <= page_size)
			return "1";
		else if (total_row % page_size == 0)
			return String.valueOf(total_row / page_size);
		else
			return String.valueOf(total_row / page_size + 1);
	}
	*/

	private String getTotalPage() {
		if (recordCount == 0)
			return "0";
		else if (recordCount <= pageSize)
			return "1";
		else if (recordCount % pageSize == 0)
			return String.valueOf(recordCount / pageSize);
		else
			return String.valueOf(recordCount / pageSize + 1);
	}

	public void loadPageElement(Element elm) {
		elm.addContent(new Element("total_row").setText("" + recordCount));
		elm.addContent(new Element("total_page").setText("" + getTotalPage()));
		elm.addContent(new Element("cur_page").setText("" + pageNo));
		elm.addContent(new Element("page_size").setText("" + pageSize));
		elm.addContent(new Element("row_start").setText(""
						+ getRecordNoFrom()));
		elm.addContent(new Element("row_end").setText("" + getRecordNoTo()));
	}

	private int getRecordNoFrom() {
		if (pageNo < 1 || pageSize <= 0) {
			return 0;
		}
		int rn = (pageNo - 1) * pageSize + 1;
		if (rn > recordCount) {
			rn = recordCount;
		}
		return rn;
	}

	public int getRecordNoTo() {
		if (pageNo < 1 || pageSize <= 0) {
			return 0;
		}
		int rn = (pageNo) * pageSize;
		if (rn > recordCount) {
			rn = recordCount;
		}
		return rn;
	}
}