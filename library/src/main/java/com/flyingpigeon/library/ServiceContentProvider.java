package com.flyingpigeon.library;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author ringle-android
 * @date 20-6-9
 * @since 1.0.0
 */
public class ServiceContentProvider extends ContentProvider {

    public static final String TAG = PREFIX + ServiceContentProvider.class.getSimpleName();
    private Gson mGson = new Gson();


    static ServiceContentProvider serviceContext;

    @Override
    public boolean onCreate() {
        Log.e(TAG, "onCreate");
        serviceContext = this;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        throw new UnsupportedOperationException();
    }


    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Log.e(TAG, "call extras:" + mGson.toJson(extras));
        Bundle response = new Bundle();
        try {
            MethodCaller methodCaller = PigeonEngine.getInstance().parseRequest(method, arg, extras);
            Object result = methodCaller.call(PigeonEngine.getInstance().parseData(arg, extras));
            PigeonEngine.getInstance().buildResponse(extras, response, result);
            response.putInt(PigeonEngine.KEY_RESPONSE_CODE, PigeonEngine.RESPONSE_RESULE_SUCCESS);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            response.putInt(PigeonEngine.KEY_RESPONSE_CODE, PigeonEngine.RESPONSE_RESULE_NO_SUCH_METHOD);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.putInt(PigeonEngine.KEY_RESPONSE_CODE, PigeonEngine.RESPONSE_RESULE_ILLGEALACCESS);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.putInt(PigeonEngine.KEY_RESPONSE_CODE, PigeonEngine.RESPONSE_RESULE_NO_SUCH_METHOD);
        }
        return response;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String authority, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Log.e(TAG, "String call:" + method + "arg:" + mGson.toJson(arg));
        return super.call(authority, method, arg, extras);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
