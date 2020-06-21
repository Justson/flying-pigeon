package com.flyingpigeon.library;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

import static com.flyingpigeon.library.ServiceManager.KEY_ROUTE;

/**
 * @author cenxiaozhong
 * @date 2020/6/21
 * @since 1.0.0
 */
public class Router {

    private String route;
    private Pigeon mPigeon;

    private Bundle mBundle;

    public Router(Pigeon pigeon, String route) {
        this.route = route;
        this.mPigeon = pigeon;
        this.mBundle = new Bundle();
    }


    public Bundle fly() {
        Bundle in = this.mBundle;
        if (in == null) {
            in = new Bundle();
        }
        in.putString(KEY_ROUTE, route);
        return mPigeon.fly(in);
    }


    /**
     * BE ATTENTION TO THIS METHOD WAS <P>SET, NOT ADD!</P>
     */
    public Router with(Bundle bundle) {
        if (null != bundle) {
            mBundle = bundle;
        }
        return this;
    }


    public Router withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }

    public Router withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }

    public Router withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }

    public Router withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public Router withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    public Router withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }

    public Router withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }

    public Router withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }

    public Router withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }

    public Router withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
        return this;
    }

    public Router withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }

    public Router withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public Router withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public Router withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        mBundle.putSparseParcelableArray(key, value);
        return this;
    }

    public Router withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public Router withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    public Router withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mBundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public Router withSerializable(@Nullable String key, @Nullable Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public Router withByteArray(@Nullable String key, @Nullable byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }

    public Router withShortArray(@Nullable String key, @Nullable short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }

    public Router withCharArray(@Nullable String key, @Nullable char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }

    public Router withFloatArray(@Nullable String key, @Nullable float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }

    public Router withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mBundle.putCharSequenceArray(key, value);
        return this;
    }

    public Router withBundle(@Nullable String key, @Nullable Bundle value) {
        mBundle.putBundle(key, value);
        return this;
    }


}
