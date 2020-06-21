package com.flyingpigeon.sample;

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

        RemoteService.startService(this);

        Log.e(TAG, "MainActivity");
        final Pigeon pigeon = Pigeon.newBuilder(this).setAuthority(TestServiceApi.class).build();

        Short aShort = 1;
        byte aByte = 10;
        ServiceApi serviceApi = pigeon.create(ServiceApi.class);
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


        serviceApi.testLargeBlock("hello,worlds", " new byte[1000]".getBytes());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                test(pigeon);
                pigeon.route("/words").withString("name", "Justson").fly();
                pigeon.route("/hello").with(new Bundle()).fly();
                pigeon.route("/world").fly();
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

    }

    private void testReturn(ServiceApi serviceApi) {
        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);

        Log.e(TAG, "int:" + serviceApi.createPoster(poster) + " double:" + serviceApi.testDouble() + " long:" + serviceApi.testLong() + " short:" + serviceApi.testShort() + " float:" + serviceApi.testFloat() + " byte:" + serviceApi.testByte() + " boolean:" + serviceApi.testBoolean() + " testParcelable:" + GsonUtils.toJson(serviceApi.testParcelable()));
    }
}
