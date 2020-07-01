package com.flyingpigeon.sample;

import android.app.Application;

/**
 * @author xiaozhongcen
 * @date 20-7-1
 * @since 1.0.0
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RemoteService.startService(this.getApplicationContext());

    }
}
