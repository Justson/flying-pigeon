package com.flyingpigeon.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.flyingpigeon.library.Config;
import com.flyingpigeon.library.Pigeon;

import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Config.PREFIX + MainActivity.class.getSimpleName();

    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RemoteService.startService(this.getApplicationContext());

        Log.e(TAG, "MainActivity");
        final Pigeon pigeon = Pigeon.newBuilder(this).setAuthority(ServiceApiImpl.class).build();

        Short aShort = 1;
        byte aByte = 10;
        final IServiceApi serviceApi = pigeon.create(IServiceApi.class);
        serviceApi.queryTest(1);
        serviceApi.queryItems(UUID.randomUUID().hashCode(), 0.001D, SystemClock.elapsedRealtime(), aShort, 0.011F, aByte, true);
        Information information = new Information("Justson", "just", 110, (short) 1, 'c', 1.22F, (byte) 14, 8989123.111D, 100000L);
        serviceApi.submitInformation(UUID.randomUUID().toString(), 123144231, information);


        Poster resultPoster = serviceApi.queryPoster(UUID.randomUUID().toString());
        Log.e(TAG, "resultPoster:" + GsonUtils.toJson(resultPoster));

        testReturn(serviceApi);
        ArrayList data = new ArrayList<String>();
        data.add("test1");
        data.add("test2");
        serviceApi.testArrayList(data);


        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);
        int posterId = serviceApi.createPoster(poster);
        Log.e(TAG, "posterId:" + posterId);

//        Log.e(TAG, "returnResult:" + returnResult);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                test(pigeon);
                pigeon.route("/words").withString("name", "Justson").fly();
                pigeon.route("/hello").with(new Bundle()).fly();
                pigeon.route("/world").fly();
//                pigeon.route("/submit/bitmap", UUID.randomUUID().toString(), new byte[1024 * 1000 * 3], 1200).resquestLarge().fly();
                byte[] data = pigeon.route("/query/bitmap", "girl.jpg", 5555).responseLarge().fly();
                if (null != data) {
                    //Arrays.toString(data)
                    Log.e(TAG, "data length:" + data.length);
                } else {
                    Log.e(TAG, "data is null.");
                }

                AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1; i++) {
                            SystemClock.sleep(50);
                            String returnResult = serviceApi.testLargeBlock("hello,worlds ", new byte[1024 * 1024 * 3]);
                            Log.e(TAG, "returnResult:" + returnResult);
                        }
                    }
                });
            }
        }, 400);
    }

    private void test(Pigeon pigeon) {
        Short aShort = 1;
        byte aByte = 10;
        Api api = pigeon.create(Api.class);
        Poster poster1 = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);
        api.createPoster(poster1);

        RemoteServiceApi remoteServiceApi = pigeon.create(RemoteServiceApi.class);
        remoteServiceApi.queryItems(UUID.randomUUID().hashCode(), 0.001D, SystemClock.elapsedRealtime(), aShort, 0.011F, aByte, true);

        byte[] bitmapData = remoteServiceApi.testLargeResponse("query Bitmap");
        if (bitmapData != null) {
            Log.e(TAG, "bitmapData:" + bitmapData.length);
        } else {
            Log.e(TAG, "bitmapData is null");
        }

    }

    private void testReturn(IServiceApi IServiceApi) {
        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);

        Log.e(TAG, "int:" + IServiceApi.createPoster(poster) + " double:" + IServiceApi.testDouble() + " long:" + IServiceApi.testLong() + " short:" + IServiceApi.testShort() + " float:" + IServiceApi.testFloat() + " byte:" + IServiceApi.testByte() + " boolean:" + IServiceApi.testBoolean() + " testParcelable:" + GsonUtils.toJson(IServiceApi.testParcelable()));
    }
}
