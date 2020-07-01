package com.flyingpigeon.library;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.flyingpigeon.library.annotations.RequestLarge;
import com.flyingpigeon.library.annotations.ResponseLarge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import androidx.annotation.NonNull;

import static com.flyingpigeon.library.Config.PREFIX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_FLAGS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LOOK_UP_APPROACH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE_CODE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESULT;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_TYPE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_ILLEGALACCESS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_LOST_CLASS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD;

/**
 * @author xiaozhongcen
 * @date 20-6-8
 * @since 1.0.0
 */
public final class Pigeon {

    private static final String TAG = PREFIX + Pigeon.class.getSimpleName();

    String authority;
    Context mContext;
    final Uri base;

    private Pigeon(Builder builder) {
        authority = builder.authority;
        mContext = builder.mContext;
        base = Uri.parse("content://" + authority);
    }


    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return (T) call(service, proxy, method, args);
            }
        });
    }

    public FlyPigeon route(String route) {
        return new FlyPigeon(this, route);
    }

    public LargeFlyPigeon route(String route, Object... params) {
        return new LargeFlyPigeon(this, route, params);
    }


    private Object call(Class<?> service, Object proxy, Method method, Object[] args) {
        RequestLarge requestLarge = method.getAnnotation(RequestLarge.class);
        ResponseLarge responseLarge = method.getAnnotation(ResponseLarge.class);
        int flags = 0;
        if (requestLarge == null) {
            flags = ParametersSpec.setRequestNormal(flags);
        } else {
            flags = ParametersSpec.setRequestLarge(flags);
        }
        if (responseLarge == null) {
            flags = ParametersSpec.setResponseNormal(flags);
        } else {
            flags = ParametersSpec.setResponseLarge(flags);
        }
        boolean isResponseLarge = ParametersSpec.isResponseParameterLarge(flags);
        if (isResponseLarge) {
            return large(service, proxy, method, args);
        }
        flags = ParametersSpec.setParamParcel(flags, false);
        ClientBoxmen<Bundle, Bundle, Object> clientBoxmen = new ClientBoxmenImpl();
        Bundle bundle = clientBoxmen.boxing(args, method.getGenericParameterTypes(), method.getGenericReturnType());
        bundle.putInt(PIGEON_KEY_FLAGS, flags);
        bundle.putString(PIGEON_KEY_CLASS, service.getName());
        RealCall realCall = newCall();
        Bundle response = realCall.execute(method, bundle);
        try {
            parseReponse(new Bundle(), response);
        } catch (CallRemoteException e) {
            e.printStackTrace();
            return null;
        }
        return clientBoxmen.unboxing(response);
    }

    private RealCall newCall() {
        return new RealCall(mContext, this);
    }

    private Object large(Class<?> service, Object proxy, Method method, Object[] args) {
        ClientBoxmen<String[], Bundle, Object> clientBoxmen = new ClientLargeBoxmenImpl();
        String[] contentValues = clientBoxmen.boxing(args, method.getGenericParameterTypes(), null);
        Cursor cursor = null;
        try {
            cursor = newCall().execute(method, service, contentValues);
            Bundle bundle = cursor.getExtras();
            parseReponse(new Bundle(), bundle);
            if (cursor.moveToFirst()) {
                String clazz = bundle.getString(PIGEON_KEY_TYPE);
                if ("String".equalsIgnoreCase(clazz)) {
                    return cursor.getString(0);
                } else if ("[B".equalsIgnoreCase(clazz)) {
                    return cursor.getBlob(0);
                }
            } else {
                Object o = clientBoxmen.unboxing(bundle);
                return o;
            }
        } catch (CallRemoteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    Bundle fly(@NonNull Bundle in) {
        in.putInt(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_ROUTE);
        ContentResolver contentResolver = mContext.getContentResolver();
        Bundle out = null;
        try {
            out = contentResolver.call(base, "", null, in);
            parseReponse(in, out);
        } catch (Throwable e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return out;
    }


    void parseReponse(Bundle in, Bundle out) throws CallRemoteException {
        out.setClassLoader(Pair.class.getClassLoader());
        int responseCode = out.getInt(PIGEON_KEY_RESPONSE_CODE);
        if (responseCode == PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD) {
            throw new CallRemoteException("404 , method not found ");
        }

        if (responseCode == PIGEON_RESPONSE_RESULE_LOST_CLASS) {
            throw new CallRemoteException("404 , class not found ");
        }

        if (responseCode == PIGEON_RESPONSE_RESULE_ILLEGALACCESS) {
            throw new CallRemoteException("404 , illegal access ");
        }

        if (responseCode == PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE) {
            String route = in.getString(PIGEON_KEY_ROUTE);
            throw new CallRemoteException(route + " was not found ");
        }
    }

    public static Pigeon.Builder newBuilder(@NonNull Context context) {
        Objects.requireNonNull(context);
        return new Builder(context);
    }

    <T> T routeLargeRequest(String route, Object[] params) {
        RouteClientBoxmen<Bundle, Object> routeClientBoxmen = new RouteClientBoxmenImpl();
        Bundle bundle = routeClientBoxmen.boxing(route, params);
        Bundle out = newCall().execute(route, bundle);
        if (null == out) {
            return null;
        }
        try {
            parseReponse(bundle, out);
        } catch (CallRemoteException e) {
            e.printStackTrace();
            return null;
        }
        out.setClassLoader(Pair.class.getClassLoader());
        return (T) routeClientBoxmen.unboxing(out);
    }

    <T> T routeLargeResponse(String route, Object[] params) {
        int length = params.length;
        String[] data = new String[length * 2 + 2];
        for (int i = 0; i < length; i++) {
            data[i] = params[i].toString();
            data[i + length + 2] = params[i].getClass().getName();
        }
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = base.buildUpon().appendPath("pigeon/10/" + route).build();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, new String[]{}, "", data, "");

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (null == cursor) {
            return null;
        }
        Bundle out = cursor.getExtras();
        Bundle in = new Bundle();
        in.putString(PIGEON_KEY_ROUTE, route);
        try {
            parseReponse(in, out);
        } catch (CallRemoteException e) {
            e.printStackTrace();
            return null;
        }
        try {
            Parcelable parcelable = out.getParcelable(PIGEON_KEY_RESULT);
            if (parcelable != null) {
                return (T) Utils.parcelableValueOut(parcelable);
            } else if (cursor.moveToFirst()) {
                String clazz = out.getString(PIGEON_KEY_TYPE);
                if ("String".equalsIgnoreCase(clazz)) {
                    return (T) cursor.getString(0);
                } else if ("[B".equalsIgnoreCase(clazz)) {
                    return (T) cursor.getBlob(0);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    public static final class Builder {

        private Context mContext;
        private String authority;

        private Builder(Context context) {
            this.mContext = context;
        }

        public Builder setAuthority(@NonNull String authority) {
            if (TextUtils.isEmpty(authority)) {
                throw new IllegalArgumentException("authorities error");
            }
            this.authority = authority;
            return this;
        }

        public Builder setAuthority(@NonNull Class<? extends ServiceContentProvider> service) {
            PackageInfo packageInfos = null;
            try {
                PackageManager packageManager = mContext.getPackageManager();
                if (packageManager != null) {
                    packageInfos =
                            packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_PROVIDERS);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageInfos != null && packageInfos.providers != null) {
                for (ProviderInfo providerInfo : packageInfos.providers) {
                    if (providerInfo.name.equals(service.getName())) {
                        authority = providerInfo.authority;
                    }
                }
            }
            if (TextUtils.isEmpty(this.authority)) {
                new IllegalArgumentException("service is not exists");
            }
            return this;
        }

        public Pigeon build() {
            return new Pigeon(this);
        }


    }

}
