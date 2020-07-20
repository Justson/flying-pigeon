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

import android.os.Parcelable;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaozhongcen
 * @date 20-6-24
 * @since 1.0.0
 */
public interface PigeonConstant {
    String PIGEON_KEY_LOOK_UP_APPROACH = "key_look_up_approach";
    String PIGEON_KEY_ROUTE = "key_path";
    int PIGEON_APPROACH_METHOD = 1;
    int PIGEON_APPROACH_ROUTE = 2;
    int PIGEON_RESPONSE_RESULE_NO_SUCH_METHOD = 404;
    int PIGEON_RESPONSE_RESULE_LOST_CLASS = 405;
    int PIGEON_RESPONSE_RESULE_ILLEGALACCESS = 403;
    int PIGEON_RESPONSE_RESULE_NOT_FOUND_ROUTE = 402;
    int PIGEON_RESPONSE_RESULE_REMOTE_EXCEPTION = 500;
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
    String PIGEON_KEY_CALLING_PACKAGE = "key_calling_package";
    String PIGEON_PATH_START = "pigeon";
    String PIGEON_PATH_SEGMENT_METHOD = "10";
    String PIGEON_PATH_SEGMENT_ROUTE = "11";


    ConcurrentHashMap<Class, ParameterHandler> map = new ConcurrentHashMap<Class, ParameterHandler>() {
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
