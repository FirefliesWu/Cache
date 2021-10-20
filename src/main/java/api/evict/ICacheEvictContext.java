package api.evict;

import api.cache.ICache;

/**
 * 驱除策略
 */
public interface ICacheEvictContext<K,V> {
    /**
     * 新加的key
     * @return
     */
    K key();

    /**
     *cache 实现
     * @return map
     */
    ICache<K,V> cache();

    /**
     *获取大小
     * @return大小
     */
    int size();


}
