package core.persist;

import api.cache.ICache;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * 无操作
 * @param <K>
 * @param <V>
 */
public class CachePersistNone<K,V> extends CachePersistAdaptor<K,V> {
    private final Log log = LogFactory.getLog(CachePersistNone.class);
    @Override
    public void persist(ICache<K, V> cache) {
        log.info("当前无持久化策略");
    }

}
