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
package com.flyingpigeon.library.invoker;

import android.os.Bundle;

import com.flyingpigeon.library.ClassUtil;
import com.flyingpigeon.library.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class RouteInvoker extends AbsMethodInvoker {

    private final Method target;
    private final String route;
    private final Object owner;

    private volatile boolean isMatchParamters;
    private int parametersLength = -1;
    private int matchLength = -1;

    public RouteInvoker(@NonNull Method target, @NonNull String route, @NonNull Object owner) {
        super(target);
        this.target = target;
        this.route = route;
        this.owner = owner;
        Class<?>[] parameters = target.getParameterTypes();
        parametersLength = parameters.length;
        isMatchParamters = (parametersLength == 2);
        for (int i = 0; i < parametersLength; i++) {
            if (!parameters[i].isAssignableFrom(Bundle.class)) {
                isMatchParamters = false;
                break;
            } else {
                matchLength += 1;
            }
        }
    }

    @Override
    public Object invoke(Object... args) throws IllegalAccessException, InvocationTargetException {
        target.setAccessible(true);

        if (isMatchParamters) {
            if (args.length == 2 && args[0] instanceof Bundle && args[1] instanceof Bundle) {
                return super.invoke(owner, args);
            } else {
                Object[] parameters = new Object[2];
                if (args[0] instanceof Bundle) {
                    parameters[0] = args[0];
                } else {
                    parameters[0] = new Bundle();
                }

                if (args[1] instanceof Bundle) {
                    parameters[1] = args[1];
                } else {
                    parameters[1] = new Bundle();
                }
                return super.invoke(owner, parameters);
            }
        } else {
            if (parametersLength == 0) {
                return super.invoke(owner, (Object[]) null);
            }
            Object[] parameters = new Object[parametersLength];
            Type[] types = target.getGenericParameterTypes();
            for (int i = 0; i < parametersLength; i++) {
                if (i < args.length && args[i] instanceof Bundle) {
                    parameters[i] = args[i];
                } else {
                    parameters[i] = Utils.getBasedata(ClassUtil.getRawType(types[i]));
                }
            }
            return invoke(owner, parameters);
        }
    }

}
