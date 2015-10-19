package com.royalstone.util.sql;

public class DAOException extends RuntimeException {

    public DAOException(String e) {
        super(e);
    }

    public DAOException(Throwable e) {
        super(e);
    }

}
