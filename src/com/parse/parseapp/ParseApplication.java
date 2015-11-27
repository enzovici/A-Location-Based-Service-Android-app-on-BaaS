package com.parse.parseapp;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

import android.app.Application;

/*All'interno di questa classe si inizializzano tutte le variabili globali utilizzate dalle varie activity della applicazione
 * Nel caso particolare, nella classe ParseApplication vengono fatte alcune impostazioni di base per poter utilizzare i servizi di Parse.com */
public class ParseApplication extends Application {

	public ParseApplication() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize Crash Reporting.
		ParseCrashReporting.enable(this);

		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);

		// Add your initialization code here
		Parse.initialize(this);

		ParseUser.enableRevocableSessionInBackground();
		ParseUser.enableAutomaticUser();

		ParseUser.getCurrentUser().saveInBackground();

		ParseInstallation.getCurrentInstallation().saveInBackground();

		// Si effettua l'iscrizione dell'applicazione al canale "Ristoranti"
		ParsePush.subscribeInBackground("Ristoranti");

	}
}
