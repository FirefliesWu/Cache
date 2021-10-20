package core.listener.slow;

import api.listener.ICacheSlowListener;

import java.util.ArrayList;
import java.util.List;

public final class CacheSlowListeners {
    public CacheSlowListeners() {
    }

    /**
     * 无操作
     * @return
     */
    public static List<ICacheSlowListener> none(){
        return new ArrayList<>();
    }

    /**
     * 默认实现
     * @return
     */
    public static List<ICacheSlowListener> defaults(){
        List<ICacheSlowListener> listeners = new ArrayList<>();
        listeners.add(new CacheSlowListener());
        return listeners;
    }
}
