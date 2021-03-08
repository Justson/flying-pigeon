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
import android.text.TextUtils;

import com.flyingpigeon.library.Pair;
import com.flyingpigeon.library.ParameterHandler;
import com.flyingpigeon.library.Utils;

import java.lang.reflect.Type;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_METHOD;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LOOK_UP_APPROACH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_TYPE_INDEX;
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


        ParameterHandler.assemble(args, types, bundle);

        bundle.putInt(PIGEON_KEY_LENGTH, types.length);
        bundle.putInt(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_METHOD);

        // build response type;
        int responseKey = -1;
        Object returnValue = Utils.getBasedata((Class<?>) returnType);
        if (returnValue != null) {
            ParameterHandler<Object> parameterHandler = map.get(Utils.typeToString(returnType));
            if (parameterHandler != null) {
                parameterHandler.apply(returnValue, -1, bundle);
            }
        }
//        Type returnType = method.getGenericReturnType();

        return bundle;
    }


    @Override
    public Object unboxing(Bundle bundle) {
        bundle.setClassLoader(Pair.class.getClassLoader());
        Parcelable parcelable = null;

        String type = bundle.getString(String.format(PIGEON_KEY_TYPE_INDEX, -1));

        if (TextUtils.isEmpty(type)) {
            return null;
        }
        ParameterHandler parameterHandler = map.get(type);
        if (parameterHandler == null) {
            return null;
        }
        android.util.Pair<Class<?>, Object> pair = parameterHandler.map(-1, bundle);
        if (pair == null) {
            return null;
        }
        return pair.second;
    }
}
