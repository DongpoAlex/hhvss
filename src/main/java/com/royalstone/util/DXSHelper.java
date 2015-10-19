package com.royalstone.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.royalstone.util.sql.DAOException;
import com.royalstone.util.sql.SqlUtil;

public class DXSHelper {
	public static String	SEE_NODE_NAME	= "SMP";

	static public void send2DXSTask(Connection conn,String desc, String topic, 
		String keys, String values, String action, String beforeExport,
		String afterExport, String beforeImport, String afterImport,String note) throws SQLException {
//		String sql = " call send_dxstask_mKey(?,?,?,?,?,?,?,?,?,?)";
//		SqlUtil.executeCall(conn, sql, new String[]{desc,topic,keys,values,action,beforeExport,afterExport,beforeImport,afterImport,note});
		String sql = "select Priority from DxsNodeTopic where Topic = ?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, topic);
		if(list.size()==0){
			throw new DAOException("尚未配置主题:"+topic);
		}
		String priority = list.get(0);
		String taskid = getDxsTaskid(conn);
		String[] ssKey = keys.split(",");
		String[] ssValue = values.split(",");
		
		if(ssKey.length!=ssValue.length){
			throw new DAOException("键值数目不匹配");
		}
		sql = "insert into DxsTaskKey (TaskID,Field,value) values(?,?,?)";
		for (int i = 0; i < ssKey.length; i++) {
			SqlUtil.executePS(conn, sql, new String[]{taskid,ssKey[i],ssValue[i]});
		}
		sql = "insert into DxsTaskFocus(TaskID,DataMode,type,Destination,Topic,Action,KeyValue,Description,Priority,Proc_Before_Export,Proc_After_Export,Proc_Before_Import,Proc_After_Import) values(?,'R','D',?,?,?,?,?,?,?,?,?,?)";
		SqlUtil.executePS(conn, sql, new String[]{taskid,desc,topic,action,values.length()>250?values.substring(0, 250):values,note,priority,beforeExport,afterExport,beforeImport,afterImport});
	}
	
	static public String getDxsTaskid(Connection conn) throws SQLException{
		String sheetid = null;
        String sql             	= "call dxs_gettaskid(?)";
        CallableStatement psc = conn.prepareCall(sql);
        psc.registerOutParameter(1, Types.VARCHAR);
        psc.execute();
        sheetid = psc.getString(1);
        psc.close();
        if( sheetid == null ) throw new SQLException( "取单传单号失败", "", -1 );
        return sheetid;
	}
}
