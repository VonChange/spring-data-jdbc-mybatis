package com.vonchange.common.util;


import com.vonchange.common.util.exception.EnumUtilErrorCode;
import com.vonchange.common.util.exception.ErrorMsg;
import com.vonchange.common.util.exception.UtilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MarkdownUtil {
    private  MarkdownUtil(){
        throw new IllegalStateException("Utility class");
    }
    private   static final String ID_END= System.getenv("ID_END");
    public static final String ID_PRE_SQL = "--";
    public static final String ID_PRE_CODE = "//";
    private   static Logger log = LoggerFactory.getLogger(MarkdownUtil.class);
    // fileId mdId-> content
    private   static  Map<String,Map<String,String>> markdownDataCache=new ConcurrentHashMap<>(256);
    private   static  Map<String,String> idContentCache=new ConcurrentHashMap<>(256);

    public     static synchronized   Map<String,String> readMarkdownFile(String id,boolean notFoundError){
        String path= UtilAll.UFile.classPath(id);
        InputStream inputStream = UtilAll.UFile.getClassResource(path+".md");
        if(null==inputStream){
             if(!notFoundError){
                 return null;
             }
            throw new UtilException(EnumUtilErrorCode.MarkdownPathNotFound,
                    ErrorMsg.builder().message(path+" not found"));
        }
        String content=null;
        try {
            content= UtilAll.UFile.readUTFString(inputStream);
        } catch (IOException e) {
            log.error("read markdown error {}:{}",path,e);
        }
        if(null==content){
            return new HashMap<>();
        }
        Map<String,String> markdownData= parseMarkDownData(content);
        markdownDataCache.put(id,markdownData);
        return markdownData;
    }


    private static  Map<String,String> parseMarkDownData(String content){
        Map<String,String> markdownDataMap = new HashMap<>();
        String scriptSym="```";
        int i=0;
        int len = content.length();
        int startLen=scriptSym.length();
        int endLen=scriptSym.length();
        StringBuilder idSB;
        while (i < len) {
            int ndx = content.indexOf(scriptSym, i);
            if(ndx==-1){
                break;
            }
            ndx += startLen;
            idSB=new StringBuilder();
            for(int j=ndx;content.charAt(j)!='\n';j++){
                idSB.append(content.charAt(j));
            }
            String key =idSB.toString().trim();//language
            int firstLineLength=idSB.length();
            ndx += firstLineLength+1;
            idSB=new StringBuilder();
            for(int j=ndx;content.charAt(j)!='\n';j++){
                idSB.append(content.charAt(j));
            }
            int zsIndex= idSB.indexOf(ID_PRE_SQL);
            if(zsIndex==-1){
                zsIndex= idSB.indexOf(ID_PRE_CODE);
            }
            if(zsIndex!=-1){
                key= idSB.substring(zsIndex+2).trim();
                ndx += idSB.length();
            }
            if(key.length()==0){
                throw new IllegalArgumentException("请定义类型或ID at "+ndx);
            }
            int ndx2 = content.indexOf(scriptSym, ndx);
            if(ndx2 == -1) {
                throw new IllegalArgumentException("无结尾 ``` 符号 at: " + (ndx - startLen));
            }
           // content=content.substring(ndx,ndx2).trim();
            markdownDataMap.put(key,content.substring(ndx,ndx2).trim());
            i=ndx2+endLen;
        }
        return markdownDataMap;
    }
    private static   String getIdSpinner(Map<String,String> contentMap,String content){
         if(UtilAll.UString.isBlank(content)){
             return content;
         }
        if(!content.contains("[@id")){
            return  content;
        }
        String startSym="[@id";
        String endSym="]";
        int len = content.length();
        int startLen=startSym.length();
        int endLen=endSym.length();
        int i=0;
        StringBuilder newSb=new StringBuilder();
        String id;
        while (i < len) {
            int ndx = content.indexOf(startSym, i);
            if(ndx==-1){
                newSb.append(i == 0?content:content.substring(i));
                break;
            }
            newSb.append(content, i, ndx);
            ndx += startLen;
            int ndx2 = content.indexOf(endSym, ndx);
            if(ndx2 == -1) {
                throw new IllegalArgumentException("无结尾 ] 符号 at: " + (ndx - startLen));
            }
            id=content.substring(ndx,ndx2).trim();
            newSb.append(extendContent(contentMap,id));
            i=ndx2+endLen;
        }
        return  newSb.toString();
    }

    private static String extendContent(Map<String,String> contentMap,String codeId) {
        if(null!=ID_END&&!"".equals(ID_END)){
            if(contentMap.containsKey(codeId+ID_END)){
                codeId=codeId+ID_END;
            }
        }
        String content =contentMap.get(codeId);
        /* 支持[@Id  递归实现*/
        return getIdSpinner(contentMap,content);
    }
    public static boolean  hasId(String id) {
         return idContentCache.containsKey(id);
    }
    public static String  getContent(String id,boolean notFoundThrowError) {
        if(idContentCache.containsKey(id)){
            return idContentCache.get(id);
        }
        Assert.notNull(id,"id can not null");
        if(!id.contains(StringPool.DOT)){
            if(!notFoundThrowError){
                return null;
            }
            throw new UtilException(EnumUtilErrorCode.MarkdownIdNotFound,
                    ErrorMsg.builder().message(id+" not found"));
        }
        String filePath= UtilAll.UString.substringBeforeLast(id, StringPool.DOT);
        String codeId= id.substring(filePath.length()+1);
        Map<String,String> contentMap= loadMdData(filePath);
        if(!contentMap.containsKey(codeId)){
            if(!notFoundThrowError){
                return null;
            }
            throw new UtilException(EnumUtilErrorCode.MarkdownIdNotFound,
                    ErrorMsg.builder().message(id+" not found"));
        }
        String content=  extendContent(contentMap,codeId);
        if(UtilAll.UString.isNotBlank(content)){
            idContentCache.put(id,content);
        }
        return content;
    }
    public static String  getContent(String id) {
        return getContent(id,true);
    }


    private static  Map<String,String>  loadMdData(String fileId){
        Map<String,String> contentMap = null;
        if(markdownDataCache.containsKey(fileId)){
            contentMap= markdownDataCache.get(fileId);
        }
        if(null==contentMap){
            contentMap=readMarkdownFile(fileId,true);
        }
        return contentMap;
    }


}
