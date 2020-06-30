package com.flyingpigeon.library;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Iterator;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESULT;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_TYPE;

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
        MethodCaller methodCaller;
        String clazz = in.getString(PIGEON_KEY_CLASS);
        BuketMethod buket = ServiceManager.getInstance().getMethods(Class.forName(clazz));
        if (buket == null) {
            throw new ClassNotFoundException();
        }
        methodCaller = buket.match(method, unboxing.first);
        Object result = methodCaller.call(ServiceManager.getInstance().parseData(arg, in));
        serverBoxmen.boxing(in, response, result);
        return response;
    }


    Bundle dispatch(String method, Bundle in, Bundle out) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String route = in.getString(PIGEON_KEY_ROUTE);
        if (TextUtils.isEmpty(route)) {
            throw new NoSuchMethodException();
        }
        ArrayDeque<MethodCaller> callers = ServiceManager.getInstance().routers.get(route);
        if (callers == null || callers.isEmpty()) {
            throw new NoSuchMethodException(route);
        }
        Iterator<MethodCaller> iterators = callers.iterator();
        while (iterators.hasNext()) {
            MethodCaller methodCaller = iterators.next();
            methodCaller.call(in, out);
        }
        return out;
    }

    Object dispatch(String method, Bundle in) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String route = in.getString(PIGEON_KEY_ROUTE);
        if (TextUtils.isEmpty(route)) {
            throw new NoSuchMethodException();
        }
        ArrayDeque<MethodCaller> callers = ServiceManager.getInstance().routers.get(route);
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


    Cursor dispatch(Uri uri, String[] arg, String route) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Log.e(TAG, "matchQuery:" + route);
        int pLength = (arg.length - 2) / 2;
        String[] params = new String[pLength];
        String[] types = new String[pLength];
        System.arraycopy(arg, 0, params, 0, pLength);
        System.arraycopy(arg, pLength + 2, types, 0, pLength);
        Object[] values = Utils.getValues(types, params);
        ArrayDeque<MethodCaller> callers = ServiceManager.getInstance().routers.get(route);
        if (callers == null || callers.isEmpty()) {
            throw new NoSuchMethodException(route);
        }
        MethodCaller methodCaller = callers.getFirst();
        Object o = methodCaller.call(values);
        if (o == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        BundleCursor bundleCursor = new BundleCursor(bundle, new String[]{PIGEON_KEY_RESULT});
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
            Utils.typeConvert(returnType, bundle, PIGEON_KEY_RESULT);
        }
        return bundleCursor;
    }


    Cursor dispatch0(Uri uri, String[] arg, String method) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        MethodCaller methodCaller = buketMethod.match(method, classes);
        Object[] values = Utils.getValues(types, params);
        Object o = methodCaller.call(values);
        if (o == null) {
            return null;
        }

        Bundle bundle = new Bundle();
        BundleCursor bundleCursor = new BundleCursor(bundle, new String[]{PIGEON_KEY_RESULT});
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
            Utils.typeConvert(returnType, bundle, PIGEON_KEY_RESULT);
        }
        return bundleCursor;
    }


    private static class Holder {
        private static final Server sInstance = new Server();
    }
}
