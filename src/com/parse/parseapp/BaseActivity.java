package com.parse.parseapp;

import android.app.Activity;
import android.widget.Toast;

public class BaseActivity extends Activity {

	public void message(final String message) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			}
		});
	}
}
