package core.load;

import api.annotation.CacheInterceptor;
import api.cache.ICache;
import api.load.ICacheLoad;
import core.cache.Cache;
import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.model.PersistAofEntry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存加载策略——AOF文件模式
 * @param <K>
 * @param <V>
 */
public class CacheLoadAof<K,V> implements ICacheLoad<K,V> {
    private static final Log log = LogFactory.getLog(CacheLoadAof.class);
    /**
     * 文件路径
     */
    private final String dbPath;
    /**
     * 方法缓存
     * 暂时比较简单，直接通过方法判断即可，不必引入参数类型增加复杂度。
     */
    private static final Map<String,Method> METHOD_MAP = new HashMap<>();

    static {
        Method[] methods = Cache.class.getMethods();

        for (Method method : methods) {
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);

            if (cacheInterceptor!=null){
                if (cacheInterceptor.aof()){
                    String methodName = method.getName();
                    METHOD_MAP.put(methodName,method);
                }
            }
        }
    }
    public CacheLoadAof(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[Load] 开始处理 path:{}",dbPath);
        if (CollectionUtil.isEmpty(lines)){
            log.info("[Load] path:{} 文件内容为空，直接返回",dbPath);
            return;
        }
        for (String line : lines) {
            if (StringUtil.isEmpty(line)) continue;
            //执行反序列化，复杂类型会失败
            PersistAofEntry aofEntry = JSON.parseObject(line, PersistAofEntry.class);
            final String methodName = aofEntry.getMethodName();
            final Object[] objects = aofEntry.getParams();
            final Method method = METHOD_MAP.get(methodName);
            try {
                //反射调用
                method.invoke(cache,objects);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
