package core.persist;

import api.cache.ICache;
import api.persist.ICachePersist;

import java.util.concurrent.TimeUnit;

/**
 * 缓存持久化-适配器模式
 * @param <K>
 * @param <V>
 */
public class CachePersistAdaptor<K,V> implements ICachePersist<K,V> {
    @Override
    public void persist(ICache<K, V> cache) {

    }

    @Override
    public long delay() {
        return this.period();
    }

    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}
