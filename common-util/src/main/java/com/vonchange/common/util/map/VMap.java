package com.vonchange.common.util.map;

import java.util.Map;

public interface VMap<K,V>  extends Map<K, V> {
    public VMap<K, V> set(K key, V value);
}
