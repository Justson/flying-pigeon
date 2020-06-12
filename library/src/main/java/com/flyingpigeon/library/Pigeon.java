package com.flyingpigeon.library;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author xiaozhongcen
 * @date 20-6-8
 * @since 1.0.0
 */
public final class Pigeon {

    private static final String TAG = PREFIX + Pigeon.class.getSimpleName();

    private String authorities;
    private Context mContext;
    private Uri base;

    private Pigeon(Builder builder) {
        authorities = builder.authorities;
        mContext = builder.mContext;
        base = Uri.parse("content://" + authorities);
    }


    public <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return (T) call(proxy, method, args);
            }
        });

    }

    Gson mGson = new Gson();

    private static final Object EMPTY = new Object();

    private Object call(Object proxy, Method method, Object[] args) {
        Bundle bundle = PigeonEngine.getInstance().buildRequest(method, args);
        ContentResolver contentResolver = mContext.getContentResolver();
        Bundle response = contentResolver.call(base, method.getName(), null, bundle);
        Object o = null;
        try {
            o = PigeonEngine.getInstance().parseReponse(response, method);
        } catch (CallRemoteException e) {
            throw new RuntimeException(e);
        }
        return o;
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
            if (authority.startsWith(":")) {
                this.authority = mContext.getPackageName().concat(authority.replace(":", ""));
                return this;
            }
            this.authority = authority;
            return this;
        }

        public Builder setAuthority(Class<?> service) {
            PackageInfo packageInfos = null;
            try {
                PackageManager mgr = mContext.getPackageManager();
                if (mgr != null) {
                    packageInfos =
                            mgr.getPackageInfo(mContext.getPackageName(), PackageManager.GET_PROVIDERS);
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
                new IllegalArgumentException("ServiceApi is not exists");
            }
            return this;
        }

        public Pigeon build() {
            return new Pigeon(this);
        }


    }

}
