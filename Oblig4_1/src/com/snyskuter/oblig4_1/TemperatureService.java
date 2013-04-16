package com.snyskuter.oblig4_1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class TemperatureService extends Service {
	@Override
	public void onCreate() {
		toast("create");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		toast("start");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		toast("destroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
