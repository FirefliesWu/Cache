package core.interceptor.aof;

import api.cache.ICache;
import api.interceptor.ICacheInterceptor;
import api.interceptor.ICacheInterceptorContext;
import api.persist.ICachePersist;
import core.model.PersistAofEntry;
import core.persist.CachePersistAof;
import com.alibaba.fastjson.JSON;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * AOF 拦截器
 * @param <K>
 * @param <V>
 */
public class CacheInterceptorAof<K,V> implements ICacheInterceptor<K,V> {
    private static final Log log = LogFactory.getLog(CacheInterceptorAof.class);
    @Override
    public void before(ICacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        //持久化类
        ICache<K,V> cache = context.cache();
        ICachePersist<K,V> persist = cache.persist();

        if (persist instanceof CachePersistAof){
            CachePersistAof<K, V> cachePersistAof = (CachePersistAof<K, V>) persist;
            PersistAofEntry aofEntry = PersistAofEntry.newInstance();
            aofEntry.setMethodName(context.method().getName());
            aofEntry.setParams(context.params());
            String json = JSON.toJSONString(aofEntry);
            //持久化
            log.debug("AOF 开始追加文件内容：{}",json);
            cachePersistAof.append(json);
            log.debug("AOF 完成追加文件内容：{}",json);
        }
    }
}
