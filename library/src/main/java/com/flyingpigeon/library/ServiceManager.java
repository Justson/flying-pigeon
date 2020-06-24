package com.flyingpigeon.library;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.flyingpigeon.library.anotation.RequestLarge;
import com.flyingpigeon.library.anotation.ResponseLarge;
import com.flyingpigeon.library.anotation.route;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public final class ServiceManager implements IServiceManager {

    static final String PREXFIX_ROUTE = "route-";
    static final String PREXFIX_METHOD = "method-";
    static final String KEY_LOOK_UP_APPROACH = "key_look_up_approach";
    static final String KEY_ROUTE = "key_path";
    static final int APPROACH_METHOD = 1;
    static final int APPROACH_ROUTE = 2;
    static final String KEY_RESPONSE_CODE = "reponse_code";
    static final int RESPONSE_RESULE_NO_SUCH_METHOD = 404;
    static final int RESPONSE_RESULE_LOST_CLASS = 405;

    static final int RESPONSE_RESULE_ILLEGALACCESS = 403;
    static final int RESPONSE_RESULE_SUCCESS = 200;

    static final String KEY_LENGTH = "key_length";
    static final String KEY_INDEX = "key_%s";
    static final String KEY_CLASS_INDEX = "key_class_%s";
    static final String KEY_CLASS = "key_class";
    static final String KEY_RESPONSE = "key_response";
    static final String KEY_FLAGS = "key_flags";

    private static final String TAG = PREFIX + ServiceManager.class.getSimpleName();
    private final Object lock = new Object();
    private ConcurrentHashMap<Class<?>, BuketMethod> sCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayDeque<MethodCaller>> routers = new ConcurrentHashMap<>();

    private ServiceManager() {
    }

    private static final ServiceManager sInstance = new ServiceManager();

    public static ServiceManager getInstance() {
        return sInstance;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Bundle buildRequest(Class<?> service, Object proxy, Method method, Object[] args) {
        Bundle bundle = new Bundle();
        Type[] types = method.getGenericParameterTypes();
        String key = KEY_INDEX;
        for (int i = 0; i < types.length; i++) {
            Log.e(TAG, "type name:" + types[i] + " method:" + method.getName() + " service:" + service);
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            if (int.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.IntHandler handler = (ParameterHandler.IntHandler) map.get(int.class);
                assert handler != null;
                handler.apply((Integer) args[i], String.format(key, i + ""), bundle);
            } else if (double.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.DoubleHandler handler = (ParameterHandler.DoubleHandler) map.get(double.class);
                assert handler != null;
                handler.apply((Double) args[i], String.format(key, i + ""), bundle);
            } else if (long.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.LongHandler handler = (ParameterHandler.LongHandler) map.get(long.class);
                assert handler != null;
                handler.apply((Long) args[i], String.format(key, i + ""), bundle);
            } else if (short.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ShortHandler handler = (ParameterHandler.ShortHandler) map.get(short.class);
                assert handler != null;
                handler.apply((Short) args[i], String.format(key, i + ""), bundle);
            } else if (float.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.FloatHandler handler = (ParameterHandler.FloatHandler) map.get(float.class);
                assert handler != null;
                handler.apply((Float) args[i], String.format(key, i + ""), bundle);
            } else if (byte.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ByteHandler handler = (ParameterHandler.ByteHandler) map.get(byte.class);
                assert handler != null;
                handler.apply((Byte) args[i], String.format(key, i + ""), bundle);
            } else if (boolean.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.BooleanHandler handler = (ParameterHandler.BooleanHandler) map.get(boolean.class);
                assert handler != null;
                handler.apply((Boolean) args[i], String.format(key, i + ""), bundle);
            } else if (String.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.StringHandler handler = (ParameterHandler.StringHandler) map.get(String.class);
                assert handler != null;
                handler.apply((String) args[i], String.format(key, i + ""), bundle);
            } else if (Parcelable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                assert handler != null;
                handler.apply((Parcelable) args[i], String.format(key, i + ""), bundle);
                Parcelable parcelable = bundle.getParcelable(String.format(key, i + ""));
            } else if (Serializable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.SerializableHandler handler = (ParameterHandler.SerializableHandler) map.get(Serializable.class);
                assert handler != null;
                handler.apply((Serializable) args[i], String.format(key, i + ""), bundle);
                Parcelable parcelable = bundle.getParcelable(String.format(key, i + ""));
            }
        }
        bundle.putInt(KEY_LENGTH, types.length);
        bundle.putInt(KEY_LOOK_UP_APPROACH, APPROACH_METHOD);
        bundle.putString(KEY_CLASS, service.getName());

        // build response type;
        String responseKey = KEY_RESPONSE;
        Type returnType = method.getGenericReturnType();
        if (int.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.IntHandler handler = (ParameterHandler.IntHandler) map.get(int.class);
            assert handler != null;
            handler.apply(0, responseKey, bundle);
        } else if (double.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.DoubleHandler handler = (ParameterHandler.DoubleHandler) map.get(double.class);
            assert handler != null;
            handler.apply(0D, responseKey, bundle);
        } else if (long.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.LongHandler handler = (ParameterHandler.LongHandler) map.get(long.class);
            assert handler != null;
            handler.apply(0L, responseKey, bundle);
        } else if (short.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.ShortHandler handler = (ParameterHandler.ShortHandler) map.get(short.class);
            assert handler != null;
            handler.apply((short) 0, responseKey, bundle);
        } else if (float.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.FloatHandler handler = (ParameterHandler.FloatHandler) map.get(float.class);
            assert handler != null;
            handler.apply(0F, responseKey, bundle);
        } else if (byte.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.ByteHandler handler = (ParameterHandler.ByteHandler) map.get(byte.class);
            assert handler != null;
            handler.apply((byte) 0, responseKey, bundle);
        } else if (boolean.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.BooleanHandler handler = (ParameterHandler.BooleanHandler) map.get(boolean.class);
            assert handler != null;
            handler.apply(false, responseKey, bundle);
        } else if (String.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.StringHandler handler = (ParameterHandler.StringHandler) map.get(String.class);
            assert handler != null;
            handler.apply("", responseKey, bundle);
        } else if (Parcelable.class.isAssignableFrom(((Class<?>) returnType))) {
            ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
            assert handler != null;
            handler.apply(new Empty(), responseKey, bundle);
        } else if (Serializable.class.isAssignableFrom(((Class<?>) returnType))) {
            ParameterHandler.SerializableHandler handler = (ParameterHandler.SerializableHandler) map.get(Serializable.class);
            assert handler != null;
            handler.apply(new Empty(), responseKey, bundle);
        } else if (Void.class.isAssignableFrom(((Class<?>) returnType))) {
        }

        return bundle;
    }


    void approachByRoute(String method, Bundle in, Bundle out) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String route = in.getString(KEY_ROUTE);
        if (TextUtils.isEmpty(route)) {
            throw new NoSuchMethodException();
        }
        ArrayDeque<MethodCaller> callers = routers.get(route);
        if (callers == null || callers.isEmpty()) {
            throw new NoSuchMethodException(route);
        }
        Iterator<MethodCaller> iterators = callers.iterator();
        while (iterators.hasNext()) {
            MethodCaller methodCaller = iterators.next();
            methodCaller.call(in, out);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    ContentValues buildRequestInsert(Class<?> service, Object proxy, Method method, Object[] args) {
        ContentValues contentValues = new ContentValues();
        Type[] types = method.getGenericParameterTypes();
        contentValues.put(KEY_LENGTH, types.length);
        contentValues.put(KEY_LOOK_UP_APPROACH, APPROACH_METHOD);
        contentValues.put(KEY_CLASS, service.getName());
        settingValues(args, contentValues, types);
        return contentValues;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void settingValues(Object[] args, ContentValues contentValues, Type[] types) {
        String key = KEY_INDEX;
        String keyClass = KEY_CLASS_INDEX;
        for (int i = 0; i < args.length; i++) {
            String index = String.format(key, i + "");
            String indexClass = String.format(keyClass, i + "");
            if (types[i] == null) {
                contentValues.put(index, "");
                contentValues.put(indexClass, "null");
                continue;
            }
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            Log.e(TAG, "typeClazz:" + typeClazz.getName() + " index:" + index + " indexClass:" + indexClass);
            if (int.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (int) args[i]);
                contentValues.put(indexClass, int.class.getName());
            } else if (double.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (double) args[i]);
                contentValues.put(indexClass, double.class.getName());
            } else if (long.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (long) args[i]);
                contentValues.put(indexClass, long.class.getName());
            } else if (short.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (short) args[i]);
                contentValues.put(indexClass, short.class.getName());
            } else if (float.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (float) args[i]);
                contentValues.put(indexClass, float.class.getName());
            } else if (byte.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (byte) args[i]);
                contentValues.put(indexClass, byte.class.getName());
            } else if (boolean.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (boolean) args[i]);
                contentValues.put(indexClass, boolean.class.getName());
            } else if (String.class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (String) args[i]);
                contentValues.put(indexClass, String.class.getName());
            } else if (byte[].class.isAssignableFrom(typeClazz)) {
                contentValues.put(index, (byte[]) args[i]);
                contentValues.put(indexClass, byte[].class.getName());
            } else if (Integer.class.isAssignableFrom(typeClazz)) {
                Integer v = (Integer) args[i];
                contentValues.put(index, v.intValue());
                contentValues.put(indexClass, int.class.getName());
            } else if (Double.class.isAssignableFrom(typeClazz)) {
                Double v = (Double) args[i];
                contentValues.put(index, v.doubleValue());
                contentValues.put(indexClass, double.class.getName());
            } else if (Long.class.isAssignableFrom(typeClazz)) {
                Long v = (Long) args[i];
                contentValues.put(index, v.longValue());
                contentValues.put(indexClass, long.class.getName());
            } else if (Short.class.isAssignableFrom(typeClazz)) {
                Short v = (Short) args[i];
                contentValues.put(index, v.shortValue());
                contentValues.put(indexClass, short.class.getName());
            } else if (Float.class.isAssignableFrom(typeClazz)) {
                Float v = (Float) args[i];
                contentValues.put(index, v.floatValue());
                contentValues.put(indexClass, float.class.getName());
            } else if (Byte.class.isAssignableFrom(typeClazz)) {
                Byte v = (Byte) args[i];
                contentValues.put(index, v.byteValue());
                contentValues.put(indexClass, byte.class.getName());
            } else if (Byte[].class.isAssignableFrom(typeClazz)) {
                Byte[] v = (Byte[]) args[i];
                contentValues.put(index, Utils.toPrimitives(v));
                contentValues.put(indexClass, byte[].class.getName());
            }
        }
    }


    MethodCaller approachByMethod(@NonNull String method, @NonNull Bundle extras) throws ClassNotFoundException, NoSuchMethodException {
        MethodCaller methodCaller;
        String key = KEY_INDEX;
        int length = extras.getInt(KEY_LENGTH);
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            Parcelable parcelable = extras.getParcelable(String.format(key, i + ""));
            if (parcelable == null) {
                break;
            }
            android.util.Pair<Class<?>, Object> data = parcelableToClazz(parcelable);
            clazzs[i] = data.first;
        }
        for (int i = 0; i < length; i++) {
            if (clazzs[i] == null) {
                throw new IllegalArgumentException("arg error");
            }
        }

        String clazz = extras.getString(KEY_CLASS);
        Log.e(TAG, "clazz:" + clazz);
        BuketMethod buket = getMethods(Class.forName(clazz));
        if (buket == null) {
            throw new ClassNotFoundException();
        }
        methodCaller = buket.match(method, clazzs);
        return methodCaller;
    }


    MethodCaller approachByMethodInsert(Uri uri, ContentValues values, String method) throws ClassNotFoundException, NoSuchMethodException {
        MethodCaller methodCaller;
        String key = KEY_INDEX;
        String keyClass = KEY_CLASS_INDEX;
        int length = values.getAsInteger(KEY_LENGTH);
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            String clazz = values.getAsString(String.format(keyClass, i + ""));
            Log.e(TAG, "approachByMethodInsert:" + clazz + "  index:" + String.format(keyClass, i + ""));
            clazzs[i] = Class.forName(clazz);
        }
        for (int i = 0; i < length; i++) {
            if (clazzs[i] == null) {
                throw new IllegalArgumentException("arg error");
            }
        }
        String clazz = values.getAsString(KEY_CLASS);
        Log.e(TAG, "clazz:" + clazz);
        BuketMethod buket = getMethods(Class.forName(clazz));
        if (buket == null) {
            throw new ClassNotFoundException();
        }
        methodCaller = buket.match(method, clazzs);
        return methodCaller;
    }

    public MethodCaller approachByRouteInsert(Uri uri, ContentValues values, String route) throws ClassNotFoundException, NoSuchMethodException {
        String key = KEY_INDEX;
        String keyClass = KEY_CLASS_INDEX;
        int length = values.getAsInteger(KEY_LENGTH);
        for (int i = 0; i < length; i++) {
            String clazz = values.getAsString(String.format(keyClass, i + ""));
            Log.e(TAG, "approachByRouteInsert:" + clazz + "  index:" + String.format(keyClass, i + ""));
        }
        ArrayDeque<MethodCaller> callers = routers.get(route);
        if (callers == null || callers.isEmpty()) {
            throw new NoSuchMethodException(route);
        }
        return callers.getFirst();
    }

    public Object[] parseDataInsert(Uri uri, ContentValues contentValues) {
        int length = contentValues.getAsInteger(KEY_LENGTH);
        Object[] values = new Object[length];
        Class<?>[] clazzs = new Class[length];
        String key = KEY_INDEX;
        String keyClass = KEY_CLASS_INDEX;
        for (int i = 0; i < length; i++) {
            String clazz = contentValues.getAsString(String.format(keyClass, i + ""));
            Log.e(TAG, "parseDataInsert clazz:" + clazz);
            if (clazz.startsWith("int")) {
                values[i] = contentValues.getAsInteger(String.format(key, i + ""));
            } else if (clazz.startsWith("double")) {
                values[i] = contentValues.getAsDouble(String.format(key, i + ""));
            } else if (clazz.startsWith("long")) {
                values[i] = contentValues.getAsLong(String.format(key, i + ""));
            } else if (clazz.startsWith("short")) {
                values[i] = contentValues.getAsShort(String.format(key, i + ""));
            } else if (clazz.startsWith("float")) {
                values[i] = contentValues.getAsFloat(String.format(key, i + ""));
            } else if (clazz.startsWith("byte")) {
                values[i] = contentValues.getAsByte(String.format(key, i + ""));
            } else if (clazz.startsWith("boolean")) {
                values[i] = contentValues.getAsBoolean(String.format(key, i + ""));
            } else if ("java.lang.String".equals(clazz)) {
                values[i] = contentValues.getAsString(String.format(key, i + ""));
            } else if ("[B".equals(clazz)) {
                values[i] = contentValues.getAsByteArray(String.format(key, i + ""));
            }
            Log.e(TAG, "values[i] :" + values[i]);
        }
        return values;
    }


    Object[] parseData(@Nullable String arg, @Nullable Bundle extras) {
        String key = KEY_INDEX;
        int length = extras.getInt(KEY_LENGTH);
        Object[] values = new Object[length];
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            Parcelable parcelable = extras.getParcelable(String.format(key, i + ""));
            if (parcelable == null) {
                break;
            }
            android.util.Pair<Class<?>, Object> data = parcelableToClazz(parcelable);
            clazzs[i] = data.first;
            values[i] = data.second;
        }
        return values;
    }


    private static final ConcurrentHashMap<Class, ParameterHandler> map = new ConcurrentHashMap<Class, ParameterHandler>() {
        {
            put(int.class, new ParameterHandler.IntHandler());
            put(double.class, new ParameterHandler.DoubleHandler());
            put(long.class, new ParameterHandler.LongHandler());
            put(short.class, new ParameterHandler.ShortHandler());
            put(float.class, new ParameterHandler.FloatHandler());
            put(byte.class, new ParameterHandler.ByteHandler());
            put(boolean.class, new ParameterHandler.BooleanHandler());
            put(Parcelable.class, new ParameterHandler.ParcelableHandler());
            put(Serializable.class, new ParameterHandler.SerializableHandler());
            put(String.class, new ParameterHandler.StringHandler());
        }
    };

    private android.util.Pair<Class<?>, Object> parcelableToClazz(Parcelable parcelable) {
        if (parcelable instanceof com.flyingpigeon.library.Pair.PairInt) {
            return new android.util.Pair<Class<?>, Object>(int.class, ((com.flyingpigeon.library.Pair.PairInt) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairDouble) {
            return new android.util.Pair<Class<?>, Object>(double.class, ((com.flyingpigeon.library.Pair.PairDouble) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairLong) {
            return new android.util.Pair<Class<?>, Object>(long.class, ((com.flyingpigeon.library.Pair.PairLong) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairShort) {
            return new android.util.Pair<Class<?>, Object>(short.class, ((com.flyingpigeon.library.Pair.PairShort) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairFloat) {
            return new android.util.Pair<Class<?>, Object>(float.class, ((com.flyingpigeon.library.Pair.PairFloat) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByte) {
            return new android.util.Pair<Class<?>, Object>(byte.class, ((com.flyingpigeon.library.Pair.PairByte) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairBoolean) {
            return new android.util.Pair<Class<?>, Object>(boolean.class, ((com.flyingpigeon.library.Pair.PairBoolean) parcelable).isValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairString) {
            try {
                return new android.util.Pair<Class<?>, Object>(Class.forName(((Pair.PairString) parcelable).getKey()), ((Pair.PairString) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            try {
                return new android.util.Pair<Class<?>, Object>(Class.forName(((Pair.PairSerializable) parcelable).getKey()), ((Pair.PairSerializable) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                return new android.util.Pair<Class<?>, Object>(Class.forName(((Pair.PairParcelable) parcelable).getKey()), ((Pair.PairParcelable) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    Object parseReponse(Bundle response, Method method) throws CallRemoteException {
        response.setClassLoader(Pair.class.getClassLoader());
        int responseCode = response.getInt(KEY_RESPONSE_CODE);
        if (responseCode == RESPONSE_RESULE_NO_SUCH_METHOD) {
            throw new CallRemoteException("404 , method not found ");
        }
        if (responseCode == RESPONSE_RESULE_LOST_CLASS) {
            throw new CallRemoteException("404 , class not found ");
        }

        if (responseCode == RESPONSE_RESULE_ILLEGALACCESS) {
            throw new CallRemoteException("404 , illegal access ");
        }

        Parcelable parcelable;
        if (responseCode == RESPONSE_RESULE_SUCCESS && (parcelable = response.getParcelable(KEY_RESPONSE)) != null) {
            return parcelableValueOut(parcelable);
        }
        return null;
    }

    void parseReponse(Bundle response) throws CallRemoteException {
        response.setClassLoader(Pair.class.getClassLoader());
        int responseCode = response.getInt(KEY_RESPONSE_CODE);
        if (responseCode == RESPONSE_RESULE_NO_SUCH_METHOD) {
            throw new CallRemoteException("404 , method not found ");
        }
        if (responseCode == RESPONSE_RESULE_LOST_CLASS) {
            throw new CallRemoteException("404 , class not found ");
        }

        if (responseCode == RESPONSE_RESULE_ILLEGALACCESS) {
            throw new CallRemoteException("404 , illegal access ");
        }
        response.remove(KEY_RESPONSE_CODE);
    }

    void buildResponse(Bundle request, Bundle response, Object result) {
        Parcelable parcelable = request.getParcelable(KEY_RESPONSE);
        if (parcelable != null) {
            parcelableValueIn(parcelable, result);
            response.putParcelable(KEY_RESPONSE, parcelable);
        }
    }


    private void parcelableValueIn(Parcelable parcelable, Object value) {
        if (parcelable instanceof com.flyingpigeon.library.Pair.PairInt) {
            ((com.flyingpigeon.library.Pair.PairInt) parcelable).setValue((Integer) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairDouble) {
            ((com.flyingpigeon.library.Pair.PairDouble) parcelable).setValue((Double) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairLong) {
            ((com.flyingpigeon.library.Pair.PairLong) parcelable).setValue((Long) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairShort) {
            ((com.flyingpigeon.library.Pair.PairShort) parcelable).setValue((Short) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairFloat) {
            ((com.flyingpigeon.library.Pair.PairFloat) parcelable).setValue((Float) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByte) {
            ((com.flyingpigeon.library.Pair.PairByte) parcelable).setValue((Byte) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairBoolean) {
            ((com.flyingpigeon.library.Pair.PairBoolean) parcelable).setValue((Boolean) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairString) {
            ((Pair.PairString) parcelable).setValue((String) value);
            ((Pair.PairString) parcelable).setKey(value.getClass().getName());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            ((Pair.PairSerializable) parcelable).setValue((Serializable) value);
            ((Pair.PairSerializable) parcelable).setKey(value.getClass().getName());
        } else {
            ((Pair.PairParcelable) parcelable).setValue((Parcelable) value);
            ((Pair.PairParcelable) parcelable).setKey(value.getClass().getName());
        }
    }

    private Object parcelableValueOut(Parcelable parcelable) {
        if (parcelable instanceof com.flyingpigeon.library.Pair.PairInt) {
            return ((com.flyingpigeon.library.Pair.PairInt) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairDouble) {
            return ((com.flyingpigeon.library.Pair.PairDouble) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairLong) {
            return ((com.flyingpigeon.library.Pair.PairLong) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairShort) {
            return ((com.flyingpigeon.library.Pair.PairShort) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairFloat) {
            return ((com.flyingpigeon.library.Pair.PairFloat) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByte) {
            return ((com.flyingpigeon.library.Pair.PairByte) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairBoolean) {
            return ((com.flyingpigeon.library.Pair.PairBoolean) parcelable).isValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairString) {
            return ((Pair.PairString) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            return ((Pair.PairSerializable) parcelable).getValue();
        } else {
            return ((Pair.PairParcelable) parcelable).getValue();
        }
    }

    @Override
    public void publish(Object service, Class<?>... interfaces) {
        if (interfaces.length == 0) {
            Log.e(TAG, "without interfaces ");
        }
        synchronized (lock) {
            for (Class<?> aInterface : interfaces) {
                BuketMethod buket = sCache.get(aInterface);
                if (buket != null) {
                    Log.e(TAG, " publish failure, " + "please don't repeat publish same api:" + aInterface.getName());
                    continue;
                }
                BuketMethod buketMethod = new BuketMethod();
                buketMethod.setOwner(service);
                buketMethod.setInterfaceClass(aInterface);
                Log.e(TAG, "publish:" + aInterface.getName());
                sCache.put(aInterface, buketMethod);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void publish(Object service) {
        synchronized (lock) {
            Class<?> clazz = service.getClass();
            Class<?>[] interfaces = ClassUtil.getValidInterface(clazz);
            publish(service, interfaces);
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                route route = method.getAnnotation(route.class);
                if (route != null) {
                    String path = route.value();
                    boolean encode = route.encoded();
                    if (TextUtils.isEmpty(path)) {
                        Log.e(TAG, " the path enable to empty .");
                        continue;
                    }
                    if (encode) {
                        try {
                            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    RequestLarge requestLarge = method.getAnnotation(RequestLarge.class);
                    ResponseLarge responseLarge = method.getAnnotation(ResponseLarge.class);
                    Log.e(TAG, " publish path:" + path);
                    method.setAccessible(true);
                    if (requestLarge == null && responseLarge == null) {
                        MethodCaller methodCaller = new RouteCaller(method, path, service);
                        cacheMethodToRoute(path, methodCaller);
                    } else if (requestLarge != null) {
                        MethodCaller methodCaller = new RouteRequestLargeCaller(method, path, service);
                        cacheMethodToRoute(path, methodCaller);
                    } else {
                        MethodCaller methodCaller = new RouteResponseLargeCaller(method, path, service);
                        cacheMethodToRoute(path, methodCaller);
                    }
                }
            }
        }
    }

    BuketMethod getMethods(Class<?> clazz) {
        return this.sCache.get(clazz);
    }

    private void cacheMethodToRoute(String key, MethodCaller caller) {
        synchronized (routers) {
            ArrayDeque<MethodCaller> methodCallers = routers.get(key);
            if (methodCallers == null) {
                methodCallers = new ArrayDeque<>();
                methodCallers.addFirst(caller);
                routers.put(key, methodCallers);
            } else {
                methodCallers.addLast(caller);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void abolition(Object service) {
        synchronized (lock) {
            Class<?>[] interfaces = ClassUtil.getValidInterface(service.getClass());
            if (interfaces.length == 0) {
                Log.e(TAG, "abolition interface is not exist");
            }
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> aInterface = interfaces[i];
                sCache.remove(aInterface);
            }

            Method[] methods = service.getClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                route route = method.getAnnotation(route.class);
                if (route != null) {
                    String path = route.value();
                    boolean encode = route.encoded();
                    if (TextUtils.isEmpty(path)) {
                        continue;
                    }
                    if (encode) {
                        try {
                            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    routers.remove(path);
                    break;
                }
            }
        }
    }

    @Override
    public void abolition(Object service, Class<?>... interfaces) {
        synchronized (lock) {
            if (interfaces.length == 0) {
                Log.e(TAG, "abolition interface is not exist");
            }
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> aInterface = interfaces[i];
                sCache.remove(aInterface);
            }
        }
    }

    void buildRequestRoute(Bundle in) {
        in.putInt(KEY_LOOK_UP_APPROACH, APPROACH_ROUTE);
    }


    String[] buildRequestQuery(String route, Object[] params) {
        int length = params.length;
        String[] data = new String[length * 2];
        for (int i = 0; i < length; i++) {
            data[i] = params[i].toString();
            data[i + length] = params[i].getClass().getName();
        }
        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ContentValues buildRouteRequestInsert(String route, Object[] params) {
        Type[] types = new Type[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                continue;
            }
            types[i] = params[i].getClass();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LENGTH, params.length);
        contentValues.put(KEY_LOOK_UP_APPROACH, APPROACH_ROUTE);
        settingValues(params, contentValues, types);
        return contentValues;
    }


}
