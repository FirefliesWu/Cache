package core.evict;

import api.cache.ICacheEntry;
import api.evict.ICacheEvictContext;

public class CacheEvictLFU<K,V> extends AbstractCacheEvict<K,V> {
    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        return null;
    }

    @Override
    public void updateKey(K key) {
        super.updateKey(key);
    }

    @Override
    public void removeKey(K key) {
        super.removeKey(key);
    }
}
