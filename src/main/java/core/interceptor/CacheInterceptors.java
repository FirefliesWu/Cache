package core.interceptor;

import api.interceptor.ICacheInterceptor;
import core.interceptor.aof.CacheInterceptorAof;
import core.interceptor.common.CacheInterceptorCost;
import core.interceptor.evict.CacheInterceptorEvict;
import core.interceptor.refresh.CacheInterceptorRefresh;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存拦截器工具类
 */
public final class CacheInterceptors {
    /**
     * 默认通用
     * @return
     */
    public static List<ICacheInterceptor> defaultCommonList(){
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorCost());
        return list;
    }

    /**
     * 默认刷新
     * @return
     */
    public static List<ICacheInterceptor> defaultRefreshList(){
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorRefresh());
        return list;
    }

    public static ICacheInterceptor evict(){
        return new CacheInterceptorEvict();
    }

    public static ICacheInterceptor aof(){
        return new CacheInterceptorAof();
    }
}
