package api.listener;

/**
 * 删除监听器上下文
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheRemoveListenerContext<K, V> {

    /**
     * 清空的 key
     * @return
     */
    K key();

    /**
     * 清空的 value
     * @return
     */
    V value();

    /**
     * 清空的 type
     * @return
     */
    String type();
}
