package com.flyingpigeon.sample;

import android.os.Bundle;
import android.os.SystemClock;

import com.flyingpigeon.library.Pigeon;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Pigeon pigeon = Pigeon.newBuilder(this).setAuthorities("com.flyingpigeon.library").build();
        MainService mainService = pigeon.create(MainService.class);
        Short aShort = 1;
        byte aByte = 10;
        mainService.queryItems(UUID.randomUUID().hashCode(), 0.001D, SystemClock.elapsedRealtime(), aShort, 0.011F, aByte, true);

        Information information = new Information("Justson", "xiaozhongcen", 110, (short) 1, 'c', 1.22F, (byte) 14, 8989123.111D, 100000L);
        mainService.submitInformation(information, UUID.randomUUID().toString(), 123144231);
    }
}
