package api.persist;

import api.cache.ICache;

import java.util.concurrent.TimeUnit;

public interface ICachePersist<K,V> {
    /**
     * 持久化缓存信息
     */
    void persist(final ICache<K,V> cache);

    /**
     * 延迟时间
     * @return
     */
    long delay();

    /**
     * 持久化周期
     * @return
     */
    long period();

    /**
     * 时间单位
     * @return
     */
    TimeUnit timeUnit();
}
