package core.bs;

import api.cache.ICache;
import api.evict.ICacheEvict;
import core.cache.Cache;
import core.evict.CacheEvicts;
import core.load.CacheLoads;
import core.persist.CachePersists;
import core.listener.slow.CacheSlowListeners;
import api.listener.ICacheRemoveListener;
import api.listener.ICacheSlowListener;
import api.load.ICacheLoad;
import api.persist.ICachePersist;
import com.github.houbb.heaven.util.common.ArgUtil;
import core.listener.remove.CacheRemoveListeners;
import core.proxy.CacheProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CacheBs<K,V> {

    public CacheBs() {
    }

    public static <K,V> CacheBs<K,V> newInstance(){
        return new CacheBs<>();
    }

    /**
     * map 实现
     */
    private Map<K,V> map = new HashMap<>();
    /**
     * 大小限制
     */
    private int size = Integer.MAX_VALUE;
    /**
     * 去除策略
     */
    private ICacheEvict<K,V> evict = CacheEvicts.fifo();
    /**
     * 加载策略
     */
    private ICacheLoad<K,V> load = CacheLoads.none();
    /**
     * 持久化
     */
    private ICachePersist<K,V> persist = CachePersists.none();

    /**
     * 删除监听器列表
     * @return
     */
    private List<ICacheRemoveListener<K,V>> removeListeners = CacheRemoveListeners.defaults();

    /**
     * 慢日志监听器列表
     */
    private List<ICacheSlowListener> slowListeners = CacheSlowListeners.defaults();

    public ICachePersist<K,V> persist(){
        return persist;
    }
    public CacheBs<K,V> persist(ICachePersist<K,V> persist){
        this.persist = persist;
        return this;
    }

    public CacheBs<K,V> load(ICacheLoad<K,V> load){
        this.load = load;
        return this;
    }

    /**
     * 设置map
     * @param map
     * @return
     */
    public CacheBs<K,V> map(Map<K,V> map){
        this.map = map;
        return this;
    }

    /**
     * 设置size信息
     * @param size
     * @return
     */
    public CacheBs<K,V> size(int size){
        this.size = size;
        return this;
    }

    /**
     * 设置去除策略
     * @param evict
     * @return
     */
    public CacheBs<K,V> evict(ICacheEvict<K,V> evict){
        this.evict = evict;
        return this;
    }

    /**
     * 添加删除监听器
     * @param removeListener
     * @return
     */
    public CacheBs<K,V> addRemoveListener(ICacheRemoveListener<K,V> removeListener){
        ArgUtil.notNull(removeListener,"removeListener");
        this.removeListeners.add(removeListener);
        return this;
    }

    public CacheBs<K,V> addSlowListener(ICacheSlowListener slowListener){
        ArgUtil.notNull(slowListener,"slowListener");
        this.slowListeners.add(slowListener);
        return this;
    }

    public ICache<K,V> build(){
        Cache<K,V> cache = new Cache<>();
        cache.map(map);
        cache.evict(evict);
        cache.sizeLimit(size);
        cache.removeListeners(removeListeners);
        cache.load(load);
        cache.persist(persist);
        cache.slowListeners(slowListeners);
        //初始化
        cache.init();

        return CacheProxy.getProxy(cache);
    }
}
