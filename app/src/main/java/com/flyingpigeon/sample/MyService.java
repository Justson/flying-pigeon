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
    public void queryItems(int id, double score, long idcard, short gender, float ring, byte b, boolean isABoy) {
        Log.e(TAG, "queryItems method call id:" + id + " score:" + score + " idcard:" + idcard + " gender:" + gender + " ring:" + ring + " b:" + b + " isABoy:" + isABoy);
    }

    @Override
    public void submitInformation(String uuid, int hash, Information information) {
        Log.e(TAG, "Information:" + GsonUtils.toJson(information) + " uuid:" + uuid + " hash:" + hash);
    }

    @Override
    public int createPoster(Poster poster) {
        Log.e(TAG, "poster:" + GsonUtils.toJson(poster));
        return 1999;
    }
}
