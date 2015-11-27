package com.parse.parseapp;

import java.util.ArrayList;

import com.parse.parseapp.data.Bid;
import com.parse.parseapp.data.BidManager;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends BaseActivity implements LocationListener {
	private View mProgress;
	//private SplashTask mTask;

	
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_load);

		mProgress = findViewById(R.id.progress);

		mProgress.setVisibility(View.VISIBLE);

		BidManager.getInstance();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isGPSEnabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);

		// Location location = ParseAppUtils.getActualLocation(locationManager);

		// mTask = new SplashTask();
		// mTask.execute(location);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class SplashTask extends AsyncTask<Location, Integer, Boolean> {
		// private ArrayList<TestImage> bids;
		private ArrayList<Bid> bids;
		private Location location;

		@Override
		protected void onPreExecute() {
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Location... arg0) {
			try {
				Thread.sleep(1000);

				location = (Location) arg0[0];

				bids = BidManager.getInstance().getBids(getApplicationContext(), location, 30);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			mProgress.setVisibility(View.GONE);

			
		}

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		System.out.println("PROVIDER ENABLED "+arg0);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	
	}

	@Override
	public void onLocationChanged(Location location) {
		mProgress.setVisibility(View.GONE);
		locationManager.removeUpdates(this);
		finish();
		Intent in = new Intent(getApplicationContext(), BidUserActivity.class);
		in.putExtra("location", location);
		startActivity(in);

		
	}

}
