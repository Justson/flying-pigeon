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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class Invoker extends AbsMethodInvoker {

    private Method target;
    private String route;
    private Object owner;

    public Invoker(@NonNull Method target, @Nullable String route, @Nullable Object owner) {
        super(target);
        this.target = target;
        this.route = route;
        this.owner = owner;
    }

    @Override
    public Object invoke(Object... arg) throws IllegalAccessException, InvocationTargetException {
        target.setAccessible(true);
        return super.invoke(owner, arg);
    }

}
