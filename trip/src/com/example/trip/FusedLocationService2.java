package com.example.trip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class FusedLocationService2 extends Service{

	private static final String TAG = "BOOMBOOMTESTGPS";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 10000;
	private static final int LOCATION_FASTESTINTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;

	private Handler handler = new Handler();

	private FileObserver pOb, rOb;
	private RecordModel RM = new RecordModel();
	private String selectedFilePath = "입력된 파일 정보 없음.";

	Location location;
	double latitude; // latitude  
	double longitude; // longitude

	private boolean isRunning = false;
	private boolean isRecord = false;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;

	private String lat="";
	private String lng="";
	private String date = "";
	public static Double currentLat,currentLong;//이 것을 이용해서 그려준다.
	private int count = 0;

	private Long now;

	/*
	 * For City name variable
	 * */
	ArrayList<LatLng> latlngArr = new ArrayList<LatLng>();/////gps list
	ArrayList<String> cityNameArr = new ArrayList<String>();


	private class LocationListener implements android.location.LocationListener{
		Location mLastLocation;
		public LocationListener(String provider)
		{
			Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}
		@Override
		public void onLocationChanged(Location location)
		{
			Log.e(TAG, "onLocationChanged: " + location);
			
			
			mLastLocation.set(location);
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			int pause = pref.getInt("PAUSE", 0);
			if(pause == 0){//not pause
				sendLocationUsingBroadCast(this.mLastLocation);
			}
		}
		@Override
		public void onProviderDisabled(String provider)
		{
			Log.e(TAG, "onProviderDisabled: " + provider);            
		}
		@Override
		public void onProviderEnabled(String provider)
		{
			Log.e(TAG, "onProviderEnabled: " + provider);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			Log.e(TAG, "onStatusChanged: " + provider);
		}
	}

	LocationListener[] mLocationListeners = new LocationListener[] {
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER)
	};
	
	public Location getLastBestStaleLocation() {
	      Location bestResult = null;
	      
	      LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	      
	     // Location lastFusedLocation=LocationServices.FusedLocationApi.getLastLocation();
	      
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
//	      if (bestResult != null && lastFusedLocation != null) {
//	         if (bestResult.getTime() < lastFusedLocation.getTime())
//	            bestResult = lastFusedLocation;
//	      }

	      return bestResult;
	   }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);   

		now = Long.valueOf(System.currentTimeMillis());

		//////////////////camera////
		String cameraDirectory = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera"; 
		String recordDirectory = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sounds";

		pOb = initSingleDirectoryObserver(cameraDirectory);
		rOb = initSingleDirectoryObserver(recordDirectory);

		pOb.startWatching();
		rOb.startWatching();


		return START_STICKY;
	}

	@Override
	public void onCreate()
	{
		Log.e(TAG, "onCreate");
		super.onCreate();

		/**
		 * @author LeeHyun-A
		 * @content notification
		 * */
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Context.NOTIFICATION_SERVICE);
		registerReceiver(buttonBroadcastReceiver, intentFilter);

		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		initializeLocationManager();
		mLocationManager.getBestProvider(criteria, true);

		/*
		try{
			isGPSEnabled = mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// getting network status
			isNetworkEnabled = mLocationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			}else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					mLocationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							LOCATION_INTERVAL,
							LOCATION_DISTANCE, mLocationListeners[1]);
					Log.d("Network", "Network");
					if (mLocationManager != null) {
						location = mLocationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						mLocationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								LOCATION_INTERVAL,
								LOCATION_DISTANCE, mLocationListeners[0]);
						Log.d("GPS Enabled", "GPS Enabled");
						if (mLocationManager != null) {
							location = mLocationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		 */

		try {		
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
					mLocationListeners[1]);
		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		}
		try {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
					mLocationListeners[0]);

		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		}
	}

	@Override
	public void onDestroy()
	{
		Log.e(TAG, "onDestroy");
		super.onDestroy();

		mkList();

		unregisterReceiver(buttonBroadcastReceiver);
		Toast.makeText(getApplicationContext(), "saving try: "+isRecord, Toast.LENGTH_SHORT).show();

		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.i(TAG, "fail to remove location listners, ignore", ex);
				}
			}
		}

		String fileInfoStr = null;
		if(isRecord||true) {
			Log.i("save", "log-data");
			fileInfoStr = saveLogData();

			//Show Map   //여기에 현재 여행한 기록반영하여 작업하도록 수정해주어야함
			Intent intent = new Intent(this, MapActivity.class);
			Bundle bund = new Bundle();
			//   Add Log Img name in bundle
			bund.putString("ID_Key", fileInfoStr);
			//   Add GPS data in bundle
			SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
			bund.putStringArray("LAT", pref.getString("DBLAT", "").split(","));
			bund.putStringArray("LNG", pref.getString("DBLNG", "").split(","));

			intent.putExtras(bund);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);      
		}
	}

	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager");
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}


	//START : Broadcast receiver

	/**
	 * @author LeeHyun-A
	 * @content broadcast for notification */
	private BroadcastReceiver buttonBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("broad", "click");
			Bundle answerBundle = intent.getExtras();
			int notiNum = answerBundle.getInt("noti");
			if( notiNum == 1){
				Log.i("noti", "pause");
				pauseMethod();
			}else if(notiNum == 2){
				Log.i("noti", "stop");
				stopMethod();
			}
		}
	};

	private void stopMethod(){
		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("service-noti", "true");
		edit.commit();

		Intent intent = new Intent(this, StartActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}
	private void pauseMethod(){
		Log.i("STATE", "PAUSE");
		if(!isRunning){
			isRunning=true;
			handler.sendEmptyMessage(0);
		}
		else{
			isRunning=false;
			handler.removeMessages(0);
		}

		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		if (pref.getInt("PAUSE", 0) == 0){
			//When service playing, so pause
			edit.putInt("PAUSE", 1);         
		}
		else{//When pause, so playing
			edit.putInt("PAUSE", 0);
		}

		edit.putInt("PAUSE-UPDATE", 1);   
		edit.commit();
		Intent intent = new Intent(this, StartActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

	private void sendLocationUsingBroadCast(Location location) {

		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("DBLAT", "");
		edit.putString("DBLNG", "");
		edit.commit();

		currentLat = location.getLatitude();
		currentLong = location.getLongitude();

		if(lat.equals("") && lng.equals("") && date.equals("")){
			lat += currentLat;
			lng += currentLong;
			date += (new Date()).toString();

		}
		else{
			lat += ","+currentLat;
			lng += ","+currentLong;
			date += ","+(new Date()).toString();
		}

		Log.i("SER-LAT", lat);
		Log.i("SER-LNG", lng);


		count++;        

		/**@brief store data to show locations**/
		pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		edit = pref.edit();
		edit.putInt("COUNT", count);
		edit.putString("DBLAT", lat);
		edit.putString("DBLNG", lng);
		edit.putString("DBDATE", this.date);
		edit.commit();//do alwase!!

		Log.d("service-count", Integer.toString(count));


	}
	//END : Broadcast receiver


	//START : FileObserver
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
				Log.i("camera", "event-here");
				String filePath = dirPath + "/" + file;
				String gpsVal="No location found";

				if (event == FileObserver.CREATE) {
					//Detecting write operation of camera file
					//Location latlng = location;
					Location latlng = mLocationListeners[0].mLastLocation;
					String latLongString;
					if(latlng == null){
						latLongString= "Lat:" + 0.0 + "\nLong:" + 0.0;
					}
					else{
						Log.i("service-", latlng.toString());
						latLongString= "Lat:" + latlng.getLatitude() + "\nLong:" + latlng.getLongitude();
					}
					gpsVal =  latLongString;
					Log.d("path",file);
					Log.d("filePath",filePath);
					Log.d("gpsVal", ""+gpsVal);
					//Distinguish photo and video
					if(file.contains("jpg")) RM.addPhotoList(filePath, gpsVal, FootTripDate.DateToString(new Date()));
					//               else if(file.contains("mp4")) RM.addVideoList(filePath, gpsVal, new Date());

					isRecord = true;
					selectedFilePath = filePath + "\n" +"�쐞移�: " + gpsVal; 
				}else if (event == FileObserver.MOVED_TO) {
					//Detecting write operation of voice file
					//Location latlng = location;
					Location latlng = mLocationListeners[0].mLastLocation;
					String latLongString;
					if(latlng == null){
						latLongString= "Lat:" + 0.0 + "\nLong:" + 0.0;
					}
					else{
						Log.i("service-2", latlng.toString());
						latLongString= "Lat:" + latlng.getLatitude() + "\nLong:" + latlng.getLongitude();
					}
					gpsVal =  latLongString;
					Log.d("path",file);
					Log.d("filePath",filePath);
					Log.d("gpsVal", ""+gpsVal);
					if(file.contains("mp4")) RM.addVideoList(filePath, gpsVal, FootTripDate.DateToString(new Date()));
					else if(file.contains("m4a")) RM.addVoiceList(filePath, gpsVal, FootTripDate.DateToString(new Date()));

					isRecord = true;
					selectedFilePath = filePath + "\n" +"�쐞移�: " + gpsVal; 
				}
			} 
		}; 
		return observer; 
	}

	public String saveLogData(){
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

			fos = new FileOutputStream(deviceIndependentRootAddress + "/FOOTTRIP/LogList_"+dateInfo+".dat");

			ObjectOutputStream objectout = new ObjectOutputStream(fos);
			objectout.writeObject(RM);
			objectout.reset();

			Log.d("RM stored?","yes");
			fos.close();
			return dateInfo;
		}
		catch (Exception e) 
		{// TODO: handle exception
			Log.e("File error #1:", e.getMessage());
		}

		return null;
	}
	/**
	 * @author ieunseong
	 * @category non-functional method
	 * @comment return date info which is one-digit to two digit 
	 *         like (Month:7/Day:9) _ 79 —> 0709
	 *   */
	public String toStr(int d){
		if(d<10) return "0"+d;
		return ""+d;
	}

	//END : File Observer

	//START : region section
	public void mkList(){
		SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

		String re_state[] = pref.getString("RECOG-STATE", "").split(",");
		String re_date[] = pref.getString("RECOG-DATE", "").split(",");
		String lat[] = pref.getString("DBLAT", "").split(",");
		String lng[] = pref.getString("DBLNG", "").split(",");
		String date[] = pref.getString("DBDATE", "").split(",");

		for(int i = 0; i<lat.length;i++){
			latlngArr.add(new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lng[i])));
		}
		convertToLocation(); 
		String regionCode = getMaxCityCode();
		String gpsPath = "";
		for(int i = 0; i < lat.length; i++){
			gpsPath += lat[i]+","+lng[i]+",";
		}

		RM.setLogPaths(gpsPath.substring(0,gpsPath.length()-1));
		RM.setRegionCode(regionCode);

	}
	public String getMaxCityCode(){

		HashMap<String, Integer> cityCnt = new HashMap<String, Integer>();
		ArrayList<String> keyArr = new ArrayList<String>();

		//calculate count of cityname
		for(int i = 0; i < cityNameArr.size(); i++){
			if(!cityCnt.containsKey(cityNameArr.get(i))){//not contains
				cityCnt.put(cityNameArr.get(i), 1);
				keyArr.add(cityNameArr.get(i));
			}else{
				cityCnt.put(cityNameArr.get(i), cityCnt.get(cityNameArr.get(i)) + 1);
			}
		}

		//find max count city names 
		int max = 0;
		ArrayList<String> maxKeyList = new ArrayList<String>();

		for(int i = 0; i < keyArr.size(); i++){
			int value = cityCnt.get(keyArr.get(i));
			if(max < value){
				max = value;
				if ( maxKeyList.size() >= 1 ){
					maxKeyList.clear();
				}
				maxKeyList.add(keyArr.get(i));
			}
			else if(max == value){
				maxKeyList.add(keyArr.get(i));
			}
		}
		String regionCode = "";
		Region r = new Region();
		for(int i = 0; i < maxKeyList.size(); i++){
			if(regionCode.equals("")){
				regionCode = Integer.toString(r.getRegionID(maxKeyList.get(i)));
			}
			else{
				regionCode += "," + Integer.toString(r.getRegionID(maxKeyList.get(i)));
			}
		}
		return regionCode;
	}

	public void convertToLocation() {   
		Log.e("fnct","ConvertToLocation");
		List<Address> addresses = null;
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		try {
			Log.e("latlngArr", "size"+latlngArr.size());
			for(int i = 0; i < latlngArr.size(); i++){
				LatLng point = latlngArr.get(i);

				addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
				Log.e("address", addresses.get(0).getAddressLine(0));
				String city = addresses.get(0).getLocality();
				Log.e("city", city);


				for(int j = 0; j < Region.region.length; j++){
					if(city.toLowerCase().contains(Region.region[j].toLowerCase())){
						Log.d(city, "->"+Region.region[j]);
						cityNameArr.add(Region.region[j]);
						break;
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Here 1 represent max location result to returned, by documents it recommended 1 to 5

	}
	//END : region section
}
