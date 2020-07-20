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
public class RouteRequestLargeInvoker implements MethodInvoker {

    private final Method target;
    private final String route;
    private final Object owner;
    private int parametersLength = -1;
    private static final String TAG = Config.PREFIX + RouteRequestLargeInvoker.class.getSimpleName();

    public RouteRequestLargeInvoker(@NonNull Method target, @NonNull String route, @NonNull Object owner) {
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
        return target.invoke(owner, args);
    }

}
