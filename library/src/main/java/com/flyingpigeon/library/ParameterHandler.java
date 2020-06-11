package com.flyingpigeon.library;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * @author ringle-android
 * @date 20-6-10
 * @since 1.0.0
 */
public abstract class ParameterHandler<T> {
    abstract void apply(T value, String key, Bundle bundle);

    public static class IntHandler extends ParameterHandler<Integer> {
        @Override
        void apply(Integer value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairInt(int.class.getName(), value));
        }
    }

    public static class DoubleHandler extends ParameterHandler<Double> {
        @Override
        void apply(Double value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairDouble(double.class.getName(), value));
        }
    }

    public static class LongHandler extends ParameterHandler<Long> {
        @Override
        void apply(Long value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairLong(long.class.getName(), value));
        }
    }

    public static class ShortHandler extends ParameterHandler<Short> {
        @Override
        void apply(Short value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairShort(short.class.getName(), value));
        }
    }

    public static class FloatHandler extends ParameterHandler<Float> {
        @Override
        void apply(Float value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairFloat(float.class.getName(), value));
        }
    }

    public static class ByteHandler extends ParameterHandler<Byte> {
        @Override
        void apply(Byte value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairByte(byte.class.getName(), value));
        }
    }

    public static class BooleanHandler extends ParameterHandler<Boolean> {
        @Override
        void apply(Boolean value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairBoolean(boolean.class.getName(), value));
        }
    }

    public static class StringHandler extends ParameterHandler<String> {
        @Override
        void apply(String value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairString(value.getClass().getName(), value));
        }
    }

    public static class ParcelableHandler extends ParameterHandler<Parcelable> {
        @Override
        void apply(Parcelable value, String key, Bundle bundle) {
            bundle.putParcelable(key, new Pair.PairParcelable(value.getClass().getName(), value));
        }
    }
}
