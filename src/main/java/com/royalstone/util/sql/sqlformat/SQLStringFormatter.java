package com.royalstone.util.sql.sqlformat;

import java.util.*;

/**
 * 格式SQL语句
 *
 * @since JDK1.4
 */
public class SQLStringFormatter {

    private static Class clazz = SQLStringFormatter.class;

    public static String formatSQLString(String sqlString) {
        String strSrc = toUpperCaseExceptConst(sqlString);

        strSrc = strSrc.replaceAll("\t", "    ");
        strSrc = strSrc.replaceAll("SELECT ", "SELECT \n");
        strSrc = strSrc.replaceAll(" \\(", " \n\\(\n ");
        strSrc = strSrc.replaceAll(" \\) ", " \n\\) \n");
        strSrc = strSrc.replaceAll(",", ", \n");
        strSrc = strSrc.replaceAll(" VALUES ", "\nVALUES \n");
        strSrc = strSrc.replaceAll(" FROM ", "\nFROM \n");
        strSrc = strSrc.replaceAll(" SET ", "\nSET \n");
        strSrc = strSrc.replaceAll(" LEFT ", "\nLEFT ");
        strSrc = strSrc.replaceAll(" INNER ", "\nINNER ");
        strSrc = strSrc.replaceAll(" ON ", " ON\n");
        strSrc = strSrc.replaceAll(" WHERE ", "\nWHERE ");
        strSrc = strSrc.replaceAll(" AND ", "\nAND ");
        strSrc = strSrc.replaceAll(" OR ", "\nOR ");
        strSrc = strSrc.replaceAll(" UNION ", "\nUNION\n");
        strSrc = strSrc.replaceAll(" ORDER ", "\nORDER ");
        strSrc = strSrc.replaceAll(" GROUP ", "\nGROUP ");
        // strSrc = strSrc.replaceAll(" BY " , " BY \n");
        strSrc = strSrc.replaceAll("\n\n", "\n");

        // StepTimeStampUtil.logTimeStamp(clazz);

        Stack stack = new Stack();

        boolean additionalIndentFlg = false;
        StringBuffer sb = new StringBuffer(strSrc.length());
        String[] lineArray = strSrc.split("\n");
        for (int i = 0; i < lineArray.length; i++) {
            String line = lineArray[i];
            if (line == null) {
                continue;
            }
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }

            int level = stack.size();

            if (line.indexOf("(") != -1) {
                stack.push("(");
            }

            int indexRight = line.indexOf(")");
            if (indexRight != -1) {
                stack.pop();
                if (indexRight == 0) {
                    level--;
                }
            }

            if (containsKeyword(line, "FROM")) {
                additionalIndentFlg = false;
            }

            if (additionalIndentFlg) {
                level++;
            }

            sb.append(createIndentByLevel(level));
            sb.append(line).append("\n");

            if (containsKeyword(line, "SELECT")) {
                additionalIndentFlg = true;
            }

        }

        // StepTimeStampUtil.logTimeStamp(clazz);

        return sb.toString();
    }

    private static Set DIVISION_MARK_SET;

    static {
        DIVISION_MARK_SET = new HashSet();
        DIVISION_MARK_SET.add(" ");
        DIVISION_MARK_SET.add("(");
        DIVISION_MARK_SET.add(")");
        DIVISION_MARK_SET.add("\t");
        DIVISION_MARK_SET.add("\r");
        DIVISION_MARK_SET.add("\n");
    }

    private static boolean containsKeyword(String str, String keyword) {
        if (StringCheckUtil.isEmpty(keyword) || StringCheckUtil.isEmpty(str)) {
            return false;
        }

        int index = str.indexOf(keyword);
        if (index == -1) {
            return false;
        }

        if (index > 0 && !DIVISION_MARK_SET.contains(String.valueOf(str.charAt(index - 1)))) {
            return false;
        }

        int keywordLength = keyword.length();

        if (index < str.length() - 1 - keywordLength
                && !DIVISION_MARK_SET.contains(String.valueOf(str.charAt(index + keywordLength + 1)))) {
            return false;
        }

        return true;
    }

    // Map＜Integer level, String indentStr＞
    private static Map INDENT_MAP = new HashMap();

    private static String createIndentByLevel(int levelValue) {
        Integer level = Integer.valueOf(String.valueOf(levelValue));
        if (INDENT_MAP.containsKey(level)) {
            return (String) INDENT_MAP.get(level);
        }

        // String indentStr = StringUtils.leftPad("", level.intValue() * 4,
        // ConstantMark.CHAR_SPACE);
        int len = level.intValue() * 4;
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            sb.append(ConstantMark.SPACE_CHAR);
        }
        String indentStr = sb.toString();

        synchronized (clazz) {
            INDENT_MAP.put(level, indentStr);
        }

        return indentStr;
    }

    private static String toUpperCaseExceptConst(String str) {
        if (StringCheckUtil.isTrimedEmpty(str)) {
            return str;
        }

        StringBuffer sb = new StringBuffer();
        String[] strArray = str.split(ConstantMark.SINGLE_QUOTAION);
        for (int i = 0; i < strArray.length; i++) {
            sb.append((i > 0) ? ConstantMark.SINGLE_QUOTAION : ConstantMark.BLANK);
            String line = StringCheckUtil.nvl(strArray[i]);
            if ((i % 2 == 0)) {
                line = trimSpace2One(line.toUpperCase());
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private static String trimSpace2One(String str) {
        if (StringCheckUtil.isEmpty(str)) {
            return str;
        }

        StringBuffer sb = new StringBuffer();
        char[] charArray = str.toCharArray();
        boolean spaceStartFlg = false;
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == ConstantMark.SPACE_CHAR || charArray[i] == ConstantMark.FULL_SPACE_CHAR
                    || charArray[i] == ConstantMark.TAB_CHAR) {
                spaceStartFlg = true;
                continue;
            }
            if (spaceStartFlg) {
                sb.append(ConstantMark.SPACE_CHAR);
                spaceStartFlg = false;
            }
            sb.append(charArray[i]);
        }
        if (spaceStartFlg) {
            sb.append(ConstantMark.SPACE_CHAR);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String sqlString = "select distinct pbp.package_name,pbp.package_order,pbp.child_order      from be_t_repeal         br,           be_t_repeal_package brp,           be_t_project_detail bpd,           pa_t_bid_package    pbp,           pa_t_bid_object     pbo,           be_t_providers      bp     where br.repeal_id = brp.repeal_id       and brp.project_device_id = bpd.project_device_id       and bpd.bid_package_id = pbp.bid_package_id       and pbp.bid_object_id = pbo.bid_object_id       and brp.provider_id = bp.provider_id       and br.repeal_id = p_repeal_id     order by pbp.package_order||pbp.child_order,pbp.child_order,pbp.package_name";
        System.out.println(formatSQLString(sqlString));
    }
}

