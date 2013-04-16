package com.snyskuter.oblig4_1;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Handler;

public class HTTP {
	private static Handler handler = new Handler();
	
	public static void GET(final String url, final DataRunnable<String> resultHandler) {
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
			    try {
			        URLConnection conn = new URL(url).openConnection();
			        
			        BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
			        StringBuilder sb = new StringBuilder();
			        
			        byte[] buffer = new byte[1024];			        
			        int bytes = 0;
			        while ((bytes = input.read(buffer)) != -1) {
			        	sb.append(new String(buffer, 0, bytes));
			        }	        
			        input.close();
			        
			        if (resultHandler != null) {
			        	resultHandler.setData(sb.toString());
			        	handler.post(resultHandler);
			        }
			    } catch (IOException e) { }
			}		
		});
	}
}
