package com.vonchange.nine.demo.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtil {
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	private JsonUtil() {
		throw new IllegalStateException("Utility class");
	}
	public static String toJson(Object object) {
		ObjectMapper objectMapper = new ObjectMapper();  
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error("序列化对象失败{}",e);
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
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		T t =null;
		if(null!=clazz){
				t = mapper.readValue(json, clazz);
		}
		if(null!=type){
				t = mapper.readValue(json, type);
		}
		return t;
	}

	private  static <T> T fromJson(String json, Class<T> clazz,TypeReference<T> type) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		T t =null;
		try {
			if(null!=clazz){
				t = mapper.readValue(json, clazz);
			}
			if(null!=type){
				t = mapper.readValue(json, type);
			}
		} catch (IOException e) {
			logger.error("反序列化对象失败{}",e);
		}
		return t;
	}
	public static <T> T fromJson(String json, Class<T> clazz) {
		return fromJson(json,clazz,null);
	}

}
