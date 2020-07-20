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
import android.util.Pair;

import com.flyingpigeon.library.log.FlyPigeonLog;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ARRAY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE_CODE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_RESPONSE_RESULE_SUCCESS;

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
        for (int i = 0; i < length; i++) {
            String index = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
            Parcelable parcelable = bundle.getParcelable(index);
            if (parcelable == null) {
                break;
            }
            android.util.Pair<Class<?>, Object> data = parcelableToClazz(parcelable, index, bundle);
            assert data != null;
            clazzs[i] = data.first;
            values[i] = data.second;
        }
        for (int i = 0; i < length; i++) {
            if (clazzs[i] == null) {
                throw new IllegalArgumentException("arg error");
            }
        }
        return new Pair<>(clazzs, values);
    }


    private android.util.Pair<Class<?>, Object> parcelableToClazz(Parcelable parcelable, String index, Bundle extras) {
        if (parcelable instanceof com.flyingpigeon.library.Pair.PairInt) {
            return new android.util.Pair<Class<?>, Object>(int.class, ((com.flyingpigeon.library.Pair.PairInt) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairDouble) {
            return new android.util.Pair<Class<?>, Object>(double.class, ((com.flyingpigeon.library.Pair.PairDouble) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairLong) {
            return new android.util.Pair<Class<?>, Object>(long.class, ((com.flyingpigeon.library.Pair.PairLong) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairShort) {
            return new android.util.Pair<Class<?>, Object>(short.class, ((com.flyingpigeon.library.Pair.PairShort) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairFloat) {
            return new android.util.Pair<Class<?>, Object>(float.class, ((com.flyingpigeon.library.Pair.PairFloat) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByte) {
            return new android.util.Pair<Class<?>, Object>(byte.class, ((com.flyingpigeon.library.Pair.PairByte) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairBoolean) {
            return new android.util.Pair<Class<?>, Object>(boolean.class, ((com.flyingpigeon.library.Pair.PairBoolean) parcelable).isValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairString) {
            try {
                return new android.util.Pair<Class<?>, Object>(Class.forName(((com.flyingpigeon.library.Pair.PairString) parcelable).getKey()), ((com.flyingpigeon.library.Pair.PairString) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairByteArray) {
            return new android.util.Pair<Class<?>, Object>(byte[].class, ((com.flyingpigeon.library.Pair.PairByteArray) parcelable).getValue());
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            try {
                return new android.util.Pair<Class<?>, Object>(Class.forName(((com.flyingpigeon.library.Pair.PairSerializable) parcelable).getKey()), ((com.flyingpigeon.library.Pair.PairSerializable) parcelable).getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                Parcelable value = ((com.flyingpigeon.library.Pair.PairParcelable) parcelable).getValue();
                if (value instanceof ParcelFileDescriptor) {
                    String lengthKey = index + PIGEON_KEY_ARRAY_LENGTH;
                    int arrayLength = extras.getInt(lengthKey);
                    FlyPigeonLog.e(TAG, "keyLength:" + arrayLength + " lengthKey:" + lengthKey);
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


    @Override
    public void boxing(Bundle in, Bundle out, Object result) {
        Parcelable parcelable = in.getParcelable(PIGEON_KEY_RESPONSE);
        if (parcelable != null) {
            parcelableValueIn(parcelable, result);
            out.putParcelable(PIGEON_KEY_RESPONSE, parcelable);
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
