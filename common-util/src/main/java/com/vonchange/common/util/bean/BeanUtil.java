
package com.vonchange.common.util.bean;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.UtilAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanUtil {
    private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);
    private static Map<String,MethodAccessData> methodAccessDataMap = new ConcurrentHashMap<>();

    public static Object getPropertyT(Object entity, String property){
        MethodAccessData methodAccessData = methodAccessData(entity.getClass());
        int index = methodAccessData.getMethodIndexMap().get("get" + UtilAll.UString.capitalize(property));
        return methodAccessData.getMethodAccess().invoke(entity,index);
    }
    public static MethodAccessData methodAccessData(Class type){
        String id= type.getName();
        if(methodAccessDataMap.containsKey(id)){
            return methodAccessDataMap.get(id);
        }
        MethodAccessData methodAccessData=initMethodAccessData(type);
        methodAccessDataMap.put(id,methodAccessData);
        return methodAccessData;
    }

    private static synchronized  MethodAccessData  initMethodAccessData(Class type){
        MethodAccess methodAccess = MethodAccess.get(type);
        Map<String,Integer> methodIndexMap=new HashMap<>();
        Map<String,Class>   paramTypeMap=new HashMap<>();
        int i=0;
        for (String methodName : methodAccess.getMethodNames()) {
            methodIndexMap.put(methodName,i);
            if(methodAccess.getParameterTypes()[i].length>0){
                paramTypeMap.put(methodName,methodAccess.getParameterTypes()[i][0]);
            }
            i++;
        }
        return new MethodAccessData(methodAccess,methodIndexMap,paramTypeMap);
    }

    public static void setProperty(Object entity, String property, Object value){
        MethodAccessData methodAccessData = methodAccessData(entity.getClass());
        String method="set" + UtilAll.UString.capitalize(property);
        int index = methodAccessData.getMethodIndexMap().get(method);
        value = ConvertUtil.toObject(value, methodAccessData.getParamTypeMap().get(method));
        methodAccessData.getMethodAccess().invoke(entity,index,value);
    }

    public static Object getProperty(Object entity, String property) {
            if (entity instanceof Map) {
                return ((Map<?, ?>) entity).get(property);
            }
            return getPropertyT(entity, property);
    }


}
