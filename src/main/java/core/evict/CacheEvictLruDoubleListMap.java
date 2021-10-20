package core.evict;

import api.cache.ICache;
import api.cache.ICacheEntry;
import api.evict.ICacheEvictContext;
import core.exception.CacheRuntimeException;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import core.model.CacheEntry;
import core.model.DoubleListNode;

import java.util.HashMap;
import java.util.Map;

public class CacheEvictLruDoubleListMap<K,V> extends AbstractCacheEvict<K,V> {
    private static final Log log = LogFactory.getLog(CacheEvictLruDoubleListMap.class);
    //头结点
    private DoubleListNode<K,V> head;
    //尾结点
    private DoubleListNode<K,V> tail;
    /**
     * map信息
     * key：元素信息
     * value：元素在list中的节点信息
     */
    private Map<K,DoubleListNode<K,V>> indexMap;
    public CacheEvictLruDoubleListMap(){
        this.indexMap = new HashMap<>();
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K,V> result = null;
        final ICache<K,V> cache = context.cache();
        //超过限制，移除队尾元素
        if (cache.size() >= context.size()){
            //获取尾巴节点的前一个元素
            DoubleListNode<K,V> tailPre = this.tail.pre();
            if (tailPre == null){
                log.error("当前列表为空，无法进行删除");
                throw new CacheRuntimeException("不可删除头结点！");
            }
            K evictKey = tailPre.key();
            removeKey(evictKey);
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey,evictValue);
        }
        return result;
    }

    /**
     * 放入元素
     *
     * （1）删除已经存在的
     * （2）新元素放到元素头部
     *
     * @param key
     */
    @Override
    public void updateKey(K key) {
        //1.delect
        this.removeKey(key);
        //2.插入元素到头部
        //head<->next
        //变成：head<->new<->next
        DoubleListNode<K,V> newNode = new DoubleListNode<>();
        newNode.key(key);
        DoubleListNode<K,V> next = this.head.next();
        this.head.next(newNode);
        newNode.pre(this.head);
        next.pre(newNode);
        newNode.next(next);

        //3.插入到map中
        indexMap.put(key,newNode);
    }

    /**
     * 移除元素
     *
     * 1. 获取 map 中的元素
     * 2. 不存在直接返回，存在执行以下步骤：
     * 2.1 删除双向链表中的元素
     * 2.2 删除 map 中的元素
     *
     * @param key
     */
    @Override
    public void removeKey(K key) {
        DoubleListNode<K,V> node = indexMap.get(key);
        if (ObjectUtil.isNull(node)){
            return;
        }
        // 删除 list node
        // A<->B<->C
        // 删除 B，需要变成： A<->C
        DoubleListNode<K,V> pre = node.pre();
        DoubleListNode<K,V> next = node.next();

        pre.next(next);
        next.pre(pre);
        //删除map中对应的信息
        this.indexMap.remove(key);
    }
}
