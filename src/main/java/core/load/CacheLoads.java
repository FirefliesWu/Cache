package core.load;

import api.load.ICacheLoad;

/**
 * 缓存加载工具类
 */
public final class CacheLoads {
    public CacheLoads() {
    }
    public static <K,V> ICacheLoad<K,V> none(){return new CacheLoadNone<>();}

    public static <K,V> ICacheLoad<K,V> dbJson(final String dbPath){
        return new CacheLoadDbJson<>(dbPath);
    }

    public static <K,V> ICacheLoad<K,V> aof(final String dbPath) { return new CacheLoadAof<>(dbPath);}
}
