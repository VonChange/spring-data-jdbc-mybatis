package com.vonchange.jdbc.mybatis.core.util;

import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.jdbc.config.ConstantJdbc;

public class JdbcMybatisUtil {

    public static String interfaceNameMd(Class<?> interfaceDefine){
        String interfaceName =ConstantJdbc.SqlPackage
                +StringPool.DOT+interfaceDefine.getSimpleName();

        boolean flag = UtilAll.UFile.isClassResourceExist(
                UtilAll.UFile.classPath(interfaceName)+StringPool.markdown_suffix);
        if(flag){
            return interfaceName;
        }
        String fullName =interfaceDefine.getName();
        flag = UtilAll.UFile.isClassResourceExist(UtilAll.UFile.classPath(fullName)+ StringPool.markdown_suffix);
        if(flag){
            return fullName;
        }
        return null;
    }

}
