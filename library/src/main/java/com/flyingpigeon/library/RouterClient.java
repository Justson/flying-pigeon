package com.flyingpigeon.library;

import android.os.Bundle;

import androidx.annotation.Nullable;

import static com.flyingpigeon.library.ServiceManager.KEY_PATH;

/**
 * @author cenxiaozhong
 * @date 2020/6/21
 * @since 1.0.0
 */
public class RouterClient {

    private String path;
    private Pigeon mPigeon;

    public RouterClient(Pigeon pigeon, String path) {
        this.path = path;
        this.mPigeon = pigeon;
    }

    public Bundle fly(@Nullable Bundle in) {
        if (in == null) {
            in = new Bundle();
        }
        in.putString(KEY_PATH, path);
        return mPigeon.fly(in);
    }

    public Bundle fly() {
        return fly(null);
    }
}
