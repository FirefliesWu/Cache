package core.listener;

import api.listener.ICacheRemoveListener;
import api.listener.ICacheRemoveListenerContext;

public class MyRemoveListener<K,V> implements ICacheRemoveListener<K,V> {
    @Override
    public void listen(ICacheRemoveListenerContext<K, V> context) {
        System.out.println("【删除提示】自定义删除监听器:----> " + context.key());
    }
}
