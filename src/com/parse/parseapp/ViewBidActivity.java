package com.parse.parseapp;

import com.parse.ParseUser;
import com.parse.parseapp.data.Bid;
import com.parse.parseapp.data.BidManager;
import com.parse.parseapp.utils.ParseAppUtils;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewBidActivity extends BaseActivity {
	private Bid bid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_bid);
		bid = (Bid) getIntent().getSerializableExtra("bid");

		ImageView imageField = (ImageView) this.findViewById(R.id.image);
		try {
			imageField.setImageBitmap(ParseAppUtils.loadBitmap(getApplicationContext(), bid.getImagePath()));
		} catch (Exception e) {
			imageField.setImageBitmap(null);
		}
		TextView titleField = (TextView) this.findViewById(R.id.titleLabel);
		titleField.setText(bid.getTitle());

		TextView descriptionField = (TextView) this.findViewById(R.id.descriptionLabel);
		descriptionField.setText(bid.getDescription());

		/*TextView originalPriceField = (TextView) this.findViewById(R.id.originalPriceLabel);
		originalPriceField.setText(bid.getOriginal_price() + " €");*/

		TextView priceField = (TextView) this.findViewById(R.id.priceLabel);
		priceField.setText(bid.getPrice() + " €");

		TextView conditionsField = (TextView) this.findViewById(R.id.conditionsLabel);
		conditionsField.setText(bid.getConditions());

		TextView locationField = (TextView) this.findViewById(R.id.location);
		locationField.setText(bid.getLocation());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.view_bid, menu);
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

	public void onClose(View v) {
		finish();
	}

	public void onOrder(View v) {
		if (BidManager.getInstance().buy(ParseUser.getCurrentUser(), bid)) {
			message("Ordine preso in carico");
		} else {
			message("Ordine NON preso in carico");
		}
	}

}
