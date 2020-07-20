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

import com.flyingpigeon.library.invoker.Invoker;
import com.flyingpigeon.library.invoker.MethodInvoker;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaozhongcen
 * @date 20-6-15
 * @since 1.0.0
 */
public class BuketMethod {

    private ConcurrentHashMap<String, MethodInvoker> methods;
    private Object owner;
    private Class<?> interfaceClass;

    private static final String TAG = Config.PREFIX + BuketMethod.class.getSimpleName();

    BuketMethod() {
        methods = new ConcurrentHashMap<>();
    }


    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    MethodInvoker match(String method, Class<?>[] clazzs) throws NoSuchMethodException {
        StringBuffer methodUnique = new StringBuffer();
        methodUnique.append(method);
        for (int i = 0; i < clazzs.length; i++) {
            methodUnique.append(clazzs[i].getSimpleName());
        }
        MethodInvoker target = methods.get(methodUnique.toString());
        if (target == null) {
            Method m = owner.getClass().getDeclaredMethod(method, clazzs);
            target = new Invoker(m, "", owner);
            methods.put(methodUnique.toString(), target);
        }
        return target;
    }

}
