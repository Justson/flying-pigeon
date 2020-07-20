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

import android.text.TextUtils;

import com.flyingpigeon.library.invoker.MethodInvoker;
import com.flyingpigeon.library.invoker.RouteInvoker;
import com.flyingpigeon.library.invoker.RouteRequestLargeInvoker;
import com.flyingpigeon.library.invoker.RouteResponseLargeInvoker;
import com.flyingpigeon.library.log.FlyPigeonLog;

import com.flyingpigeon.library.annotations.RequestLarge;
import com.flyingpigeon.library.annotations.ResponseLarge;
import com.flyingpigeon.library.annotations.route;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public final class ServiceManager implements IServiceManager {

    private static final String TAG = PREFIX + ServiceManager.class.getSimpleName();
    private final Object lock = new Object();
    private ConcurrentHashMap<Class<?>, BuketMethod> sCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayDeque<MethodInvoker>> routers = new ConcurrentHashMap<>();

    private ServiceManager() {
    }

    private static final ServiceManager sInstance = new ServiceManager();

    public static ServiceManager getInstance() {
        return sInstance;
    }


    @Override
    public void publish(Object service, Class<?>... interfaces) {
        if (interfaces.length == 0) {
            FlyPigeonLog.e(TAG, "without interfaces ");
            return;
        }
        synchronized (lock) {
            for (Class<?> aInterface : interfaces) {
                BuketMethod buket = sCache.get(aInterface);
                if (buket != null) {
                    FlyPigeonLog.e(TAG, " publish failure, " + "please don't repeat publish same api:" + aInterface.getName());
                    continue;
                }
                BuketMethod buketMethod = new BuketMethod();
                buketMethod.setOwner(service);
                buketMethod.setInterfaceClass(aInterface);
                FlyPigeonLog.e(TAG, "publish:" + aInterface.getName());
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
                    String routeValue = route.value();
                    boolean encode = route.encoded();
                    if (TextUtils.isEmpty(routeValue)) {
                        FlyPigeonLog.e(TAG, " the route enable to empty .");
                        continue;
                    }
                    if (encode) {
                        try {
                            routeValue = URLDecoder.decode(routeValue, StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    RequestLarge requestLarge = method.getAnnotation(RequestLarge.class);
                    ResponseLarge responseLarge = method.getAnnotation(ResponseLarge.class);
                    if (requestLarge != null && responseLarge != null) {
                        throw new IllegalArgumentException("framework unsupport RequestLarge and ResponseLarge together");
                    }
//                    Log.e(TAG, " publish route:" + routeValue);
                    method.setAccessible(true);
                    if (requestLarge == null && responseLarge == null) {
                        MethodInvoker methodInvoker = new RouteInvoker(method, routeValue, service);
                        cacheMethodToRoute(routeValue, methodInvoker);
                    } else if (requestLarge != null) {
                        MethodInvoker methodInvoker = new RouteRequestLargeInvoker(method, routeValue, service);
                        cacheMethodToRoute(routeValue, methodInvoker);
                    } else {
                        MethodInvoker methodInvoker = new RouteResponseLargeInvoker(method, routeValue, service);
                        cacheMethodToRoute(routeValue, methodInvoker);
                    }
                }
            }
        }
    }

    BuketMethod getMethods(Class<?> clazz) {
        return this.sCache.get(clazz);
    }


    ArrayDeque<MethodInvoker> lookupMethods(String route) {
        return this.routers.get(route);
    }

    private void cacheMethodToRoute(String key, MethodInvoker caller) {
        synchronized (routers) {
            ArrayDeque<MethodInvoker> methodInvokers = routers.get(key);
            if (methodInvokers == null) {
                methodInvokers = new ArrayDeque<>();
                methodInvokers.addFirst(caller);
                routers.put(key, methodInvokers);
            } else {
                methodInvokers.addLast(caller);
            }
        }
    }

    @Override
    public void unpublish(Object service) {
        synchronized (lock) {
            Class<?>[] interfaces = ClassUtil.getValidInterface(service.getClass());
            if (interfaces.length == 0) {
                FlyPigeonLog.e(TAG, "unpublish interface is not exist");
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
    public void unpublish(Object service, Class<?>... interfaces) {
        synchronized (lock) {
            if (interfaces.length == 0) {
                FlyPigeonLog.e(TAG, "unpublish interface is not exist");
            }
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> aInterface = interfaces[i];
                sCache.remove(aInterface);
            }
        }
    }


}
