package core.proxy.proxyType;

import core.proxy.ICacheProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 无代理
 */
public class NoneProxy implements InvocationHandler, ICacheProxy {
    /**
     * 代理对象
     */
    private final Object target;

    public NoneProxy(Object target) {
        this.target = target;
    }

    /**
     * 无代理，返回初始对象
     * @return
     */
    @Override
    public Object proxy() {
        return this.target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(proxy,args);
    }
}
