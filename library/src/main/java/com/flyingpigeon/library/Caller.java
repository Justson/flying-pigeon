package com.flyingpigeon.library;

import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.flyingpigeon.library.PigeonEngine.PREXFIX_METHOD;
import static com.flyingpigeon.library.PigeonEngine.PREXFIX_ROUTE;

/**
 * @author ringle-android
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
    public void call(Object... arg) throws IllegalAccessException, InvocationTargetException {
        target.setAccessible(true);
        target.invoke(owner, arg);
    }

    @Override
    public String callerId() {
        if (!TextUtils.isEmpty(route)) {
            return PREXFIX_ROUTE + route;
        } else {
            return PREXFIX_METHOD + target.getName();
        }
    }
}
