package core.evict;

import api.cache.ICacheEntry;
import api.evict.ICacheEvictContext;

public class CacheEvictNone<K,V> extends AbstractCacheEvict<K,V> {

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        return null;
    }

}
