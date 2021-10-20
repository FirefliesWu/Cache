package api.interceptor;

import api.cache.ICache;

import java.lang.reflect.Method;

/**
 * 拦截器上下文接口
 * （1）get
 * （2）put
 * （3）remove
 * （4）expire
 * （5）evict
 * @param <K>
 * @param <V>
 */
public interface ICacheInterceptorContext<K, V> {
    /**
     * 缓存信息
     * @return
     */
    ICache<K,V> cache();

    /**
     * 执行的方法信息
     * @return
     */
    Method method();

    /**
     * 执行的方法信息
     * @return
     */
    Object[] params();

    /**
     * 执行的方法结果
     * @return
     */
    Object result();

    /**
     * 开始时间
     * @return 时间
     */
    long startMills();

    /**
     * 结束时间
     * @return 时间
     */
    long endMills();
}
