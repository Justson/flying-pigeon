package com.flyingpigeon.library;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.flyingpigeon.library.Config.PREFIX;
import static com.flyingpigeon.library.ServiceManager.APPROACH_METHOD;
import static com.flyingpigeon.library.ServiceManager.APPROACH_ROUTE;
import static com.flyingpigeon.library.ServiceManager.KEY_LOOK_UP_APPROACH;

/**
 * @author xiaozhongcen
 * @date 20-6-9
 * @since 1.0.0
 */
public class ServiceContentProvider extends ContentProvider {

    public static final String TAG = PREFIX + ServiceContentProvider.class.getSimpleName();
    private Gson mGson = new Gson();


    @Override
    public boolean onCreate() {
        Log.e(TAG, "onCreate");
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
        try {
            if (path.startsWith("pigeon/10")) {
                cursor = ServiceManager.getInstance().matchQuery(uri, selectionArgs, path.replace("/pigeon/10/", ""));
            } else if (path.startsWith("pigeon/11")) {
                cursor = ServiceManager.getInstance().matchQuery0(uri, selectionArgs, path.replace("/pigeon/11/", ""));

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle response = new Bundle();
        try {
            extras.setClassLoader(Pair.class.getClassLoader());
            MethodCaller methodCaller;
            int approach = extras.getInt(KEY_LOOK_UP_APPROACH);
            if (approach == APPROACH_METHOD) {
                methodCaller = ServiceManager.getInstance().approachByMethod(method, extras);
                Object result = methodCaller.call(ServiceManager.getInstance().parseData(arg, extras));
                ServiceManager.getInstance().buildResponse(extras, response, result);
                response.putInt(ServiceManager.KEY_RESPONSE_CODE, ServiceManager.RESPONSE_RESULE_SUCCESS);
            } else {
                ServiceManager.getInstance().approachByRoute(method, extras, response);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            response.putInt(ServiceManager.KEY_RESPONSE_CODE, ServiceManager.RESPONSE_RESULE_NO_SUCH_METHOD);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.putInt(ServiceManager.KEY_RESPONSE_CODE, ServiceManager.RESPONSE_RESULE_ILLEGALACCESS);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.putInt(ServiceManager.KEY_RESPONSE_CODE, ServiceManager.RESPONSE_RESULE_NO_SUCH_METHOD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.putInt(ServiceManager.KEY_RESPONSE_CODE, ServiceManager.RESPONSE_RESULE_LOST_CLASS);
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
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.e(TAG, "insert:" + uri.toString() + " uri.getPath():" + uri.getPath());
        try {
            int approach = values.getAsInteger(KEY_LOOK_UP_APPROACH);
            MethodCaller methodCaller;
            if (approach == APPROACH_METHOD) {
                methodCaller = ServiceManager.getInstance().approachByMethodInsert(uri, values, uri.getPath().replace("/pigeon/0/", ""));
                Object result = methodCaller.call(ServiceManager.getInstance().parseDataInsert(uri, values));
                if (result instanceof String) {
                    Uri returnUri = uri.buildUpon().appendQueryParameter("result", result.toString()).build();
                    return returnUri;
                } else {
                    return uri;
                }
            } else if (approach == APPROACH_ROUTE) {
                methodCaller = ServiceManager.getInstance().approachByRouteInsert(uri, values, uri.getPath().replace("/pigeon/1/", ""));
                Object result = methodCaller.call(ServiceManager.getInstance().parseDataInsert(uri, values));
                if (result instanceof String) {
                    Uri returnUri = uri.buildUpon().appendQueryParameter("result", result.toString()).build();
                    return returnUri;
                } else {
                    return uri;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return uri;
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
