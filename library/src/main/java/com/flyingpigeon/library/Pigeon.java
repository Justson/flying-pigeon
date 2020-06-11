package com.flyingpigeon.library;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author ringle-android
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
                Log.e(TAG, " method:" + method + " args:" + new Gson().toJson(args) + " proxy:" + proxy.getClass());
                call(proxy, method, args);
                return null;
            }
        });

    }

    Gson mGson = new Gson();


    private void call(Object proxy, Method method, Object[] args) {
        Bundle bundle = PigeonEngine.getInstance().buildRequest(method, args);
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.call(base, method.getName(), null, bundle);
    }


    public static Pigeon.Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static final class Builder {

        private Context mContext;
        private String authorities;

        private Builder(Context context) {
            this.mContext = context;
        }

        public Builder setAuthorities(String authorities) {
            if (TextUtils.isEmpty(authorities)) {
                throw new IllegalArgumentException("authorities error");
            }
            if (authorities.startsWith(":")) {
                this.authorities = mContext.getPackageName().concat(authorities.replace(":", ""));
                return this;
            }
            this.authorities = authorities;
            return this;
        }

        public Pigeon build() {
            return new Pigeon(this);
        }


    }

}
