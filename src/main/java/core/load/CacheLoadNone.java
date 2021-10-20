package core.load;

import api.cache.ICache;
import api.load.ICacheLoad;

public class CacheLoadNone<K,V> implements ICacheLoad<K,V> {
    @Override
    public void load(ICache cache) {
        //nothing
    }
}
