package core.expire;

import api.cache.ICache;
import api.expire.ICacheExpire;
import api.listener.ICacheRemoveListener;
import api.listener.ICacheRemoveListenerContext;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.enums.CacheRemoveType;
import core.listener.remove.CacheRemoveListenerContext;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 采用快、慢两个模式进行键过期
 * map 转 list 来获取随机键
 * @param <K>
 * @param <V>
 */
public class CacheExpireRandom<K,V> implements ICacheExpire<K,V> {
    private static final Log log = LogFactory.getLog(CacheExpireRandom.class);
    /**
     * 单轮清空的数量限制
     */
    private static final int COUNT_LIMIT = 100;
    /**
     * 过期 Map
     */
    private final Map<K,Long> expireMap = new HashMap<>();
    /**
     * 缓存实现
     */
    private final ICache<K,V> cache;
    /**
     * 是否采用快模式
     */
    private volatile boolean fastMode = false;
    /**
     * 线程执行类
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpireRandom(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    /**
     * 初始化任务
     */

    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThreadRandom(),10,10, TimeUnit.SECONDS);
    }

    /**
     * 定时执行任务
     */
    private class ExpireThreadRandom implements Runnable{
        @Override
        public void run() {
            //检查是否为空
            if (expireMap.isEmpty()){
                log.info("ExpireMap 为空，跳过本次处理。");
                return;
            }
            //是否启用快模式
            if (fastMode){
                expireKeys(10L);
            }else {
                //慢模式
                expireKeys(100L);
            }
        }
    }
    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key,expireAt);
    }

    @Override
    public void refreshExpire(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)) {
            return;
        }

        // 判断大小，小的作为外循环。一般都是过期的 keys 比较小。
        if(keyList.size() <= expireMap.size()) {
            for(K key : keyList) {
                Long expireAt = expireMap.get(key);
                expireKey(key, expireAt);
            }
        } else {
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.expireKey(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    /**
     *过期信息
     * @param timeoutMills 超时时间
     */
    private void expireKeys(final long timeoutMills){
        //设置超时时间
        final long timeLimit = System.currentTimeMillis() + timeoutMills;
        //重置 fastmode
        this.fastMode = false;
        //获取key进行处理
        int count = 0;
        while (true){
            if (count >= COUNT_LIMIT){
                log.info("淘汰次数已达到最大次数：{},完成本次执行",COUNT_LIMIT);
                return;
            }
            if (System.currentTimeMillis() >= timeLimit){
                this.fastMode = true;
                log.info("过期时间已达到时间限制，中断本次执行，设置 FastMode = true；");
                return;
            }
            //随机过期
            K key = getRandomKey();
            Long expireAt = expireMap.get(key);
            expireKey(key,expireAt);
            count++;
        }
    }

    /**
     * 随机获取一个key
     * @return
     */
    private K getRandomKey(){
        Random random = ThreadLocalRandom.current();

        Set<K> keySet = expireMap.keySet();
        List<K> list = new ArrayList<>(keySet);
        int randomIndex =random.nextInt(list.size());
        return list.get(randomIndex);
    }
    /**
     * 过期处理 key
     * @param key key
     * @param expireAt 过期时间
     * @return 是否执行过期
     */
    private boolean expireKey(final K key, final Long expireAt) {
        if(expireAt == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if(currentTime >= expireAt) {
            expireMap.remove(key);
            // 再移除缓存，后续可以通过惰性删除做补偿
            V removeValue = cache.remove(key);

            // 执行淘汰监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(key).value(removeValue).type(CacheRemoveType.EXPIRE.code());
            for(ICacheRemoveListener<K,V> listener : cache.removeListeners()) {
                listener.listen(removeListenerContext);
            }

            return true;
        }

        return false;
    }
}
