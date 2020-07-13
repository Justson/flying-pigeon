package com.flyingpigeon.library.boxing;

import android.os.Bundle;
import android.util.Pair;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public interface ServerBoxmen<T> {

    Pair<Class<?>[], Object[]> unboxing(T t);

    void boxing(Bundle in, Bundle out, Object value);

}
