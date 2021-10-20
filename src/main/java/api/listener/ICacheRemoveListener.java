package api.listener;

/**
 * 删除监听器接口
 * @param <K>
 * @param <V>
 */
public interface ICacheRemoveListener<K,V> {
    /**
     * 监听
     * @param context
     */
    void listen(final ICacheRemoveListenerContext<K,V> context);
}
