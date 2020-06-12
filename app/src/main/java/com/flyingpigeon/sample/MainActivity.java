package com.flyingpigeon.sample;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.flyingpigeon.library.Pigeon;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Pigeon pigeon = Pigeon.newBuilder(this).setServiceApi(TestServiceApi.class).build();
        ServiceApi serviceApi = pigeon.create(ServiceApi.class);
        serviceApi.queryTest(1);
        Short aShort = 1;
        byte aByte = 10;
        serviceApi.queryItems(UUID.randomUUID().hashCode(), 0.001D, SystemClock.elapsedRealtime(), aShort, 0.011F, aByte, true);
        Information information = new Information("Justson", "just", 110, (short) 1, 'c', 1.22F, (byte) 14, 8989123.111D, 100000L);
        serviceApi.submitInformation(UUID.randomUUID().toString(), 123144231, information);


        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);
        int posterId = serviceApi.createPoster(poster);
        Log.e(TAG, "posterId:" + posterId);

        Poster resultPoster = serviceApi.queryPoster(UUID.randomUUID().toString());
        Log.e(TAG, "resultPoster:" + GsonUtils.toJson(resultPoster));

        testReturn(serviceApi);
    }

    private void testReturn(ServiceApi serviceApi) {
        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);

        Log.e(TAG, "int:" + serviceApi.createPoster(poster) + " double:" + serviceApi.testDouble() + " long:" + serviceApi.testLong() + " short:" + serviceApi.testShort() + " float:" + serviceApi.testFloat() + " byte:" + serviceApi.testByte() + " boolean:" + serviceApi.testBoolean() + " testParcelable:" + GsonUtils.toJson(serviceApi.testParcelable()));
    }
}
