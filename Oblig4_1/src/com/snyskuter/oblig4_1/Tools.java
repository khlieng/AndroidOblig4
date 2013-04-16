package com.snyskuter.oblig4_1;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class Tools {
	private static Context context;
	private static Handler handler = new Handler();
	
	public static void init(Context context) {
		Tools.context = context;
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static void downloadFileAsync(final String url, final String saveAs, final Runnable done) {
    	AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
			    try {
			        URLConnection conn = new URL(url).openConnection();
			        
			        BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
			        FileOutputStream output = context.openFileOutput(saveAs, Context.MODE_PRIVATE);
			
			        byte[] buffer = new byte[4096];
			        int bytes = 0;
			        while ((bytes = input.read(buffer)) != -1) {
			        	output.write(buffer, 0, bytes);
			        }	        
			        input.close();
			        output.close();
			        
			        if (done != null) {
			        	handler.post(done);
			        }
			    } catch (IOException e) { }
			}		
		});
    }
}
