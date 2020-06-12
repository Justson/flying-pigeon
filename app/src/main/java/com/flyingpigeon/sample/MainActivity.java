package com.flyingpigeon.sample;

import android.os.Bundle;
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

        Pigeon pigeon = Pigeon.newBuilder(this).setService(MyService.class).build();
        MainService mainService = pigeon.create(MainService.class);
//        mainService.queryTest(1);
//        Short aShort = 1;
//        byte aByte = 10;
//        mainService.queryItems(UUID.randomUUID().hashCode(), 0.001D, SystemClock.elapsedRealtime(), aShort, 0.011F, aByte, true);
//        Information information = new Information("Justson", "just", 110, (short) 1, 'c', 1.22F, (byte) 14, 8989123.111D, 100000L);
//        mainService.submitInformation(UUID.randomUUID().toString(), 123144231, information);


        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);
        int posterId = mainService.createPoster(poster);
        Log.e(TAG, "posterId:" + posterId);

        Poster resultPoster = mainService.queryPoster(UUID.randomUUID().toString());
        Log.e(TAG, "resultPoster:" + GsonUtils.toJson(resultPoster));

        testReturn(mainService);
    }

    private void testReturn(MainService mainService) {
        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);

        Log.e(TAG, "int:" + mainService.createPoster(poster) + " double:" + mainService.testDouble() + " long:" + mainService.testLong() + " short:" + mainService.testShort() + " float:" + mainService.testFloat() + " byte:" + mainService.testByte() + " boolean:" + mainService.testBoolean() + " testParcelable:" + GsonUtils.toJson(mainService.testParcelable()));
    }
}
