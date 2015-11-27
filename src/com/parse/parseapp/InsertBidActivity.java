package com.parse.parseapp;

import java.io.ByteArrayOutputStream;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class InsertBidActivity extends Activity {
	private static final int RESULT_LOAD_IMAGE = 1;
	private String operation;
	private ParseObject bid;
	private boolean isTitle, isDescription, isLocation;
	private EditText txtTitle;
	private EditText txtDescription;
	private EditText txtLocation;
	private Spinner categoryField;
	private String category;
	private ImageView imgBid;
	private Button cmdSubmit;
	private String imgDecodableString;
	private Bitmap bitmapLoaded;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_bid);
		isTitle = isDescription = isLocation = true;
		txtTitle = (EditText) this.findViewById(R.id.txtTitle);

		imgBid = (ImageView) this.findViewById(R.id.imgBid);
		txtDescription = (EditText) this.findViewById(R.id.txtDescription);

		txtLocation = (EditText) this.findViewById(R.id.txtLocation);

		cmdSubmit = (Button) this.findViewById(R.id.cmdInsert);

		cmdSubmit.setEnabled(false);

		txtTitle.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {

				String txtTitleContent = txtTitle.getText().toString();

				if (txtTitleContent.equals(getResources().getString(R.string.insert_title_default))) {
					txtTitle.setText("");
				}
			}
		});

		txtTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				String title = arg0.toString();
				isTitle = title == null || title.isEmpty();
				if (isTitle) {
					txtTitle.setError("E' necessario inserire un titolo");
					cmdSubmit.setEnabled(false);
				} else {
					txtTitle.setError(null);
					if (isTitle || isDescription || isLocation) {
						cmdSubmit.setEnabled(false);
					} else {
						cmdSubmit.setEnabled(true);
					}
				}

			}
		});
		txtDescription.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String txtDescriptionContent = txtDescription.getText().toString();

				if (txtDescriptionContent.equals(getResources().getString(R.string.insert_description_default))) {
					txtDescription.setText("");
				}
			}
		});
		txtDescription.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				String description = arg0.toString();
				isDescription = description == null || description.isEmpty();
				if (isDescription) {
					txtDescription.setError("E' necessario descrivere l'annuncio");
					cmdSubmit.setEnabled(false);
				} else {
					txtDescription.setError(null);
					if (isTitle || isDescription || isLocation) {
						cmdSubmit.setEnabled(false);
					} else {
						cmdSubmit.setEnabled(true);
					}
				}

			}
		});

		txtLocation.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {

				String txtLocationContent = txtLocation.getText().toString();

				if (txtLocationContent.equals(getResources().getString(R.string.insert_location_default))) {
					txtLocation.setText("");
				}
			}
		});

		txtLocation.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				String location = arg0.toString();
				isLocation = location == null || location.isEmpty();
				if (isLocation) {
					txtLocation.setError("E'necessario fissare una locazione");
					cmdSubmit.setEnabled(false);
				} else {
					txtLocation.setError(null);
					if (isTitle || isDescription || isLocation) {
						cmdSubmit.setEnabled(false);
					} else {
						cmdSubmit.setEnabled(true);
					}
				}

			}
		});
		categoryField = (Spinner) findViewById(R.id.categoryField);

		categoryField.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				category = parent.getItemAtPosition(pos).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		
		
		cmdSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String description = txtDescription.getText().toString();
				final String location = txtLocation.getText().toString();
				final String title = txtTitle.getText().toString();
				
				bid = new ParseObject("Bid");

				if (operation != null) {
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Bid");

					query.getInBackground(operation, new GetCallback<ParseObject>() {

						@Override
						public void done(ParseObject bid, ParseException arg1) {
							bid.put("title", title);
							bid.put("title_lowercase", title.toLowerCase());
							bid.put("description", description);
							bid.put("location", location);
							bid.put("category", category);
							
							if (bitmapLoaded != null) {
								ByteArrayOutputStream stream = new ByteArrayOutputStream();

								bitmapLoaded.compress(Bitmap.CompressFormat.JPEG, 33, stream);

								ParseFile photo = new ParseFile("photo.jpg", stream.toByteArray());

								

								bid.put("photo", photo);
							}
							
							
							bid.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									if (e == null) {
										ParsePush push = new ParsePush();
										push.setChannel("Ristoranti");
										push.setMessage("L'offerta è stata modificata ");
										push.sendInBackground();

										finish();
									} else {

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(getApplicationContext(), "Offerta non modificata",
														Toast.LENGTH_LONG).show();

											}
										});
										e.printStackTrace();

									}

								}
							});

						}
					});
					return;
				}

				bid.put("title", title);
				bid.put("description", description);
				bid.put("location", location);
				bid.put("createdby", ParseUser.getCurrentUser().getUsername());
				bid.put("category", category);

				if (bitmapLoaded != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();

					bitmapLoaded.compress(Bitmap.CompressFormat.JPEG, 33, stream);

					ParseFile photo = new ParseFile("photo.jpg", stream.toByteArray());

					

					bid.put("photo", photo);
				}
				bid.saveInBackground(new SaveCallback() {
					public void done(ParseException e) {
						if (e == null) {
							ParsePush push = new ParsePush();
							push.setChannel("Ristoranti");
							push.setMessage("E' stato inserita una nuova offerta ");
							push.sendInBackground();

							finish();

						} else {

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(getApplicationContext(), "Offerta non inserita", Toast.LENGTH_LONG)
											.show();

								}
							});
							e.printStackTrace();

						}
					}
				});

			}

		});

		Button cmdReset = (Button) this.findViewById(R.id.cmdReset);
		cmdReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				txtTitle.setText("");
				txtLocation.setText("");

				txtDescription.setText("");
				imgBid.setImageBitmap(BitmapFactory.decodeFile(""));

			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			operation = extras.getString("objectid");
			String title = extras.getString("title");
			txtTitle.setText(title);
			String location = extras.getString("location");
			txtLocation.setText(location);
			String description = extras.getString("description");
			txtDescription.setText(description);

			byte[] photo = extras.getByteArray("photo");
			this.bitmapLoaded = BitmapFactory.decodeByteArray(photo, 0, photo.length);
			imgBid.setImageBitmap(bitmapLoaded);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.insert_bid, menu);
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

	public void onInsertPhoto(View v) {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
				// Get the Image from data

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgDecodableString = cursor.getString(columnIndex);
				cursor.close();

				imgBid = (ImageView) findViewById(R.id.imgBid);
				// Set the Image in ImageView after decoding the String
				System.out.println("LOADING SIZE: " + imgDecodableString.length());
				bitmapLoaded = BitmapFactory.decodeFile(imgDecodableString);
				imgBid.setImageBitmap(bitmapLoaded);
				System.out.println("LOADED SIZE: " + imgDecodableString.length());

			} else {
				Toast.makeText(this, "Non hai scelto nessuna immagine", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "C'è stato qualche problema", Toast.LENGTH_LONG).show();
		}

	}
}
