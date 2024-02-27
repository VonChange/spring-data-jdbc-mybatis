package com.vonchange.common.util.map;

import com.vonchange.common.util.ConvertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持链式调用的HashMap
 * 
 * @author von_change@163.com
 *         2015-6-14 下午10:37:59
 * @param <K>
 * @param <V>
 */
public class HashMapFluent<K, V> extends HashMap<K, V> implements VMap<K,V>{

	public HashMapFluent() {
		super();
	}

	public HashMapFluent(Map<? extends K, ? extends V> map) {
		super(map);
	}

	public HashMapFluent<K, V> set(K key, V value) {
		super.put(key, value);
		return this;
	}

	@Override
	public HashMapFluent<K, V> setAll(Map<K, V> vMap) {
		super.putAll(vMap);
		return null;
	}

	@Override
	public <T> T get(K key, Class<T> type) {
		return ConvertUtil.toObject(super.get(key),type);
	}
}
