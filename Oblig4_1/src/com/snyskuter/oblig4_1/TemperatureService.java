package com.snyskuter.oblig4_1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

public class TemperatureService extends Service {
	private static TemperatureService instance;

	private static final int MY_NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	private Notification notification;
	
	private final String myBlog = "http://hin.no";

	public static TemperatureService getInstance() {
		return instance;
	}

	private static ArrayList<String> places;

	private int updateInterval = 15 * 60000;
	private Timer updateTimer = new Timer();
	private TimerTask updateTask;
	private ArrayList<TemperatureData> temperatures;
	private static ArrayList<Runnable> updateListeners = new ArrayList<Runnable>();
	private Handler handler = new Handler();
	
	View view;
	@Override
	public void onCreate() {
		instance = this;
		places = new ArrayList<String>();
		temperatures = new ArrayList<TemperatureData>();
		loadPlaces();

		startUpdateTimer();

		toast("create");
		notiFyMe(view);

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
		updateInterval = interval * 60000;
		updateTask.cancel();
		startUpdateTimer();
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public static void addUpdateListener(Runnable r) {
		updateListeners.add(r);
	}

	private void startUpdateTimer() {
		updateTask = new TimerTask() {
			@Override
			public void run() {
				updateTemperatures();
			}
		};
		updateTimer.schedule(updateTask, 0, updateInterval);
	}

	private void updateTemperatures() {
		temperatures.clear();
		for (final String url : places) {
			updateTemperature(url, false);
		}
		updateFinished();
	}

	private void updateTemperature(String url, boolean notifyListeners) {
		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = fac.newDocumentBuilder();
			Document doc = builder.parse(url);
			doc.getDocumentElement().normalize();
			Element e = (Element) doc.getElementsByTagName("weatherstation")
					.item(0);
			String place = ((Element) doc.getElementsByTagName("name").item(0))
					.getTextContent();
			String temperature = e.getElementsByTagName("temperature").item(0)
					.getAttributes().getNamedItem("value").getNodeValue();

			temperatures.add(new TemperatureData(place, url, temperature));
		} catch (Exception e) {
		}

		if (notifyListeners) {
			updateFinished();
		}
	}

	private void updateFinished() {
		for (Runnable listener : updateListeners) {
			listener.run();
		}
	}

	public static ArrayList<String> getPlaces() {
		return places;
	}

	public static void addPlace(String url) {
		if (!places.contains(url)) {
			places.add(url);

			if (instance != null) {
				instance.updateTemperature(url, true);
			}

			savePlaces();
		}
	}

	public static void removePlace(String url) {
		if (places.contains(url)) {
			places.remove(url);
			savePlaces();
		}
	}

	private void loadPlaces() {
		try {
			Scanner scan = new Scanner(getApplicationContext().openFileInput(
					"trackedPlaces.txt"));
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
			FileOutputStream output = Tools.getContext().openFileOutput(
					"trackedPlaces.txt", Context.MODE_PRIVATE);
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
	
	/**
	 * Brukes for å lage en notification
	 * @param v
	 */
	public void notiFyMe(View v) {
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.add, "Notification!",System.currentTimeMillis());
		Context context = getApplicationContext();
		String notificationTitle = "Temperatur Notification";
		String notificationText = "Temperatur";
		Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myBlog));
		PendingIntent pendingIntent = PendingIntent.getActivity(TemperatureService.this, 0, myIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.defaults |= notification.DEFAULT_SOUND;
		notification.flags |= notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, notificationTitle,
				notificationText, pendingIntent);
		notificationManager.notify(MY_NOTIFICATION_ID, notification);
	}//end method

}//end class
