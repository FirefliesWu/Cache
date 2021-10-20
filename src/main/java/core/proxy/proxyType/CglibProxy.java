package core.proxy.proxyType;

import api.cache.ICache;
import core.proxy.ICacheProxy;
import core.proxy.CacheProxyBs;
import core.proxy.CacheProxyBsContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB 代理类
 */
public class CglibProxy implements MethodInterceptor, ICacheProxy {
    /**
     * 被代理的对象
     */
    private final ICache target;

    public CglibProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        //目标对象类
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        //通过字节码技术创建目标对象类的子类实例作为代理
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        CacheProxyBsContext cacheProxyBsContext = CacheProxyBsContext.newInstance()
                .method(method).params(objects).target(target);
        return CacheProxyBs.newInstance().context(cacheProxyBsContext).execute();
    }
}
