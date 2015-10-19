package com.royalstone.certificate.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Element;

import com.royalstone.certificate.bean.CerUpAddCert;
import com.royalstone.certificate.bean.CerUpCustomer;
import com.royalstone.certificate.bean.CerUpProduct;
import com.royalstone.certificate.bean.CerUpTask;
import com.royalstone.certificate.bean.Config;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author 白剑 处理对外上传证照接口
 */
public class UpTaskListDAO {
	final private Connection	conn;

	public UpTaskListDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	/**
	 * 获取待处理任务清单
	 * 
	 * @param parmas
	 * @return
	 */
	public Element getTaskList(Map parmas) {
		Filter filter = cookFilter(parmas);
		String sql_where = "";
		if (filter.count() > 0) {
			sql_where = " where " + filter.toString();
		}

		String sql = " select ta.taskid,ta.sheetid,ta.seq,ta.flag,ta.tries,ta.checker,ta.checktime,ta.note, "
				+ " tc.venderid,te.vendername,tb.certificateid,tb.certificatename,td.ctname,tc.type,tb.seqno "
				+ " from certasklist ta "
				+ " join certificateitem tb on (ta.sheetid=tb.sheetid and ta.seq=tb.seqno) "
				+ " join certificate tc on (ta.sheetid=tc.sheetid)"
				+ " join certificatetype td on (td.ctid=tb.ctid)"
				+ " join vender te on (te.venderid=tc.venderid) " + sql_where;

		return SqlUtil.getRowSetElement(conn, sql, "list");
	}

	/**
	 * 导出供应商清单，execl
	 * 
	 * @param file
	 * @throws SQLException
	 * @throws IOException
	 */
	public void getExportVenderList(File file) throws SQLException, IOException {
		String sql = " select ta.venderid,tc.vendername,tb.ctname from cert_export_vender ta "
				+ " join certificatetype tb on ta.ctid=tb.ctid "
				+ " join vender tc on tc.venderid=ta.venderid ";

		String[] title = { "供应商编码", "供应商名称", "证照类型" };
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		excel.cookExcelFile(file, rs, title, "导出证照供应商清单");
		SqlUtil.close(rs);
	}

	public void setExportVenderList(ArrayList<String[]> list) {
		String sql_del = " delete from cert_export_vender where venderid=? and ctid=? ";
		String sql_ins = " insert into cert_export_vender(venderid,ctid) values(?,?) ";

		for (String[] ss : list) {
			String venderid = ss[0];
			String ctid = ss[1];
			SqlUtil.executePS(conn, sql_del, new Object[] { venderid, ctid });
			SqlUtil.executePS(conn, sql_ins, new Object[] { venderid, ctid });
		}
	}

	/**
	 * 取得待处理的任务，前1000行
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<CerUpTask> getTask() throws SQLException {
		ArrayList list = new ArrayList<CerUpTask>();
		String sql = "select * from certasklist where flag=0 or (flag=-1 and tries<5) and rownum<=1000 order by tries,taskid";
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		while (rs.next()) {
			CerUpTask tmp = new CerUpTask();
			tmp.setTaskid(rs.getInt("taskid"));
			tmp.setSheetid(rs.getString("sheetid"));
			tmp.setSeq(rs.getInt("seq"));
			list.add(tmp);
		}
		return list;
	}

	/**
	 * 标记为错误状态
	 * 
	 * @param taskid
	 * @param note
	 */
	public void setError(int taskid, String note) {
		String sql = "update certasklist set tries=tries+1,flag=-1,exporttime=sysdate,note=? where taskid=?";
		SqlUtil.executePS(conn, sql, new Object[] { note, taskid });
	}

	/**
	 * 重置状态为待传
	 * 
	 * @param taskid
	 * @param note
	 */
	public void setRedo(int taskid, String note) {
		String sql = "update certasklist set tries=0,flag=0,exporttime=null,note=? where taskid=?";
		SqlUtil.executePS(conn, sql, new Object[] { note, taskid });
	}

	/**
	 * 标记为成功，并转到log表
	 * 
	 * @param taskid
	 * @throws SQLException
	 */
	public void setSuccess(int taskid) throws SQLException {
		String sql_ins = "insert into certasklistlog(taskid,sheetid,seq,flag,checker,tries,checktime,exporttime,note) "
				+ " select taskid,sheetid,seq,100,checker,tries,checktime,exporttime,'' from certasklist where taskid=? ";
		String sql_del = "delete from certasklist where taskid=?";

		SqlUtil.executePS(conn, sql_ins, new Object[] { taskid });
		SqlUtil.executePS(conn, sql_del, new Object[] { taskid });
	}

	public CerUpAddCert getAddCert(CerUpTask cerUpTask,Config cfg) throws SQLException, IOException {
		CerUpAddCert res = new CerUpAddCert();
		String venderid = "";
		String sql = "select b.certificateid CerCode,d.ctname CerType,c.venderid, "
				+ " b.barcodeid Procode,b.goodsname,to_char(sysdate,'YYYYMMDD') Ipc, to_char(sysdate,'YYYY-MM-DD') FrmDate,"
				+ " to_char(b.expirydate,'YYYY-MM-DD') ToDate,decode(c.vendertype,2,c.vendertypename,f.vendername) Pentityname "
				+ " from certasklist a,certificateitem b,certificate c,certificatetype d,vender f "
				+ " where a.sheetid=b.sheetid and a.seq=b.seqno and b.ctid=d.ctid and a.sheetid=c.sheetid "
				+ " and f.venderid=c.venderid and taskid=?";

		ResultSet rs = SqlUtil.queryPS(conn, sql, cerUpTask.getTaskid());

		if (rs.next()) {
			res.setCerCode(rs.getString("CerCode"));
			res.setIpccercode(rs.getString("goodsname"));
			res.setCerType(rs.getString("CerType"));
			res.setFrmDate(rs.getString("FrmDate"));
			res.setToDate(rs.getString("ToDate"));
			res.setPentitycode(rs.getString("Pentityname"));
			res.setPentityname(rs.getString("Pentityname"));

			res.setProcode(rs.getString("Procode"));
			res.setIpc(rs.getString("Ipc"));

			res.setIpcmark("");
			res.setEntitycode("");
			res.setEntityname("");

			venderid = rs.getString("venderid");
			res.setVenderID(venderid);
		}
		rs.close();

		// 取图片，只取第一张图片
		sql = "select imgfile from certificateitemimg where sheetid=? and seqno=? and imgseqno=1";
		rs = SqlUtil.queryPS(conn, sql, new Object[] { cerUpTask.getSheetid(), cerUpTask.getSeq() });
		if (rs.next()) {
			String filename = rs.getString(1);
			res.setPictype(filename.substring(filename.lastIndexOf(".") + 1, filename.length()));
			res.setIpcpath(filename);

			String url = cfg.getImageURL();
			url += "?site="+cfg.getSid()+"&venderid="+venderid+"&filename="+filename;
			byte[] mFileByte = getImg(url);
			res.setSpath(filename);
			res.setCerimage(mFileByte);
		} else {
			throw new IOException(cerUpTask.getTaskid() + "，图片无法读取");
		}
		return res;
	}

	
	public CerUpProduct getGoods(String procode) throws SQLException {
		CerUpProduct res = new CerUpProduct();
		String sql = "select * from goods where barcode=?";
		ResultSet rs = SqlUtil.queryPS(conn, sql, procode);
		if(rs.next()){
			res.setProCode(procode);
			res.setProName(rs.getString("goodsname"));
			res.setArtNo(rs.getString("goodsid"));
			res.setProType("0103:其他食品");
			res.setProSpec(rs.getString("spec"));
			res.setBrand("");
			res.setUnit(rs.getString("unitname"));
			res.setPrice(0);
			res.setOutDay(360);
			res.setAuthType("生产许可证");
			res.setAuthNumber("");
			res.setpEntityCode("");
			res.setEntityName("");
			res.setpTel("");
			res.setpAddress("");
			res.setEntityCode("");
			res.setEntityName("");
		}
		SqlUtil.close(rs);
		return res;
	}

	public CerUpCustomer getCustomer(String venderid,String sheetid) throws SQLException {
		CerUpCustomer res = new CerUpCustomer();
		String sql = "select a.*,c.certificateid from vender a,certificate b,certificateitem c where a.venderid=b.venderid and b.sheetid=c.sheetid and c.ctid=1 and a.venderid=? and b.sheetid=?";
		ResultSet rs = SqlUtil.queryPS(conn, sql, new String[]{venderid,sheetid});
		if(rs.next()){
			res.setEntityCode(rs.getString("certificateid"));
			res.setEntityName(rs.getString("vendername"));
			res.setManager(rs.getString("vendername"));
			res.setTel(rs.getString("telno"));
			res.setAddress(rs.getString("address"));
			res.setEntityType("公司企业");
			res.setEntityMode("生产批发");
			res.setFax(rs.getString("faxno"));
			res.setEmail(rs.getString("email"));
			res.setWebSite(rs.getString("website"));
			res.setCustomerType(0);
		}
		SqlUtil.close(rs);
		return res;
	}
	
	private byte[] getImg(String uri) throws IOException {
		byte[] b = null;
		HttpMethod get = new GetMethod(uri);
		HttpClient httpclient = new HttpClient();
		int code = httpclient.executeMethod(get);
		if (code == 200) {
			b = get.getResponseBody();
		} else {
			throw new IOException("远程图片读取失败");
		}
		get.releaseConnection();
		return b;
	}

	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("ta.sheetid IN (" + val_sheetid.toString4String() + ") ");

			return filter;
		}

		ss = (String[]) map.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			filter.add("tc.venderid=" + ValueAdapter.toString4String(ss[0]));
		}

		ss = (String[]) map.get("flag");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_status = new Values(ss);
			filter.add("ta.flag IN (" + val_status.toString4String() + ") ");
		}

		return filter;
	}

	
}
