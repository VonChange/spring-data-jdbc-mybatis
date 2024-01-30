package com.vonchange.jdbc.abstractjdbc.config;

/**
 * Created by 冯昌义 on 2018/3/20.
 */
public class ConstantJdbc {
    private ConstantJdbc() { throw new IllegalStateException("Utility class");}
    public static  class  PageParam{
        public static final String COUNT = "Count";
        public static final String AT = "@";
    }
    public  static final String SQL_FLAG= "@sql";
    public static  class  DataSource{
        public static final String DEFAULT = "dataSource";
        public static final String FLAG = "@ds:";
    }
    public static final String COUNTFLAG = "Count";
    public static final String MAPFIELDSPLIT = "#";
}
