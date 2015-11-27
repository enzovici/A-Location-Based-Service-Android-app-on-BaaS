package com.parse.parseapp;

import java.util.Calendar;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.parseapp.utils.ParseAppUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SignUpActivity extends Activity {

	protected EditText usernameEditText;
	protected EditText passwordEditText;
	protected EditText emailEditText;
	protected Spinner roleSpinner;
	protected EditText addressEditText;
	protected TextView dateOfBirthTextView;
	protected Button signUpButton;

	private String role;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_sign_up);

		
		usernameEditText = (EditText) findViewById(R.id.usernameField);
		passwordEditText = (EditText) findViewById(R.id.passwordField);
		emailEditText = (EditText) findViewById(R.id.emailField);
		roleSpinner = (Spinner) findViewById(R.id.roleField);

		roleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				role = parent.getItemAtPosition(pos).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		addressEditText = (EditText) findViewById(R.id.addressField);
		dateOfBirthTextView = (EditText) findViewById(R.id.dateOfBirthField);
		signUpButton = (Button) findViewById(R.id.signupButton);

		signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String address = addressEditText.getText().toString();
				String dateOfBirth = dateOfBirthTextView.getText().toString();

				username = username.trim();
				password = password.trim();
				email = email.trim();
				dateOfBirth = dateOfBirth.trim();

				if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role.isEmpty()
						|| dateOfBirth.equals(R.string.date_of_birth_hint) || address.isEmpty()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
					builder.setMessage(R.string.signup_error_message).setTitle(R.string.signup_error_title)
							.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					setProgressBarIndeterminateVisibility(true);

					ParseUser newUser = ParseUser.getCurrentUser();

					newUser.setUsername(username);
					newUser.setPassword(password);
					newUser.setEmail(email);
					newUser.put("role", role.toLowerCase());
					newUser.put("dateOfBirth", dateOfBirth);
					newUser.put("address", address);
					newUser.signUpInBackground(new SignUpCallback() {
						@Override
						public void done(ParseException e) {
							setProgressBarIndeterminateVisibility(false);

							if (e == null) {
								// Success!
								Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							} else {
								AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
								builder.setMessage(e.getMessage()).setTitle(R.string.signup_error_title)
										.setPositiveButton(android.R.string.ok, null);
								AlertDialog dialog = builder.create();
								dialog.show();
							}
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.sign_up, menu);
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

	public void onAddBirth(View view) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "Date Picker");
	}

	/**
	 * A simple {@link Fragment} subclass.
	 */
	private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the date picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month + 1, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user

			String stringOfDate = day + "-" + (month + 1) + "-" + year;
			dateOfBirthTextView.setText(stringOfDate);
		}
	}

}
