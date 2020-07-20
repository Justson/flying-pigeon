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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.flyingpigeon.library.log.FlyPigeonLog;

import java.lang.reflect.Method;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_FLAGS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_PATH_SEGMENT_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_PATH_SEGMENT_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_PATH_START;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public class RealCall {

    private static final String TAG = RealCall.class.getSimpleName();
    private Context mContext;
    private Pigeon mPigeon;

    RealCall(Context context, Pigeon pigeon) {
        this.mContext = context;
        this.mPigeon = pigeon;
    }

    Bundle execute(Method method, Bundle bundle) {
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            Bundle response = contentResolver.call(mPigeon.base, method.getName(), null, bundle);
            return response;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    Bundle execute(String route, Bundle bundle) {
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            Uri uri = mPigeon.base.buildUpon().appendPath(PIGEON_PATH_START).appendPath(PIGEON_PATH_SEGMENT_ROUTE).appendPath(route).build();
            int flags = 0;
            flags = ParametersSpec.setParamParcel(flags, false);
            bundle.putInt(PIGEON_KEY_FLAGS, flags);
            FlyPigeonLog.e(TAG, "uri:" + uri.toString() + " contentValues:" + bundle + " contentResolver:" + contentResolver);
            Bundle result = contentResolver.call(uri, "", "", bundle);
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


    Cursor execute(Method method, Class<?> service, String[] contentValues) {
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            Uri uri = mPigeon.base.buildUpon().appendPath(PIGEON_PATH_START).appendPath(PIGEON_PATH_SEGMENT_METHOD).appendPath(method.getName()).appendQueryParameter(PIGEON_KEY_CLASS, service.getName()).build();
            return contentResolver.query(uri, new String[]{}, "", contentValues, "");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
