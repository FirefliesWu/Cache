package core.proxy;

import api.cache.ICache;
import core.proxy.proxyType.CglibProxy;
import core.proxy.proxyType.DynamicProxy;
import core.proxy.proxyType.NoneProxy;
import com.github.houbb.heaven.util.lang.ObjectUtil;

import java.lang.reflect.Proxy;

/**
 * 代理信息
 */
@SuppressWarnings("unchecked")
public final class CacheProxy {
    private CacheProxy() {
    }

    /**
     * 获取对象代理
     * @param cache 对象代理
     * @param <K> key
     * @param <V> value
     * @return 代理信息
     */
    public static <K,V>ICache<K,V> getProxy(final ICache<K,V> cache){
        if (ObjectUtil.isNull(cache)){
            return (ICache<K, V>) new NoneProxy(cache).proxy();
        }
        final Class clazz = cache.getClass();

        // 如果targetClass本身是个接口或者targetClass是JDK Proxy生成的,则使用JDK动态代理。
        // 参考 spring 的 AOP 判断
        if (clazz.isInterface() || Proxy.isProxyClass(clazz)){
            return (ICache<K, V>) new DynamicProxy(cache).proxy();
        }
        return (ICache<K, V>) new CglibProxy(cache).proxy();
    }
}
