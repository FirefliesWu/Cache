package core.listener.remove;

import api.listener.ICacheRemoveListener;
import api.listener.ICacheRemoveListenerContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * 内置删除监听器，默认且无法删除
 * @param <K>
 * @param <V>
 */
public class CacheRemoveListener<K,V> implements ICacheRemoveListener<K,V> {
    private static final Log log = LogFactory.getLog(CacheRemoveListener.class);
    @Override
    public void listen(ICacheRemoveListenerContext<K, V> context) {
        log.debug("Remove key: {}, value: {}, type: {}",
                context.key(),context.value(),context.type());
    }
}
