package com.flyingpigeon.sample;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author cenxiaozhong
 * @date 3/7/21
 * @since 1.0.0
 */

public class ToolsContentProvider extends ContentProvider {

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        try {
            getCallingPackage();
            RemoteService.mIMyAidlInterface.basicTypes(1000, 100000L, false, 0.00002F, 1273891.938120D, "Tests at the age of seven provide a benchmark against which the child's progress at school can be measured.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putString("result", "1");
        return bundle;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
