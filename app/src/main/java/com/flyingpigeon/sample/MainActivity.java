package com.flyingpigeon.sample;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyingpigeon.library.Config;
import com.flyingpigeon.library.Pigeon;
import com.flyingpigeon.library.ServiceManager;
import com.flyingpigeon.library.annotations.Route;
import com.flyingpigeon.library.annotations.thread.MainThread;

import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Config.PREFIX + MainActivity.class.getSimpleName();

    Handler mHandler = new Handler(Looper.getMainLooper());
    TextView appName;
    private Pigeon mPigeon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.appName = this.findViewById(R.id.appName);


        Log.e(TAG, "MainActivity");
        mPigeon = Pigeon.newBuilder(this).setAuthority(ServiceApiImpl.class).build();

        Short aShort = 1;
        byte aByte = 10;
        final IServiceApi serviceApi = mPigeon.create(IServiceApi.class);
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
//
//
//        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);
//        int posterId = serviceApi.createPoster(poster);
//        Log.e(TAG, "posterId:" + posterId);
//
////        Log.e(TAG, "returnResult:" + returnResult);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                test(pigeon);
//                pigeon.route("/words").withString("name", "Justson").fly();
//                pigeon.route("/hello").with(new Bundle()).fly();
//                pigeon.route("/world2").fly();
////                pigeon.route("/world/error").fly();
//                pigeon.route("/submit/bitmap", UUID.randomUUID().toString(), new byte[1024 * 1000 * 3], 1200).resquestLarge().fly();
//
//                Integer resquestLargeResult = (Integer) pigeon.route("/submit/bitmap2", UUID.randomUUID().toString(), new byte[1024 * 1000 * 3], 1200).resquestLarge().fly();
//                Log.e(TAG, "resquestLargeResult:" + resquestLargeResult);
//                byte[] data = (byte[]) pigeon.route("/query/bitmap", "girl.jpg", 5555).responseLarge().fly();
//                if (null != data) {
//                    //Arrays.toString(data)
//                    Log.e(TAG, "data length:" + data.length);
//                } else {
//                    Log.e(TAG, "data is null.");
//                }
//
//                AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < 1; i++) {
//                            SystemClock.sleep(50);
//                            String returnResult = serviceApi.testLargeBlock("hello,worlds ", new byte[1024 * 1024 * 3]);
//                            Log.e(TAG, "returnResult:" + returnResult);
//                        }
//                    }
//                });
//            }
//        }, 400);
//
//        // 跨应用通信
//        this.findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Pigeon flyPigeon = Pigeon.newBuilder(MainActivity.this).setAuthority("com.flyingpigeon.ipc_sample").build();
//                flyPigeon.route("/submit/bitmap", "submit-bitmap", new byte[1024]).resquestLarge().fly();
//                Bundle bundle = flyPigeon.route("/query/username").withString("userid", UUID.randomUUID().toString()).fly();
//                if (bundle != null) {
//                    Log.e(TAG, "bundle:" + bundle.toString());
//                    appName.setText(bundle.getString("username"));
//                } else {
//                    Log.e(TAG, "bundle == null");
//                }
//            }
//        });
//
//        ServiceManager.getInstance().publish(this);

        this.findViewById(R.id.benchmark)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                            @Override
                            public void run() {
                                runBenchmark();
                            }
                        });
                    }
                });

    }


    private void runBenchmark() {
        IMyAidlInterface iMyAidlInterface = RemoteService.mIMyAidlInterface;

//        if (iMyAidlInterface == null) {
//            Log.e(TAG, "error , iMyAidlInterface==null");
//            return;
//        }
//        long start = SystemClock.elapsedRealtime();
//        for (int i = 0; i < 1000; i++) {
//            try {
//                int result = iMyAidlInterface.basicTypes(1000, 100000L, false, 0.00002F, 1273891.938120D, "Tests at the age of seven provide a benchmark against which the child's progress at school can be measured.");
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("Benchmark,aidl method , used time:" + (SystemClock.elapsedRealtime() - start));


        IMyAidlInterface remoteIMyAidlInterface = mPigeon.create(IMyAidlInterface.class);
        for (int i = 0; i < 10; i++) {
            long start0 = SystemClock.elapsedRealtime();

            try {
                int result = remoteIMyAidlInterface.basicTypes(1000, 100000L, false, 0.00002F, 1273891.938120D, "Tests at the age of seven provide a benchmark against which the child's progress at school can be measured.");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            System.out.println("Benchmark,Pigeon method , used time:" + (SystemClock.elapsedRealtime() - start0));

        }

        Uri tools = Uri.parse("content://com.flyingpigeon.sample.tools");
        ContentResolver contentResolver = this.getContentResolver();
        for (int i = 0; i < 10; i++) {
            long start1 = SystemClock.elapsedRealtime();
            try {
                Bundle bundle = new Bundle();
                bundle.putString("a", "Tests at the age of seven provide a benchmark against which the child's progress at school can be measured.");
                bundle.putInt("b", 1000000);
                contentResolver.call(tools, "basicTypes", null, new Bundle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Benchmark,content provider method , used time:" + (SystemClock.elapsedRealtime() - start1));
        }

    }

    @MainThread
    @Route("/show/myapp/name")
    public void showMyAppName(final Bundle in, Bundle out) {
        out.putString("name", "fly-pigeon");
        String name = in.getString("name");
        appName.setText(name);
        Log.e(TAG, "current Thread:" + Thread.currentThread().getName());
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

    private void testReturn(IServiceApi serviceApi) {
        Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);

//        Log.e(TAG, "int:" + serviceApi.createPoster(poster) + " double:" + serviceApi.testDouble() + " long:" + serviceApi.testLong() + " short:" + serviceApi.testShort() + " float:" + serviceApi.testFloat() + " byte:" + serviceApi.testByte() + " boolean:" + serviceApi.testBoolean() + " testParcelable:" + GsonUtils.toJson(serviceApi.testParcelable()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceManager.getInstance().unpublish(this);
    }
}
