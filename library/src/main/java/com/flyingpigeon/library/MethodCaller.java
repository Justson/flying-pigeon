package com.flyingpigeon.library;

import java.lang.reflect.InvocationTargetException;

/**
 * @author ringle-android
 * @date 20-6-11
 * @since 1.0.0
 */
public interface MethodCaller {

    void call(Object... arg) throws IllegalAccessException, InvocationTargetException;

    String callerId();
}
