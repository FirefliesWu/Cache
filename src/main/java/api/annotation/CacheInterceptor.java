package api.annotation;

import java.lang.annotation.*;

/**
 * 缓存拦截器
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheInterceptor {
    /**
     * 通用拦截器
     * 1、耗时统计
     * 2、慢日志统计
     * etc.
     * @return 默认开启
     */
    boolean common() default true;

    /**
     * 是否启用刷新
     * @return
     */
    boolean refresh() default false;

    /**
     * 是否执行驱逐更新
     *
     * 主要用于 LRU/LFU 等驱除策略
     * @return 是否
     */
    boolean evict() default false;

    /**
     * 操作是否需要 append to file ，默认为 false
     * 主要针对 cache 内容有变更的操作，不包含查询操作。
     * 包括删除、添加、过期等操作
     * @return
     */
    boolean aof() default false;
}
