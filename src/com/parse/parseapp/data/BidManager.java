package com.parse.parseapp.data;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import android.content.Context;
import android.location.Location;

public class BidManager {
	private static BidManager instance;
	private ParseQuery<ParseObject> query;

	public static BidManager getInstance() {
		if (instance == null)
			instance = new BidManager();
		return instance;
	}

	public ArrayList<Bid> getAllBid(Context context) throws Exception {
		query = ParseQuery.getQuery("Bid");

		List<ParseObject> queryRes = query.find();

		return this.getBids(context, queryRes);
	}

	public ArrayList<Bid> getBidFromCategory(Context context, Location location, int maxDistance, String category)
			throws Exception {
		query = ParseQuery.getQuery("Bid");
		if (location != null) {
			ParseGeoPoint pgo = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
			//query.orderByAscending("geolocation");
			query.whereNear("geolocation", pgo);
			query.whereWithinKilometers("geolocation", pgo, maxDistance);
		}

		query.whereEqualTo("category", category);
		return getBids(context, query.find());
	}

	public ArrayList<Bid> getBidWithTitle(Context context, Location location, int maxDistance, String title) throws Exception{
		query = ParseQuery.getQuery("Bid");
		if (location != null) {
			ParseGeoPoint pgo = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

			query.whereNear("geolocation", pgo);
			query.whereWithinKilometers("geolocation", pgo, maxDistance);
		}
		query.whereContains("title_lowercase", title.toLowerCase());
		return getBids(context, query.find());
	}
	
	public void insertBid(Bid bid) throws Exception {
		ParseObject b = new ParseObject("Bid");

		b.add("title", bid.getTitle());
		b.add("description", bid.getDescription());
		b.add("conditions", bid.getConditions());
		b.add("category", bid.getCategory());
		// b.add("original_price", bid.getOriginal_price());
		// b.add("price", bid.getPrice());
		b.add("createdby", bid.getCreatedBy());
		// b.add("deadline", bid.getDeadline());

		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// bid.getImage().compress(Bitmap.CompressFormat.JPEG, 33, stream);
		// ParseFile photo = new ParseFile("photo.jpg", stream.toByteArray());

		// b.add("photo", photo);

		b.save();
	}

	private ArrayList<Bid> getBids(Context context, List<ParseObject> list) throws Exception {
		ArrayList<Bid> ret = new ArrayList<Bid>();
		
		for (ParseObject r : list) {
			
			Bid bid = new Bid();
			bid.setId(r.getObjectId());

			bid.setTitle(r.getString("title"));
			bid.setDescription(r.getString("description"));
			bid.setCategory(r.getString("category"));
			bid.setConditions(r.getString("conditions"));
			bid.setCreatedBy(r.getString("createdby"));
			bid.setLocation(r.getString("location"));
			bid.setPrice(r.getDouble("price"));
			bid.setOriginal_price(r.getDouble("original_price"));
			bid.setDeadline(r.getDate("deadline"));

			ParseGeoPoint geo=r.getParseGeoPoint("geolocation");
			bid.setLatitude(geo.getLatitude());
			bid.setLongitude(geo.getLongitude());
			
			ParseFile bmpf = r.getParseFile("photo");

			String imgURL = bmpf.getUrl();

			String fileImageName = imgURL.substring(imgURL.lastIndexOf("/") + 1, imgURL.length());

			byte[] bmpd = bmpf.getData();

			System.out.println("FILE IMAGE" + fileImageName + " BYTE: " + bmpd.length);

			FileOutputStream fout = context.openFileOutput(fileImageName, Context.MODE_PRIVATE);

			fout.write(bmpd);
			fout.flush();
			fout.close();

			bid.setImagePath(fileImageName);
			ret.add(bid);

		}

		return ret;
	}

	public boolean buy(ParseUser currentUser, Bid bid) {
		query = ParseQuery.getQuery("Bid");
		try {
			ParseObject b = query.get(bid.getId());

			ParseRelation<ParseObject> relation = currentUser.getRelation("purchased");

			relation.add(b);

			currentUser.save();
			return true;
		} catch (ParseException e) {

			return false;
		}
	}

	public ArrayList<Bid> getBids(Context context, Location location, int maxDistance) throws Exception {
		System.out.println("FIND START");

		query = ParseQuery.getQuery("Bid");
		if (location != null) {
			ParseGeoPoint pgo = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
			query.whereNear("geolocation", pgo);

			query.whereWithinKilometers("geolocation", pgo, maxDistance);
			//query.orderByAscending("geolocation");
		}

		List<ParseObject> list = query.find();
		return getBids(context, list);


	}

}
