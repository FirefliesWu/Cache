package api.listener;

/**
 * 慢日志监听上下文
 */
public interface ICacheSlowListenerContext {
    /**
     * 方法名
     * @return
     */
    String methodName();

    /**
     * 参数信息
     * @return 参数列表
     */
    Object[] params();

    /**
     * 方法结果
     * @return
     */
    Object result();

    /**
     * 开始时间
     * @return
     */
    long startTimeMills();

    /**
     * 结束时间
     * @return
     */
    long endTimeMills();

    /**
     * 消耗时间
     * @return
     */
    long costTimeMills();
}
