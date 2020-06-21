package com.flyingpigeon.library;

import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class RouteCaller implements MethodCaller {

    private final Method target;
    private final String route;
    private final Object owner;

    private Boolean isMatchParamters;
    private int parametersLength = -1;
    private int matchLength = -1;

    public RouteCaller(@NonNull Method target, @NonNull String route, @Nullable Object owner) {
        this.target = target;
        this.route = route;
        this.owner = owner;
    }

    @Override
    public Object call(Object... args) throws IllegalAccessException, InvocationTargetException {
        target.setAccessible(true);

        synchronized (route) {
            if (isMatchParamters == null) {
                Class<?>[] parameters = target.getParameterTypes();
                parametersLength = parameters.length;
                isMatchParamters = (parametersLength == 2);
                if (isMatchParamters) {
                    for (int i = 0; i < parametersLength; i++) {
                        if (!parameters[i].isAssignableFrom(Bundle.class)) {
                            isMatchParamters = false;
                            break;
                        } else {
                            matchLength += 1;
                        }
                    }
                }
            }
        }
        if (isMatchParamters) {
            return target.invoke(owner, args);
        } else {
            if (matchLength > 0) {
                Object[] parameters = new Object[parametersLength];
                System.arraycopy(args, 0, parameters, 0, matchLength);
                return target.invoke(owner, parameters);
            } else {
                return target.invoke(owner, (Object[]) null);
            }
        }
    }

}
