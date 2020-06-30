package com.flyingpigeon.library;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaozhongcen
 * @date 20-6-24
 * @since 1.0.0
 */
interface PigeonConstant {
    String PIGEON_KEY_LOOK_UP_APPROACH = "key_look_up_approach";
    String PIGEON_KEY_ROUTE = "key_path";
    int PIGEON_APPROACH_METHOD = 1;
    int PIGEON_APPROACH_ROUTE = 2;
    int PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD = 404;
    int PIGEON_RESPONSE_RESULE_LOST_CLASS = 405;
    int PIGEON_RESPONSE_RESULE_ILLEGALACCESS = 403;
    int PIGEON_RESPONSE_RESULE_SUCCESS = 200;
    String PIGEON_KEY_RESPONSE_CODE = "reponse_code";
    String PIGEON_KEY_LENGTH = "key_length";
    String PIGEON_KEY_INDEX = "key_%d";
    String PIGEON_KEY_CLASS_INDEX = "key_class_%d";
    String PIGEON_KEY_CLASS = "key_class";
    String PIGEON_KEY_TYPE = "key_type";
    String PIGEON_KEY_RESPONSE = "key_response";
    String PIGEON_KEY_FLAGS = "key_flags";
    String PIGEON_KEY_ARRAY_LENGTH = "_array_length";
    String PIGEON_KEY_RESULT = "key_result";

    static final ConcurrentHashMap<Class, ParameterHandler> map = new ConcurrentHashMap<Class, ParameterHandler>() {
        {
            put(int.class, new ParameterHandler.IntHandler());
            put(double.class, new ParameterHandler.DoubleHandler());
            put(long.class, new ParameterHandler.LongHandler());
            put(short.class, new ParameterHandler.ShortHandler());
            put(float.class, new ParameterHandler.FloatHandler());
            put(byte.class, new ParameterHandler.ByteHandler());
            put(byte[].class, new ParameterHandler.ByteArrayHandler());
            put(boolean.class, new ParameterHandler.BooleanHandler());
            put(Parcelable.class, new ParameterHandler.ParcelableHandler());
            put(Serializable.class, new ParameterHandler.SerializableHandler());
            put(String.class, new ParameterHandler.StringHandler());
        }
    };

}
