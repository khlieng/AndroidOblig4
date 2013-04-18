package com.snyskuter.oblig4_1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button saveButtonAlertDialog;
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Tools.init(this);
		
		startService(new Intent(this, TemperatureService.class));
		//stopService(new Intent(this, TemperatureService.class));
		
		//Intent intent = new Intent(this, SearchActivity.class);
		//startActivity(intent);
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
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.LeggTil:
			 Toast.makeText(this, "Legg til", Toast.LENGTH_SHORT).show();
			 Intent i = new Intent(this,SearchActivity.class);
			 startActivity(i);
			break;
		case R.id.Slett:
			Toast.makeText(this, "Slett", Toast.LENGTH_SHORT).show();
			break;
		case R.id.Instillinger:
			Toast.makeText(this, "Instillinger", Toast.LENGTH_SHORT).show();
			initAlertDialog();
			break;
		case R.id.start:
			Log.e("Service STARTED", "Service Started");
			Toast.makeText(this, "Starting service", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this,TemperatureService.class);
			startService(intent);
			break;
		case R.id.stop:
			Log.e("Service STOP", "Service Stopped");
			Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
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
		

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.textAlertDialog);
		text.setText("Ganster!");
		
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonSaveAlert);
		
		
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Log.e("Saving button clicked", "Click");
							Toast.makeText(getApplicationContext(), "Saving.....", Toast.LENGTH_SHORT).show();
							
							dialog.dismiss();
						}
					});
		 
					dialog.show();
	}

}//end activiy
