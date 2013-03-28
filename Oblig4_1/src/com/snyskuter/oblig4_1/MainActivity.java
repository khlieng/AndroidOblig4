package com.snyskuter.oblig4_1;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		done();
		//downloadAsync("http://fil.nrk.no/yr/viktigestader/noreg.txt", "norge.txt");
		//downloadAsync("http://192.168.1.3/places/Narvik", "test.txt");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void done() {
		try {
			ArrayList<String> places = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			BufferedInputStream in = new BufferedInputStream(openFileInput("norge.txt"));
			int i = 0;
			boolean end = false;
			while (!end) {
				try {
					sb.delete(0, sb.length());
					int b = 0;
					while ((b = in.read()) != -1 && b != '\n') {
						sb.append((char)b);
					}
					if (b == -1) end = true;
					String[] split = sb.toString().split("\t");
					if (split.length > 1) {
						places.add(split[1]+";"+split[12]);
					}
					i++;					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Toast.makeText(this, places.size() + "", Toast.LENGTH_LONG).show();
			AutoCompleteTextView text = (AutoCompleteTextView)this.findViewById(R.id.autocomplete);
			text.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, places));
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
	}
    
    private void downloadAsync(final String url, final String saveAs) {
    	AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
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
			        
			        //done();
			    } catch (IOException e) { }
			}		
		});
    }
}
