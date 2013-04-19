package com.snyskuter.oblig4_1;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	final Context context = this;
	private Handler handler = new Handler();
	private boolean removing = false;
	
	private EditText intervall;
	private EditText nedreGrense;
	private EditText ovreGrense;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Tools.init(this);
		
		TemperatureService.addUpdateListener(new Runnable() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						setListAdapter(new TemperatureAdapter(context, R.layout.row_temperature, TemperatureService.getInstance().getTemperatures()));
					}
				});
			}
		});

		if (TemperatureService.getInstance() != null) {
			setListAdapter(new TemperatureAdapter(context, R.layout.row_temperature, TemperatureService.getInstance().getTemperatures()));
		}
		else {
			startService(new Intent(this, TemperatureService.class));
		}
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (removing) {
					TemperatureData data = (TemperatureData)getListAdapter().getItem(arg2);
					((ArrayAdapter<TemperatureData>)getListAdapter()).remove(data);
					TemperatureService.removePlace(data.getUrl());
				}
			}			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		
		return true;
	}
	
	/** (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.LeggTil:
			 Intent i = new Intent(this,SearchActivity.class);
			 startActivity(i);
			break;
		case R.id.Slett:
			removing = !removing;
			if (removing) {
				item.getIcon().setColorFilter(Color.RED, Mode.MULTIPLY);
			}
			else {
				item.getIcon().clearColorFilter();
			}
			break;
		case R.id.Instillinger:
			initAlertDialog();
			break;
		case R.id.start:
			Log.e("Service STARTED", "Service Started");
			Intent intent = new Intent(this,TemperatureService.class);
			startService(intent);
			break;
		case R.id.stop:
			Log.e("Service STOP", "Service Stopped");
			Intent inte = new Intent(this,TemperatureService.class);
			stopService(inte);
			break;

		default:
			break;
		}
		return true;
	}
	
	public void initAlertDialog(){
		
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.properties_custom);
		dialog.setTitle("Instillinger");
		
		final TemperatureService service = TemperatureService.getInstance();
		
		// set the custom dialog components - text, image and button
		nedreGrense = (EditText)dialog.findViewById(R.id.editTextNedreGrense);
		ovreGrense = (EditText)dialog.findViewById(R.id.editTextOvreGrense);
		intervall = (EditText)dialog.findViewById(R.id.editText1);
		
		if (service != null) {
			nedreGrense.setText(service.getNedreGrense() + "");
			ovreGrense.setText(service.getOvreGrense() + "");
			intervall.setText(service.getUpdateInterval() + "");
		}
		
		Button dialogButton = (Button)dialog.findViewById(R.id.dialogButtonSaveAlert);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("Saving button clicked", "Click");

				if (service != null) {
					service.setUpdateInterval(Integer.parseInt(intervall.getText().toString()));
					service.setNedreGrense(Float.parseFloat(nedreGrense.getText().toString()));
					service.setOvreGrense(Float.parseFloat(ovreGrense.getText().toString()));
				}
				dialog.dismiss();
			}
		});
 
		dialog.show();
	}

	private class TemperatureAdapter extends ArrayAdapter<TemperatureData> {
		private ArrayList<TemperatureData> entries;
		
		public TemperatureAdapter(Context context, int textViewResourceId, ArrayList<TemperatureData> entries) {
			super(context, textViewResourceId, entries);
			this.entries = entries;
		}
		
		@Override
        public View getView(final int position, View view, ViewGroup parent) {
            final View v = getLayoutInflater().inflate(R.layout.row_temperature, null);
            TextView place = (TextView)v.findViewById(R.id.place);
            place.setFocusable(false);
            place.setFocusableInTouchMode(false);
            place.setText(entries.get(position).getPlace());
            
            TextView temperature = (TextView)v.findViewById(R.id.temperature);
            temperature.setFocusable(false);
            temperature.setFocusableInTouchMode(false);
            temperature.setText(entries.get(position).getTemperature() + (char)0x00B0);
            
            float temp = Float.parseFloat(entries.get(position).getTemperature());
            if (temp < 0) {
            	temperature.setTextColor(Color.BLUE);
            }
            else {
            	temperature.setTextColor(Color.RED);
            }
            
            return v;
		}
		
		
	}
}//end activity
