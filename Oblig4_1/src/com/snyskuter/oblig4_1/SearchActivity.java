package com.snyskuter.oblig4_1;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchActivity extends ListActivity {
	private final Context context = this;
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new ArrayList<String>());
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
							ArrayList<String> results = new ArrayList<String>();
							String[] lines = getData().split("\n");
							for (String line : lines) {
								String[] data = line.split("\t");
								if (data.length > 0) {
									results.add(data[0]);
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
