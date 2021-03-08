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
package com.flyingpigeon.library.boxing;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;

import com.flyingpigeon.library.ParameterHandler;
import com.flyingpigeon.library.log.FlyPigeonLog;

import java.io.Serializable;
import java.util.Locale;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE_CODE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_TYPE_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_SUCCESS;
import static com.flyingpigeon.library.PigeonConstant.map;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public class ServerBoxmenImpl implements ServerBoxmen<Bundle> {
    private static final String TAG = ServerBoxmenImpl.class.getSimpleName();

    @Override
    public Pair<Class<?>[], Object[]> unboxing(Bundle bundle) {
        int length = bundle.getInt(PIGEON_KEY_LENGTH);
        Class<?>[] clazzs = new Class[length];
        Object[] values = new Object[length];
        FlyPigeonLog.e(TAG, "length:" + length);
        for (int i = 0; i < length; i++) {
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, i);
            String type = bundle.getString(keyType);
            FlyPigeonLog.e(TAG, "keyType:" + keyType + " i:" + i + " type:" + type);
            ParameterHandler parameterHandler = map.get(type);
            if (parameterHandler == null) {
                break;
            }
            android.util.Pair<Class<?>, Object> data = parameterHandler.map(i, bundle);
            if (data == null) {
                throw new IllegalArgumentException("arg error");
            }
            clazzs[i] = data.first;
            values[i] = data.second;
            FlyPigeonLog.e(TAG, "clazzs:" + (clazzs[i]) + " values:" + values[i]);
        }
        for (int i = 0; i < length; i++) {
            if (clazzs[i] == null) {
                throw new IllegalArgumentException("arg error");
            }
        }
        return new Pair<>(clazzs, values);
    }


    @Override
    public void boxing(Bundle in, Bundle out, Object result) {
        String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, -1);
        String type = in.getString(keyType);
        if (!TextUtils.isEmpty(type)) {
            ParameterHandler parameterHandler = map.get(type);
            if (parameterHandler != null) {
                parameterHandler.apply(result, -1, out);
            }
        }
        out.putInt(PIGEON_KEY_RESPONSE_CODE, PIGEON_RESPONSE_RESULE_SUCCESS);
    }

    private void parcelableValueIn(Parcelable parcelable, Object value) {
        if (parcelable instanceof com.flyingpigeon.library.Pair.PairInt) {
            ((com.flyingpigeon.library.Pair.PairInt) parcelable).setValue((Integer) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairDouble) {
            ((com.flyingpigeon.library.Pair.PairDouble) parcelable).setValue((Double) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairLong) {
            ((com.flyingpigeon.library.Pair.PairLong) parcelable).setValue((Long) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairShort) {
            ((com.flyingpigeon.library.Pair.PairShort) parcelable).setValue((Short) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairFloat) {
            ((com.flyingpigeon.library.Pair.PairFloat) parcelable).setValue((Float) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByte) {
            ((com.flyingpigeon.library.Pair.PairByte) parcelable).setValue((Byte) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairBoolean) {
            ((com.flyingpigeon.library.Pair.PairBoolean) parcelable).setValue((Boolean) value);
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairString) {
            ((com.flyingpigeon.library.Pair.PairString) parcelable).setValue((String) value);
            ((com.flyingpigeon.library.Pair.PairString) parcelable).setKey(value.getClass().getName());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            ((com.flyingpigeon.library.Pair.PairSerializable) parcelable).setValue((Serializable) value);
            ((com.flyingpigeon.library.Pair.PairSerializable) parcelable).setKey(value.getClass().getName());
        } else {
            ((com.flyingpigeon.library.Pair.PairParcelable) parcelable).setValue((Parcelable) value);
            ((com.flyingpigeon.library.Pair.PairParcelable) parcelable).setKey(value.getClass().getName());
        }
    }

}
