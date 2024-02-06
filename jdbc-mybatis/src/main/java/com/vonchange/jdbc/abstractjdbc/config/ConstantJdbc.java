package com.vonchange.jdbc.abstractjdbc.config;

/**
 * Created by 冯昌义 on 2018/3/20.
 */
public class ConstantJdbc {
    private ConstantJdbc() { throw new IllegalStateException("Utility class");}

    public  static final String DataSourceDefault= "dataSource";
    public  static final String EntityType= "entityType";
    public  static final String SqlPackage= "sql";
    public  static final String SQL_START= "sql";
    public  static final String SQL_DIALECT_ENV= "SQL_DIALECT_ENV";
    public static final String COUNTFLAG = "Count";
    public static final String MAPFIELDSPLIT = "#";
}
