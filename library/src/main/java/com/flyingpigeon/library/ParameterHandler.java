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
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author xiaozhongcen
 * @date 20-6-10
 * @since 1.0.0
 */
public abstract class ParameterHandler<T> {
    public abstract void apply(T value, String key, Bundle bundle);

    public static class IntHandler extends ParameterHandler<Integer> {
        @Override
        public void apply(Integer value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairInt(int.class.getName(), value));
        }
    }

    public static class DoubleHandler extends ParameterHandler<Double> {
        @Override
        public void apply(Double value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairDouble(double.class.getName(), value));
        }
    }

    public static class LongHandler extends ParameterHandler<Long> {
        @Override
        public void apply(Long value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairLong(long.class.getName(), value));
        }
    }

    public static class ShortHandler extends ParameterHandler<Short> {
        @Override
        public void apply(Short value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairShort(short.class.getName(), value));
        }
    }

    public static class FloatHandler extends ParameterHandler<Float> {
        @Override
        public void apply(Float value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairFloat(float.class.getName(), value));
        }
    }

    public static class ByteHandler extends ParameterHandler<Byte> {
        @Override
        public void apply(Byte value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairByte(byte.class.getName(), value));
        }
    }

    public static class ByteArrayHandler extends ParameterHandler<byte[]> {

        @Override
        public void apply(byte[] value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairByteArray(byte[].class.getName(), value));
        }
    }

    public static class BooleanHandler extends ParameterHandler<Boolean> {
        @Override
        public void apply(Boolean value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairBoolean(boolean.class.getName(), value));
        }
    }

    public static class StringHandler extends ParameterHandler<String> {
        @Override
        public void apply(String value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairString(value.getClass().getName(), value));
        }
    }

    public static class ParcelableHandler extends ParameterHandler<Parcelable> {
        @Override
        public void apply(Parcelable value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairParcelable(value.getClass().getName(), value));
        }
    }

    public static class ParcelFileDescriptorHandler extends ParameterHandler<ParcelFileDescriptor> {

        @Override
        public void apply(ParcelFileDescriptor value, String key, Bundle bundle) {

        }
    }

    public static class SerializableHandler extends ParameterHandler<Serializable> {
        @Override
        public void apply(Serializable value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairSerializable(value.getClass().getName(), value));
        }
    }
}
