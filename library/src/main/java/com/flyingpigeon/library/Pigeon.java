package com.flyingpigeon.library;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author ringle-android
 * @date 20-6-8
 * @since 1.0.0
 */
public final class Pigeon {

    private Pigeon() {
    }


    public <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });

    }


}
