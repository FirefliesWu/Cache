package core.evict;

import api.evict.ICacheEvict;

public class CacheEvicts<K,V>{
    private CacheEvicts(){}

    /**
     * 无策略
     * @return
     */
    public static <K,V> ICacheEvict<K,V> none(){
        return new CacheEvictNone<>();
    }

    /**
     * fifo
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheEvict<K,V> fifo(){
        return new CacheEvictFIFO<>();
    }

    /**
     * 朴素lru，基于链表
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheEvict<K,V> lru(){ return new CacheEvictLRU<>(); }

    /**
     * 基于双向链表和哈希表的改进 LRU
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheEvict<K,V> lruDoubleListMap(){ return new CacheEvictLruDoubleListMap<>(); }

    /**
     * 基于LinkedHashMap的改进 LRU
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheEvict<K,V> lruLinkedHashMap(){ return new CacheEvictLruLinkedHashMap<>(); }

    /**
     * 基于Two-Queue的改进 LRU
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheEvict<K,V> lru2Q(){ return new CacheEvictLRU2Q<>(); }

    /**
     * 基于LRU-K的改进 LRU-2
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheEvict<K,V> lru2(){ return new CacheEvictLRU2<>(); }


}
