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

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_FLAGS;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ROUTE;

/**
 * @author cenxiaozhong
 * @date 2020/6/21
 * @since 1.0.0
 */
public class FlyPigeon {

    protected String route;
    protected Pigeon mPigeon;
    protected Bundle mBundle;

    public FlyPigeon(Pigeon pigeon, String route) {
        this.route = route;
        this.mPigeon = pigeon;
        this.mBundle = new Bundle();
    }


    public Bundle fly() {
        Bundle in = this.mBundle;
        if (in == null) {
            in = new Bundle();
        }
        in.putString(PIGEON_KEY_ROUTE, route);
        int flags = 0;
        flags = ParametersSpec.setParamParcel(flags, true);
        in.putInt(PIGEON_KEY_FLAGS, flags);
        return mPigeon.fly(in);
    }


    public FlyPigeon with(Bundle bundle) {
        if (null != bundle) {
            mBundle = bundle;
        }
        return this;
    }


    public FlyPigeon withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }

    public FlyPigeon withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }

    public FlyPigeon withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }

    public FlyPigeon withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public FlyPigeon withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    public FlyPigeon withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }

    public FlyPigeon withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }

    public FlyPigeon withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }

    public FlyPigeon withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }

    public FlyPigeon withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
        return this;
    }

    public FlyPigeon withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }

    public FlyPigeon withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public FlyPigeon withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public FlyPigeon withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        mBundle.putSparseParcelableArray(key, value);
        return this;
    }

    public FlyPigeon withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public FlyPigeon withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    public FlyPigeon withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mBundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public FlyPigeon withSerializable(@Nullable String key, @Nullable Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public FlyPigeon withByteArray(@Nullable String key, @Nullable byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }

    public FlyPigeon withShortArray(@Nullable String key, @Nullable short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }

    public FlyPigeon withCharArray(@Nullable String key, @Nullable char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }

    public FlyPigeon withFloatArray(@Nullable String key, @Nullable float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }

    public FlyPigeon withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mBundle.putCharSequenceArray(key, value);
        return this;
    }

    public FlyPigeon withBundle(@Nullable String key, @Nullable Bundle value) {
        mBundle.putBundle(key, value);
        return this;
    }


}
