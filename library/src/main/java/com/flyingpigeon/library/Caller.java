package com.flyingpigeon.library;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class Caller implements MethodCaller {

    private Method target;
    private String route;
    private Object owner;

    public Caller(@NonNull Method target, @Nullable String route, @Nullable Object owner) {
        this.target = target;
        this.route = route;
        this.owner = owner;
    }

    @Override
    public Object call(Object... arg) throws IllegalAccessException, InvocationTargetException {
        target.setAccessible(true);
        return target.invoke(owner, arg);
    }

}
