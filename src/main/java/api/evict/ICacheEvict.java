package api.evict;

import api.cache.ICacheEntry;

/**
 * 驱除策略
 * @param <K>
 * @param <V>
 */
public interface ICacheEvict<K,V>{
    /**
     *驱除策略
     * @param context 上下文
     */
    ICacheEntry<K,V> evict(ICacheEvictContext<K,V> context);

    /**
     * 更新 key 信息
     * @param key
     */
    void updateKey(final K key);

    /**
     * 删除 key 信息
     * @param key
     */
    void removeKey(final K key);
}
