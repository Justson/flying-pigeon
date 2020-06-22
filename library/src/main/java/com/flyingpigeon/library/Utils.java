package com.flyingpigeon.library;

/**
 * @author xiaozhongcen
 * @date 20-6-22
 * @since 1.0.0
 */
public class Utils {


    static byte[] toPrimitives(Byte[] oBytes) {
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

    static Object getBasedata(Class<?> clazz) {
        if (int.class.isAssignableFrom(clazz)) {
            return 0;
        } else if (double.class.isAssignableFrom(clazz)) {
            return 0.0D;
        } else if (long.class.isAssignableFrom(clazz)) {
            return 0.0D;
        } else if (short.class.isAssignableFrom(clazz)) {
            return 0.0D;
        } else if (float.class.isAssignableFrom(clazz)) {
            return 0.0D;
        } else if (byte.class.isAssignableFrom(clazz)) {
            return 0.0D;
        } else if (boolean.class.isAssignableFrom(clazz)) {
            return 0.0D;
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
}
