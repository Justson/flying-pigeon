package com.flyingpigeon.ipc_sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flyingpigeon.library.Pigeon;
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
        Button ipcBtn = this.findViewById(R.id.ipcBtn);
        ipcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pigeon pigeon = Pigeon.newBuilder(MainActivity.this).setAuthority("com.flyingpigeon.sample.main.api").build();
                Bundle bundle = pigeon.route("/show/myapp/name").withString("name", "ipc-sample").fly();
                String name = bundle.getString("name");
                ipcLabel.setText(name);
            }
        });
    }

    @route("/query/username")
    public void queryUsername(final Bundle in, final Bundle out) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ipcLabel.setText("received other app message,\n message:" + in.getString("userid"));
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
