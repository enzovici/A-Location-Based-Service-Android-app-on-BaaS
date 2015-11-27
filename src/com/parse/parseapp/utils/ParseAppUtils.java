package com.parse.parseapp.utils;

import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class ParseAppUtils {
	public static Bitmap loadBitmap(Context context, String imagePath) throws Exception {

		FileInputStream fis = context.openFileInput(imagePath);

		Bitmap image = BitmapFactory.decodeStream(fis);
		fis.close();

		return image;
	}

	public static final boolean isNumeric(final String s) {
		if (s == null || s.isEmpty())
			return false;
		for (int x = 0; x < s.length(); x++) {
			final char c = s.charAt(x);
			if (x == 0 && (c == '-'))
				continue; // negative
			if ((c >= '0') && (c <= '9'))
				continue; // 0 - 9
			return false; // invalid
		}
		return true; // valid
	}

	public static Location getActualLocation(LocationManager locationManager) {

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		return location;
	}
}
