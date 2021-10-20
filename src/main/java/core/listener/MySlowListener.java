package core.listener;

import api.listener.ICacheSlowListener;
import api.listener.ICacheSlowListenerContext;

public class MySlowListener implements ICacheSlowListener {
    @Override
    public void listen(ICacheSlowListenerContext context) {
        System.out.println("【慢日志】 name: " + context.methodName());
    }

    @Override
    public long slowerThanMills() {
        return 0;
    }
}
