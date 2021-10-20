package core.load;

import api.cache.ICache;
import api.load.ICacheLoad;
import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.model.PersistRdbEntry;

import java.util.List;
@SuppressWarnings("unchecked")
public class CacheLoadDbJson<K,V> implements ICacheLoad<K,V> {
    private static final Log log = LogFactory.getLog(CacheLoadDbJson.class);
    //文件路径
    private final String dbPath;

    public CacheLoadDbJson(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[Load] 开始处理 path:{}",dbPath);
        if (lines.isEmpty()){
            log.info("[Load] path: {} 文件内容为空，直接返回",dbPath);
            return;
        }
        for (String line : lines) {
            if (StringUtil.isEmpty(line)){
                continue;
            }
            //执行反序列化，复杂的反序列化会失败
            PersistRdbEntry<K,V> entry = JSON.parseObject(line, PersistRdbEntry.class);
            K key = entry.getKey();
            V value = entry.getValue();
            Long expire = entry.getExpire();
            cache.put(key,value);
            if (ObjectUtil.isNotNull(expire)){
                cache.expireAt(key,expire);
            }
        }
    }
}
