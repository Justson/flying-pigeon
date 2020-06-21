package com.flyingpigeon.library;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import androidx.annotation.RequiresApi;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author xiaozhongcen
 * @date 20-6-8
 * @since 1.0.0
 */
public final class Pigeon {

    private static final String TAG = PREFIX + Pigeon.class.getSimpleName();

    private String authority;
    private Context mContext;
    private Uri base;

    private Pigeon(Builder builder) {
        authority = builder.authority;
        mContext = builder.mContext;
        base = Uri.parse("content://" + authority);
    }


    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return (T) call(service, proxy, method, args);
            }
        });
    }

    public FlyPigeon route(String route) {
        return new FlyPigeon(this, route);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Object call(Class<?> service, Object proxy, Method method, Object[] args) {
        Bundle bundle = ServiceManager.getInstance().buildRequest(service, proxy, method, args);
        ContentResolver contentResolver = mContext.getContentResolver();
        Bundle response = contentResolver.call(base, method.getName(), null, bundle);
        Object o = null;
        try {
            o = ServiceManager.getInstance().parseReponse(response, method);
        } catch (CallRemoteException e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    Bundle fly(Bundle in) {
        ServiceManager.getInstance().buildRequestRoute(in);
        ContentResolver contentResolver = mContext.getContentResolver();
        Bundle response = contentResolver.call(base, "", null, in);
        try {
            ServiceManager.getInstance().parseReponse(response);
        } catch (CallRemoteException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public static Pigeon.Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static final class Builder {

        private Context mContext;
        private String authority;

        private Builder(Context context) {
            this.mContext = context;
        }

        public Builder setAuthority(String authority) {
            if (TextUtils.isEmpty(authority)) {
                throw new IllegalArgumentException("authorities error");
            }
            this.authority = authority;
            return this;
        }

        public Builder setAuthority(Class<?> service) {
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
