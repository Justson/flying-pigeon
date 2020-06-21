package com.flyingpigeon.sample;

import android.util.Log;

import com.flyingpigeon.library.ServiceContentProvider;
import com.flyingpigeon.library.ServiceManager;

/**
 * @author cenxiaozhong
 * @date 2020/6/21
 * @since 1.0.0
 */
public class MainProcessApi extends ServiceContentProvider implements MainProcessService {
    @Override
    public boolean onCreate() {
        ServiceManager.getInstance().publish(this);
        return super.onCreate();
    }

    @Override
    public void login(String username, String password) {
        Log.e(TAG, "login:" + username + " password:" + password);
    }
}
