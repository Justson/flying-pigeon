/*
 * Copyright (C)  Justson(https://github.com/Justson/flying-pigeon)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flyingpigeon.library;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import java.lang.reflect.Method;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_FLAGS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_PATH_SEGMENT_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_PATH_START;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public class RealCall {

    private static final String TAG = RealCall.class.getSimpleName();
    private final Pigeon mPigeon;
    private final ProviderConnection connection;

    RealCall(Context context, Pigeon pigeon) {
        this.mPigeon = pigeon;
        this.connection = new ProviderConnection(context.getContentResolver(), pigeon.base);
    }

    Bundle execute(Method method, Bundle bundle) {
        return execute(method.getName(), null, bundle);
    }

    Bundle execute(String method, String arg, Bundle bundle) {
        try {
            return connection.call(method, arg, bundle);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    Bundle execute(String route, Bundle bundle) {
        int flags = 0;
        flags = ParametersSpec.setParamParcel(flags, false);
        bundle.putInt(PIGEON_KEY_FLAGS, flags);
        return execute("", "", bundle);
    }


    Cursor execute(Method method, Class<?> service, String[] contentValues) {
        Uri uri = mPigeon.base.buildUpon().appendPath(PIGEON_PATH_START).appendPath(PIGEON_PATH_SEGMENT_METHOD).appendPath(method.getName()).appendQueryParameter(PIGEON_KEY_CLASS, service.getName()).build();
        return query(uri, contentValues);
    }

    Cursor query(Uri uri, String[] contentValues) {
        try {
            return connection.query(uri, new String[]{}, "", contentValues, "");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    void close() {
        connection.close();
    }
}
