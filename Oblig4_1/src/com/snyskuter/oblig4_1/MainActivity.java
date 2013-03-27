package com.snyskuter.oblig4_1;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				download("http://fil.nrk.no/yr/viktigestader/noreg.txt", "norge.txt");
			}		
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
    	 
    private void download(String url, String saveAs) {
	    try {
	        URLConnection conn = new URL(url).openConnection();
	        
	        BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
	        FileOutputStream output = openFileOutput(saveAs, Context.MODE_PRIVATE);
	
	        byte[] buffer = new byte[4096];
	        int bytes = 0;
	        while ((bytes = input.read(buffer)) != -1) {
	        	output.write(buffer, 0, bytes);
	        }	        
	        input.close();
	        output.close();	
	    } catch (IOException e) { }
    }
}
