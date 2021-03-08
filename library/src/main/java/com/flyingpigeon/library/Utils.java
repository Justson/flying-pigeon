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
import android.text.TextUtils;
import android.util.Pair;

import java.io.Serializable;
import java.lang.reflect.Type;

import static com.flyingpigeon.library.PigeonConstant.map;


/**
 * @author xiaozhongcen
 * @date 20-6-22
 * @since 1.0.0
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static byte[] toPrimitives(Byte[] oBytes) {
        if (oBytes == null || oBytes.length <= 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            if (oBytes[i] == null) {
                continue;
            }
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    public static Object getBasedata(Class<?> clazz) {
        if (int.class.isAssignableFrom(clazz)) {
            return 0;
        } else if (double.class.isAssignableFrom(clazz)) {
            return 0.0D;
        } else if (long.class.isAssignableFrom(clazz)) {
            return 0;
        } else if (short.class.isAssignableFrom(clazz)) {
            return 0;
        } else if (float.class.isAssignableFrom(clazz)) {
            return 0.0F;
        } else if (byte.class.isAssignableFrom(clazz)) {
            return 0;
        } else if (boolean.class.isAssignableFrom(clazz)) {
            return Boolean.FALSE;
        } else if (char.class.isAssignableFrom(clazz)) {
            return '0';
        } else if (byte[].class.isAssignableFrom(clazz)) {
            return new byte[0];
        } else {
            return null;
        }
    }

    public static Class<?> getClass(String clazz) throws ClassNotFoundException {
        if (clazz.startsWith("int")) {
            return int.class;
        } else if (clazz.startsWith("double")) {
            return double.class;
        } else if (clazz.startsWith("long")) {
            return long.class;
        } else if (clazz.startsWith("short")) {
            return short.class;
        } else if (clazz.startsWith("float")) {
            return float.class;
        } else if (clazz.startsWith("byte")) {
            return byte.class;
        } else if (clazz.startsWith("boolean")) {
            return boolean.class;
        } else {
            return Class.forName(clazz);
        }
    }

    public static boolean isAssignableFrom(Class<?> parameter, Object arg) {
        if (parameter.isAssignableFrom(arg.getClass())) {
            return true;
        }
        if (int.class.isAssignableFrom(parameter)) {
            return arg instanceof Integer;
        } else if (double.class.isAssignableFrom(parameter)) {
            return arg instanceof Double;
        } else if (long.class.isAssignableFrom(parameter)) {
            return arg instanceof Long;
        } else if (short.class.isAssignableFrom(parameter)) {
            return arg instanceof Short;
        } else if (float.class.isAssignableFrom(parameter)) {
            return arg instanceof Float;
        } else if (byte.class.isAssignableFrom(parameter)) {
            return arg instanceof Byte;
        } else if (boolean.class.isAssignableFrom(parameter)) {
            return arg instanceof Boolean;
        } else {
            return false;
        }
    }

    public static Pair<Class<?>, Object> getValue(String type, String param) throws ClassNotFoundException, IllegalArgumentException {
        Class<?> clazz;
        Object value = null;
        if (type.startsWith("java.lang")) {
            clazz = Class.forName(type);
            if (type.endsWith("String")) {
                value = String.valueOf(param);
            } else if (type.endsWith("Integer")) {
                value = Integer.valueOf(param);
            } else if (type.endsWith("Double")) {
                value = Double.valueOf(param);
            } else if (type.endsWith("Long")) {
                value = Long.valueOf(param);
            } else if (type.endsWith("Short")) {
                value = Short.valueOf(param);
            } else if (type.endsWith("Float")) {
                value = Float.valueOf(param);
            } else if (type.endsWith("Byte")) {
                value = Byte.valueOf(param);
            } else if (type.endsWith("Boolean")) {
                value = Boolean.valueOf(param);
            } else if (type.endsWith("Character")) {
                value = param.toCharArray()[0];
            }
        } else {
            clazz = null;
            if (type.startsWith("int")) {
                clazz = int.class;
                value = Integer.valueOf(param);
            } else if (type.startsWith("double")) {
                clazz = double.class;
                value = Double.valueOf(param);
            } else if (type.startsWith("long")) {
                clazz = long.class;
                value = Long.valueOf(param);
            } else if (type.startsWith("short")) {
                clazz = short.class;
                value = Short.valueOf(param);
            } else if (type.startsWith("float")) {
                clazz = float.class;
                value = Float.valueOf(param);
            } else if (type.startsWith("byte")) {
                clazz = byte.class;
                value = Byte.valueOf(param);
            } else if (type.startsWith("boolean")) {
                clazz = boolean.class;
                value = Boolean.valueOf(param);
            } else {
                throw new IllegalArgumentException();
            }
        }
        return new Pair<Class<?>, Object>(clazz, value);

    }

    static Object[] getValues(String[] types, String[] params) throws ClassNotFoundException {
        Object[] values = new Object[params.length];
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            String param = params[i];
            Pair<Class<?>, Object> pair = getValue(type, param);
            values[i] = pair.second;
        }
        return values;
    }

    static Class<?>[] getClazz(String[] types, String[] params) throws ClassNotFoundException {
        Class<?>[] values = new Class<?>[params.length];
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            String param = params[i];
            Pair<Class<?>, Object> pair = getValue(type, param);
            values[i] = pair.first;
        }
        return values;
    }

    static void typeConvert(Type returnType, Bundle bundle) {
        if (int.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "int");
        } else if (double.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "double");
        } else if (long.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "long");
        } else if (short.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "short");
        } else if (float.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "float");
        } else if (byte.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "byte");
        } else if (boolean.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "boolean");
        } else if (String.class.isAssignableFrom((Class<?>) returnType)) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "string");
        } else if (Parcelable.class.isAssignableFrom(((Class<?>) returnType))) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "parcelable");
        } else if (Serializable.class.isAssignableFrom(((Class<?>) returnType))) {
            bundle.putString(PigeonConstant.PIGEON_KEY_RESULT, "perializable");
        } else if (Void.class.isAssignableFrom(((Class<?>) returnType))) {
        }
    }


    public static String typeToString(Type type) {
        if (int.class.isAssignableFrom((Class<?>) type)) {
            return "int";
        } else if (double.class.isAssignableFrom((Class<?>) type)) {
            return "double";
        } else if (long.class.isAssignableFrom((Class<?>) type)) {
            return "long";
        } else if (short.class.isAssignableFrom((Class<?>) type)) {
            return "short";
        } else if (float.class.isAssignableFrom((Class<?>) type)) {
            return "float";
        } else if (byte.class.isAssignableFrom((Class<?>) type)) {
            return "byte";
        } else if (boolean.class.isAssignableFrom((Class<?>) type)) {
            return "boolean";
        } else if (byte[].class.isAssignableFrom((Class<?>) type)) {
            return "[b";
        } else if (Byte[].class.isAssignableFrom((Class<?>) type)) {
            return "[b";
        } else if (String.class.isAssignableFrom((Class<?>) type)) {
            return "string";
        } else if (Parcelable.class.isAssignableFrom(((Class<?>) type))) {
            return "parcelable";
        } else if (Serializable.class.isAssignableFrom(((Class<?>) type))) {
            return "serializable";
        } else if (Void.class.isAssignableFrom(((Class<?>) type))) {
        }
        return "";
    }


    public static void convert(Bundle bundle, Type type, Object arg) {
        Class<?> typeClazz = ClassUtil.getRawType(type);
        String typeString = typeToString(typeClazz);
        if (TextUtils.isEmpty(typeString)) {
            return;
        }
        ParameterHandler parameterHandler = map.get(typeString);
        if (parameterHandler == null) {
            return;
        }
        parameterHandler.apply(arg, -1, bundle);
    }


    public static Object parcelableValueOut(Parcelable parcelable) {
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
            return ((com.flyingpigeon.library.Pair.PairString) parcelable).getValue();
        } else if (parcelable instanceof com.flyingpigeon.library.Pair.PairSerializable) {
            return ((com.flyingpigeon.library.Pair.PairSerializable) parcelable).getValue();
        } else {
            return ((com.flyingpigeon.library.Pair.PairParcelable) parcelable).getValue();
        }
    }

}
