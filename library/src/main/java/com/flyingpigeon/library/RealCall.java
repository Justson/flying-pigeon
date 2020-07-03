package com.flyingpigeon.library;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.flyingpigeon.library.log.Log;

import java.lang.reflect.Method;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_FLAGS;

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
            Uri uri = mPigeon.base.buildUpon().appendPath("pigeon/1").appendPath(route).build();
            int flags = 0;
            flags = ParametersSpec.setParamParcel(flags, false);
            bundle.putInt(PIGEON_KEY_FLAGS, flags);
            Log.e(TAG, "uri:" + uri.toString() + " contentValues:" + bundle + " contentResolver:" + contentResolver);
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
            Uri uri = mPigeon.base.buildUpon().appendPath("pigeon/11/" + method.getName()).appendQueryParameter(PIGEON_KEY_CLASS, service.getName()).build();
            return contentResolver.query(uri, new String[]{}, "", contentValues, "");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
