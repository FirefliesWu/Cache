package core.evict;

import api.cache.ICache;
import api.cache.ICacheEntry;
import api.evict.ICacheEvictContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.model.CacheEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * 驱除策略——LRU 最近最少使用
 * @param <K>
 * @param <V>
 */
public class CacheEvictLRU<K,V> extends AbstractCacheEvict<K,V> {
    private static final Log log = LogFactory.getLog(CacheEvictLRU.class);
    /**
     * 链表实现
     */
    private final List<K> list = new ArrayList<>();

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K,V> result = null;
        final ICache<K,V> cache = context.cache();
        //超过限制，移除队尾元素
        if (cache.size() >= context.size()){
            K evictKey = list.remove(list.size()-1);
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey,evictValue);
        }
        return result;
    }

    /**
     * 删除已有的key，插入到头部
     * @param key
     */
    @Override
    public void updateKey(K key) {
        this.list.remove(key);
        this.list.add(0,key);
    }

    @Override
    public void removeKey(K key) {
        this.list.remove(key);
    }
}
