package com.vonchange.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
	private static Logger log = LoggerFactory.getLogger(JsonUtil.class);
	private JsonUtil() {
		throw new IllegalStateException("Utility class");
	}
	static ObjectMapper objectMapper;
	static List<String> moduleList=new ArrayList<>();
	static {
		objectMapper=new ObjectMapper();
		moduleList.add("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");
		moduleList.add("com.fasterxml.jackson.module.paramnames.ParameterNamesModule");
		moduleList.add("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
		for (String module : moduleList) {
			if(ClazzUtils.isClassExists(module)){
				try {
					objectMapper.registerModule((Module) Class.forName(module).newInstance());
				} catch (Exception e) {
					log.info("registerModule {} {}",module,e.getMessage());
				}
			}
		}
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}
	public static String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("toJson",e);
		}
		return null;	
	}

	public static <T> T fromJson(String json, TypeReference<T> type) {
		return fromJson(json,null,type);
	}
	public static <T> T evalJson(String json, TypeReference<T> type) throws IOException {
		return evalJson(json,null,type);
	}
	public static <T> T evalJson(String json, Class<T> clazz) throws IOException {
		return evalJson(json,clazz,null);
	}
	public static <T> T evalJson(String json, Class<T> clazz,TypeReference<T> type) throws IOException {
		T t =null;
		if(null!=clazz){
				t = objectMapper.readValue(json, clazz);
		}
		if(null!=type){
				t = objectMapper.readValue(json, type);
		}
		return t;
	}

	private  static <T> T fromJson(String json, Class<T> clazz,TypeReference<T> type) {
		T t =null;
		try {
			t=evalJson(json,clazz,type);
		} catch (IOException e) {
			log.error("fromJson",e);
		}
		return t;
	}
	public static <T> T fromJson(String json, Class<T> clazz) {
		return fromJson(json,clazz,null);
	}

}
