package core.listener.remove;

import api.listener.ICacheRemoveListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存删除监视器类
 */
public class CacheRemoveListeners {
    public CacheRemoveListeners() {
    }

    /**
     * 默认监视器
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> List<ICacheRemoveListener<K,V>> defaults(){
        List<ICacheRemoveListener<K,V>> listeners = new ArrayList<>();
        listeners.add(new CacheRemoveListener<>());
        return listeners;
    }
}
