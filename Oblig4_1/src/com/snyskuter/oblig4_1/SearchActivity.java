package com.snyskuter.oblig4_1;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends ListActivity {
	private final Context context = this;
	private ArrayAdapter<SearchItem> adapter;
	
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
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				SearchItem item = (SearchItem)getListView().getItemAtPosition(arg2);
				String url = item.getURL();
				
				if (!selected.contains(url)) {
					selected.add(url);
					item.setName(item.getName() + " <--");
					adapter.notifyDataSetChanged();
				}
				else {
					selected.remove(url);
					item.setName(item.getName().substring(0, item.getName().length() - 4));
					adapter.notifyDataSetChanged();
				}
				Toast.makeText(context, url + ", " + selected.size(), Toast.LENGTH_LONG).show();
			}
		});
		setListAdapter(adapter);
		
		TextView searchInput = (TextView)findViewById(R.id.searchInput);
		searchInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable text) { }
			
			@Override
			public void beforeTextChanged(CharSequence text, int start, int count, int after) { }
			
			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
				if (text.length() > 0) {
					DataRunnable<String> doneHandler = new DataRunnable<String>() {
						@Override
						public void run() {
							ArrayList<SearchItem> results = new ArrayList<SearchItem>();
							String[] lines = getData().split("\n");
							for (String line : lines) {
								String[] data = line.split("\t");
								String name = data[0];
								String url = "";
								if (data.length > 1) {
									url = data[1];
									if (selected.contains(url)) {
										name += " <--";
									}
								}
								
								if (data.length > 0) {
									results.add(new SearchItem(name, url));
								}
							}
							adapter.clear();
							adapter.addAll(results);
						}
					};
					text = text.toString().replace(' ', '+');
					if (Character.isDigit(text.charAt(0))) {
						Tools.downloadStringAsync("http://kenh.dyndns.org/postnr/" + text, doneHandler);
					}
					else {
						Tools.downloadStringAsync("http://kenh.dyndns.org/sted/" + text, doneHandler);
					}
				}
				else {
					adapter.clear();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}
}
