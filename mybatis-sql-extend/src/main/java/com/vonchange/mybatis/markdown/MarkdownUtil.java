package com.vonchange.mybatis.markdown;


import com.vonchange.common.util.FileUtil;
import com.vonchange.common.util.StringUtils;
import com.vonchange.mybatis.config.Constant;
import com.vonchange.mybatis.exception.MybatisMinRuntimeException;
import com.vonchange.mybatis.markdown.bean.MdWithInnerIdTemp;
import com.vonchange.mybatis.markdown.bean.SqlInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * markdown 组件
 * von_change
 */
public class MarkdownUtil {
    private  MarkdownUtil(){
        throw new IllegalStateException("Utility class");
    }
    private   static Logger logger = LoggerFactory.getLogger(MarkdownUtil.class);
    private   static  Map<String,MarkdownDTO> idMarkdownMap=new ConcurrentHashMap<>();


    public static boolean markdownFileExist(String packageName,String fileName){
        String url= "classpath:"+FileUtils.getFileURLPath(packageName,fileName);
        Resource resource= FileUtils.getResource(url);
        return resource.exists();
    }
    public    static  MarkdownDTO readMarkdownFile(String packageName,String fileName,boolean needReadMdLastModified){
        String id = packageName+"."+fileName;
        if(!needReadMdLastModified&&idMarkdownMap.containsKey(id)){
                return idMarkdownMap.get(id);
        }
        String url= "classpath:"+FileUtils.getFileURLPath(packageName,fileName);
        Resource resource= FileUtils.getResource(url);
       long lastModified;
       try {
             lastModified=  resource.lastModified();
             if(lastModified==0){
                 return null;
             }
        } catch (IOException e) {
           logger.error("read markdown file error {}",url);
           throw new MybatisMinRuntimeException("read markdown file error "+url);
        }
        boolean needLoad=true;
        if(idMarkdownMap.containsKey(id)){
            MarkdownDTO markdownDTO=idMarkdownMap.get(id);
            if((lastModified+"").equals(markdownDTO.getVersion())){
                needLoad=false;
            }
        }
        if(!needLoad){
            return  idMarkdownMap.get(id);
        }
        String content=null;
        try {
            content= FileUtil.readUTFString(resource.getInputStream());
        } catch (IOException e) {
            logger.error("读取markdown文件出错{}:{}",url,e);
        }
        MarkdownDTO markdownDTO= getMarkDownInfo(content,id,lastModified+"");
        idMarkdownMap.put(id,markdownDTO);
        return markdownDTO;
    }
    private   static  MarkdownDTO readMarkdown(String content,String id,String version){
        if(null==id||"".equals(id.trim())){
            id=getId(content);
        }
        if(null==version||"".equals(version.trim())){
            version=getVersion(content);
        }
        boolean needLoad=true;
        if(null!=idMarkdownMap.get(id)){
            MarkdownDTO markdownDTO=idMarkdownMap.get(id);
            if((version).equals(markdownDTO.getVersion())){
                needLoad=false;
            }
        }
        if(!needLoad){
            return  idMarkdownMap.get(id);
        }
        MarkdownDTO markdownDTO= getMarkDownInfo(content,id,version);
        idMarkdownMap.put(id,markdownDTO);
        return markdownDTO;
    }
    private   static  String getId(String result){
        String idSym= Constant.MDID;
        int vdx = result.indexOf(idSym);
        if(vdx == -1) {
            throw new IllegalArgumentException("无"+Constant.MDID);
        }
        StringBuilder idSB=new StringBuilder();
        for(int j=vdx+idSB.length();result.charAt(j)!='\n';j++){
            idSB.append(result.charAt(j));
        }
        return idSB.toString();
    }
    private   static  String getVersion(String result){
        String versionSym=Constant.MDVERSION;
        int vdx = result.indexOf(versionSym);
        if(vdx == -1) {
            throw new IllegalArgumentException("无"+Constant.MDVERSION);
        }
        StringBuilder versionSB=new StringBuilder();
        for(int j=vdx+versionSym.length();result.charAt(j)!='\n';j++){
            versionSB.append(result.charAt(j));
        }
        return versionSB.toString();
    }

    private static  MarkdownDTO getMarkDownInfo(String result,String id,String version){
        MarkdownDTO markdownDTO= new MarkdownDTO();
        String scriptSym="```";
        if(null==id||"".equals(id.trim())){
            markdownDTO.setId(getId(result));
        }else{
            markdownDTO.setId(id);
        }
        if(null==version||"".equals(version.trim())){
            markdownDTO.setVersion(getVersion(result));
        }else{
            markdownDTO.setVersion(version);
        }
        int i=0;
        int len = result.length();
        int startLen=scriptSym.length();
        int endLen=scriptSym.length();
        StringBuilder idSB;
        while (i < len) {
            int ndx = result.indexOf(scriptSym, i);
            if(ndx==-1){
                break;
            }
            ndx += startLen;
            idSB=new StringBuilder();
            for(int j=ndx;result.charAt(j)!='\n';j++){
                idSB.append(result.charAt(j));
            }
            int firstLineLength=idSB.length();
            idSB=new StringBuilder();
            for(int j=ndx+firstLineLength+1;result.charAt(j)!='\n';j++){
                idSB.append(result.charAt(j));
            }
            int zsIndex= idSB.indexOf(Constant.IDPREF);
            if(zsIndex==-1){
                throw new IllegalArgumentException("无id注释"+idSB);
            }
            String key= idSB.substring(zsIndex+Constant.IDPREF.length()).trim();
            if(key.length()==0){
                throw new IllegalArgumentException("请定义类型和ID at "+ndx);
            }
            ndx += idSB.length()+firstLineLength+1;
            int ndx2 = result.indexOf(scriptSym, ndx);
            if(ndx2 == -1) {
                throw new IllegalArgumentException("无结尾 ``` 符号 at: " + (ndx - startLen));
            }
            String content=result.substring(ndx,ndx2).trim();
            markdownDTO.getContentMap().put(key,content);
            i=ndx2+endLen;
        }
        return markdownDTO;
    }
    public static   String getSqlSpinner(MarkdownDTO markdownDTO,String sql){
         if(StringUtils.isBlank(sql)){
             return sql;
         }
        if(!sql.contains("[@sql")){
            return  sql;
        }
        String startSym="[@sql";
        String endSym="]";
        int len = sql.length();
        int startLen=startSym.length();
        int endLen=endSym.length();
        int i=0;
        StringBuilder newSb=new StringBuilder();
        String model;
        while (i < len) {
            int ndx = sql.indexOf(startSym, i);
            if(ndx==-1){
                newSb.append(i == 0?sql:sql.substring(i));
                break;
            }
            newSb.append(sql, i, ndx);
            ndx += startLen;
            int ndx2 = sql.indexOf(endSym, ndx);
            if(ndx2 == -1) {
                throw new IllegalArgumentException("无结尾 } 符号 at: " + (ndx - startLen));
            }
            model=sql.substring(ndx,ndx2).trim();
            newSb.append(MarkdownDataUtil.getSql(markdownDTO,model));
            i=ndx2+endLen;
        }
        return  newSb.toString();
    }

    public static String  getSql(String sqlId) {
        MdWithInnerIdTemp mdWithInnerIdTemp=loadConfigData(sqlId,false);
        return  MarkdownDataUtil.getSql(mdWithInnerIdTemp.getMarkdownDTO(),mdWithInnerIdTemp.getInnnerId());
    }
    public static boolean  containsSqlId(String sqlId) {
        MdWithInnerIdTemp mdWithInnerIdTemp=loadConfigData(sqlId,false);
        return mdWithInnerIdTemp.getMarkdownDTO().getContentMap().containsKey(sqlId);
    }
    public static SqlInfo getSqlInfo(String sqlId, boolean needReadMdLastModified) {
        MdWithInnerIdTemp mdWithInnerIdTemp=loadConfigData(sqlId,needReadMdLastModified);
        String sql = MarkdownDataUtil.getSql(mdWithInnerIdTemp.getMarkdownDTO(),mdWithInnerIdTemp.getInnnerId());
        SqlInfo sqlInfo=new SqlInfo();
        sqlInfo.setInnnerId(mdWithInnerIdTemp.getInnnerId());
        sqlInfo.setMarkdownDTO(mdWithInnerIdTemp.getMarkdownDTO());
        sqlInfo.setSql(sql);
        return sqlInfo;
    }
    public   static MdWithInnerIdTemp loadConfigData(String sqlId,boolean needReadMdLastModified) {
        MdWithInnerIdTemp mdWithInnerIdTemp=new MdWithInnerIdTemp();
        if(sqlId.startsWith(Constant.ISSQLFLAG)){
            MarkdownDTO markdownDTO= new MarkdownDTO();
            markdownDTO.setId(Constant.MAINFLAG+"_"+ StringUtils.uuid());
            Map<String,String> map=new HashMap<>();
            String sql=sqlId.substring(Constant.ISSQLFLAG.length());
            if(sqlId.endsWith(Constant.COUNTFLAG)){
                sql=null;
            }
            map.put(Constant.MAINFLAG,sql);
            markdownDTO.setContentMap(map);
            String innerId=Constant.MAINFLAG;
            if(sqlId.endsWith(Constant.COUNTFLAG)){
                innerId="main"+Constant.COUNTFLAG;
            }
            mdWithInnerIdTemp.setInnnerId(innerId);
            mdWithInnerIdTemp.setMarkdownDTO(markdownDTO);
            return mdWithInnerIdTemp;
        }
        if(sqlId.startsWith(Constant.ISMDFLAG)){
            MarkdownDTO markdownDTO= MarkdownUtil.readMarkdown(sqlId.substring(Constant.ISMDFLAG.length()),null,null);
            mdWithInnerIdTemp.setInnnerId(Constant.MAINFLAG);
            mdWithInnerIdTemp.setMarkdownDTO(markdownDTO);
            return mdWithInnerIdTemp;
        }

        String[] sqlIds = StringUtils.split(sqlId, ".");
        if (sqlIds.length < 2) {
            throw  new MybatisMinRuntimeException("error config  id:"+sqlId);
        }
        StringBuilder packageName = new StringBuilder();
        for (int i = 0; i < sqlIds.length - 2; i++) {
             packageName.append(sqlIds[i]).append(".");
        }
        String fileName=sqlIds[sqlIds.length - 2] + ".md";
        String needFindId=sqlIds[sqlIds.length - 1];
        MarkdownDTO markdownDTO= MarkdownUtil.readMarkdownFile(packageName.substring(0,packageName.length()-1),fileName,needReadMdLastModified);
        mdWithInnerIdTemp.setInnnerId(needFindId);
        mdWithInnerIdTemp.setMarkdownDTO(markdownDTO);
        return mdWithInnerIdTemp;
    }
    public static  String initStringMd(String md){
        return  initStringMd(md,StringUtils.uuid(),Constant.MDINITVERSION);
    }
    public static  String initStringMd(String md,String id,String version){
        if(!md.contains(Constant.MDVERSION)){
            md=Constant.MDVERSION+" "+version+" \n"+md;
        }
        if(!md.contains(Constant.MDID)){
            md=Constant.MDID+" "+id + "\n"+md;
        }
        if(!md.startsWith(Constant.ISMDFLAG)){
            md=Constant.ISMDFLAG+md;
        }
        return  md;
    }
    public static  String initSqlInSqlId(String sqlId){
        if(!sqlId.startsWith(Constant.ISSQLFLAG)){
            return  Constant.ISSQLFLAG+sqlId;
        }
        return  sqlId;
    }

}
