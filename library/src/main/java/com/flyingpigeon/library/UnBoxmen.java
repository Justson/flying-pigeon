package com.flyingpigeon.library;

import android.os.Bundle;
import android.util.Pair;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public interface UnBoxmen<T, O> {

    Pair<T, O> unboxing(Bundle bundle);


}
