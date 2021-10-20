package core.model;

import api.cache.ICacheEntry;

public class CacheEntry<K,V> implements ICacheEntry<K,V> {
    private final K key;
    private final V value;

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 新建元素
     * @param key
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> CacheEntry<K,V> of(final K key,final V value){
        return new CacheEntry<>(key,value);
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
