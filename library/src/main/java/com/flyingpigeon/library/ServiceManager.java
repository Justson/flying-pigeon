package com.flyingpigeon.library;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.flyingpigeon.library.annotations.RequestLarge;
import com.flyingpigeon.library.annotations.ResponseLarge;
import com.flyingpigeon.library.annotations.route;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import static com.flyingpigeon.library.Config.PREFIX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LOOK_UP_APPROACH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE_CODE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_ILLEGALACCESS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_LOST_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public final class ServiceManager implements IServiceManager {

    private static final String TAG = PREFIX + ServiceManager.class.getSimpleName();
    private final Object lock = new Object();
    private ConcurrentHashMap<Class<?>, BuketMethod> sCache = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ArrayDeque<MethodCaller>> routers = new ConcurrentHashMap<>();

    private ServiceManager() {
    }

    private static final ServiceManager sInstance = new ServiceManager();

    public static ServiceManager getInstance() {
        return sInstance;
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
                params[i] = args[i].toString();
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


}
