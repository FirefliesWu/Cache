package api.listener;

/**
 * 慢日志接口
 */
public interface ICacheSlowListener {
    /**
     * 监听
     * @param context
     */
    void listen(final ICacheSlowListenerContext context);

    /**
     * 慢日志的阈值
     * @return
     */
    long slowerThanMills();
}
