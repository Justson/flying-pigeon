package com.flyingpigeon.sample;

import android.os.Bundle;

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
        mainService.queryItems(UUID.randomUUID().hashCode());
    }
}
