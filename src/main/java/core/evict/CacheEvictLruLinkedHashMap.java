package core.evict;

import api.cache.ICache;
import api.cache.ICacheEntry;
import api.evict.ICacheEvict;
import api.evict.ICacheEvictContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.model.CacheEntry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 丢弃策略——LRU 最近最少使用
 * 基于LinkedHashMap实现
 * @param <K>
 * @param <V>
 */
public class CacheEvictLruLinkedHashMap<K,V> extends LinkedHashMap<K,V>
        implements ICacheEvict<K,V> {
    private static final Log log = LogFactory.getLog(CacheEvictLruLinkedHashMap.class);
    /**
     *   是否移除标志
     */
    private volatile boolean removeFlag =false;
    /**
     * 最旧的元素
     */
    private transient Map.Entry<K,V> eldest = null;

    public CacheEvictLruLinkedHashMap() {
        super(16, 0.75f, true);
    }

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K,V> result = null;
        final ICache<K,V> cache = context.cache();
        //超过限制，移除队尾元素
        if (cache.size() >= context.size()){
            removeFlag = true;
            //执行put操作
            super.put(context.key(),null);
            //构建淘汰的元素
            K evictKey = eldest.getKey();
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey,evictValue);
        }else {
            removeFlag = false;
        }
        return result;
    }

    @Override
    public void updateKey(K key) {
        super.put(key,null);
    }

    @Override
    public void removeKey(K key) {
        super.remove(key);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        this.eldest = eldest;
        return removeFlag;
    }
}
