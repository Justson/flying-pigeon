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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.text.TextUtils;

import com.flyingpigeon.library.annotations.support.NonNull;
import com.flyingpigeon.library.annotations.support.Nullable;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import static com.flyingpigeon.library.Config.PREFIX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CALLING_PACKAGE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_FLAGS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LOOK_UP_APPROACH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE_CODE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_ILLEGALACCESS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_LOST_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_REMOTE_EXCEPTION;

/**
 * @author xiaozhongcen
 * @date 20-6-9
 * @since 1.0.0
 */
public class ServiceContentProvider extends ContentProvider {

    public static final String TAG = PREFIX + ServiceContentProvider.class.getSimpleName();


    @Override
    public boolean onCreate() {
        ServiceManager.getInstance().publish(this);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (!path.startsWith("/pigeon")) {
            return null;
        }
        assert selectionArgs != null;
        try {
            if (isSegmentMethod(path)) {
                cursor = Server.getInstance().dispatch(uri, selectionArgs, getMethod(path));
            } else if (isSegmentRoute(path)) {
                cursor = Server.getInstance().dispatch0(uri, selectionArgs, getRoute(path));
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Bundle bundle = new Bundle();
            BundleCursor bundleCursor = new BundleCursor(bundle, new String[]{});
            bundle.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_REMOTE_EXCEPTION);

            cursor = bundleCursor;
        }
        return cursor;
    }

    private String getMethod(String path) {
        return path.replace("/pigeon/10/", "");
    }

    private String getRoute(String path) {
        return path.replace("/pigeon/11/", "");
    }

    private boolean isSegmentRoute(String path) {
        return path.startsWith("/pigeon/11");
    }

    private boolean isSegmentMethod(String path) {
        return path.startsWith("/pigeon/10");
    }


    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle in) {
        if (in == null) {
            return null;
        }
        Bundle response = new Bundle();
        in.setClassLoader(Pair.class.getClassLoader());
        Parcelable returnResponse = in.getParcelable(PIGEON_KEY_RESPONSE);
        response.putParcelable(PIGEON_KEY_RESPONSE, returnResponse);
        try {
            String calling = getCallingPackage();
            if (!TextUtils.isEmpty(calling)) {
                in.putString(PIGEON_KEY_CALLING_PACKAGE, calling);
            }
            int approach = in.getInt(PIGEON_KEY_LOOK_UP_APPROACH);
            if (approach == PIGEON_APPROACH_METHOD) {
                Server.getInstance().dispatch(method, response, arg, in);
            } else {
                String route = in.getString(PIGEON_KEY_ROUTE);
                int flags = in.getInt(PIGEON_KEY_FLAGS);
                if (ParametersSpec.isParamParcel(flags)) {
                    Server.getInstance().dispatch(method, in, response);
                } else {
                    Server.getInstance().dispatch0(method, in, response);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD);
        } catch (NotFoundRouteException e) {
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_ILLEGALACCESS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_LOST_CLASS);
        } catch (Throwable e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_REMOTE_EXCEPTION);
        }
        return response;
    }


    @Nullable
    @Override
    public Bundle call(@NonNull String authority, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        return super.call(authority, method, arg, extras);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return uri.toString();
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    @NonNull
    @Override
    public <T> ParcelFileDescriptor openPipeHelper(@NonNull Uri uri, @NonNull String mimeType, @Nullable Bundle opts, @Nullable T args, @NonNull PipeDataWriter<T> func) throws FileNotFoundException {
        return super.openPipeHelper(uri, mimeType, opts, args, func);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
