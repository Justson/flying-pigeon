package com.flyingpigeon.library.boxing;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public interface RouteClientBoxmen<T, R> {

    T boxing(String route, Object[] params);

    R unboxing(T t);
}
