package com.royalstone.util.sql.sqlformat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StringCheckUtil {
    //////////////////////////////////////////////////////////   
    //Properties   

    private static Set BOOLEAN_VALUE_SET;


    //////////////////////////////////////////////////////////   
    //static   

    static {
        BOOLEAN_VALUE_SET = new HashSet();
        BOOLEAN_VALUE_SET.add("true");
        BOOLEAN_VALUE_SET.add("false");
    }


    //////////////////////////////////////////////////////////   
    //public static method   

    public static boolean isNull(String str) {
        return str == null;
    }

    public static boolean isEmpty(String str) {
        return isNull(str) || str.length() == 0;
    }

    public static boolean isTrimedEmpty(String str) {
        return isNull(str) || str.trim().length() == 0;
    }

    public static String nvl(String str) {
        return isNull(str) ? "" : str;
    }

    public static boolean isBooleanType(String str) {
        return !isEmpty(str)
                && BOOLEAN_VALUE_SET.contains(str.toLowerCase())
                ;
    }

    public static boolean isPermitted(Set permittedValueSet, String value) {
        if (value == null
                || value.length() == 0
                || permittedValueSet == null
                || permittedValueSet.size() == 0
                ) {
            return true;
        }

        int limit = 0;
        for (Iterator i = permittedValueSet.iterator(); i.hasNext(); ) {
            String permittedValue = (String) i.next();
            int permittedValueLength = (permittedValue == null)
                    ? 0 : permittedValue.length();
            limit = (permittedValueLength > limit)
                    ? permittedValueLength : limit;
        }

        if (limit == 0
                ) {
            return true;
        }

        value = value.toUpperCase();
        int valueLength = value.length();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < valueLength; i++) {
            sb.append(value.charAt(i));
            if (permittedValueSet.contains(sb.toString())
                    ) {
                sb = new StringBuffer();
                continue;
            }
            if (sb.length() >= limit
                    || i == valueLength - 1
                    ) {
                return false;
            }
        }
        return true;

    }
}  
