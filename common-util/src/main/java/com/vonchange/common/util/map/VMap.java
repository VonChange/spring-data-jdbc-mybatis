package com.vonchange.common.util.map;

import java.util.Map;

public interface VMap<K,V>  extends Map<K, V> {
    public VMap<K, V> set(K key, V value);
    VMap<K, V> setAll(Map<K, V> vMap);
    <T> T get(K key, Class<T> type);
}
