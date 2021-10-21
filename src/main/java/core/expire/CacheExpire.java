package core.expire;

import api.cache.ICache;
import api.expire.ICacheExpire;
import api.listener.ICacheRemoveListener;
import core.listener.remove.CacheRemoveListenerContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.enums.CacheRemoveType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Deprecated
/**
 * 查询过期Key时不是随机的，性能不够
 */
public class CacheExpire<K,V> implements ICacheExpire<K,V> {
    private static final Log log = LogFactory.getLog(CacheExpire.class);
    /**
     * 单次清空的数量限制
     */
    private static final int LIMIT = 100;

    /**
     * 存储过期map
     */
    private final Map<K,Long> expireMap = new HashMap<>();

    /**
     * 缓存实现
     */
    private final ICache<K,V> cache;

    public CacheExpire(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    /**
     * 线程执行类
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    /**
     * 初始化任务
     */
    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(),100,100, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时执行任务
     */
    private class ExpireThread implements Runnable{
        @Override
        public void run() {
            //判断是否为空
            if (expireMap.isEmpty()){
                return;
            }
            //获取 key 进行处理
            int count = 0;
            for (Map.Entry<K, Long> entry : expireMap.entrySet()) {
                if (count >= LIMIT){
                    return;
                }
                expireKey(entry.getKey(),entry.getValue());
                count++;
            }

        }
    }

    /**
     * 处理过期key
     * @param key
     * @param expireAt
     */
    private void expireKey(final K key,final Long expireAt){
        if (expireAt == null){
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime >= expireAt){
            expireMap.remove(key);
            //再移除缓存，后续通过惰性删除进行补偿
            V removeValue = cache.remove(key);
            //过期移除key后执行删除监听器
            CacheRemoveListenerContext<K, V> removeListenerContext = CacheRemoveListenerContext.<K, V>newInstance()
                    .key(key)
                    .value(removeValue)
                    .type(CacheRemoveType.EXPIRE.code());
            for (ICacheRemoveListener<K, V> removeListener : cache.removeListeners()) {
                removeListener.listen(removeListenerContext);
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key,expireAt);
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    @Override
    public void refreshExpire(Collection<K> keyList) {
        if (keyList.isEmpty() || expireMap.isEmpty()) return;
        //判断大小，小的作为外循环，一般过期的keys比较小
        if (keyList.size() <= expireMap.size()){
            for (K key : keyList) {
                Long expireAt = expireMap.get(key);
                expireKey(key, expireAt);
            }
        }else {
            for (Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.expireKey(entry.getKey(), entry.getValue());
            }
        }
    }
}

