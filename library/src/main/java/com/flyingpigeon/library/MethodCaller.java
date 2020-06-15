package com.flyingpigeon.library;

import java.lang.reflect.InvocationTargetException;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public interface MethodCaller {

    Object call(Object... arg) throws IllegalAccessException, InvocationTargetException;

}
