package com.example.trip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

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
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
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
	
	
	//camera
	//member variable for File Read and Write
	private FileObserver pOb, rOb;
	//object for Record data
	private RecordModel RM = new RecordModel();
	//boolean which check if device record
	private boolean isRecord = false;
	//to indicate file path data(for test, will remove) 이변수랑 관련된 모든건 테스트용임
	private String selectedFilePath = "입력된 파일 정보 없음.";
	private Location location;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		now = Long.valueOf(System.currentTimeMillis());
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		Log.d("ONSTART", "UPDATE");
		notification();
		
		
		//////////////////camera////
		String cameraDirectory = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera"; 
		String recordDirectory = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sounds";

		//Log.d("record", cameraDirectory);
		//					Log.d("record", recordDirectory);
		pOb = initSingleDirectoryObserver(cameraDirectory);
		rOb = initSingleDirectoryObserver(recordDirectory);

		//watching file(photo/video/record) write
		pOb.startWatching();
		rOb.startWatching();
		
		
		
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
	/** 
	 * @author  ieunseong
	 * @content Initiate a single observer of a given directory 
	 * @param   partial path of the directory to keep watch of. 
	 */ 
	private FileObserver initSingleDirectoryObserver(String directoryPath) { 
		final String dirPath = directoryPath; 
		FileObserver observer = new FileObserver(directoryPath) { 
			@Override 
			public void onEvent(int event, String file) {

				String filePath = dirPath + "/" + file;
				String gpsVal="No location found";

				if (event == FileObserver.CREATE) {
					//Detecting write operation of camera file
					Log.i("service-", "hhh");
//					Location latlng = getLastBestStaleLocation();
					Location latlng = location;
					Log.i("service-", latlng.toString());
					String latLongString= "Lat:" + latlng.getLatitude() + "\nLong:" + latlng.getLongitude();
					gpsVal =  latLongString;
					Log.d("path",file);
					Log.d("filePath",filePath);
					Log.d("gpsVal", ""+gpsVal);

					//Distinguish photo and video
					if(file.contains("jpg")) RM.addPhotoList(filePath, gpsVal, new Date());
//					else if(file.contains("mp4")) RM.addVideoList(filePath, gpsVal, new Date());

					isRecord = true;
					selectedFilePath = filePath + "\n" +"�쐞移�: " + gpsVal; 
				}else if (event == FileObserver.MOVED_TO) {
					//Detecting write operation of voice file
					Log.i("service-", "hhh2");
//					Location latlng = getLastBestStaleLocation();
					Location latlng = location;
					Log.i("service-2", latlng.toString());
					String latLongString= "Lat:" + latlng.getLatitude() + "\nLong:" + latlng.getLongitude();
					gpsVal =  latLongString;
					Log.d("path",file);
					Log.d("filePath",filePath);
					Log.d("gpsVal", ""+gpsVal);

					if(file.contains("mp4")) RM.addVideoList(filePath, gpsVal, new Date());
					else if(file.contains("m4a")) RM.addVoiceList(filePath, gpsVal, new Date());

					isRecord = true;
					selectedFilePath = filePath + "\n" +"�쐞移�: " + gpsVal; 
				}
			} 
		}; 
		return observer; 
	} 
	
	/**
	 * @author ieunseong
	 * @content save log data on ~/FOOTTRIP
	 * */
	public boolean saveLogData(){
		FileOutputStream fos;
		String deviceIndependentRootAddress = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
		File file  = new File(deviceIndependentRootAddress + "/FOOTTRIP");
		Log.d("tests", "save start");
		if(!file.exists()){
			file.mkdir(); 
			Log.d("mkdir","make directory");
		}

		//Write file in the FOOTTRIP
		try {
			Date dt = new Date();
			String dateInfo = ""+toStr(dt.getYear()-100)+toStr(dt.getMonth()+1 )+toStr(dt.getDate())+"_"+toStr(dt.getHours())+toStr(dt.getMinutes())+toStr(dt.getSeconds());

			fos = new FileOutputStream(deviceIndependentRootAddress + "/FOOTTRIP/LogList"+dateInfo+".dat");

			ObjectOutputStream objectout = new ObjectOutputStream(fos);
			objectout.writeObject(RM);
			objectout.reset();

			Log.d("RM stored?","yes");
			fos.close();
		}
		catch (Exception e) 
		{// TODO: handle exception
			Log.e("File error #1:", e.getMessage());
			return false;
		}

		return true;
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

			//
			this.location = location;
			//
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

	/**
	 * @author ieunseong
	 * @category non-functional method
	 * @comment return date info which is one-digit to two digit 
	 *			like (Month:7/Day:9) _ 79 —> 0709
	 *	*/
	public String toStr(int d){
		if(d<10) return "0"+d;
		return ""+d;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if(mLocationClient.isConnected())
            mLocationClient.disconnect();
    	mNoti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNoti.cancel(1);
		
		Toast.makeText(getApplicationContext(), "saving try: "+isRecord, Toast.LENGTH_SHORT).show();

		//when user stop recording store recorded data
		if(isRecord) saveLogData();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}