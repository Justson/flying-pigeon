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

import com.flyingpigeon.library.ashmem.Ashmem;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Locale;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ARRAY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_TYPE_INDEX;
import static com.flyingpigeon.library.PigeonConstant.map;

/**
 * @author xiaozhongcen
 * @date 20-6-10
 * @since 1.0.0
 */
public abstract class ParameterHandler<T> {
    public abstract void apply(T value, int key, Bundle bundle);

    public abstract android.util.Pair<Class<?>, Object> map(int key, Bundle bundle);


    public static void assemble(Object[] args, Type[] types, Bundle bundle) {
        for (int i = 0; i < types.length; i++) {
            int key = i;
//            Log.e(TAG, "type name:" + types[i] + " method:" + method.getName() + " service:" + service);
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            if (int.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.IntHandler handler = (ParameterHandler.IntHandler) map.get("int");
                assert handler != null;
                handler.apply((Integer) args[i], key, bundle);
            } else if (double.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.DoubleHandler handler = (ParameterHandler.DoubleHandler) map.get("double");
                assert handler != null;
                handler.apply((Double) args[i], key, bundle);
            } else if (long.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.LongHandler handler = (ParameterHandler.LongHandler) map.get("long");
                assert handler != null;
                handler.apply((Long) args[i], key, bundle);
            } else if (short.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ShortHandler handler = (ParameterHandler.ShortHandler) map.get("short");
                assert handler != null;
                handler.apply((Short) args[i], key, bundle);
            } else if (float.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.FloatHandler handler = (ParameterHandler.FloatHandler) map.get("float");
                assert handler != null;
                handler.apply((Float) args[i], key, bundle);
            } else if (byte.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.ByteHandler handler = (ParameterHandler.ByteHandler) map.get("byte");
                assert handler != null;
                handler.apply((Byte) args[i], key, bundle);
            } else if (byte[].class.isAssignableFrom(typeClazz)) {
                byte[] array = (byte[]) args[i];
                ParameterHandler.ByteArrayHandler byteArrayHandler = (ParameterHandler.ByteArrayHandler) map.get("[b");
                byteArrayHandler.apply(array, key, bundle);

            } else if (Byte[].class.isAssignableFrom(typeClazz)) {
                byte[] array = Utils.toPrimitives((Byte[]) args[i]);
                ParameterHandler.ByteArrayHandler byteArrayHandler = (ParameterHandler.ByteArrayHandler) map.get("[b");
                assert byteArrayHandler != null;
                byteArrayHandler.apply(array, key, bundle);
            } else if (boolean.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.BooleanHandler handler = (ParameterHandler.BooleanHandler) map.get("boolean");
                assert handler != null;
                handler.apply((Boolean) args[i], key, bundle);
            } else if (String.class.isAssignableFrom(typeClazz)) {
                ParameterHandler.StringHandler handler = (ParameterHandler.StringHandler) map.get("string");
                assert handler != null;
                handler.apply((String) args[i], key, bundle);
            } else if (Parcelable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get("parcelable");
                assert handler != null;
                handler.apply((Parcelable) args[i], key, bundle);
            } else if (Serializable.class.isAssignableFrom((typeClazz))) {
                ParameterHandler.SerializableHandler handler = (ParameterHandler.SerializableHandler) map.get("serializable");
                assert handler != null;
                handler.apply((Serializable) args[i], key, bundle);
            }
        }
    }


    protected String formatKeyValue(int key) {
        return String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
    }

    protected String formatKeyType(int key) {
        return String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
    }

    public static class IntHandler extends ParameterHandler<Integer> {
        @Override
        public void apply(Integer value, int key, Bundle bundle) {
            bundle.putInt(formatKeyValue(key), value);
            bundle.putString(formatKeyType(key), "int");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(int.class, bundle.getInt(formatKeyValue(key)));
        }
    }

    public static class DoubleHandler extends ParameterHandler<Double> {
        @Override
        public void apply(Double value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putDouble(keyValue, value);
            bundle.putString(keyType, "double");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(double.class, bundle.getDouble(formatKeyValue(key)));
        }
    }

    public static class LongHandler extends ParameterHandler<Long> {
        @Override
        public void apply(Long value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putLong(keyValue, value);
            bundle.putString(keyType, "long");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(long.class, bundle.getLong(formatKeyValue(key)));
        }
    }

    public static class ShortHandler extends ParameterHandler<Short> {
        @Override
        public void apply(Short value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putShort(keyValue, value);
            bundle.putString(keyType, "short");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(short.class, bundle.getShort(formatKeyValue(key)));
        }
    }

    public static class FloatHandler extends ParameterHandler<Float> {
        @Override
        public void apply(Float value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putFloat(keyValue, value);
            bundle.putString(keyType, "float");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(float.class, bundle.getFloat(formatKeyValue(key)));
        }
    }

    public static class ByteHandler extends ParameterHandler<Byte> {
        @Override
        public void apply(Byte value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putByte(keyValue, value);
            bundle.putString(keyType, "byte");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(byte
                    .class, bundle.getByte(formatKeyValue(key), (byte) 0));
        }
    }

    public static class ByteArrayHandler extends ParameterHandler<Object> {

        @Override
        public void apply(Object value, int key, Bundle bundle) {

            if (value instanceof Byte[]) {
                byte[] array = Utils.toPrimitives((Byte[]) value);
                value = array;
            }

            if (value instanceof byte[]) {
                byte[] array = (byte[]) value;
                if (array.length > 8 * 1024) {
                    String keyLength = key + PIGEON_KEY_ARRAY_LENGTH;
                    ParcelFileDescriptor parcelFileDescriptor = Ashmem.byteArrayToFileDescriptor(array);
                    bundle.putInt(keyLength, array.length);
                    ParameterHandler.ParcelableHandler handler = (ParameterHandler.ParcelableHandler) map.get(Parcelable.class);
                    assert handler != null;
                    handler.apply(parcelFileDescriptor, key, bundle);
                } else {
                    String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
                    String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
                    bundle.putByteArray(keyValue, (byte[]) value);
                    bundle.putString(keyType, "[b");
                }
            }
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(byte[]
                    .class, bundle.getByteArray(formatKeyValue(key)));
        }
    }

    public static class BooleanHandler extends ParameterHandler<Boolean> {
        @Override
        public void apply(Boolean value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putBoolean(keyValue, value);
            bundle.putString(keyType, "boolean");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(boolean
                    .class, bundle.getBoolean(formatKeyValue(key)));
        }
    }

    public static class StringHandler extends ParameterHandler<String> {
        @Override
        public void apply(String value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putString(keyValue, value);
            bundle.putString(keyType, "string");
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return new android.util.Pair<Class<?>, Object>(String
                    .class, bundle.getString(formatKeyValue(key)));
        }
    }

    public static class ParcelableHandler extends ParameterHandler<Parcelable> {
        @Override
        public void apply(Parcelable value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putString(keyType, "parcelable");
            bundle.putParcelable(keyValue, new Pair.PairParcelable(value.getClass().getName(), value));
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int index, Bundle bundle) {
            Parcelable parcelable = bundle.getParcelable(formatKeyValue(index));
            try {
                Parcelable value = ((com.flyingpigeon.library.Pair.PairParcelable) parcelable).getValue();
                if (value instanceof ParcelFileDescriptor) {
                    String key = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, index);
                    String lengthKey = key + PIGEON_KEY_ARRAY_LENGTH;
                    int arrayLength = bundle.getInt(lengthKey);
                    ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) value;
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
                    byte[] bytes = new byte[arrayLength];
                    try {
                        fileInputStream.read(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return new android.util.Pair<Class<?>, Object>(byte[].class, bytes);
                } else {
                    return new android.util.Pair<Class<?>, Object>(Class.forName(((com.flyingpigeon.library.Pair.PairParcelable) parcelable).getKey()), ((com.flyingpigeon.library.Pair.PairParcelable) parcelable).getValue());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class ParcelFileDescriptorHandler extends ParameterHandler<ParcelFileDescriptor> {

        @Override
        public void apply(ParcelFileDescriptor value, int key, Bundle bundle) {

        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            return null;
        }
    }

    public static class SerializableHandler extends ParameterHandler<Serializable> {
        @Override
        public void apply(Serializable value, int key, Bundle bundle) {
            String keyValue = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, key);
            String keyType = String.format(Locale.ENGLISH, PIGEON_KEY_TYPE_INDEX, key);
            bundle.putString(keyType, "serializable");
            bundle.putParcelable(keyValue, new Pair.PairSerializable(value.getClass().getName(), value));
        }

        @Override
        public android.util.Pair<Class<?>, Object> map(int key, Bundle bundle) {
            try {
                Parcelable parcelable = bundle.getParcelable(formatKeyValue(key));
                assert parcelable != null;
                return new android.util.Pair<Class<?>, Object>(Class.forName(((com.flyingpigeon.library.Pair.PairSerializable) parcelable).getKey()), ((com.flyingpigeon.library.Pair.PairSerializable) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
