//package com.example.trip;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//
//
//
//public class LocationService extends Service{
//
//	private int count = 0;
//	private String lat="";
//	private String lng="";
//
//	LocationManager lm;
//	String mProvider;
//	int interval = 10;//10초에 35m
//
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		//현재 좌표를 얻을 수 있는 최적의 것을 선택함.gps, network, cel\l tower 중.
////		mProvider = lm.getBestProvider(new Criteria(), true);
////		if(mProvider == null){
////			Log.d("PROVIDER", "null");
////		}else{
////			Log.d("PROVIDER", mProvider);
////		}
////
////		//최적의 provider를 이용해서 계속 부름. (provider, 다시부를 시간, 거리, listener)
////		//거리는 몇 미터 이내가 아닐 경우 다시 부르게 한다. 이걸 이용해서 배터리 소모 줄일 수 있을 듯.
////		//지금은 테스트 이므로 0.
////		lm.requestLocationUpdates(mProvider, interval*1000, 0, locationListener);
//		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval*1000, 0, locationListener);
//		  
//
//		Log.d("ONSTART", "UPDATE");
//		return Service.START_STICKY;
//	}
//	private LocationListener locationListener = new LocationListener() {
//
//		@Override
//		public void onLocationChanged(Location location) {
//			Log.d("LOCATION", "CHANGE");
//			//sqlite에 저장하기 취해.
//			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
//			SharedPreferences.Editor edit = pref.edit();
//			edit.putString("DBLAT", "");
//			edit.putString("DBLNG", "");
//			edit.commit();
//			if(lat.equals("") && lng.equals("")){
//				lat += location.getLatitude();
//				lng += location.getLongitude();				
//			}
//			else{
//				lat += ","+location.getLatitude();
//				lng += ","+location.getLongitude();
//			}
//			Log.i("SER-LAT", lat);
//			Log.i("SER-LNG", lng);
//			
//			
//			count++;
//			/**@brief store data to show locations**/
//			pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
//			edit = pref.edit();
//
//			edit.putString("LAT"+(count), Double.toString(location.getLatitude()));		
//			edit.putString("LNG"+(count), Double.toString(location.getLongitude()));
//			edit.putInt("COUNT", count);
//			edit.putString("DBLAT", lat);
//			edit.putString("DBLNG", lng);
//			edit.commit();//do alwase!!
//			
//			Log.d("service-count", Integer.toString(count));
//			Log.d("LATLNG", Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude()));
//
//			String tmp = lm.getBestProvider(new Criteria(), true);
//			if(!mProvider.equals(tmp)){
//				mProvider = tmp;
//				Log.d("BEST", mProvider);
//				lm.removeUpdates(locationListener);
//				lm.requestLocationUpdates(mProvider, interval*1000, 0, locationListener);
//			}
//
//		
//		}
//
//		@Override
//		public void onProviderDisabled(String provider) {}
//
//		@Override
//		public void onProviderEnabled(String provider) {}
//
//		@Override
//		public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//	};
//	@Override
//	public void onCreate() {
//		// TODO Auto-generated method stub
//		super.onCreate();
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	
//
//		count = 0;
//		lm.removeUpdates(locationListener);
//	}
//
//
//}
