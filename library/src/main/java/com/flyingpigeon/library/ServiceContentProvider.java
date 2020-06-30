package com.flyingpigeon.library;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.flyingpigeon.library.Config.PREFIX;
import static com.flyingpigeon.library.PigeonConstant.*;

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
        Log.e(TAG, "path:" + path);
        if (!path.startsWith("/pigeon")) {
            return null;
        }
        try {
            if (path.startsWith("/pigeon/10")) {
                cursor = Server.getInstance().dispatch(uri, selectionArgs, path.replace("/pigeon/10/", ""));
            } else if (path.startsWith("/pigeon/11")) {
                cursor = Server.getInstance().dispatch0(uri, selectionArgs, path.replace("/pigeon/11/", ""));
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
            assert extras != null;
            extras.setClassLoader(Pair.class.getClassLoader());
            int approach = extras.getInt(PIGEON_KEY_LOOK_UP_APPROACH);
            if (approach == PIGEON_APPROACH_METHOD) {
                return Server.getInstance().dispatch(method, response, arg, extras);
            } else {
                String route = extras.getString(PIGEON_KEY_ROUTE);
                int flags = extras.getInt(PIGEON_KEY_FLAGS);
                Log.e(TAG, "call route:" + route + " isParamParcel:" + ParametersSpec.isParamParcel(flags));
                if (ParametersSpec.isParamParcel(flags)) {
                    return Server.getInstance().dispatch(method, extras, response);
                } else {
                    Server.getInstance().dispatch0(method, extras, response);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_ILLEGALACCESS);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_LOST_CLASS);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }


    @NonNull
    @Override
    public <T> ParcelFileDescriptor openPipeHelper(@NonNull Uri uri, @NonNull String mimeType, @Nullable Bundle opts, @Nullable T args, @NonNull PipeDataWriter<T> func) throws FileNotFoundException {
        return super.openPipeHelper(uri, mimeType, opts, args, func);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
