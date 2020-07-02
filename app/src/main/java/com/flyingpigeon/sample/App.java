package com.flyingpigeon.sample;

import android.app.Application;
import android.content.Context;

/**
 * @author xiaozhongcen
 * @date 20-7-1
 * @since 1.0.0
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RemoteService.startService(this.getApplicationContext());

    }
}
