package api.expire;

import java.util.Collection;

/**
 * 缓存过期接口
 */
public interface ICacheExpire<K,V> {
    /**
     *  指定过期信息
     * @param key key
     * @param expireAt 什么时候过期
     */
    void expire(final K key,final long expireAt);

    /**
     * 惰性删除需要处理的keys
     * @param keyList keys
     */
    void refreshExpire(final Collection<K> keyList);

    /**
     * 待过期的 key
     * @param key
     * @return
     */
    Long expireTime(final K key);
}
