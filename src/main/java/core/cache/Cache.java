package core.cache;

import api.annotation.CacheInterceptor;
import api.cache.ICache;
import api.cache.ICacheContext;
import api.cache.ICacheEntry;
import api.evict.ICacheEvict;
import api.expire.ICacheExpire;
import api.listener.ICacheRemoveListener;
import api.listener.ICacheSlowListener;
import api.load.ICacheLoad;
import api.persist.ICachePersist;
import core.exception.CacheRuntimeException;
import core.expire.CacheExpire;
import core.expire.CacheExpireRandom;
import core.listener.remove.CacheRemoveListenerContext;
import core.persist.InnerCachePersist;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import core.enums.CacheRemoveType;
import core.evict.CacheEvictContext;
import core.proxy.CacheProxy;

import java.util.*;
@SuppressWarnings("unchecked")
public class Cache<K,V> implements ICache<K,V> {
    private Map<K,V> map;
    private int sizeLimit;
    private ICacheEvict<K,V> evict;
    private ICacheLoad<K,V> load;
    private ICachePersist<K,V> persist;
    private List<ICacheRemoveListener<K,V>> removeListeners;
    private List<ICacheSlowListener> slowListeners;

    /**
     * 过期策略
     */
    private ICacheExpire<K,V> expire;


    @Override
    @CacheInterceptor
    public ICacheExpire<K, V> expire() {
        return this.expire;
    }

    /**
     * 初始化
     */
    public void init(){
        //默认随机检查过期key
        this.expire = new CacheExpireRandom<>(this);
        this.load.load(this);
        //初始化持久化
        if (persist != null){
            new InnerCachePersist<>(this,persist);
        }
    }

    public Cache() {
    }



    public Cache(ICacheContext<K,V> context){
        this.map = context.map();
        this.evict = context.cacheEvict();
        this.sizeLimit = context.size();
    }



    public Cache<K,V> map(Map<K,V> map){
        this.map = map;
        return this;
    }
    public Cache<K,V> sizeLimit(int sizeLimit){
        this.sizeLimit = sizeLimit;
        return this;
    }
    public ICacheEvict<K,V> evict(){
        return this.evict;
    }
    public Cache<K,V> evict(ICacheEvict<K,V> evict){
        this.evict = evict;
        return this;
    }

    /**
     * 持久化
     * @return
     */
    @Override
    public ICachePersist<K, V> persist() {
        return persist;
    }
    public Cache<K,V> persist(ICachePersist<K,V> persist){
        this.persist = persist;
        return this;
    }

    @Override
    public ICacheLoad<K, V> load() {
        return load;
    }

    public Cache<K,V> load(ICacheLoad<K,V> load){
        this.load = load;
        return this;
    }
    public boolean isSizeLimit(){
        final int currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }


    @Override
    @CacheInterceptor(refresh = true)
    public int size() {
        return map.size();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    @CacheInterceptor(refresh = true,evict = true)
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @CacheInterceptor(evict = true)
    public V get(Object key) {
        //刷新所有过期信息
        K genericKey = (K) key;
        this.expire.refreshExpire(Collections.singletonList(genericKey));
        return map.get(key);
    }

    @Override
    @CacheInterceptor(evict = true,aof = true)
    public V put(K key, V value) {
        //尝试驱除
        CacheEvictContext<K, V> context = new CacheEvictContext<>();
        context.key(key).size(sizeLimit).cache(this);
        ICacheEntry<K, V> evictEntry = this.evict.evict(context);
        //驱除成功时执行删除监听器
        if(ObjectUtil.isNotNull(evictEntry)){
            //执行删除监听器
            CacheRemoveListenerContext<K, V> listenerContext = CacheRemoveListenerContext.<K, V>newInstance()
                    .key(evictEntry.key())
                    .value(evictEntry.value())
                    .type(CacheRemoveType.EVICT.code());
            for (ICacheRemoveListener<K, V> removeListener : context.cache().removeListeners()) {
                removeListener.listen(listenerContext);
            }
        }

        //判断驱除后的信息
        if (isSizeLimit()){
            throw new CacheRuntimeException("当前队列已满，数据添加失败");
        }
        //执行添加
        return map.put(key,value);
    }

    @Override
    @CacheInterceptor(aof = true,refresh = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    @CacheInterceptor(aof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(refresh = true,aof = true)
    public void clear() {
        map.clear();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }


    /**
     * 过期
     */
    @Override
    @CacheInterceptor
    public ICache<K, V> expire(K key, long timeInMills) {
        long expireTime = System.currentTimeMillis() + timeInMills;

//        使用代理调用
        Cache<K,V> cacheProxy = (Cache<K, V>) CacheProxy.getProxy(this);
        return cacheProxy.expireAt(key,expireTime);
//        return this.expireAt(key,expireTime);
    }

    @Override
    @CacheInterceptor(aof = true)
    public ICache<K, V> expireAt(K key, long timeInMills) {
        this.expire.expire(key,timeInMills);
        return this;
    }

    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return this.removeListeners;
    }
    public ICache<K,V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners){
        this.removeListeners = removeListeners;
        return this;
    }

    @Override
    public List<ICacheSlowListener> slowListeners() {
        return slowListeners;
    }

    public ICache<K,V> slowListeners(List<ICacheSlowListener> slowListeners) {
        this.slowListeners = slowListeners;
        return this;
    }
}
