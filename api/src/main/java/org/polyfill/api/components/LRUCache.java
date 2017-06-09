package org.polyfill.api.components;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by smo on 6/8/17.
 */
public class LRUCache<K, T> extends LinkedHashMap<K, T> {

    private int cacheSize;

    public LRUCache(int cacheSize) {
        super(16, 0.75F, true);
        this.cacheSize = cacheSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, T> eldest) {
        return size() >= cacheSize;
    }
}
