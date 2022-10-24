package com.vonchange.common.util.map;

import java.util.Map;


/**
 *支持链式调用的HashMap<String, Object>
 * @author vonchange@163.com
 */
public class MyHashMap extends HashMap<String, Object> implements Map<String, Object>{


	public MyHashMap() {
	}

	public MyHashMap(Map<String, Object> map) {
		if (null == map) {
			map = new MyHashMap();
		}
		for (Entry<String, Object> entry : map.entrySet()) {
			this.set(entry.getKey(), entry.getValue());
		}
	}
    @Override
	public MyHashMap set(String key, Object value) {
		super.put(key, value);
		return this;
	}
	public MyHashMap setAll(Map<String,Object> map) {
		super.putAll(map);
		return this;
	}
	public MyHashMap put(String key, Object value) {
		return this.set(key, value);
	}
}
