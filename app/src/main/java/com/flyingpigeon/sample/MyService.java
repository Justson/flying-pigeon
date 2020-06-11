package com.flyingpigeon.sample;

import android.util.Log;

import com.flyingpigeon.library.ServiceContentProvider;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author ringle-android
 * @date 20-6-10
 * @since 1.0.0
 */
public class MyService extends ServiceContentProvider implements MainService {
    private static final String TAG = PREFIX + MyService.class.getSimpleName();

    @Override
    public void queryItems(int id) {
        Log.e(TAG, "queryItems method call id:" + id);
    }
}
