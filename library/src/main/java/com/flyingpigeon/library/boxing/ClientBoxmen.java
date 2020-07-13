package com.flyingpigeon.library.boxing;

import java.lang.reflect.Type;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public interface ClientBoxmen<T, P, R> {

    T boxing(Object[] args, Type[] types, Type returnType);

    R unboxing(P p);
}
