package com.snyskuter.oblig4_1;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends ListActivity {
	private final Context context = this;
	private ArrayAdapter<SearchItem> adapter;
	
	int highlight = Color.argb(128, 30, 133, 188);
	
	private class SearchItem {
		private String name;
		private String url;
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getURL() { return url; }
		
		public SearchItem(String name, String url) {
			this.name = name;
			this.url = url;
		}
		
		public String toString() {
			return name;
		}
	}
	
	private ArrayList<String> selected = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		adapter = new ArrayAdapter<SearchItem>(context, android.R.layout.simple_list_item_1, new ArrayList<SearchItem>());		
		setListAdapter(adapter);
		
		TextView searchInput = (TextView)findViewById(R.id.searchInput);
		searchInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
				if (text.length() > 0) {
					text = text.toString().replace(' ', '+');
					
					DataRunnable<String> resultHandler = new DataRunnable<String>() {
						@Override
						public void run() {
							updateList(getData());
						}
					};
					
					if (Character.isDigit(text.charAt(0))) {
						HTTP.GET("http://kenh.dyndns.org/postnr/" + text, resultHandler);
					}
					else {
						HTTP.GET("http://kenh.dyndns.org/sted/" + text, resultHandler);
					}
				}
				else {
					adapter.clear();
				}
			}
			
			@Override
			public void afterTextChanged(Editable text) { }
			
			@Override
			public void beforeTextChanged(CharSequence text, int start, int count, int after) { }			
		});
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				SearchItem item = (SearchItem)getListView().getItemAtPosition(arg2);
				String url = item.getURL();
				
				if (!TemperatureService.getPlaces().contains(url)) {
					TemperatureService.addPlace(url);
					//selected.add(url);
					arg1.setBackgroundColor(highlight);
					adapter.notifyDataSetChanged();
				}
				else {
					TemperatureService.removePlace(url);
					//selected.remove(url);
					arg1.setBackgroundColor(Color.TRANSPARENT);
					adapter.notifyDataSetChanged();
				}
				Toast.makeText(context, url + ", " + selected.size(), Toast.LENGTH_LONG).show();
			}
		});
	}

	
	private void updateList(String data) {
		ArrayList<SearchItem> results = new ArrayList<SearchItem>();
		String[] lines = data.split("\n");
		int i = 0;
		ArrayList<String> places = TemperatureService.getPlaces();
		for (String line : lines) {
			String[] lineSplit = line.split("\t");
			String name = lineSplit[0];
			String url = "";
			if (lineSplit.length > 1) {
				url = lineSplit[1];

				if (getListView().getChildAt(i) != null) {
					getListView().getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
				}
				
				if (places.contains(url)) {
					//name += " <--";
					if (getListView().getChildAt(i) != null) {
						getListView().getChildAt(i).setBackgroundColor(highlight);
					}
				}
			}
			
			if (lineSplit.length > 0) {
				results.add(new SearchItem(name, url));
			}
			i++;
		}
		adapter.clear();
		adapter.addAll(results);
	}
}
