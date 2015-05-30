//package com.example.trip;
//
//import java.util.Date;
//
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.ActivityRecognitionClient;
//
//public class RecognationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener {
//
//	Thread serviceThread;
//	boolean isRunning = true;
//
//	Location location;
//	Context c;
//
//	//
//	private ActivityRecognitionClient arclient;
//	private PendingIntent pIntent;
//	private BroadcastReceiver receiver;
//	private String state = "";
//	private String date = "";
//	
//	@Override 
//	public int onStartCommand(Intent  intent,  int flags, int startId) { 
//		//  TODO Launch a background  thread to do processing. 
//		int resp =GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//		if(resp == ConnectionResult.SUCCESS){
//			arclient = new ActivityRecognitionClient(this, this, this);
//			arclient.connect();
//		}
//		else{
//			Toast.makeText(this, "Please install Google Play Service.", Toast.LENGTH_SHORT).show();
//		}
//
//		receiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				String tem = intent.getStringExtra("Activity");
//				savePreference(tem);
//				Log.d("RECOG",tem);
//			}
//		};
//
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("com.kpbird.myactivityrecognition.ACTIVITY_RECOGNITION_DATA");
//		registerReceiver(receiver, filter);
//
//		return Service.START_STICKY; 
//	} 
//	public void savePreference(String state){
//		Log.d("recog", "save");
//		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
//		SharedPreferences.Editor edit = pref.edit();
//
//		if(this.date.equals("") && this.state.equals("")){
//			this.date += (new Date()).toString();
//			this.state += state;
//		}
//		else{
//			this.date += ","+(new Date()).toString();
//			this.state += ","+state;		
//		}
//		
//		edit.putString("RECOG-DATE", this.date);
//		edit.putString("RECOG-STATE", this.state);
//        edit.commit();
//		
//	}
//
//	public void onCreate() { 
//		//  TODO:  Actions to perform  whenservice is created. 
//	} 
//	@Override 
//	public IBinder onBind(Intent  intent) { 
//		//  TODO: Replace withservice binding implementation. 
//		return  null; 
//	} 
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		if(arclient!=null){
//			arclient.removeActivityUpdates(pIntent);
//			arclient.disconnect();
//		}
//		unregisterReceiver(receiver);
//	}
//
//	@Override
//	public void onConnectionFailed(ConnectionResult arg0) {
//		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
//	}
//	@Override
//	public void onConnected(Bundle arg0) {
//		Intent intent = new Intent(this, ActivityRecognitionService.class);
//		pIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//		arclient.requestActivityUpdates(1000, pIntent);   
//	}
//	@Override
//	public void onDisconnected() {
//		Intent intent = new Intent(this, ActivityRecognitionService.class);
//		pIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//		arclient.removeActivityUpdates(pIntent); 
//		
//		stopSelf();
//	}
//}
