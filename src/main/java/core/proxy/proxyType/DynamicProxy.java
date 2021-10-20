package core.proxy.proxyType;

import api.cache.ICache;
import core.proxy.ICacheProxy;
import core.proxy.CacheProxyBs;
import core.proxy.CacheProxyBsContext;
import core.proxy.ICacheProxyBsContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements InvocationHandler, ICacheProxy {
    /**
     * 被代理的对象
     */
    private final ICache target;

    public DynamicProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object proxy() {
        // 我们要代理哪个真实对象，就将该对象传进去，最后是通过该真实对象来调用其方法的
        InvocationHandler handler = new DynamicProxy(target);

        return Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                target.getClass().getInterfaces(),handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ICacheProxyBsContext context = CacheProxyBsContext.newInstance().method(method).params(args).target(target);
        return CacheProxyBs.newInstance().context(context).execute();
    }
}
