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

import com.flyingpigeon.library.annotations.thread.MainThread;
import com.flyingpigeon.library.annotations.thread.SingleThread;
import com.flyingpigeon.library.execute.Executors;
import com.queue.library.Dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author xiaozhongcen
 * @date 20-7-20
 * @since 1.0.0
 */
public abstract class AbsMethodInvoker implements MethodInvoker {

    private final Method target;
    private Dispatch mDispatch;

    public AbsMethodInvoker(Method target) {
        this.target = target;
        if (target.getAnnotation(MainThread.class) != null) {
            mDispatch = Executors.getMainThreadExecutor();
        } else if (target.getAnnotation(SingleThread.class) != null) {
            mDispatch = Executors.getSingleThreadExecutor();
        } else {
            mDispatch = null;
        }
    }


    protected Object invoke(Object owner, Object... parameters) throws InvocationTargetException, IllegalAccessException {
        return mDispatch == null ? target.invoke(owner, parameters) : invokeByDispatch(owner, parameters);
    }

    protected Object invokeByDispatch(final Object owner, final Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        final Exception[] exceptions = new Exception[1];
        Object o = mDispatch.call(new Callable<Object>() {
            @Override
            public Object call() {
                Object returnObject = null;
                try {
                    returnObject = target.invoke(owner, parameters);
                } catch (Exception exception) {
                    exceptions[0] = exception;
                }
                return returnObject;
            }
        });
        if (exceptions[0] != null) {
            if (exceptions[0] instanceof InvocationTargetException) {
                throw (InvocationTargetException) exceptions[0];
            } else if (exceptions[0] instanceof IllegalAccessException) {
                throw (IllegalAccessException) exceptions[0];
            } else {
                throw (RuntimeException) exceptions[0];
            }
        }
        return o;
    }

}
