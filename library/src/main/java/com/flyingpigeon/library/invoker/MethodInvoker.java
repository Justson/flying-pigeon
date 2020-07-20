package com.flyingpigeon.library.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public interface MethodInvoker {

    Object invoke(Object... arg) throws IllegalAccessException, InvocationTargetException;

}
