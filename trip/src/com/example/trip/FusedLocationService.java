package com.example.trip;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class FusedLocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(10*1000)//10second
			.setFastestInterval(1000)
			.setNumUpdates(9999)
			.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	public static final String LOCATION_RECEIVED = "fused.location.received";
	private Long now;

	private LocationClient mLocationClient;
	private final Object locking = new Object();
	private Runnable onFusedLocationProviderTimeout;
	private Handler handler = new Handler();

	//
	private int count = 0;
	private String lat="";
	private String lng="";

	//notification
	public static final String TEST_BROADCAST_STRING = "test_broadcast_string";
	public static final String TEST_SERIVCE_STRING = "test_service_string";
	private NotificationManager mNoti;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		now = Long.valueOf(System.currentTimeMillis());
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		Log.d("ONSTART", "UPDATE");
		notification();
		return START_STICKY;
	}
	public void notification(){
		mNoti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNoti.cancel(1);


		// TODO Auto-generated method stub
		Notification notification = new Notification(R.drawable.ic_launcher, "FootTrip", System.currentTimeMillis());
		//		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		/*
		 * 釉뚮줈�뱶罹먯뒪�듃 �궗�슜
		 */
		Intent bdIntent = new Intent("action");
		bdIntent.putExtra("bdString", TEST_BROADCAST_STRING);
		bdIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/*
		 * �꽌鍮꾩뒪 �궗�슜 
		 */
		Intent svIntent = new Intent("testService");
		svIntent.putExtra("svString", TEST_SERIVCE_STRING);
		svIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Intent activityIntent = new Intent(this, ActionTab.class);


		PendingIntent content = PendingIntent.getActivity(this, 0, activityIntent, 0);
		PendingIntent svContent = PendingIntent.getService(this, 0, svIntent, 0);
		PendingIntent bdContent = PendingIntent.getBroadcast(this, 0, bdIntent, 0);

		RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notifiview);
		remoteViews.setOnClickPendingIntent(R.id.btn_pause, svContent);
		remoteViews.setOnClickPendingIntent(R.id.btn_stop, bdContent);

		notification.contentView = remoteViews;
		notification.contentIntent = content;

		mNoti.notify(1, notification);


	}
	@Override
	public void onConnected(Bundle bundle) {
		Log.d("FusedLocationService", "Fused Location Provider got connected successfully");
		
		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		int pause = pref.getInt("PAUSE", 0);
		if(pause == 0){//not pause
			mLocationClient.requestLocationUpdates(REQUEST,this);
		}
		else{//pause
			
		}
		onFusedLocationProviderTimeout = new Runnable() {
			public void run() {
					Log.d("FusedLocationService", "location Timeout");

					Location lastbestStaleLocation=getLastBestStaleLocation();
					sendLocationUsingBroadCast(lastbestStaleLocation);

					if(lastbestStaleLocation!=null)
						Log.d("FusedLocationService", "Last best location returned ["+lastbestStaleLocation.getLatitude()+","+lastbestStaleLocation.getLongitude()+"] in "+(Long.valueOf(System.currentTimeMillis())-now)+" ms");
					Log.e("test", "here2");
					Log.e("test", "here3");
				}
		};
	}

	private void sendLocationUsingBroadCast(Location location) {
		//
		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("DBLAT", "");
		edit.putString("DBLNG", "");
		edit.commit();
		if(lat.equals("") && lng.equals("")){
			lat += location.getLatitude();
			lng += location.getLongitude();				
		}
		else{
			lat += ","+location.getLatitude();
			lng += ","+location.getLongitude();
		}
		Log.i("SER-LAT", lat);
		Log.i("SER-LNG", lng);


		count++;        

		/**@brief store data to show locations**/
		pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		edit = pref.edit();

		edit.putString("LAT"+(count), Double.toString(location.getLatitude()));		
		edit.putString("LNG"+(count), Double.toString(location.getLongitude()));
		edit.putInt("COUNT", count);
		edit.putString("DBLAT", lat);
		edit.putString("DBLNG", lng);
		edit.commit();//do alwase!!

		Log.d("service-count", Integer.toString(count));
		Log.d("LATLNG", Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude()));


	}

	@Override
	public void onDisconnected() {
		Log.d("FusedLocationService","Fused Location Provider got disconnected successfully");
		stopSelf();
	}

	@Override
	public void onLocationChanged(Location location) {
		synchronized (locking){
			Log.d("FusedLocationService", "Location received successfully ["+location.getLatitude()+","+location.getLongitude()+"] in "+(Long.valueOf(System.currentTimeMillis()-now))+" ms");

			handler.removeCallbacks(onFusedLocationProviderTimeout);
			
			
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			int pause = pref.getInt("PAUSE", 0);
			if(pause == 0){//not pause
				sendLocationUsingBroadCast(location);
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("FusedLocationService", "Error connecting to Fused Location Provider");
	}

	public Location getLastBestStaleLocation() {
		Location bestResult = null;
		LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location lastFusedLocation=mLocationClient.getLastLocation();
		Location gpsLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location networkLocation = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (gpsLocation != null && networkLocation != null) {
			if (gpsLocation.getTime() > networkLocation.getTime()){
				Log.i("PROVIDER", "GPS");
				bestResult = gpsLocation;
			}
		} else if (gpsLocation != null) {
			Log.i("PROVIDER", "GPS");
			bestResult = gpsLocation;
		} else if (networkLocation != null) {
			Log.i("PROVIDER", "network");
			bestResult = networkLocation;
		}

		//take Fused Location in to consideration while checking for last stale location
		if (bestResult != null && lastFusedLocation != null) {
			if (bestResult.getTime() < lastFusedLocation.getTime())
				bestResult = lastFusedLocation;
		}
		
		return bestResult;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if(mLocationClient.isConnected())
            mLocationClient.disconnect();
    	mNoti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNoti.cancel(1);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}