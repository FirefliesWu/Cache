package core.evict;

import api.cache.ICache;
import api.evict.ICacheEvictContext;
import core.model.CacheEntry;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 丢弃策略，先进先出
 * @param <K>
 * @param <V>
 */
public class CacheEvictFIFO<K,V> extends AbstractCacheEvict<K,V> {
    private final Queue<K> queue = new LinkedList<>();

    @Override
    public CacheEntry<K,V> doEvict(ICacheEvictContext<K, V> context) {
        CacheEntry<K,V> result = null;
        final ICache<K,V> cache = context.cache();
        if(cache.size() >= context.size()){
            K evictKey = queue.remove();
            //移除最开始的元素
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey,evictValue);
        }
        final K key = context.key();
        queue.add(key);
        return result;
    }


}
