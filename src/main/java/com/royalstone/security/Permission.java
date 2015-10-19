/*
 * Created on 2008-09-03
 */
package com.royalstone.security;

import java.util.ArrayList;

/**
 * @author baijian
 * 
 */
public class Permission {

	public Permission() {
		this.right = 0;
	}

	public Permission(int right) {
		this.right = right;
	}

	public boolean include(int action) {
		return !((action & right) == 0);
	}

	public void add(int right_add) {
		this.right |= right_add;
	}

	public static Permission merge(Permission a, Permission b) {
		int right = a.right | b.right;
		return new Permission(right);
	}

	public String toString() {
		ArrayList<String> list = new ArrayList<>();
		if (this.include(READ))
			list.add("查看");
		if (this.include(EDIT))
			list.add("编辑");
		if (this.include(INSERT))
			list.add("添加");
		if (this.include(DELETE))
			list.add("删除");
		if (this.include(PRINT))
			list.add("打印");
		if (this.include(FORMAT))
			list.add("修改打印格式");
		if (this.include(UNLOAD))
			list.add("导出数据");
		if (this.include(CHECK))
			list.add("审核一");
		if (this.include(CONFIRM))
			list.add("审核二");
		if (this.include(VERIFY))
			list.add("审核三");
		if (this.include(CATEGORY))
			list.add("分组");
		 String str = "";
		if (list.size() > 0)
			str =list.get(0);
		if (list.size() > 1)
			for (int i = 1; i < list.size(); i++)
				str += ", " + list.get(i);
		return str;
	}

	static public String  name4action(int action) {
		if (action == READ)
			return "查看";
		if (action == EDIT)
			return "编辑";
		if (action == INSERT)
			return "添加";
		if (action == DELETE)
			return "删除";
		if (action == PRINT)
			return "打印";
		if (action == FORMAT)
			return "修改打印格式";
		if (action == UNLOAD)
			return "导出数据";
		if (action == CHECK)
			return "消息查看";
		if (action == CATEGORY)
			return "分组";
		if (action == CONFIRM)
			return "类别权限";
		if (action == VERIFY)
			return "保留";
		return "未定义";
	}

	public int getRight() {
		return right;
	}

	private int right;

	final static public int READ = 1;

	final static public int EDIT = 2;

	final static public int INSERT = 4;

	final static public int DELETE = 8;

	final static public int PRINT = 16;	//打印

	final static public int FORMAT = 32;	//修改打印格式

	final static public int UNLOAD = 64;

	final static public int CHECK = 128; // 审核一

	final static public int CATEGORY = 256;	//消息查看

	final static public int CONFIRM = 512; // 类别权限

	final static public int VERIFY = 1024; // 审核三
}
