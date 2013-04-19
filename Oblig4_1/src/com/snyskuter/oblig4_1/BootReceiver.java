package com.snyskuter.oblig4_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "BOOT", Toast.LENGTH_LONG).show();
		context.startService(new Intent(context, TemperatureService.class));
	}
}
