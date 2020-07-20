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
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import com.flyingpigeon.library.ClassUtil;
import com.flyingpigeon.library.Empty;
import com.flyingpigeon.library.Pair;
import com.flyingpigeon.library.ParameterHandler;
import com.flyingpigeon.library.Utils;
import com.flyingpigeon.library.ashmem.Ashmem;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Locale;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ARRAY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LOOK_UP_APPROACH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE;
import static com.flyingpigeon.library.PigeonConstant.map;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public class ClientBoxmenImpl implements ClientBoxmen<Bundle, Bundle, Object> {
    @Override
    public Bundle boxing(Object[] args, Type[] types, Type returnType) {
        Bundle bundle = new Bundle();
//        Type[] types = method.getGenericParameterTypes();
        for (int i = 0; i < types.length; i++) {
            String key = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
//            Log.e(TAG, "type name:" + types[i] + " method:" + method.getName() + " service:" + service);
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            if (int.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.IntHandler handler = (ParameterHandler.IntHandler) map.get(int.class);
                assert handler != null;
                handler.apply((Integer) args[i], key, bundle);
            } else if (double.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.DoubleHandler handler = (ParameterHandler.DoubleHandler) map.get(double.class);
                assert handler != null;
                handler.apply((Double) args[i], key, bundle);
            } else if (long.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.LongHandler handler = (ParameterHandler.LongHandler) map.get(long.class);
                assert handler != null;
                handler.apply((Long) args[i], key, bundle);
            } else if (short.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ShortHandler handler = (ParameterHandler.ShortHandler) map.get(short.class);
                assert handler != null;
                handler.apply((Short) args[i], key, bundle);
            } else if (float.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.FloatHandler handler = (ParameterHandler.FloatHandler) map.get(float.class);
                assert handler != null;
                handler.apply((Float) args[i], key, bundle);
            } else if (byte.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ByteHandler handler = (ParameterHandler.ByteHandler) map.get(byte.class);
                assert handler != null;
                handler.apply((Byte) args[i], key, bundle);
            } else if (byte[].class.isAssignableFrom(typeClazz)) {
                byte[] array = (byte[]) args[i];
                if (array.length > 8 * 1024) {
                    String keyLength = key + PIGEON_KEY_ARRAY_LENGTH;
                    ParcelFileDescriptor parcelFileDescriptor = Ashmem.byteArrayToFileDescriptor(array);
                    bundle.putInt(keyLength, array.length);
                    ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                    assert handler != null;
                    handler.apply(parcelFileDescriptor, key, bundle);
                    Parcelable parcelable = bundle.getParcelable(key);
                } else {
                    ParameterHandler.ByteArrayHandler byteArrayHandler = (ParameterHandler.ByteArrayHandler) map.get(byte[].class);
                    byteArrayHandler.apply(array, key, bundle);
                }

            } else if (Byte[].class.isAssignableFrom(typeClazz)) {
                byte[] array = Utils.toPrimitives((Byte[]) args[i]);
                if (array.length > 8 * 1024) {
                    ParcelFileDescriptor parcelFileDescriptor = Ashmem.byteArrayToFileDescriptor(array);
                    String keyLength = key + PIGEON_KEY_ARRAY_LENGTH;
                    bundle.putInt(keyLength, array.length);
                    ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                    assert handler != null;
                    handler.apply(parcelFileDescriptor, key, bundle);
                    Parcelable parcelable = bundle.getParcelable(key);
                } else {
                    ParameterHandler.ByteArrayHandler byteArrayHandler = (ParameterHandler.ByteArrayHandler) map.get(byte[].class);
                    byteArrayHandler.apply(array, key, bundle);
                }

            } else if (boolean.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.BooleanHandler handler = (ParameterHandler.BooleanHandler) map.get(boolean.class);
                assert handler != null;
                handler.apply((Boolean) args[i], key, bundle);
            } else if (String.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.StringHandler handler = (ParameterHandler.StringHandler) map.get(String.class);
                assert handler != null;
                handler.apply((String) args[i], key, bundle);
            } else if (Parcelable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                assert handler != null;
                handler.apply((Parcelable) args[i], key, bundle);
                Parcelable parcelable = bundle.getParcelable(key);
            } else if (Serializable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.SerializableHandler handler = (ParameterHandler.SerializableHandler) map.get(Serializable.class);
                assert handler != null;
                handler.apply((Serializable) args[i], key, bundle);
                Parcelable parcelable = bundle.getParcelable(key);
            }
        }

        bundle.putInt(PIGEON_KEY_LENGTH, types.length);
        bundle.putInt(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_METHOD);

        // build response type;
        String responseKey = PIGEON_KEY_RESPONSE;
//        Type returnType = method.getGenericReturnType();
        if (int.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.IntHandler handler = (ParameterHandler.IntHandler) map.get(int.class);
            assert handler != null;
            handler.apply(0, responseKey, bundle);
        } else if (double.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.DoubleHandler handler = (ParameterHandler.DoubleHandler) map.get(double.class);
            assert handler != null;
            handler.apply(0D, responseKey, bundle);
        } else if (long.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.LongHandler handler = (ParameterHandler.LongHandler) map.get(long.class);
            assert handler != null;
            handler.apply(0L, responseKey, bundle);
        } else if (short.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.ShortHandler handler = (ParameterHandler.ShortHandler) map.get(short.class);
            assert handler != null;
            handler.apply((short) 0, responseKey, bundle);
        } else if (float.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.FloatHandler handler = (ParameterHandler.FloatHandler) map.get(float.class);
            assert handler != null;
            handler.apply(0F, responseKey, bundle);
        } else if (byte.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.ByteHandler handler = (ParameterHandler.ByteHandler) map.get(byte.class);
            assert handler != null;
            handler.apply((byte) 0, responseKey, bundle);
        } else if (boolean.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.BooleanHandler handler = (ParameterHandler.BooleanHandler) map.get(boolean.class);
            assert handler != null;
            handler.apply(false, responseKey, bundle);
        } else if (String.class.isAssignableFrom((Class<?>) returnType)) {
            ParameterHandler.StringHandler handler = (ParameterHandler.StringHandler) map.get(String.class);
            assert handler != null;
            handler.apply("", responseKey, bundle);
        } else if (Parcelable.class.isAssignableFrom(((Class<?>) returnType))) {
            ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
            assert handler != null;
            handler.apply(new Empty(), responseKey, bundle);
        } else if (Serializable.class.isAssignableFrom(((Class<?>) returnType))) {
            ParameterHandler.SerializableHandler handler = (ParameterHandler.SerializableHandler) map.get(Serializable.class);
            assert handler != null;
            handler.apply(new Empty(), responseKey, bundle);
        } else if (Void.class.isAssignableFrom(((Class<?>) returnType))) {
        }

        return bundle;
    }

    @Override
    public Object unboxing(Bundle bundle) {
        bundle.setClassLoader(Pair.class.getClassLoader());
        Parcelable parcelable = null;
        if ((parcelable = bundle.getParcelable(PIGEON_KEY_RESPONSE)) == null) {
            return null;
        }
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
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairParcelable) {
            return ((Pair.PairParcelable) parcelable).getValue();
        }
        return null;
    }
}
