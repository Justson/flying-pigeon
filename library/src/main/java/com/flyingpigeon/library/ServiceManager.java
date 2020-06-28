package com.flyingpigeon.library;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.flyingpigeon.library.annotations.RequestLarge;
import com.flyingpigeon.library.annotations.ResponseLarge;
import com.flyingpigeon.library.annotations.route;
import com.flyingpigeon.library.ashmem.Ashmem;
import com.flyingpigeon.library.serialization.ParcelableUtils;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.flyingpigeon.library.Config.PREFIX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ARRAY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LOOK_UP_APPROACH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE_CODE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_TYPE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_ILLEGALACCESS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_LOST_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_SUCCESS;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public final class ServiceManager implements IServiceManager {

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

    Bundle buildRequest(Class<?> service, Object proxy, Method method, Object[] args) {
        Bundle bundle = new Bundle();
        Type[] types = method.getGenericParameterTypes();
        for (int i = 0; i < types.length; i++) {
            String key = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
            Log.e(TAG, "type name:" + types[i] + " method:" + method.getName() + " service:" + service);
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            if (int.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.IntHandler handler = (ParameterHandler.IntHandler) map.get(int.class);
                assert handler != null;
                handler.apply((Integer) args[i], key, bundle);
            } else if (double.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.DoubleHandler handler = (ParameterHandler.DoubleHandler) map.get(double.class);
                assert handler != null;
                handler.apply((Double) args[i], key, bundle);
            } else if (long.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.LongHandler handler = (ParameterHandler.LongHandler) map.get(long.class);
                assert handler != null;
                handler.apply((Long) args[i], key, bundle);
            } else if (short.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ShortHandler handler = (ParameterHandler.ShortHandler) map.get(short.class);
                assert handler != null;
                handler.apply((Short) args[i], key, bundle);
            } else if (float.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.FloatHandler handler = (ParameterHandler.FloatHandler) map.get(float.class);
                assert handler != null;
                handler.apply((Float) args[i], key, bundle);
            } else if (byte.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ByteHandler handler = (ParameterHandler.ByteHandler) map.get(byte.class);
                assert handler != null;
                handler.apply((Byte) args[i], key, bundle);
            } else if (byte[].class.isAssignableFrom(typeClazz)) {
                byte[] array = (byte[]) args[i];
                if (array.length > 8 * 1024) {
                    String keyLength = key + PIGEON_KEY_ARRAY_LENGTH;
                    Log.e(TAG, "keyLength:" + keyLength + " length:" + array.length);
                    ParcelFileDescriptor parcelFileDescriptor = Ashmem.byteArrayToFileDescriptor(array);
                    bundle.putInt(keyLength, array.length);
                    ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                    assert handler != null;
                    handler.apply(parcelFileDescriptor, key, bundle);
                    Parcelable parcelable = bundle.getParcelable(key);
                } else {
                    ParameterHandler.ByteArrayHandler byteArrayHandler = (ParameterHandler.ByteArrayHandler) map.get(byte[].class);
                    byteArrayHandler.apply(array, key, bundle);
                }

            } else if (Byte[].class.isAssignableFrom(typeClazz)) {
                byte[] array = Utils.toPrimitives((Byte[]) args[i]);
                if (array.length > 8 * 1024) {
                    ParcelFileDescriptor parcelFileDescriptor = Ashmem.byteArrayToFileDescriptor(array);
                    String keyLength = key + PIGEON_KEY_ARRAY_LENGTH;
                    Log.e(TAG, "keyLength:" + keyLength + " length:" + array.length);
                    bundle.putInt(keyLength, array.length);
                    ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                    assert handler != null;
                    handler.apply(parcelFileDescriptor, key, bundle);
                    Parcelable parcelable = bundle.getParcelable(key);
                } else {
                    ParameterHandler.ByteArrayHandler byteArrayHandler = (ParameterHandler.ByteArrayHandler) map.get(byte[].class);
                    byteArrayHandler.apply(array, key, bundle);
                }

            } else if (boolean.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.BooleanHandler handler = (ParameterHandler.BooleanHandler) map.get(boolean.class);
                assert handler != null;
                handler.apply((Boolean) args[i], key, bundle);
            } else if (String.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.StringHandler handler = (ParameterHandler.StringHandler) map.get(String.class);
                assert handler != null;
                handler.apply((String) args[i], key, bundle);
            } else if (Parcelable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                assert handler != null;
                handler.apply((Parcelable) args[i], key, bundle);
                Parcelable parcelable = bundle.getParcelable(key);
            } else if (Serializable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.SerializableHandler handler = (ParameterHandler.SerializableHandler) map.get(Serializable.class);
                assert handler != null;
                handler.apply((Serializable) args[i], key, bundle);
                Parcelable parcelable = bundle.getParcelable(key);
            }
        }

        bundle.putInt(PIGEON_KEY_LENGTH, types.length);
        bundle.putInt(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_METHOD);
        bundle.putString(PIGEON_KEY_CLASS, service.getName());

        // build response type;
        String responseKey = PIGEON_KEY_RESPONSE;
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
        String route = in.getString(PIGEON_KEY_ROUTE);
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


    ContentValues buildRequestInsert(Class<?> service, Object proxy, Method method, Object[] args) {
        ContentValues contentValues = new ContentValues();
        Type[] types = method.getGenericParameterTypes();
        contentValues.put(PIGEON_KEY_LENGTH, types.length);
        contentValues.put(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_METHOD);
        contentValues.put(PIGEON_KEY_CLASS, service.getName());
        settingValues(args, contentValues, types);
        return contentValues;
    }

    String[] buildRequestQuery(Class<?> service, Object proxy, Method method, Object[] args) {
        Type[] types = method.getGenericParameterTypes();
        String[] values = settingValues(args, types);
        return values;
    }


    private String[] settingValues(Object[] args, Type[] types) {
        int pLength = types.length;
        String[] params = new String[pLength * 2 + 2];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                params[i] = "";
                params[i + pLength + 2] = ClassUtil.getRawType(types[i]).getName();
                continue;
            }
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            if (int.class.isAssignableFrom(typeClazz)) {
                params[i] = args[i] + "";
                params[i + pLength + 2] = int.class.getName();
            } else if (double.class.isAssignableFrom(typeClazz)) {
                params[i] = args[i] + "";
                params[i + pLength + 2] = double.class.getName();
            } else if (long.class.isAssignableFrom(typeClazz)) {
                params[i] = args[i] + "";
                params[i + pLength + 2] = long.class.getName();
            } else if (short.class.isAssignableFrom(typeClazz)) {

                params[i] = args[i] + "";
                params[i + pLength + 2] = short.class.getName();
            } else if (float.class.isAssignableFrom(typeClazz)) {
                params[i] = args[i] + "";
                params[i + pLength + 2] = float.class.getName();
            } else if (byte.class.isAssignableFrom(typeClazz)) {
                params[i] = args[i] + "";
                params[i + pLength + 2] = byte.class.getName();
            } else if (boolean.class.isAssignableFrom(typeClazz)) {

                params[i] = args[i] + "";
                params[i + pLength + 2] = boolean.class.getName();
            } else if (String.class.isAssignableFrom(typeClazz)) {

                params[i] = args[i] + "";
                params[i + pLength + 2] = String.class.getName();
            } else if (Integer.class.isAssignableFrom(typeClazz)) {
                Integer v = (Integer) args[i];
                params[i] = v.intValue() + "";
                params[i + pLength + 2] = int.class.getName();
            } else if (Double.class.isAssignableFrom(typeClazz)) {
                Double v = (Double) args[i];
                params[i] = v.doubleValue() + "";
                params[i + pLength + 2] = double.class.getName();
            } else if (Long.class.isAssignableFrom(typeClazz)) {
                Long v = (Long) args[i];
                params[i] = v.longValue() + "";
                params[i + pLength + 2] = long.class.getName();
            } else if (Short.class.isAssignableFrom(typeClazz)) {
                Short v = (Short) args[i];
                params[i] = v.shortValue() + "";
                params[i + pLength + 2] = short.class.getName();
            } else if (Float.class.isAssignableFrom(typeClazz)) {
                Float v = (Float) args[i];
                params[i] = v.floatValue() + "";
                params[i + pLength + 2] = float.class.getName();
            } else if (Byte.class.isAssignableFrom(typeClazz)) {
                Byte v = (Byte) args[i];
                params[i] = v.byteValue() + "";
                params[i + pLength + 2] = byte.class.getName();
            }
        }
        return params;
    }


    private void settingValues0(Object[] args, Bundle bundle, Type[] types) {
        for (int i = 0; i < args.length; i++) {
            String index = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
            String indexClass = String.format(Locale.ENGLISH, PIGEON_KEY_CLASS_INDEX, i);
            if (types[i] == null) {
                bundle.putString(index, "");
                bundle.putString(indexClass, "null");
                continue;
            }
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            Utils.convert(index, bundle, typeClazz, args[i]);
        }
    }


    private void settingValues(Object[] args, ContentValues contentValues, Type[] types) {
        for (int i = 0; i < args.length; i++) {
            String index = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
            String indexClass = String.format(Locale.ENGLISH, PIGEON_KEY_CLASS_INDEX, i);
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
                byte[] array = (byte[]) args[i];
                contentValues.put(index, array);
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
                byte[] array = Utils.toPrimitives(v);
                contentValues.put(index, array);
                contentValues.put(indexClass, byte[].class.getName());
            }
        }
    }


    MethodCaller approachByMethod(@NonNull String method, @NonNull Bundle extras) throws ClassNotFoundException, NoSuchMethodException {
        MethodCaller methodCaller;
        int length = extras.getInt(PIGEON_KEY_LENGTH);
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            String index = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
            Parcelable parcelable = extras.getParcelable(index);
            if (parcelable == null) {
                break;
            }
            android.util.Pair<Class<?>, Object> data = parcelableToClazz(parcelable, index, extras);
            assert data != null;
            clazzs[i] = data.first;
            Log.e(TAG, "clazz:" + clazzs[i]);
        }
        for (int i = 0; i < length; i++) {
            if (clazzs[i] == null) {
                throw new IllegalArgumentException("arg error");
            }
        }

        String clazz = extras.getString(PIGEON_KEY_CLASS);
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
        String key = PIGEON_KEY_INDEX;
        String keyClass = PIGEON_KEY_CLASS_INDEX;
        int length = values.getAsInteger(PIGEON_KEY_LENGTH);
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            String clazz = values.getAsString(String.format(Locale.ENGLISH, keyClass, i));
            Log.e(TAG, "approachByMethodInsert:" + clazz + "  index:" + String.format(Locale.ENGLISH, keyClass, i + ""));
            clazzs[i] = Class.forName(clazz);
        }
        for (int i = 0; i < length; i++) {
            if (clazzs[i] == null) {
                throw new IllegalArgumentException("arg error");
            }
        }
        String clazz = values.getAsString(PIGEON_KEY_CLASS);
        Log.e(TAG, "clazz:" + clazz);
        BuketMethod buket = getMethods(Class.forName(clazz));
        if (buket == null) {
            throw new ClassNotFoundException();
        }
        methodCaller = buket.match(method, clazzs);
        return methodCaller;
    }

    MethodCaller approachByRouteInsert(Uri uri, ContentValues values, String route) throws ClassNotFoundException, NoSuchMethodException {
        String key = PIGEON_KEY_INDEX;
        String keyClass = PIGEON_KEY_CLASS_INDEX;
        int length = values.getAsInteger(PIGEON_KEY_LENGTH);
        for (int i = 0; i < length; i++) {
            String clazz = values.getAsString(String.format(Locale.ENGLISH, keyClass, i));
            Log.e(TAG, "approachByRouteInsert:" + clazz + "  index:" + String.format(Locale.ENGLISH, keyClass, i + ""));
        }
        ArrayDeque<MethodCaller> callers = routers.get(route);
        if (callers == null || callers.isEmpty()) {
            throw new NoSuchMethodException(route);
        }
        return callers.getFirst();
    }

    Object[] parseDataInsert(Uri uri, ContentValues contentValues) {
        int length = contentValues.getAsInteger(PIGEON_KEY_LENGTH);
        Object[] values = new Object[length];
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            String keyClass = String.format(Locale.ENGLISH, PIGEON_KEY_CLASS_INDEX, i);
            String clazz = contentValues.getAsString(keyClass);
            Log.e(TAG, "parseDataInsert clazz:" + clazz);
            if (clazz.startsWith("int")) {
                values[i] = contentValues.getAsInteger(keyClass);
            } else if (clazz.startsWith("double")) {
                values[i] = contentValues.getAsDouble(keyClass);
            } else if (clazz.startsWith("long")) {
                values[i] = contentValues.getAsLong(keyClass);
            } else if (clazz.startsWith("short")) {
                values[i] = contentValues.getAsShort(keyClass);
            } else if (clazz.startsWith("float")) {
                values[i] = contentValues.getAsFloat(keyClass);
            } else if (clazz.startsWith("byte")) {
                values[i] = contentValues.getAsByte(keyClass);
            } else if (clazz.startsWith("boolean")) {
                values[i] = contentValues.getAsBoolean(keyClass);
            } else if ("java.lang.String".equals(clazz)) {
                values[i] = contentValues.getAsString(keyClass);
            } else if ("[B".equals(clazz)) {
                values[i] = contentValues.getAsByteArray(keyClass);
            } else if (clazz.equals(ParcelFileDescriptor.class.getName())) {
                String parcelFile = contentValues.getAsString(keyClass);
                int arrayLength = contentValues.getAsInteger(keyClass + PIGEON_KEY_ARRAY_LENGTH);
                ParcelFileDescriptor parcelFileDescriptor = ParcelableUtils.string2Parcelable(parcelFile, ParcelFileDescriptor.CREATOR);
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
                byte[] bytes = new byte[arrayLength];
                try {
                    fileInputStream.read(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                values[i] = bytes;
            }
            Log.e(TAG, "values[i] :" + values[i]);
        }
        return values;
    }


    Object[] parseData(@Nullable String arg, @Nullable Bundle extras) {
        int length = extras.getInt(PIGEON_KEY_LENGTH);
        Object[] values = new Object[length];
        Class<?>[] clazzs = new Class[length];
        for (int i = 0; i < length; i++) {
            String index = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
            Parcelable parcelable = extras.getParcelable(index);
            if (parcelable == null) {
                break;
            }
            android.util.Pair<Class<?>, Object> data = parcelableToClazz(parcelable, index, extras);
            clazzs[i] = data.first;
            values[i] = data.second;
        }
        return values;
    }


    static final ConcurrentHashMap<Class, ParameterHandler> map = new ConcurrentHashMap<Class, ParameterHandler>() {
        {
            put(int.class, new ParameterHandler.IntHandler());
            put(double.class, new ParameterHandler.DoubleHandler());
            put(long.class, new ParameterHandler.LongHandler());
            put(short.class, new ParameterHandler.ShortHandler());
            put(float.class, new ParameterHandler.FloatHandler());
            put(byte.class, new ParameterHandler.ByteHandler());
            put(byte[].class, new ParameterHandler.ByteArrayHandler());
            put(boolean.class, new ParameterHandler.BooleanHandler());
            put(Parcelable.class, new ParameterHandler.ParcelableHandler());
            put(Serializable.class, new ParameterHandler.SerializableHandler());
            put(String.class, new ParameterHandler.StringHandler());
        }
    };

    private android.util.Pair<Class<?>, Object> parcelableToClazz(Parcelable parcelable, String index, Bundle extras) {
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
        } else if (parcelable instanceof Pair.PairByteArray) {
            return new android.util.Pair<Class<?>, Object>(byte[].class, ((com.flyingpigeon.library.Pair.PairByteArray) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairString) {
            try {
                return new android.util.Pair<Class<?>, Object>(Class.forName(((Pair.PairString) parcelable).getKey()), ((Pair.PairString) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByteArray) {
            return new android.util.Pair<Class<?>, Object>(byte[].class, ((Pair.PairByteArray) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            try {
                return new android.util.Pair<Class<?>, Object>(Class.forName(((Pair.PairSerializable) parcelable).getKey()), ((Pair.PairSerializable) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                Parcelable value = ((Pair.PairParcelable) parcelable).getValue();
                if (value instanceof ParcelFileDescriptor) {
                    String lengthKey = index + PIGEON_KEY_ARRAY_LENGTH;
                    int arrayLength = extras.getInt(lengthKey);
                    Log.e(TAG, "keyLength:" + arrayLength + " lengthKey:" + lengthKey);
                    ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) value;
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
                    byte[] bytes = new byte[arrayLength];
                    try {
                        fileInputStream.read(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return new android.util.Pair<Class<?>, Object>(byte[].class, bytes);
                } else {
                    return new android.util.Pair<Class<?>, Object>(Class.forName(((Pair.PairParcelable) parcelable).getKey()), ((Pair.PairParcelable) parcelable).getValue());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    Object parseReponse(Bundle response, Method method) throws CallRemoteException {
        response.setClassLoader(Pair.class.getClassLoader());
        int responseCode = response.getInt(PIGEON_KEY_RESPONSE_CODE);
        if (responseCode == PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD) {
            throw new CallRemoteException("404 , method not found ");
        }
        if (responseCode == PIGEON_RESPONSE_RESULE_LOST_CLASS) {
            throw new CallRemoteException("404 , class not found ");
        }

        if (responseCode == PIGEON_RESPONSE_RESULE_ILLEGALACCESS) {
            throw new CallRemoteException("404 , illegal access ");
        }

        Parcelable parcelable;
        if (responseCode == PIGEON_RESPONSE_RESULE_SUCCESS && (parcelable = response.getParcelable(PIGEON_KEY_RESPONSE)) != null) {
            return parcelableValueOut(parcelable);
        }
        return null;
    }

    void parseReponse(Bundle response) throws CallRemoteException {
        response.setClassLoader(Pair.class.getClassLoader());
        int responseCode = response.getInt(PIGEON_KEY_RESPONSE_CODE);
        if (responseCode == PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD) {
            throw new CallRemoteException("404 , method not found ");
        }
        if (responseCode == PIGEON_RESPONSE_RESULE_LOST_CLASS) {
            throw new CallRemoteException("404 , class not found ");
        }

        if (responseCode == PIGEON_RESPONSE_RESULE_ILLEGALACCESS) {
            throw new CallRemoteException("404 , illegal access ");
        }
        response.remove(PIGEON_KEY_RESPONSE_CODE);
    }

    void buildResponse(Bundle request, Bundle response, Object result) {
        Parcelable parcelable = request.getParcelable(PIGEON_KEY_RESPONSE);
        if (parcelable != null) {
            parcelableValueIn(parcelable, result);
            response.putParcelable(PIGEON_KEY_RESPONSE, parcelable);
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

    Object parcelableValueOut(Parcelable parcelable) {
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
                    if (requestLarge != null && responseLarge != null) {
                        throw new IllegalArgumentException("framework unsupport RequestLarge and ResponseLarge together");
                    }
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
        in.putInt(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_ROUTE);
    }


    String[] buildRequestQuery(String route, Object[] params) {
        int length = params.length;
        String[] data = new String[length * 2 + 2];
        for (int i = 0; i < length; i++) {
            data[i] = params[i].toString();
            data[i + length + 2] = params[i].getClass().getName();
        }
        return data;
    }

    Bundle buildRouteRequestInsert(String route, Object[] params) {
        Type[] types = new Type[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                continue;
            }
            types[i] = params[i].getClass();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(PIGEON_KEY_LENGTH, params.length);
        bundle.putString(PIGEON_KEY_ROUTE, route);
        bundle.putInt(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_ROUTE);
        settingValues0(params, bundle, types);
        return bundle;
    }


    Cursor matchQuery(Uri uri, String[] arg, String route) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Log.e(TAG, "matchQuery:" + route);
        int pLength = (arg.length - 2) / 2;
        String[] params = new String[pLength];
        String[] types = new String[pLength];
        System.arraycopy(arg, 0, params, 0, pLength);
        System.arraycopy(arg, pLength + 2, types, 0, pLength);
        Object[] values = Utils.getValues(types, params);
        ArrayDeque<MethodCaller> callers = routers.get(route);
        if (callers == null || callers.isEmpty()) {
            throw new NoSuchMethodException(route);
        }
        MethodCaller methodCaller = callers.getFirst();
        Object o = methodCaller.call(values);
        if (o == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        BundleCursor bundleCursor = new BundleCursor(bundle, new String[]{"result"});
        ;
        if (o instanceof String) {
            bundle.putString(PIGEON_KEY_TYPE, "String");
            bundleCursor.addRow(new Object[]{o.toString()});
        } else if (o instanceof Byte[]) {
            bundle.putString(PIGEON_KEY_TYPE, "[B");
            bundleCursor.addRow(new Object[]{Utils.toPrimitives((Byte[]) o)});
        } else if (o instanceof byte[]) {
            bundle.putString(PIGEON_KEY_TYPE, "[B");
            bundleCursor.addRow(new Object[]{o});
        } else {
            RouteResponseLargeCaller routeResponseLargeCaller = (RouteResponseLargeCaller) methodCaller;
            Method target = routeResponseLargeCaller.target;
            Type returnType = target.getGenericReturnType();
            Utils.typeConvert(returnType, bundle, "result");
        }
        return bundleCursor;
    }

    Cursor matchQuery0(Uri uri, String[] arg, String method) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int pLength = (arg.length - 2) / 2;
        String[] params = new String[pLength];
        String[] types = new String[pLength];
        System.arraycopy(arg, 0, params, 0, pLength);
        System.arraycopy(arg, pLength + 2, types, 0, pLength);
        Class<?>[] classes = Utils.getClazz(types, params);
        String clazz = uri.getQueryParameter(PIGEON_KEY_CLASS);
        BuketMethod buketMethod = getMethods(Class.forName(clazz));
        if (buketMethod == null) {
            throw new NoSuchMethodException(method);
        }
        MethodCaller methodCaller = buketMethod.match(method, classes);
        Object[] values = Utils.getValues(types, params);
        Object o = methodCaller.call(values);
        if (o == null) {
            return null;
        }

        Bundle bundle = new Bundle();
        BundleCursor bundleCursor = new BundleCursor(bundle, new String[]{"result"});
        if (o instanceof String) {
            bundle.putString(PIGEON_KEY_TYPE, "String");
            bundleCursor.addRow(new Object[]{o.toString()});
        } else if (o instanceof Byte[]) {
            bundle.putString(PIGEON_KEY_TYPE, "[B");
            bundleCursor.addRow(new Object[]{Utils.toPrimitives((Byte[]) o)});
        } else if (o instanceof byte[]) {
            bundle.putString(PIGEON_KEY_TYPE, "[B");
            bundleCursor.addRow(new Object[]{o});
        } else {
            RouteResponseLargeCaller routeResponseLargeCaller = (RouteResponseLargeCaller) methodCaller;
            Method target = routeResponseLargeCaller.target;
            Type returnType = target.getGenericReturnType();
            Utils.typeConvert(returnType, bundle, "result");
        }
        return bundleCursor;
    }

    public Object routeQuery(String method, Bundle in) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String route = in.getString(PIGEON_KEY_ROUTE);
        if (TextUtils.isEmpty(route)) {
            throw new NoSuchMethodException();
        }
        ArrayDeque<MethodCaller> callers = routers.get(route);
        if (callers == null || callers.isEmpty()) {
            throw new NoSuchMethodException(route);
        }
        Iterator<MethodCaller> iterators = callers.iterator();
        if (iterators.hasNext()) {
            MethodCaller methodCaller = iterators.next();
            Object[] params = ServiceManager.getInstance().parseData("", in);
            return methodCaller.call(params);
        }
        return null;
    }

}
