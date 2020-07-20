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
