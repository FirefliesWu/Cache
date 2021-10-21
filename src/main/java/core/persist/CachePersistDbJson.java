package core.persist;

import api.cache.ICache;
import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.model.PersistRdbEntry;

import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@SuppressWarnings("unchecked")
public class CachePersistDbJson <K,V> extends CachePersistAdaptor<K,V> {

    /**
     * 数据库路径
     */
    private final String dbPath;
    private final Log log = LogFactory.getLog(CachePersistDbJson.class);
    public CachePersistDbJson(String dbPath) {
        this.dbPath = dbPath;
    }

    /**
     * 持久化
     * key长度 key+value
     * 第一个空格，获取 key 的长度，然后截取
     * @param cache 缓存
     */
    @Override
    public void persist(ICache<K,V> cache) {
        log.info("进行RDB持久化，目标路径: {}",dbPath);
        Set<Map.Entry<K, V>> entrySet = cache.entrySet();
        //创建文件
        FileUtil.createFile(dbPath);
        //清空文件
        FileUtil.truncate(dbPath);
        for (Map.Entry<K, V> entry : entrySet) {
            K key = entry.getKey();
            Long expireTime = cache.expire().expireTime(key);
            PersistRdbEntry<K,V> persistEntry = new PersistRdbEntry<>();
            persistEntry.setKey(key);
            persistEntry.setValue(entry.getValue());
            persistEntry.setExpire(expireTime);

            String line = JSON.toJSONString(persistEntry);
            FileUtil.write(dbPath, line, StandardOpenOption.APPEND);
        }
    }

    @Override
    public long delay() {
        return 5;
    }

    @Override
    public long period() {
        return 5;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}
