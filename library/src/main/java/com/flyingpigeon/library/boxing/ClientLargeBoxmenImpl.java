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

import com.flyingpigeon.library.ClassUtil;
import com.flyingpigeon.library.Pair;

import java.lang.reflect.Type;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESULT;


/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public class ClientLargeBoxmenImpl implements ClientBoxmen<String[], Bundle, Object> {

    @Override
    public String[] boxing(Object[] args, Type[] types, Type returnType) {
        return settingValues(args, types);
    }

    @Override
    public Object unboxing(Bundle bundle) {
        Parcelable parcelable = bundle.getParcelable(PIGEON_KEY_RESULT);
        if (parcelable != null) {
            return parcelableValueOut(parcelable);
        }
        return null;
    }


    Object parcelableValueOut(Parcelable parcelable) {
        if (parcelable instanceof com.flyingpigeon.library.Pair.PairInt) {
            return ((com.flyingpigeon.library.Pair.PairInt) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairDouble) {
            return ((com.flyingpigeon.library.Pair.PairDouble) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairLong) {
            return ((com.flyingpigeon.library.Pair.PairLong) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairShort) {
            return ((com.flyingpigeon.library.Pair.PairShort) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairFloat) {
            return ((com.flyingpigeon.library.Pair.PairFloat) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByte) {
            return ((com.flyingpigeon.library.Pair.PairByte) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairBoolean) {
            return ((com.flyingpigeon.library.Pair.PairBoolean) parcelable).isValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairString) {
            return ((Pair.PairString) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            return ((Pair.PairSerializable) parcelable).getValue();
        } else {
            return ((Pair.PairParcelable) parcelable).getValue();
        }
    }


    private String[] settingValues(Object[] args, Type[] types) {
        int pLength = types.length;
        String[] params = new String[pLength * 2 + 2];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                params[i] = "";
                params[i + pLength + 2] = ClassUtil.getRawType(types[i]).getName();
                continue;
            }
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            params[i] = args[i].toString();
            if (int.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = int.class.getName();
            } else if (double.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = double.class.getName();
            } else if (long.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = long.class.getName();
            } else if (short.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = short.class.getName();
            } else if (float.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = float.class.getName();
            } else if (byte.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = byte.class.getName();
            } else if (boolean.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = boolean.class.getName();
            } else if (String.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = String.class.getName();
            } else if (Integer.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = int.class.getName();
            } else if (Double.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = double.class.getName();
            } else if (Long.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = long.class.getName();
            } else if (Short.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = short.class.getName();
            } else if (Float.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = float.class.getName();
            } else if (Byte.class.isAssignableFrom(typeClazz)) {
                params[i + pLength + 2] = byte.class.getName();
            }
        }
        return params;
    }

}
