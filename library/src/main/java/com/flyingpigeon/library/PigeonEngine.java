package com.flyingpigeon.library;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author ringle-android
 * @date 20-6-11
 * @since 1.0.0
 */
public final class PigeonEngine {

    static final String PREXFIX_ROUTE = "route-";
    static final String PREXFIX_METHOD = "method-";
    static final String KEY_LOOK_UP_APPROACH = "key_look_up_approach";
    static final int APPROACH_METHOD = 1;
    static final int APPROACH_ROUTE = 2;
    static final String KEY_RESPONSE_CODE = "reponse_code";
    static final int RESPONSE_RESULE_NO_SUCH_METHOD = 1;

    static final String KEY_LENGTH = "key_length";


    private static final String TAG = PigeonEngine.class.getSimpleName();
    private Gson mGson = new Gson();
    private ConcurrentHashMap<String, MethodCaller> callers = new ConcurrentHashMap<>();

    private PigeonEngine() {
    }

    private static final PigeonEngine sInstance = new PigeonEngine();

    public static PigeonEngine getInstance() {
        return sInstance;
    }


    Bundle buildRequest(Method method, Object[] args) {
        Bundle bundle = new Bundle();
        Type[] types = method.getGenericParameterTypes();
        String key = "key_%s";
        for (int i = 0; i < types.length; i++) {
            Log.e(TAG, "type name:" + types[i] + " method:" + method.getName());
            if (int.class.isAssignableFrom((Class<?>) types[i])) {
                ParameterHandler.IntHandler handler = (ParameterHandler.IntHandler) map.get(int.class);
                assert handler != null;
                handler.apply((Integer) args[i], String.format(key, i + ""), bundle);
            }
        }
        bundle.putInt(KEY_LENGTH, types.length);
        bundle.putInt(KEY_LOOK_UP_APPROACH, APPROACH_METHOD);
        return bundle;
    }

    MethodCaller parseRequest(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) throws NoSuchMethodException, IllegalAccessException {
        Log.e(TAG, "call:" + method + " arg:" + mGson.toJson(arg) + " size:" + extras.size() + " ServiceContentProvider.serviceContext:" + ServiceContentProvider.serviceContext);
        MethodCaller methodCaller;
        int approach = extras.getInt(KEY_LOOK_UP_APPROACH);
        Object owner = null;
        if (approach == APPROACH_METHOD) {
            owner = ServiceContentProvider.serviceContext;
        }
        if (approach == APPROACH_METHOD && (methodCaller = lookupMethodByCache(method)) != null) {
            return methodCaller;
        }
        String key = "key_%s";
        int length = extras.getInt(KEY_LENGTH);
        Class<?>[] clazzs = new Class[length];
        Log.e(TAG, "length:" + length);
        for (int i = 0; i < length; i++) {
            Parcelable parcelable = extras.getParcelable(String.format(key, i + ""));
            if (parcelable == null) {
                break;
            }
            Log.e(TAG, "parcelable:" + mGson.toJson(parcelable) + " parcelable:" + parcelable);
            android.util.Pair<Class<?>, Object> data = parcelableToClazz(parcelable);
            clazzs[i] = data.first;
        }
        assert owner != null;
        Log.e(TAG, "method:" + method);
        Method target = owner.getClass().getDeclaredMethod(method, clazzs);
        target.setAccessible(true);
        methodCaller = new Caller(target, "", ServiceContentProvider.serviceContext);
        cacheMethodToMemory(methodCaller);
        return methodCaller;
    }


    Object[] parseData(@Nullable String arg, @Nullable Bundle extras) {
        String key = "key_%s";
        int length = extras.getInt(KEY_LENGTH);
        Object[] values = new Object[length];
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            Parcelable parcelable = extras.getParcelable(String.format(key, i + ""));
            if (parcelable == null) {
                break;
            }
            Log.e(TAG, "parcelable:" + mGson.toJson(parcelable) + " parcelable:" + parcelable);
            android.util.Pair<Class<?>, Object> data = parcelableToClazz(parcelable);
            clazzs[i] = data.first;
            values[i] = data.second;
        }
        return values;
    }

    private void cacheMethodToMemory(MethodCaller methodCaller) {
        callers.put(methodCaller.callerId(), methodCaller);
    }

    private MethodCaller lookupMethodByCache(String method) {
        return callers.get(PREXFIX_METHOD + method);
    }

    private static final ConcurrentHashMap<Class, ParameterHandler> map = new ConcurrentHashMap<Class, ParameterHandler>() {
        {
            put(int.class, new ParameterHandler.IntHandler());
            put(double.class, new ParameterHandler.DoubleHandler());
            put(long.class, new ParameterHandler.LongHandler());
            put(short.class, new ParameterHandler.ShortHandler());
            put(byte.class, new ParameterHandler.ByteHandler());
            put(float.class, new ParameterHandler.FloatHandler());
            put(boolean.class, new ParameterHandler.BooleanHandler());

        }
    };

    android.util.Pair<Class<?>, Object> parcelableToClazz(Parcelable parcelable) {
        if (parcelable instanceof com.flyingpigeon.library.Pair.PairInt) {
            return new android.util.Pair<Class<?>, Object>(int.class, ((com.flyingpigeon.library.Pair.PairInt) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairLong) {
            return new android.util.Pair<Class<?>, Object>(long.class, ((com.flyingpigeon.library.Pair.PairLong) parcelable).getValue());
        } else {
            return new android.util.Pair<Class<?>, Object>(int.class, ((com.flyingpigeon.library.Pair.PairBoolean) parcelable).isValue());
        }
    }

}
