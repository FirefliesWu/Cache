package core.interceptor.refresh;

import api.cache.ICache;
import api.interceptor.ICacheInterceptor;
import api.interceptor.ICacheInterceptorContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * 刷新
 * @param <K>
 * @param <V>
 */
public class CacheInterceptorRefresh<K,V> implements ICacheInterceptor<K,V> {
    private static final Log log = LogFactory.getLog(CacheInterceptorRefresh.class);
    @Override
    public void before(ICacheInterceptorContext<K, V> context) {
        log.debug("Refresh start!");
        final ICache<K,V> cache = context.cache();
        cache.expire().refreshExpire(cache.keySet());
    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {

    }
}
