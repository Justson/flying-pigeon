package com.flyingpigeon.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.flyingpigeon.library.ServiceManager;

import androidx.annotation.Nullable;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author xiaozhongcen
 * @date 20-6-15
 * @since 1.0.0
 */
public class RemoteService extends Service implements RemoteServiceApi {

	public static final String TAG = PREFIX + RemoteService.class.getSimpleName();

	public static void startService(Context context) {
		context.startService(new Intent(context, RemoteService.class));
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ServiceManager.getInstance().publish(this);
		ServiceManager.getInstance().publish(mApi);

	}

	Api mApi = new Api() {
		@Override
		public int createPoster(Poster poster) {
			Log.e(TAG, "poster:" + GsonUtils.toJson(poster));
			return 11;
		}
	};

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void queryItems(int id, double score, long timestamp, short gender, float ring, byte b, boolean isABoy) {
		Log.e(TAG, "queryItems");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ServiceManager.getInstance().abolition(this);
	}
}
