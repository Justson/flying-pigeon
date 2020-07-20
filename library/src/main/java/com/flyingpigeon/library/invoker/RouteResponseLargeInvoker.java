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

import com.flyingpigeon.library.Config;
import com.flyingpigeon.library.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class RouteResponseLargeInvoker extends AbsMethodInvoker {


    public final Method target;
    private final String route;
    private final Object owner;
    private int parametersLength = -1;
    private static final String TAG = Config.PREFIX + RouteRequestLargeInvoker.class.getSimpleName();

    public RouteResponseLargeInvoker(@NonNull Method target, @NonNull String route, @NonNull Object owner) {
        super(target);
        this.target = target;
        this.route = route;
        this.owner = owner;
    }

    @Override
    public Object invoke(Object... args) throws IllegalAccessException, InvocationTargetException {
        target.setAccessible(true);
        boolean isMatchParamters = false;
        Class<?>[] parameters = target.getParameterTypes();
        if (parametersLength == -1) {
            parametersLength = parameters.length;
        }
        if (args == null) {
            args = new Object[parametersLength];
        } else {
            isMatchParamters = (parametersLength == args.length);
            if (!isMatchParamters) {
                Object[] p = new Object[parametersLength];
                System.arraycopy(args, 0, p, 0, Math.min(parametersLength, args.length));
                args = p;
            }
        }

        for (int i = 0; i < parametersLength; i++) {
            if (args[i] == null || !Utils.isAssignableFrom(parameters[i], args[i])) {
                Object o = Utils.getBasedata(parameters[i]);
                args[i] = o;
            }
        }
        return super.invoke(owner, args);
    }


}
