# 项目简介
本项目实现了一个可拓展的渐进式本地缓存，模仿 Redis 实现了一些类似的功能。本项目采用 fluent 流式风格编程，是一套简单易用的缓存框架。
## 特性
+ fluent 流式编程
+ 支持自定义 map 实现策略
+ 支持自定义 cache 大小
+ 支持 expire 过期特性
+ 支持自定义 evict 驱除策略，内置多种驱除策略
+ 支持自定义删除监听器
+ 支持 load 初始化和 persist 持久化
+ 持久化支持 RDB 和 AOF 两种模式
## 属性配置
`CacheBs`为缓存的引导类，采用 fluent 流式写法。
```
ICache<String,String> cache = CacheBs.<String,String>newInstance().size(3).build();
cache.put("1","1");
cache.put("2","2");
cache.put("3","3");
cache.put("4","4");
cache.put("5","5");
System.out.println(cache.keySet());
```
默认驱除策略为先进先出，此时输出 keys 内容如下
```
[3, 4, 5]
```
上述配置也等价于
```
ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .evict(CacheEvicts.<String, String>fifo())
                .size(3)
                .build();
```
## 淘汰策略
目前内置了几种淘汰策略，可通过`CacheEvicts`工具类来创建。
策略|说明
-|-
none|没有任何淘汰策略
fifo|先进先出（默认）
lru|最基本的朴素LRU
lruDoubleListMap|基于双向链表+Map实现的朴素LRU
lruLinkedHashMap|基于linkedHashMap实现的朴素LRU
lru2Q|基于 LRU Two-Queue 的改进版 LRU 实现
lru2|基于 LRU-2 的改进版 LRU 实现
lfu（新增）|最少使用频次
## 过期 Expire 和 ExpireAt
```
ICache<String, String> cache = CacheBs.<String,String>newInstance()
        .size(3)
        .build();

cache.put("1", "1");
cache.put("2", "2");

cache.expire("1", 10);
```
`cache.expire("1", 10)` 指对应的 key 在10ms后过期。
## 删除监听器
### 说明
淘汰和过期为缓存的内部行为，用户可自定义删除监听器来监听。  
直接实现`ICacheRemoveListener`接口即可
```
public class MyRemoveListener<K,V> implements ICacheRemoveListener<K,V> {

    @Override
    public void listen(ICacheRemoveListenerContext<K, V> context) {
       System.out.println("【删除提示】自定义删除监听器:----> " + context.key());
    }

}
```
### 使用
```
ICache<String, String> cache = CacheBs.<String,String>newInstance()
        .size(1)
        .addRemoveListener(new MyRemoveListener<String, String>())
        .build();

cache.put("1", "1");
cache.put("2", "2");
```
+ 输出
```
【删除提示】自定义删除监听器:----> 1
```
## 慢操作监听器
### 说明
Redis 中会存储慢操作的相关日志信息，设置阈值 slowlog-log-slower-than，单位是毫秒，默认值是10000.  
引入类似删除的监听器.
### 自定义监听器
实现接口`ICacheSlowListener`可自定义监听器，设置不同的阈值可以做到细分操作。
```
public class MySlowListener implements ICacheSlowListener {

    @Override
    public void listen(ICacheSlowListenerContext context) {
        System.out.println("【慢日志】name: " + context.methodName());
    }
    //慢日志的时间阈值
    @Override
    public long slowerThanMills() {
        return 0;
    }
}
```
### 使用
```
ICache<String, String> cache = CacheBs.<String,String>newInstance()
        .addSlowListener(new MySlowListener())
        .build();

cache.put("1", "2");
cache.get("1");
cache.clear();
```
### 输出
```
[DEBUG] [2021-10-20 20:20:37.754] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: put
[DEBUG] [2021-10-20 20:20:37.755] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: put, cost: 2ms
【慢日志】 name: put
[DEBUG] [2021-10-20 20:20:37.756] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: get
[DEBUG] [2021-10-20 20:20:37.756] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: get, cost: 1ms
【慢日志】 name: get
[DEBUG] [2021-10-20 20:20:37.756] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: clear
[DEBUG] [2021-10-20 20:20:37.757] [main] [c.i.r.CacheInterceptorRefresh.before] - Refresh start!
[DEBUG] [2021-10-20 20:20:37.757] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: clear, cost: 1ms
【慢日志】 name: clear
```
## 添加 persist 持久类
### 说明
如果只是将文件存储在内存里，一旦重启，信息就丢失了。因此引入持久化，将Keys、Values信息存储下来。
### 持久化
目前有两种持久化方式：RDB 和 AOF。
### RDB 模式
`CachePersists.<String, String>dbJson("1.rdb")`指定将数据文件持久化存储在`1.rdb`文件中。定期执行，默认 5 min 持久化一次。  
支持自定义时间，修改`CachePersistDbJson.java`中的方法即可
```
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
        return TimeUnit.MINUTES;
    }
```
+ 测试
```
public void persistTest() throws InterruptedException {
    ICache<String, String> cache = CacheBs.<String,String>newInstance()          
            .persist(CachePersists.<String, String>dbJson("1.rdb"))
            .build();
    TimeUnit.MINUTES.sleep(5);
}
```
+ `1.rdb`文件内容
```
{"key":"1","value":"1"}
{"key":"2","value":"2"}
```
### AOF 模式
``指定将数据文件持久化存储到`1.aof`文件中。
#### 说明
AOF 仅追加`put`,`expire`,`remove`,`clear`等修改内容的指令到文件中，默认周期为 1 s ,可自定义周期。
+ 测试
```
ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .persist(CachePersists.<String, String>aof("1.aof"))
                .build();
        cache.put("1","1");
        cache.put("2","2");
        cache.expire("1",1);
        cache.put("3","3");
        cache.remove("2");
        cache.clear();
```
+ 日志
```
[DEBUG] [2021-10-20 20:41:15.116] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: put
[DEBUG] [2021-10-20 20:41:15.117] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: put, cost: 3ms
[DEBUG] [2021-10-20 20:41:15.182] [main] [c.i.a.CacheInterceptorAof.after] - AOF 开始追加文件内容：{"methodName":"put","params":["1","1"]}
[DEBUG] [2021-10-20 20:41:15.182] [main] [c.i.a.CacheInterceptorAof.after] - AOF 完成追加文件内容：{"methodName":"put","params":["1","1"]}
[DEBUG] [2021-10-20 20:41:15.182] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: put
[DEBUG] [2021-10-20 20:41:15.183] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: put, cost: 0ms
[DEBUG] [2021-10-20 20:41:15.183] [main] [c.i.a.CacheInterceptorAof.after] - AOF 开始追加文件内容：{"methodName":"put","params":["2","2"]}
[DEBUG] [2021-10-20 20:41:15.183] [main] [c.i.a.CacheInterceptorAof.after] - AOF 完成追加文件内容：{"methodName":"put","params":["2","2"]}
[DEBUG] [2021-10-20 20:41:15.183] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: expire
[DEBUG] [2021-10-20 20:41:15.184] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: expireAt
[DEBUG] [2021-10-20 20:41:15.184] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: expireAt, cost: 0ms
[DEBUG] [2021-10-20 20:41:15.184] [main] [c.i.a.CacheInterceptorAof.after] - AOF 开始追加文件内容：{"methodName":"expireAt","params":["1",1634733675184]}
[DEBUG] [2021-10-20 20:41:15.184] [main] [c.i.a.CacheInterceptorAof.after] - AOF 完成追加文件内容：{"methodName":"expireAt","params":["1",1634733675184]}
[DEBUG] [2021-10-20 20:41:15.185] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: expire, cost: 1ms
[DEBUG] [2021-10-20 20:41:15.185] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: put
[DEBUG] [2021-10-20 20:41:15.185] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: put, cost: 0ms
[DEBUG] [2021-10-20 20:41:15.185] [main] [c.i.a.CacheInterceptorAof.after] - AOF 开始追加文件内容：{"methodName":"put","params":["3","3"]}
[DEBUG] [2021-10-20 20:41:15.186] [main] [c.i.a.CacheInterceptorAof.after] - AOF 完成追加文件内容：{"methodName":"put","params":["3","3"]}
[DEBUG] [2021-10-20 20:41:15.186] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: remove
[DEBUG] [2021-10-20 20:41:15.186] [main] [c.i.r.CacheInterceptorRefresh.before] - Refresh start!
[DEBUG] [2021-10-20 20:41:15.187] [main] [c.l.r.CacheRemoveListener.listen] - Remove key: 1, value: 1, type: expire
[DEBUG] [2021-10-20 20:41:15.187] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: remove, cost: 1ms
[DEBUG] [2021-10-20 20:41:15.187] [main] [c.i.a.CacheInterceptorAof.after] - AOF 开始追加文件内容：{"methodName":"remove","params":["2"]}
[DEBUG] [2021-10-20 20:41:15.188] [main] [c.i.a.CacheInterceptorAof.after] - AOF 完成追加文件内容：{"methodName":"remove","params":["2"]}
[DEBUG] [2021-10-20 20:41:15.188] [main] [c.i.c.CacheInterceptorCost.before] - Cost start, method: clear
[DEBUG] [2021-10-20 20:41:15.188] [main] [c.i.r.CacheInterceptorRefresh.before] - Refresh start!
[DEBUG] [2021-10-20 20:41:15.188] [main] [c.i.c.CacheInterceptorCost.after] - Cost end, method: clear, cost: 0ms
[DEBUG] [2021-10-20 20:41:15.188] [main] [c.i.a.CacheInterceptorAof.after] - AOF 开始追加文件内容：{"methodName":"clear","params":[]}
[DEBUG] [2021-10-20 20:41:15.188] [main] [c.i.a.CacheInterceptorAof.after] - AOF 完成追加文件内容：{"methodName":"clear","params":[]}
```
+ `1.aof`文件内容
```
{"methodName":"put","params":["1","1"]}
{"methodName":"put","params":["2","2"]}
{"methodName":"expireAt","params":["1",1634733675184]}
{"methodName":"put","params":["3","3"]}
{"methodName":"remove","params":["2"]}
{"methodName":"clear","params":[]}
```
## Load 加载器
### 说明
我们需要在 cache 初始化时（读取RDB、AOF等文件），添加对应的加载器即可。
### 实现
实现`ICacheLoad`接口即可
```
public class MyCacheLoad implements ICacheLoad<String,String> {

    @Override
    public void load(ICache<String, String> cache) {
        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
    }
}
```
该自定义初始化加载器在缓存初始化的时候放入了3个元素。
### 测试
```
ICache<String, String> cache = CacheBs.<String,String>newInstance()
        .load(new MyCacheLoad())
        .build();
System.out.println(cache.keySet());
```
+ 输出
```
[1, 2, 3]
```
### 加载持久化文件
+ 加载路径为`1.rdb`的 RDB 持久化文件
```
ICache<String,String> cache = CacheBs.<String,String>newInstance().load(CacheLoads.dbJson("1.rdb")).build();
```
+ 加载路径为`1.aof`的 AOF 持久化文件
```
ICache<String,String> cache = CacheBs.<String,String>newInstance().load(CacheLoads.<String,String>aof("1.aof")).build();
```
