package com.flyingpigeon.library.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class Invoker extends AbsMethodInvoker {

    private Method target;
    private String route;
    private Object owner;

    public Invoker(@NonNull Method target, @Nullable String route, @Nullable Object owner) {
        super(target);
        this.target = target;
        this.route = route;
        this.owner = owner;
    }

    @Override
    public Object invoke(Object... arg) throws IllegalAccessException, InvocationTargetException {
        target.setAccessible(true);
        return super.invoke(owner, arg);
    }

}
