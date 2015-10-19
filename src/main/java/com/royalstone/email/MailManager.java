package com.royalstone.email;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;

/**
 * �ʼ������ߣ�����ҵ���߼�
 * 
 * @author baibai
 * 
 */
public class MailManager {

	public MailManager(Connection conn, Token token) {
		this.conn = conn;
		this.token = token;
	}

	/**
	 * ��ȡ�ʼ���Ϣ
	 * 
	 * @param reader
	 * @param mailid
	 * @return
	 * @throws SQLException
	 */
	public Element readMail(int mailid, int type) throws SQLException {
		Element elm = null;
		if (type == 100) {
			MailStatistician ms = new MailStatistician(conn);
			ms.setReadTime(token.loginid, mailid);// ���ö�ȡʱ��
		}

		MailBrowser mb = new MailBrowser(conn);
		elm = mb.getMail(mailid);

		return elm;
	}

	/**
	 * ȡ�ø����������б�
	 * 
	 * @param reader
	 * @param type
	 * @param receivedate 
	 * @param senddate 
	 * @param receiver 
	 * @param sender 
	 * @return
	 * @throws SQLException
	 */
	public Element getMailList(String reader, int type,String minSendDate, String maxSendDate, String sender, String receiver, String minReceiveDate, String maxReceiveDate) throws SQLException {
		Element elm = null;

		MailBrowser browse = new MailBrowser(conn);
		elm = browse.scanMailList(reader, type, minSendDate, maxSendDate,sender,receiver,minReceiveDate,maxReceiveDate);

		return elm;
	}


	public void saveMail(Element elm_xparam) throws Exception {
		String receiptor = elm_xparam.getChildText("receiptor");
		String cc = elm_xparam.getChildText("cc");
		String content = elm_xparam.getChildText("content");
		String title = elm_xparam.getChildText("title");
		String operation = elm_xparam.getChildText("operation");
		String fid = elm_xparam.getChildText("fileid");
		int fileid = Integer.parseInt(fid);

		String[] arrRece = checkReceiptor(receiptor, cc);

		MailBuilder mb = new MailBuilder(conn, token.loginid, receiptor, cc, title, content, fileid, arrRece);

		if (operation.equals("NewMail")) {// ���ʼ�
			String bid = elm_xparam.getChildText("backid");
			int backid = Integer.parseInt(bid);
			int newID = mb.saveMail(1);
			if (backid != -1) {//
				MailStatistician ms = new MailStatistician(conn);
				ms.setWriteBack(token.loginid, backid, newID);
			}

		} else if (operation.equals("Draft")) {// ��ݸ�
			mb.saveMail(0);
		}

	}

	/**
	 * ���ռ��ˣ���������װ�����飬��Set���˵��ظ���ַ
	 * 
	 * @param rece
	 * @param cc
	 * @return
	 */
	private Set fixReceiptor(String rece) {
		LinkedHashSet receSet = new LinkedHashSet();

		String[] arrRece = null;

		if (rece.length() > 0) arrRece = rece.split(",");

		if (arrRece != null) {
			for (int i = 0; i < arrRece.length; i++) {
				if (arrRece[i].length() > 0) receSet.add(arrRece[i]);
			}
		}

		return receSet;
	}

	/**
	 * ��֤�ռ��˵�ַ
	 * 
	 * @param rece
	 * @param cc
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public String[] checkReceiptor(String rece, String cc) throws NamingException, SQLException,
			InvalidDataException {
		Set receSet = fixReceiptor(rece);
		if (receSet.size() == 0) throw new InvalidDataException("����д�ռ��˵�ַ");

		receSet.addAll(fixReceiptor(cc));

		Iterator it = receSet.iterator();
		Collection receList = new LinkedList();

		MailGroup mg = new MailGroup(conn, token);
		MailHelper mh = new MailHelper(token);
		StringBuffer badAddr = new StringBuffer();

			if (token.isVender) {// ��Ӧ����
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (mh.hasUserById(conn, temp) && !mh.isVender(conn, temp)) {// ��������û������Ҳ��ǹ�Ӧ��
						receList.add(temp);
						// }else if( mg.hasMailGroupById(temp)){//��������ʼ���
						// receList.addAll( mg.getMailGroupMember(temp) );
					} else {// ���û�������
						badAddr.append(temp + ",");
					}
				}
			} else {
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (mh.hasUserById(conn, temp)) {// ��������û�
						receList.add(temp);
					} else if (mg.hasMailGroupById(temp)) {// ��������ʼ���
						receList.addAll(mg.getMailGroupMember(temp));
					} else {// ���û�������
						badAddr.append(temp + ",");
					}
				}
			}

		if (badAddr.length() > 1) throw new InvalidDataException("�ʼ���ַ:" + badAddr.toString()
				+ "�����ڣ����޸���ȷ���ٷ���");

		Object[] o = receList.toArray();
		String[] s = new String[o.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = (String) o[i];
		}

		return s;
	}

	/**
	 * �ƶ��ʼ������ļ���(�ı��ʼ�״̬)
	 * 
	 * @param mailid
	 * @param type
	 * @param action
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public void moveMail(String mailid, String type, String action) throws SQLException, InvalidDataException {
		String[] arrMailid = mailid.split(",");
		int[] arrmid = new int[arrMailid.length];
		for (int i = 0; i < arrMailid.length; i++) {
			arrmid[i] = Integer.parseInt(arrMailid[i]);
		}
		MailCargador mc = new MailCargador(conn, arrmid);

		if (action.equals("ReceiptMail")) {
			mc.moveReceiptToRecycle(token.loginid);
		} else if (action.equals("SendMail")) {
			mc.moveSendToRecycle();
		} else if (action.equals("DraftMail")) {
			mc.moveSendToRecycle();
		} else if (action.equals("Recover")) {
			if (type.equals("send")) {
				mc.recoverSend();
			} else if (type.equals("receipt")) {
				mc.recoverReceipt(token.loginid);
			} else {
				throw new InvalidDataException("receipt not define");
			}
		} else if (action.equals("DelCompletely")) {
			if (type.equals("send")) {
				mc.moveSendToDel();
			} else if (type.equals("receipt")) {
				mc.moveReceiptToDel(token.loginid);
			} else {
				throw new InvalidDataException("receipt not define");
			}
		} else {
			throw new InvalidDataException("action not define");
		}

	}

	/**
	 * �ƶ�ȫ���ʼ�(���ĳ�ļ���)
	 * 
	 * @param action
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public void moveAllMail(String action) throws InvalidDataException, SQLException {
		MailBrowser mb = new MailBrowser(conn);
		int[] arrMailId = null;
		if (action.equals("ReceiptMail")) {
			// �ռ��Ƶ�����
			arrMailId = mb.scanMailId(token.loginid, MailBrowser.RECE_MAIL);
			MailCargador mc = new MailCargador(conn, arrMailId);
			mc.moveReceiptToRecycle(token.loginid);
		} else if (action.equals("SendMail")) {
			// �����Ƶ�����
			arrMailId = mb.scanMailId(token.loginid, MailBrowser.SEND_MAIL);
			MailCargador mc = new MailCargador(conn, arrMailId);
			mc.moveSendToRecycle();
		} else if (action.equals("DraftMail")) {
			// �ݸ��ƶ�������
			arrMailId = mb.scanMailId(token.loginid, MailBrowser.DRAFT_MAIL);
			MailCargador mc = new MailCargador(conn, arrMailId);
			mc.moveSendToRecycle();
		} else if (action.equals("Recover")) {
			// �ָ�����վ����ʼ�
			arrMailId = mb.scanMailId(token.loginid, MailBrowser.RECY_MAIL);
			MailCargador mc = new MailCargador(conn, arrMailId);
			mc.recoverSend();
			mc.recoverReceipt(token.loginid);
		} else if (action.equals("DelCompletely")) {
			// ɾ������վ����ʼ�
			arrMailId = mb.scanMailId(token.loginid, MailBrowser.RECY_MAIL);
			MailCargador mc = new MailCargador(conn, arrMailId);
			mc.moveSendToDel();
			mc.moveReceiptToDel(token.loginid);
		} else {
			throw new InvalidDataException("action not define");
		}
	}

	/**
	 * �õ����ʼ�(δ�Ķ�)�ʼ�����
	 * 
	 * @return
	 */
	public int getNewMailCount() throws SQLException {
		int count = 0;
		MailBrowser mb = new MailBrowser(conn);
		int[] arr_mailid = mb.scanMailId(token.loginid, MailBrowser.NEW_MAIL);
		if (arr_mailid != null) count = arr_mailid.length;
		return count;
	}

	/**
	 * �õ�Ĭ���ռ���
	 * 
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public String getDefaultReceiptor() throws NamingException, SQLException {
		String def = "";
		String[] arrDef = token.getEnv("defaultPayer");

		if (arrDef.length == 0) {
			def = "undefine";
		} else {
			def = arrDef[0];
		}
		return def;
	}

	/**
	 * ����Ĭ���û�,�����û�������
	 * 
	 * @param elm
	 * @throws NamingException
	 * @throws SQLException
	 */
	public String setDefaulter(Element elm) throws NamingException, SQLException {
		MailHelper mh = new MailHelper(token);
		return mh.setUserEnvironment(elm);
	}

	/**
	 * ���������ʼ����û�
	 * 
	 * @param elm
	 * @throws NamingException
	 * @throws SQLException
	 */
	public String setMailGroupMember(Element elm) throws NamingException, SQLException {
		MailGroup mg = new MailGroup(conn, token);
		return mg.setMailGroupMember(elm);
	}

	/**
	 * ȡ���ʼ����б�
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Element getMailGroupList() throws SQLException {
		MailGroup mg = new MailGroup(conn, token);
		return mg.getMailGroupList();
	}

	public Element getMailGroupItemList(String groupId) throws SQLException {
		MailGroup mg = new MailGroup(conn, token);
		return mg.getMailGroupItemList(groupId);
	}

	/**
	 * ɾ��һ���ʼ���
	 * 
	 * @param groupId
	 * @throws SQLException
	 */
	public void delMailGroup(String groupId) throws SQLException {
		MailGroup mg = new MailGroup(conn, token);
		mg.delMailGroup(groupId);
	}

	public void addMailGroup(String groupId, String groupName) throws SQLException {
		MailGroup mg = new MailGroup(conn, token);
		mg.createMailGroupMember(groupId, groupName);
	}

	public void delMailGroupItem(String groupId, String loginId) throws SQLException {
		MailGroup mg = new MailGroup(conn, token);
		String[] arrLoginId = loginId.split(",");
		mg.delMailGroupItem(groupId, arrLoginId);
	}

	/**
	 * ͳ���ʼ��շ����
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */

	public int statisticMailBack(Map map, File file) throws InvalidDataException, SQLException, IOException {
		MailStatistician ms = new MailStatistician(conn);
		return ms.statisticMailBack(map, file);
	}

	/**
	 * �õ��ʼ��շ������excel��ʽ
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public int getMailBackDetailBook(Map map, File file) throws InvalidDataException, SQLException,
			IOException {
		MailStatistician ms = new MailStatistician(conn);

		return ms.getMailBackDetailBook(map, file);
	}

	/**
	 * �ʼ�������Ŀ����ͳ��
	 * 
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public int getMailSendCount(Map map, File file) throws InvalidDataException, SQLException, IOException {
		MailStatistician ms = new MailStatistician(conn);

		return ms.getSenderCount(map, file);
	}

	final private Connection	conn;
	final private Token			token;

}
