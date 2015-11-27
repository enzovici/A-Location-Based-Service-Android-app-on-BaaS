package com.parse.parseapp;

import java.util.ArrayList;
import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Activity principale per la gestione degli annunci creati da un gestore.
 * L'activity consente al gestore degli annunci di poter creare, modificare, cancellare annunci.
 * L'Activity estende una ListActivity quindi tutti gli annunci dell'utente sono mostrati su una lista
 * Ogni elemento della lista, creato dall'adapter BidManagerAdapter presenta un pulsante modifica e cancella
 * Lo stesso BidManagerAdapter provvede ad inserire in fondo alla lista un pulsante che consente all'utente di poter aggiungere un nuovo annuncio 
 */
public class BidManagerActivity extends ListActivity {
	private String username;
	private ParseQuery<ParseObject> bidQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		username = ParseUser.getCurrentUser().getUsername();

		ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
		postACL.setPublicReadAccess(true);
		postACL.setPublicWriteAccess(true);
		ParseACL.setDefaultACL(postACL, true);

		refresh_manager();

	}

	/*Metodo richiamato quando è necessario recuperare tutti gli annunci creati dall'utente e presentarli sulla lista
	 * Questo metodo è richiamato all'avvio dell'activity e ogni qualvolta si effettua un operazione di modifica e cancellazione
	 * */
	private void refresh_manager() {
		bidQuery = ParseQuery.getQuery("Bid");
		bidQuery.whereEqualTo("createdby", username);
		bidQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				if (arg1 != null) {
					System.out.println("EX: " + arg1.getMessage());
					return;
				}
				ArrayList<ParseObject> bids = (ArrayList<ParseObject>) arg0;

				BidManagerAdapter adapter = new BidManagerAdapter(getApplicationContext(), bids, username);
				setListAdapter(adapter);

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		refresh_manager();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//getMenuInflater().inflate(R.menu.menu_bid_user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class BidManagerAdapter extends ArrayAdapter<ParseObject> {
		private final Context context;
		private final ArrayList<ParseObject> values;

		public BidManagerAdapter(Context context, ArrayList<ParseObject> bids, String username) {
			super(context, R.layout.row_insert_bid, bids);
			this.context = context;
			this.values = bids;

			bids.add(new ParseObject(""));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView;

			final ParseObject bid = values.get(position);

			String title = (String) bid.get("title");

			if (title != null) {
				rowView = inflater.inflate(R.layout.row_insert_bid, parent, false);

				final TextView textView = (TextView) rowView.findViewById(R.id.label);
				textView.setText(title);

				Button cmdModify = (Button) rowView.findViewById(R.id.cmdModifyBid);
				cmdModify.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent in = new Intent(context, InsertBidActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						in.putExtra("objectid", bid.getObjectId());
						in.putExtra("title", "" + bid.get("title"));
						in.putExtra("description", "" + bid.get("description"));
						in.putExtra("location", "" + bid.get("location"));
						ParseFile photo=bid.getParseFile("photo");
						try {
							System.out.println("DATA"+photo.getData().length);
							in.putExtra("photo", photo.getData());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						context.startActivity(in);
					}
				});
				Button cmdDelete = (Button) rowView.findViewById(R.id.cmdDeleteBid);
				cmdDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						bid.deleteInBackground(new DeleteCallback() {

							@Override
							public void done(ParseException e) {
								if (e == null) {

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Toast.makeText(context, "Cancellazione avvenuta con successo",
													Toast.LENGTH_LONG).show();
										}
									});

									refresh_manager();

								} else {

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Toast.makeText(context, "Cancellazione non avvenuta", Toast.LENGTH_LONG)
													.show();
										}
									});
								}
							}
						});
					}
				});

			} else {

				rowView = inflater.inflate(R.layout.row_insert_button, parent, false);
				Button cmdInsertBid = (Button) rowView.findViewById(R.id.cmdInsertBid);
				cmdInsertBid.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent in = new Intent(context, InsertBidActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(in);

					}
				});
			}

			return rowView;
		}

	}
}
