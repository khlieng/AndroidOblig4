package com.snyskuter.oblig4_1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class TemperatureService extends Service {
	private static TemperatureService instance;
	private static ArrayList<String> places = new ArrayList<String>();
	
	public static TemperatureService getInstance() {
		return instance;
	}
	
	@Override
	public void onCreate() {
		instance = this;
		loadPlaces();
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
	
	public static void addPlace(String url) {
		if (!places.contains(url)) {
			places.add(url);
			savePlaces();
		}
	}
	
	public static void removePlace(String url) {
		places.remove(url);
	}
	
	private static void loadPlaces() {
		try {
			Scanner scan = new Scanner(instance.openFileInput("trackedPlaces.txt"));
			while (scan.hasNextLine()) {
				places.add(scan.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void savePlaces() {
		try {
			FileOutputStream output = Tools.getContext().openFileOutput("trackedPlaces.txt", Context.MODE_PRIVATE);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
