package com.flyingpigeon.library;

import android.os.RemoteException;

/**
 * @author ringle-android
 * @date 20-6-12
 * @since 1.0.0
 */
public class CallRemoteException extends RemoteException {

    public CallRemoteException(String message) {
        super(message);
    }
}
