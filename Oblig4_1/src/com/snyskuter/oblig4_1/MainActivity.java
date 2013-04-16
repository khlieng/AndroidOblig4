package com.snyskuter.oblig4_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

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
			break;

		default:
			break;
		}
		return true;
	}

}
