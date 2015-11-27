package com.parse.parseapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.parseapp.data.Bid;
import com.parse.parseapp.data.BidManager;
import com.parse.parseapp.utils.ParseAppUtils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;

public class BidUserActivity extends BaseActivity implements LocationListener {
	private ListView lw;
	private MyPerformanceArrayAdapter adapter;
	private Location location;
	private int distance;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		lw = (ListView) this.findViewById(R.id.listView1);


		location = (Location) getIntent().getParcelableExtra("location");

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		String dist = prefs.getString("distance", "30");
		if (ParseAppUtils.isNumeric(dist)) {
			distance = Integer.parseInt(dist);
		}

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);

		lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

				final Bid item = (Bid) parent.getItemAtPosition(position);
				Intent in = new Intent(getApplicationContext(), ViewBidActivity.class);
				in.putExtra("bid", item);
				startActivity(in);

			}

		});

		Spinner categorySpinner = (Spinner) this.findViewById(R.id.spinner1);

		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				String categorySelected = ((String) parent.getItemAtPosition(position)).toLowerCase();

				try {
					ArrayList<Bid> bids;
					if (!categorySelected.equals("tutte")) {
						bids = BidManager.getInstance().getBidFromCategory(getApplicationContext(), location, distance,
								categorySelected);
					} else {
						bids = BidManager.getInstance().getBids(getApplicationContext(), location, distance);
					}
					adapter = new MyPerformanceArrayAdapter(getApplicationContext(), bids);
					lw.setAdapter(adapter);
				} catch (Exception e) {

					message(e.getMessage());
				}

				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

	}
	
	private void testPopupMenu(){
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.popup_button, null);
		final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
		btnDismiss.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Intent in = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(in);
				
				
			}
		});

		getWindow().getDecorView().post(new Runnable() {
			public void run() {
				popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {

				try {
					if (!query.isEmpty()) {

						ArrayList<Bid> bids = BidManager.getInstance().getBidWithTitle(getApplicationContext(),
								location, distance, query);

						adapter = new MyPerformanceArrayAdapter(getApplicationContext(), bids);
						lw.setAdapter(adapter);
						return false;

					}

				} catch (Exception e) {

					message("Non è stata trovata nessuna città");
				}

				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent in = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(in);
		} else if (id == R.id.action_search) {
			System.out.println("SEARCH ON");
		}

		return super.onOptionsItemSelected(item);
	}

	static class ViewHolder {
		public TextView title;
		public TextView createdBy;
		public TextView price;
		public TextView originalPrice;
		public TextView from;
		public TextView distance;
		public ImageView image;
	}

	public class MyPerformanceArrayAdapter extends ArrayAdapter<Bid> {
		private final List<Bid> names;
		// Lo scopo della hashmap è per impedire che ogni volta che si scorra la
		// lista si vada a riscaricare l'immagine da Parse
		private HashMap<String, Bitmap> bidImage;

		public MyPerformanceArrayAdapter(Context context, List<Bid> names) {
			super(context, R.layout.row_bid, names);
			this.names = names;
			this.bidImage = new HashMap<String, Bitmap>();

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			// reuse views
			if (rowView == null) {
				LayoutInflater inflater = getLayoutInflater();
				rowView = inflater.inflate(R.layout.row_bid, null);

				// Costruzione dell'elemento della tabella
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.title = (TextView) rowView.findViewById(R.id.titleLabel);
				viewHolder.createdBy = (TextView) rowView.findViewById(R.id.createdByLabel);
				// viewHolder.originalPrice = (TextView)
				// rowView.findViewById(R.id.originalPriceLabel);
				viewHolder.price = (TextView) rowView.findViewById(R.id.priceLabel);
				viewHolder.from = (TextView) rowView.findViewById(R.id.fromLabel);
				viewHolder.distance = (TextView) rowView.findViewById(R.id.distanceLabel);
				viewHolder.image = (ImageView) rowView.findViewById(R.id.image);

				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();

			Bid s = names.get(position);

			holder.title.setText(s.getTitle());
			holder.createdBy.setText("Inserito da " + s.getCreatedBy());
			// holder.originalPrice.setText(s.getOriginal_price() + " €");
			holder.price.setText(s.getPrice() + " €");

			holder.from.setText(s.getLocation());

			Location loc = new Location("");
			loc.setLatitude(s.getLatitude());
			loc.setLongitude(s.getLongitude());
			holder.distance.setText(Math.round(loc.distanceTo(location) / 1000) + " km");

			Bitmap bmp = bidImage.get(s.getId());
			try {
				if (bmp == null) {
					bmp = ParseAppUtils.loadBitmap(getApplicationContext(), s.getImagePath());
					bidImage.put(s.getId(), bmp);
					if (bmp == null) {
						holder.image.setImageResource(R.drawable.ico_actionbar);
					} else {
						holder.image.setImageBitmap(bmp);
					}
				} else {
					holder.image.setImageBitmap(bmp);
				}
			} catch (Exception e) {
				message(e.getMessage());
			}

			return rowView;
		}
	}

	// Se lo spostamento è uguale o superiore al km allora si provvede ad
	// aggiornare di nuovo la lista delle offerte.
	@Override
	public void onLocationChanged(Location locationChanged) {

		if (location.distanceTo(locationChanged) >= 1000) {
			location = locationChanged;
			try {
				ArrayList<Bid> bids = BidManager.getInstance().getBids(getApplicationContext(), location, distance);
				adapter = new MyPerformanceArrayAdapter(getApplicationContext(), bids);
				lw.setAdapter(adapter);
			} catch (Exception e) {
				message("Non è possibile scaricare nuove inserzioni");
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (prefs != null) {
			String dist = prefs.getString("distance", "30");
			if (ParseAppUtils.isNumeric(dist)) {
				int newDist = Integer.parseInt(dist);
				if (newDist != distance) {
					distance = newDist;

					ArrayList<Bid> bids;
					try {
						bids = BidManager.getInstance().getBids(getApplicationContext(), location, distance);
						adapter = new MyPerformanceArrayAdapter(getApplicationContext(), bids);
						lw.setAdapter(adapter);
					} catch (Exception e) {
						message("Non è possibile scaricare nuove inserzioni");
					}

				}
			}

		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
