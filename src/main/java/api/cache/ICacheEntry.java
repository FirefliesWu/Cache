package api.cache;

/**
 * 缓存明细信息
 * @param <K>
 * @param <V>
 */
public interface ICacheEntry<K,V> {
    K key();
    V value();
}
