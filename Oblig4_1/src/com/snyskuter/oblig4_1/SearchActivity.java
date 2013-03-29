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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		TextView searchInput = (TextView)findViewById(R.id.searchInput);
		searchInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) { }
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			
			@Override
			public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
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
							setListAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, results));
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
					setListAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new String[0]));
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
