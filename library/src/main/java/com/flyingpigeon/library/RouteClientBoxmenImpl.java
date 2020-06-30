package com.flyingpigeon.library;

import android.os.Bundle;
import android.os.Parcelable;

import java.lang.reflect.Type;
import java.util.Locale;

import static com.flyingpigeon.library.PigeonConstant.PIGEON_APPROACH_ROUTE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_CLASS_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_INDEX;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LENGTH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_LOOK_UP_APPROACH;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_RESPONSE;
import static com.flyingpigeon.library.PigeonConstant.PIGEON_KEY_ROUTE;

/**
 * @author xiaozhongcen
 * @date 20-6-30
 * @since 1.0.0
 */
public class RouteClientBoxmenImpl implements RouteClientBoxmen<Bundle, Object> {
    @Override
    public Bundle boxing(String route, Object[] params) {
        Type[] types = new Type[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                continue;
            }
            types[i] = params[i].getClass();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(PIGEON_KEY_LENGTH, params.length);
        bundle.putString(PIGEON_KEY_ROUTE, route);
        bundle.putInt(PIGEON_KEY_LOOK_UP_APPROACH, PIGEON_APPROACH_ROUTE);
        settingValues0(params, bundle, types);
        return bundle;
    }

    private void settingValues0(Object[] args, Bundle bundle, Type[] types) {
        for (int i = 0; i < args.length; i++) {
            String index = String.format(Locale.ENGLISH, PIGEON_KEY_INDEX, i);
            String indexClass = String.format(Locale.ENGLISH, PIGEON_KEY_CLASS_INDEX, i);
            if (types[i] == null) {
                bundle.putString(index, "");
                bundle.putString(indexClass, "null");
                continue;
            }
            Class<?> typeClazz = ClassUtil.getRawType(types[i]);
            Utils.convert(index, bundle, typeClazz, args[i]);
        }
    }

    @Override
    public Object unboxing(Bundle bundle) {
        Parcelable parcelable = bundle.getParcelable(PIGEON_KEY_RESPONSE);
        if (parcelable == null) {
            return null;
        }
        return Utils.parcelableValueOut(parcelable);
    }
}
