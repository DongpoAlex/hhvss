package com.royalstone.util.sql;

public class Params {
    private String field;//列名
    private String compare;//比较符号
    private String vlaue;//值

    public Params(String field, String compare, String vlaue) {
        super();
        this.field = field;
        this.compare = compare;
        this.vlaue = vlaue;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCompare() {
        return this.compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    public String getVlaue() {
        return this.vlaue;
    }

    public void setVlaue(String vlaue) {
        this.vlaue = vlaue;
    }
}
