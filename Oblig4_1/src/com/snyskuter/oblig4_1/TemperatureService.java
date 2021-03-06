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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class TemperatureService extends Service {
	private static TemperatureService instance;
	public static TemperatureService getInstance() {
		return instance;
	}

	private static final int MY_NOTIFICATION_ID = 1;
	private static final int MY_NOTIFICATION_ID2 = 2;	

	private static ArrayList<String> places;

	private int updateInterval = 15 * 60000;
	private Timer updateTimer = new Timer();
	private TimerTask updateTask;
	private ArrayList<TemperatureData> temperatures;
	private static ArrayList<Runnable> updateListeners = new ArrayList<Runnable>();

	private float nedreGrense = -10;
	private float ovreGrense = 20;	
	String s = "";
	String b = "";
	
	@Override
	public void onCreate() {
		instance = this;
		places = new ArrayList<String>();
		temperatures = new ArrayList<TemperatureData>();
		loadPlaces();

		startUpdateTimer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		updateTimer.cancel();
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
		return updateInterval / 60000;
	}
	
	public void setNedreGrense(float nG) {
		 nedreGrense = nG;
	}
	
	public float getNedreGrense() {		
		return nedreGrense;
	}
	
	public void setOvreGrense(float oG) {
		ovreGrense = oG;
	}
	
	public float getOvreGrense() {
		return ovreGrense;
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
		s = "";
		b = "";
		for (final String url : places) {
			updateTemperature(url, false);
		}		
		if(s != "") {
			notify(s,MY_NOTIFICATION_ID,"Temperatur �vre grense");
		}
		if(b != "") {
			notify(b, MY_NOTIFICATION_ID2, "Temperatur nedre grense");
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
			Element e = (Element) doc.getElementsByTagName("weatherstation").item(0);
			String place = ((Element) doc.getElementsByTagName("name").item(0)).getTextContent();
			String temperature = e.getElementsByTagName("temperature").item(0).getAttributes().getNamedItem("value").getNodeValue();
			
			float temp = Float.parseFloat(temperature);
			if(temp > ovreGrense){
				s += place + " , ";				
			}
			else if(temp < nedreGrense){
				b += place + " , ";
			}
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
			Scanner scan = new Scanner(getApplicationContext().openFileInput("trackedPlaces.txt"));
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
	
	private void notify (String text, int id, String title) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.add)
		.setContentTitle(title)
		.setDefaults(Notification.DEFAULT_ALL)
		.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
		Notification notification = builder.build();
		
		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(id, notification);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
