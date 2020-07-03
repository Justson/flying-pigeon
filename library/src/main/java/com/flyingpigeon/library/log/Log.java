package com.flyingpigeon.library.log;

import com.flyingpigeon.library.BuildConfig;

/**
 * @author xiaozhongcen
 * @date 20-7-3
 * @since 1.0.0
 */
public class Log {

    public static boolean DEBUG = BuildConfig.DEBUG;

    private Log() {
    }

    static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }
}
