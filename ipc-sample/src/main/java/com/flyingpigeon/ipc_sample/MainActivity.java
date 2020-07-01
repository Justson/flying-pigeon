package com.flyingpigeon.ipc_sample;

import android.os.Bundle;
import android.widget.TextView;

import com.flyingpigeon.library.ServiceManager;
import com.flyingpigeon.library.annotations.route;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView ipcLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipcLabel = this.findViewById(R.id.ipcLabel);
        ServiceManager.getInstance().publish(this);
    }

    @route("/query/username")
    public void queryUsername(Bundle in, Bundle out) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ipcLabel.setText("queryUsername");
            }
        });
        out.putString("username", "ipc-sample");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceManager.getInstance().unpublish(this);
    }
}
