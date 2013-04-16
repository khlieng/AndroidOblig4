package com.snyskuter.oblig4_1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class TemperatureService extends Service {
	private static TemperatureService instance;
	public static TemperatureService getInstance() {
		return instance;
	}
	private static ArrayList<String> places = new ArrayList<String>();
	
	private int updateInterval = 15 * 60000;
	private Timer updateTimer = new Timer();
	private ArrayList<TemperatureData> temperatures = new ArrayList<TemperatureData>();
	private Handler handler = new Handler();
	
	@Override
	public void onCreate() {
		instance = this;
		loadPlaces();
		
		updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateTemperatures();
			}
		}, 0, updateInterval);
		
		toast("create");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		toast("start");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		updateTimer.cancel();
		
		toast("destroy");
	}
	
	public ArrayList<TemperatureData> getTemperatures() {
		return temperatures;
	}
	
	public void setUpdateInterval(int interval) {
		updateInterval = interval;
	}
	
	public int getUpdateInterval() {
		return updateInterval;
	}
	
	private void updateTemperatures() {
		for (final String url : places) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					HTTP.GET(url, new DataRunnable<String>() {
						@Override
						public void run() {
							String xml = getData();
							
							// Parse xml og putt temperaturen i temperaturlista her
						}				
					});
				}
			});
		}
	}
	
	public static ArrayList<String> getPlaces() {
		return places;
	}
	
	public static void addPlace(String url) {
		if (!places.contains(url)) {
			places.add(url);
			savePlaces();
		}
	}
	
	public static void removePlace(String url) {
		if (places.contains(url)) {
			places.remove(url);
			savePlaces();
		}
	}
	
	private static void loadPlaces() {
		try {
			Scanner scan = new Scanner(Tools.getContext().openFileInput("trackedPlaces.txt"));
			while (scan.hasNextLine()) {
				places.add(scan.nextLine());
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void savePlaces() {
		try {
			FileOutputStream output = Tools.getContext().openFileOutput("trackedPlaces.txt", Context.MODE_PRIVATE);
			OutputStreamWriter writer = new OutputStreamWriter(output);
			
			try {
				for (String place : places) {					
					writer.write(place + "\n");
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	
	public class TemperatureData {
		private String place;
		private String url;
		private String temperature;
		
		public String getPlace() { return place; }
		public String getUrl() { return url; }
		public String getTemperature() { return temperature; }
		
		public TemperatureData(String place, String url, String temperature) {
			this.place = place;
			this.url = url;
			this.temperature = temperature;
		}
		
		public String toString() {
			return place;
		}
	}
}
