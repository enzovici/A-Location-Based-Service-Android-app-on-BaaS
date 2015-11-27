package com.parse.parseapp;

import com.parse.parseapp.utils.ParseAppUtils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Window;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

	}

	public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.preferences);
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			System.out.println("AGGIORNA");
			updatePreferences(findPreference(key));

		}

		private void updatePreferences(Preference p) {
			if (p instanceof EditTextPreference) {
				EditTextPreference editTextPref = (EditTextPreference) p;
				
				p.setSummary(editTextPref.getText());

			}
		}
	}

}
