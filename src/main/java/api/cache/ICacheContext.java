package api.cache;

import api.evict.ICacheEvict;

import java.util.Map;

/**
 * 缓存上下文
 */
public interface ICacheContext<K,V> {
    /**
     * map信息
     * @return
     */
    Map<K,V> map();

    /**
     * 大小信息
     * @return
     */
    int size();

    /**
     *驱除策略
     * @return
     */
    ICacheEvict<K,V> cacheEvict();
}
