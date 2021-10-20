import api.cache.ICache;
import core.bs.CacheBs;
import core.evict.CacheEvicts;
import core.listener.MyRemoveListener;
import core.persist.CachePersistDbJson;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        //test1 size
////        ICache<String, String> cache = CacheBs.<String, String>newInstance().size(4).build();
////        cache.put("1","1");
////        cache.put("2","2");
////        cache.put("3","3");
////        cache.put("4","4");
////        cache.put("5","5");
////        System.out.println(cache.keySet());
//        Map<String,String> map = new HashMap();
//        System.out.println(map.isEmpty());

        //test2 expire
//        ICache<String,String> cache = CacheBs.<String,String>newInstance().size(3).build();
//        cache.put("1","1");
//        cache.put("2","2");
//
//        cache.expire("1",10);
//        System.out.println(cache.size()==2);
//        System.out.println("=================");
//        TimeUnit.MILLISECONDS.sleep(500);
//        System.out.println(cache.size()==1);
//        System.out.println(cache.keySet());
//    }
//        //test3 load
//        ICache<String, String> cache = CacheBs.<String, String>newInstance().load(new MyCacheLoad()).build();
//        System.out.println(cache.size());
//        System.out.println(cache.keySet());
        //test4 persist

//        ICache<String,String> cache = CacheBs.<String,String>newInstance().load(CacheLoads.dbJson("1.rdb"))
//                .build();
//        System.out.println(cache.keySet());
//        TimeUnit.SECONDS.sleep(5);
//        ICache<String,String> cache = CacheBs.<String,String>newInstance().size(1)
//                .addRemoveListener(new MyRemoveListener<>()).build();
//        cache.put("1","1");
//        cache.put("2","2");
//        cache.expire("2",10000);
//        ICache<String,String> cache = CacheBs.<String,String>newInstance().persist(CachePersists.<String,String>aof("1.aof")).build();
//        cache.put("1","1");
//        TimeUnit.SECONDS.sleep(10);
//        cache.expire("1",10);
//        TimeUnit.SECONDS.sleep(10);
//        cache.remove("2");
//        TimeUnit.SECONDS.sleep(10);

//        ICache<String,String> cache = CacheBs.<String,String>newInstance().persist(CachePersists.<String,String>aof("1.aof")).build();
//        cache.put("1","1");
//        cache.put("2","2");
//        cache.expire("1",1);
//        cache.put("3","3");
//        cache.remove("2");
//        cache.clear();
//        System.out.println("------"+cache.keySet());
//        ICache<String,String> cache = CacheBs.<String,String>newInstance().load(CacheLoads.<String,String>aof("1.aof")).build();
//        System.out.println(cache.keySet());
        ICache<String,String> cache = CacheBs.<String,String>newInstance().size(3).evict(CacheEvicts.<String,String>lru2()).build();
        cache.put("A","HELLO");
        cache.put("B","World");
        cache.put("C","FIFO");
        cache.get("A");
        cache.put("D","LRU");
        System.out.println(cache.size());
        System.out.println(cache.keySet());

    }

    public void test2(){
        ICache<String,String> cache = CacheBs.<String,String>newInstance().size(1).persist(new CachePersistDbJson<>("1.rdb"))
                .addRemoveListener(new MyRemoveListener<>()).build();
        cache.put("1","1");
        cache.put("2","2");
        cache.expire("2",1);
    }
}
