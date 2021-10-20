package core.persist;

import api.persist.ICachePersist;

/**
 * 缓存持久化工具类
 */
public final class CachePersists {
    private CachePersists(){}
    /**
     * 无操作
     */
    public static <K,V> ICachePersist<K,V> none(){
        return new CachePersistNone<>();
    }
    /**
     * DBJson
     */
    public static <K,V> ICachePersist<K,V> dbJson(final String path){
        return new CachePersistDbJson<>(path);
    }

    /**
     * AOF
     * @param path
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICachePersist<K,V> aof(final String path){ return new CachePersistAof<>(path);}
}
