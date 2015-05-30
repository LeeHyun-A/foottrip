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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class FusedLocationService3 extends Service implements 
GoogleApiClient.ConnectionCallbacks, 
GoogleApiClient.OnConnectionFailedListener, 
LocationListener
{

	private static final String TAG = "FuseLocationServie";
	private static final long INTERVAL = 1000*10; //10 second
	private static final long FASTEST_INTERVAL = 1000; //1 second

	LocationRequest mLocationRequest;
	GoogleApiClient mGoogleApiClient;
	Location mCurrentLocation;

	protected void createLocationRequest(){
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		mLocationRequest.setNumUpdates(9999);//?????
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		//PRIORITY_BALANCED_POWER_ACCURACY
		//PRIORITY_HIGH_ACCURACY
	}


	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(10*1000)//10second
			.setFastestInterval(1000)
			.setNumUpdates(9999)
			.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

	public static final String LOCATION_RECEIVED = "fused.location.received";
	private Long now;
	private int count = 0;
	private String lat="";
	private String lng="";
	private String date = "";

	/*
	 * For City name variable
	 * */
	ArrayList<LatLng> latlngArr = new ArrayList<LatLng>();/////gps list
	ArrayList<String> cityNameArr = new ArrayList<String>();


	//camera
	//member variable for File Read and Write
	private FileObserver pOb, rOb;
	//object for Record data
	private RecordModel RM = new RecordModel();
	//boolean which check if device record
	private boolean isRecord = false;
	//to indicate file path data(for test, will remove) 이변수랑 관련된 모든건 테스트용임

	public static Double currentLat,currentLong;//이 것을 이용해서 그려준다.

	@Override
	public void onCreate() {
		super.onCreate();

		if (!isGooglePlayServicesAvailable()) {
			/*
			 * TODO
			 * 서비스 종료해야함 ex) finish 처럼  
			 * */
			stopSelf();
		}
		createLocationRequest();
		/**
		 * @author LeeHyun-A
		 * @content notification
		 * */
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Context.NOTIFICATION_SERVICE);
		registerReceiver(buttonBroadcastReceiver, intentFilter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		now = Long.valueOf(System.currentTimeMillis());
		/*
		 * GPS section
		 * */
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addApi(LocationServices.API)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.build();
		mGoogleApiClient.connect();

		/*
		 * Camera observe section
		 * */
		String cameraDirectory = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera"; 
		String recordDirectory = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sounds";
		pOb = initSingleDirectoryObserver(cameraDirectory);
		rOb = initSingleDirectoryObserver(recordDirectory);
		pOb.startWatching();
		rOb.startWatching();
		return START_STICKY;
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("FusedLocationService", "Fused Location Provider got connected successfully");
		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		int pause = pref.getInt("PAUSE", 0);
		if(pause == 0){//not pause
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
		else{//pause

		}
	}
	@Override
	public void onConnectionSuspended(int i) {
		Log.d("FusedLocationService","Fused Location Provider got disconnected successfully");
		stopSelf();
	}
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("FusedLocationService", "Error connecting to Fused Location Provider");
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
			Log.d("FusedLocationService", "Location received successfully ["+location.getLatitude()+","+location.getLongitude()+"] in "+(Long.valueOf(System.currentTimeMillis()-now))+" ms");
			//mCurrentLocation = location;
			mCurrentLocation = getLastBestStaleLocation(location);
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			int pause = pref.getInt("PAUSE", 0);
			if(pause == 0){//not pause
				sendLocationUsingBroadCast(mCurrentLocation);
			}
	}

	public Location getLastBestStaleLocation(Location currentLocation) {
		Location bestResult = currentLocation;

		LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Location lastFusedLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		Location networkLocation = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Location gpsLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		//long gptime = gpsLocation.getTime();
		//long nttime = networkLocation.getTime();
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
	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			/*
			 * TODO 
			 * 구글 플레이 서비스 연결 실패시 에러 메세지 다이얼로그 생성해야함  
			 * */
			return false;
		}
	}

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
	private boolean isRunning = false;

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
		}
		else{
			isRunning=false;
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
					Location latlng = mCurrentLocation;
					String latLongString;
					if(latlng == null){
						//latLongString= "Lat:" + 0.0 + "\nLong:" + 0.0;
						latLongString = 0.0+","+0.0;
					}
					else{
						Log.i("service-", latlng.toString());
						//latLongString= "Lat:" + latlng.getLatitude() + "\nLong:" + latlng.getLongitude();
						latLongString= latlng.getLatitude() + "," + latlng.getLongitude();
					}
					gpsVal =  latLongString;
					Log.d("path",file);
					Log.d("filePath",filePath);
					Log.d("gpsVal", ""+gpsVal);
					//Distinguish photo and video
					if(file.contains("jpg")) RM.addPhotoList(filePath, gpsVal, FootTripDate.DateToINTString(new Date()));
					//               else if(file.contains("mp4")) RM.addVideoList(filePath, gpsVal, new Date());

					isRecord = true;
				}else if (event == FileObserver.MOVED_TO) {
					//Detecting write operation of voice file
					Location latlng = mCurrentLocation;
					String latLongString;
					if(latlng == null){
						//latLongString= "Lat:" + 0.0 + "\nLong:" + 0.0;
						latLongString = 0.0+","+0.0;
					}
					else{
						Log.i("service-2", latlng.toString());
						//latLongString= "Lat:" + latlng.getLatitude() + "\nLong:" + latlng.getLongitude();
						latLongString= latlng.getLatitude() + "," + latlng.getLongitude();
					}
					gpsVal =  latLongString;
					Log.d("path",file);
					Log.d("filePath",filePath);
					Log.d("gpsVal", ""+gpsVal);
					if(file.contains("mp4")) RM.addVideoList(filePath, gpsVal, FootTripDate.DateToString(new Date()));
					else if(file.contains("m4a")) RM.addVoiceList(filePath, gpsVal, FootTripDate.DateToString(new Date()));
					isRecord = true;
				}
			} 
		}; 
		return observer; 
	}

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
		/*
      edit.putString("LAT"+(count), Double.toString(location.getLatitude()));      
      edit.putString("LNG"+(count), Double.toString(location.getLongitude()));*/
		edit.putInt("COUNT", count);
		edit.putString("DBLAT", lat);
		edit.putString("DBLNG", lng);
		edit.putString("DBDATE", this.date);
		edit.commit();//do alwase!!

		Log.d("service-count", Integer.toString(count));


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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mkList();

		if(mGoogleApiClient.isConnected())
			mGoogleApiClient.disconnect();
		unregisterReceiver(buttonBroadcastReceiver);
		Toast.makeText(getApplicationContext(), "saving try: "+isRecord, Toast.LENGTH_SHORT).show();

		//when user stop recording store recorded data
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


	@Override
	public IBinder onBind(Intent intent) {
		return null;
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
}