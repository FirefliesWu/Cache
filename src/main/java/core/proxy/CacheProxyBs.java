package core.proxy;

import api.annotation.CacheInterceptor;
import api.cache.ICache;
import api.interceptor.ICacheInterceptor;
import core.interceptor.CacheInterceptors;
import core.interceptor.CacheInterceptorContext;

import java.util.List;

/**
 * 代理引导类
 */
@SuppressWarnings("unchecked")
public class CacheProxyBs {
    public CacheProxyBs() {
    }

    /**
     * 代理上下文
     */
    private ICacheProxyBsContext context;
    /**
     * 默认通用拦截器：慢日志操作
     */
    private final List<ICacheInterceptor> commonInterceptors = CacheInterceptors.defaultCommonList();
    /**
     * 默认刷新拦截器
     */
    private final List<ICacheInterceptor> refreshInterceptors = CacheInterceptors.defaultRefreshList();
    /**
     * 驱除拦截器
     */
    private final ICacheInterceptor evictInterceptors = CacheInterceptors.evict();
    /**
     * aof持久化拦截器
     */
    private final ICacheInterceptor persistInterceptor = CacheInterceptors.aof();

    /**
     * 新建实例
     * @return
     */
    public static CacheProxyBs newInstance(){ return new CacheProxyBs();}

    public CacheProxyBs context(ICacheProxyBsContext context){
        this.context = context;
        return this;
    }

    /**
     * 执行
     * @return 执行结果
     * @throws Throwable 异常
     */
    public Object execute() throws Throwable{
        //开始时间
        final long startMills = System.currentTimeMillis();
        final ICache cache = context.target();
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.newInstance()
                .startMills(startMills)
                .method(context.method())
                .params(context.params())
                .cache(context.target());
        //1.获取刷新注释信息
        CacheInterceptor cacheInterceptor = context.interceptor();
        //方法执行前
        this.interceptorHandler(cacheInterceptor,interceptorContext,cache,true);
        //2.方法执行
        Object result = context.process();

        final long endMills = System.currentTimeMillis();
        interceptorContext.endMills(endMills).result(result);
        //3.方法执行完成后
        this.interceptorHandler(cacheInterceptor,interceptorContext,cache,false);
        return result;
    }

    /**
     * 拦截器执行类
     * @param cacheInterceptor 缓存拦截器
     * @param interceptorContext 拦截器上下文
     * @param cache 缓存
     * @param before 拦截器是否执行
     */
    private void interceptorHandler(CacheInterceptor cacheInterceptor,
                                    CacheInterceptorContext interceptorContext,
                                    ICache cache,
                                    boolean before){
        if (cacheInterceptor!=null){
            //1.通用
            if (cacheInterceptor.common()){
                for (ICacheInterceptor commonInterceptor : commonInterceptors) {
                    if (before){
                        commonInterceptor.before(interceptorContext);
                    }else{
                        commonInterceptor.after(interceptorContext);
                    }
                }
            }
            //2.刷新
            if (cacheInterceptor.refresh()){
                for (ICacheInterceptor refreshInterceptor : refreshInterceptors) {
                    if (before){
                        refreshInterceptor.before(interceptorContext);
                    }else {
                        refreshInterceptor.after(interceptorContext);
                    }
                }
            }
            //3.AOF
            if (cacheInterceptor.aof()){
                if (before){
                    persistInterceptor.before(interceptorContext);
                }else {
                    persistInterceptor.after(interceptorContext);
                }
            }
            //4.驱除策略
            if (cacheInterceptor.evict()){
                if (before){
                    evictInterceptors.before(interceptorContext);
                }else {
                    evictInterceptors.after(interceptorContext);
                }
            }
        }
    }
}
