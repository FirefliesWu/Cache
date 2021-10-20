package core.model;

import api.cache.ICacheEntry;

/**
 * LRU-Map接口
 * @param <K>
 * @param <V>
 */
public interface ILruMap<K,V> {
    /**
     * 移除最老的元素
     * @return
     */
    ICacheEntry<K,V> removeEldest();

    /**
     * 更新key
     * @param key
     */
    void updateKey(K key);

    /**
     * 移除Key
     * @param key
     */
    void removeKey(K key);

    /**
     * 是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 是否有元素key
     * @param key
     * @return
     */
    boolean containsKey(final K key);
}
