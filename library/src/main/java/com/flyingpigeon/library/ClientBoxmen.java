package com.flyingpigeon.library;

import java.lang.reflect.Type;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public interface ClientBoxmen<T, R> {

    T boxing(Object[] args, Type[] types, Type returnType);

    R unboxing(T t);
}
