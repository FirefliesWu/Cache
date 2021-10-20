package core.interceptor.common;

import api.interceptor.ICacheInterceptor;
import api.interceptor.ICacheInterceptorContext;
import api.listener.ICacheSlowListener;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.listener.slow.CacheSlowListenerContext;

import java.util.List;

/**
 * 统计耗时
 * （1）耗时
 * （2）慢日志
 * @param <K>
 * @param <V>
 */
public class CacheInterceptorCost<K,V> implements ICacheInterceptor<K,V> {
    private static final Log log = LogFactory.getLog(CacheInterceptorCost.class);
    @Override
    public void before(ICacheInterceptorContext<K, V> context) {
        log.debug("Cost start, method: {}",context.method().getName());
    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        long costMills = context.endMills() - context.startMills();
        final String methodName = context.method().getName();
        log.debug("Cost end, method: {}, cost: {}ms",methodName,costMills);
        //添加慢日志操作
        List<ICacheSlowListener> slowListeners = context.cache().slowListeners();
        if (!slowListeners.isEmpty()){
            CacheSlowListenerContext slowListenerContext = CacheSlowListenerContext.newInstance().startTimeMills(context.startMills())
                    .endTimeMills(context.endMills())
                    .costTimeMills(costMills)
                    .methodName(methodName)
                    .params(context.params())
                    .result(context.result());
            //执行慢日志监听器列表
            for (ICacheSlowListener slowListener : slowListeners) {
                long slowThanMills = slowListener.slowerThanMills();
                if (costMills >= slowThanMills){
                    slowListener.listen(slowListenerContext);
                }
            }
        }
    }
}
