package com.parse.parseapp;

import com.parse.ParseBroadcastReceiver;
import com.parse.ParsePushBroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ParseAppBroadcastReceiver /*extends ParseBroadcastReceiver*/ extends ParsePushBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent){
		System.out.println("PUSH");
		
	}
	
	 
	@Override
	protected int getSmallIconId(Context context, Intent intent) {
	     return R.drawable.ic_stat_pa;
	 }

	 
	@Override
	protected Bitmap getLargeIcon(Context context, Intent intent) {
	     
		return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_stat_pa);
	 }
}
