package com.vonchange.mybatis.config;

public class Constant {
    private Constant(){
        throw new IllegalStateException("Utility class");
    }
    public static final  String PARAM_NOT_NULL="Parameter name must not be null";
    public static final String NOLOWER = "@-lower";
    public static final String NOORM = "@-orm";
    public static  class  Dialog{
        public static final String MYSQL = "mysql";
        public static final String ORACLE = "oracle";
    }
    public static  class  PageParam{
        public static final String COUNT = "_count";
        public static final String AT = "@";
    }
    public  static final String ISSQLFLAG= "@sql";
    public  static final String ISMDFLAG= "@md";
    public  static final String MAINFLAG= "main";
    public  static final String MDID= "### id";
    public  static final String MDVERSION= "#### version";
    public  static final String MDINITVERSION= "1.0";
 /*   public static  class  DataSource{
        public static final String DEFAULT = "dataSource";
        public static final String FLAG = "@ds:";
    }*/
    public static final String COUNTFLAG = "Count";
    public static final String MAPFIELDSPLIT = "#";
    public static final String TABLES = "tables";

}
