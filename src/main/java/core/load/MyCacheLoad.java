package core.load;

import api.cache.ICache;
import api.load.ICacheLoad;

public class MyCacheLoad implements ICacheLoad<String,String> {
    @Override
    public void load(ICache<String, String> cache) {
        cache.put("1","1");
        cache.put("2","2");
        cache.put("3","3");
    }
}
