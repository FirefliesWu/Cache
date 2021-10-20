package core.listener.slow;

import api.listener.ICacheSlowListener;
import api.listener.ICacheSlowListenerContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

public class CacheSlowListener implements ICacheSlowListener {
    private static final Log log = LogFactory.getLog(CacheSlowListener.class);
    @Override
    public void listen(ICacheSlowListenerContext context) {
        log.warn("[Slow] methodName: {} , params: {} , cost time: {}",
                context.methodName(),context.params(),context.costTimeMills());
    }

    @Override
    public long slowerThanMills() {
        return 1000L;
    }
}
