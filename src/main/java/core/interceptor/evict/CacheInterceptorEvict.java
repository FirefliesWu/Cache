package core.interceptor.evict;

import api.evict.ICacheEvict;
import api.interceptor.ICacheInterceptor;
import api.interceptor.ICacheInterceptorContext;

import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class CacheInterceptorEvict<K,V> implements ICacheInterceptor<K,V> {
    @Override
    public void before(ICacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        ICacheEvict<K, V> cacheEvict = context.cache().evict();
        Method method = context.method();
        final K key = (K) context.params()[0];
        if ("remove".equals(method.getName())){
            cacheEvict.removeKey(key);
        }else {
            cacheEvict.updateKey(key);
        }
    }
}
