package com.vonchange.jdbc.abstractjdbc.util.markdown;

import java.util.HashMap;
import java.util.Map;


public class MarkdownDTO {
    private  String id;//文件或整个文档ID
    private String version;//版本号
    private Map<String,String> contentMap=new HashMap<>();
    private Map<String,Map<String,Object>> jsonMap=new HashMap<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getContentMap() {
        return contentMap;
    }

    public void setContentMap(Map<String, String> contentMap) {
        this.contentMap = contentMap;
    }

    public Map<String, Map<String, Object>> getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Map<String, Map<String, Object>> jsonMap) {
        this.jsonMap = jsonMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
