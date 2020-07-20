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

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import com.flyingpigeon.library.boxing.ServerBoxmen;
import com.flyingpigeon.library.boxing.ServerBoxmenImpl;
import com.flyingpigeon.library.invoker.MethodInvoker;
import com.flyingpigeon.library.invoker.RouteResponseLargeInvoker;
import com.flyingpigeon.library.log.FlyPigeonLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Iterator;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE_CODE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESULT;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_TYPE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_SUCCESS;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public class Server {


    private static final String TAG = Server.class.getSimpleName();

    private Server() {
    }


    public static Server getInstance() {
        return Holder.sInstance;
    }

    public Bundle dispatch(String method, Bundle response, String arg, Bundle in) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerBoxmen<Bundle> serverBoxmen = new ServerBoxmenImpl();
        Pair<Class<?>[], Object[]> unboxing = serverBoxmen.unboxing(in);
        MethodInvoker methodInvoker;
        String clazz = in.getString(PIGEON_KEY_CLASS);
        BuketMethod buket = ServiceManager.getInstance().getMethods(Class.forName(clazz));
        if (buket == null) {
            throw new ClassNotFoundException();
        }
        methodInvoker = buket.match(method, unboxing.first);
        Object result = methodInvoker.invoke(unboxing.second);
        serverBoxmen.boxing(in, response, result);
        response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_SUCCESS);
        return response;
    }


    Bundle dispatch(String method, Bundle in, Bundle out) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NotFoundRouteException {
        String route = in.getString(PIGEON_KEY_ROUTE);
        if (TextUtils.isEmpty(route)) {
            throw new NotFoundRouteException(route + " was not found");
        }
        ArrayDeque<MethodInvoker> callers = ServiceManager.getInstance().lookupMethods(route);
        if (callers == null || callers.isEmpty()) {
            throw new NotFoundRouteException(route + " was not found");
        }
        Iterator<MethodInvoker> iterators = callers.iterator();
        while (iterators.hasNext()) {
            MethodInvoker methodInvoker = iterators.next();
            methodInvoker.invoke(in, out);
        }
        out.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_SUCCESS);
        return out;
    }

    void dispatch0(String method, Bundle in, Bundle response) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String route = in.getString(PIGEON_KEY_ROUTE);
        if (TextUtils.isEmpty(route)) {
            throw new NotFoundRouteException(route + " was not found");
        }
        ArrayDeque<MethodInvoker> callers = ServiceManager.getInstance().lookupMethods(route);
        if (callers == null || callers.isEmpty()) {
            throw new NotFoundRouteException(route + " was not found");
        }
        ServerBoxmenImpl serverBoxmen = new ServerBoxmenImpl();
        Pair<Class<?>[], Object[]> pair = serverBoxmen.unboxing(in);
        Iterator<MethodInvoker> iterators = callers.iterator();
        if (iterators.hasNext()) {
            MethodInvoker methodInvoker = iterators.next();
            Object[] params = pair.second;
            Object o = methodInvoker.invoke(params);
            if (o != null) {
                Class<?> clazz = o.getClass();
//                Log.e(TAG, "clazz:" + clazz + " o:" + o);
                Utils.convert(PIGEON_KEY_RESPONSE, in, clazz, o);
                serverBoxmen.boxing(in, response, o);
            }
        }
        response.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_SUCCESS);
    }


    Cursor dispatch(Uri uri, String[] arg, String route) {
        Bundle bundle = new Bundle();
        BundleCursor bundleCursor = new BundleCursor(bundle, new String[]{PIGEON_KEY_RESULT});
        bundle.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_SUCCESS);
        try {
            FlyPigeonLog.e(TAG, "matchQuery:" + route);
            int pLength = (arg.length - 2) / 2;
            String[] params = new String[pLength];
            String[] types = new String[pLength];
            System.arraycopy(arg, 0, params, 0, pLength);
            System.arraycopy(arg, pLength + 2, types, 0, pLength);
            Object[] values = Utils.getValues(types, params);
            ArrayDeque<MethodInvoker> callers = ServiceManager.getInstance().lookupMethods(route);
            if (callers == null || callers.isEmpty()) {
                throw new NotFoundRouteException(route + " was not found");
            }
            MethodInvoker methodInvoker = callers.getFirst();
            Object o = methodInvoker.invoke(values);
            if (o == null) {
                return bundleCursor;
            }
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
                RouteResponseLargeInvoker routeResponseLargeCaller = (RouteResponseLargeInvoker) methodInvoker;
                Method target = routeResponseLargeCaller.target;
                Type returnType = target.getGenericReturnType();
                Utils.typeConvert(returnType, bundle, PIGEON_KEY_RESULT);
            }
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            bundle.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE);
        } catch (NotFoundRouteException e) {
            bundle.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE);
            e.printStackTrace();
        }
        return bundleCursor;
    }


    Cursor dispatch0(Uri uri, String[] arg, String method) {
        Bundle bundle = new Bundle();
        BundleCursor bundleCursor = new BundleCursor(bundle, new String[]{PIGEON_KEY_RESULT});
        bundle.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_SUCCESS);
        try {
            int pLength = (arg.length - 2) / 2;
            String[] params = new String[pLength];
            String[] types = new String[pLength];
            System.arraycopy(arg, 0, params, 0, pLength);
            System.arraycopy(arg, pLength + 2, types, 0, pLength);
            Class<?>[] classes = Utils.getClazz(types, params);
            String clazz = uri.getQueryParameter(PIGEON_KEY_CLASS);
            BuketMethod buketMethod = ServiceManager.getInstance().getMethods(Class.forName(clazz));
            if (buketMethod == null) {
                throw new NoSuchMethodException(method);
            }
            MethodInvoker methodInvoker = buketMethod.match(method, classes);
            Object[] values = Utils.getValues(types, params);
            Object o = methodInvoker.invoke(values);
            if (o == null) {
                return bundleCursor;
            }

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
                RouteResponseLargeInvoker routeResponseLargeCaller = (RouteResponseLargeInvoker) methodInvoker;
                Method target = routeResponseLargeCaller.target;
                Type returnType = target.getGenericReturnType();
                Utils.typeConvert(returnType, bundle, PIGEON_KEY_RESULT);
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            bundle.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD);
        } catch (NotFoundRouteException e) {
            bundle.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD);
        }
        return bundleCursor;
    }


    private static class Holder {
        private static final Server sInstance = new Server();
    }
}
