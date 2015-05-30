package com.example.trip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import android.location.Address;
import android.location.Geocoder;

public class StartActivity extends Activity implements OnClickListener{
	private ImageView logo;
	//private float screenWidth;
	//private float screenHeight;

	static final String[] region = { "서울","부산","인천","대구","대전","광주","울산","수원","창원","성남","고양","용인","부천","청주",
		"안산","전주","안양","천안","남양주","포항","김해","화성","의정부","시흥","구미","제주","평택","진주","광명",
		"파주","원주","익산","아산","군포","춘천","여수","경산","군산","순천","경주","양산","목포","거제",
		"광주","김포","강릉","충주","이천","양주","구리","오산","안성","안동","서산","의왕","당진","포천",
		"하남","광양","제천","서귀포","통영","김천","공주","논산","정읍","영주","사천","밀양","상주","보령",
		"영천","동두천","동해","김제","속초","남원","나주","문경","삼척","과천","태백","계룡","세종","여주" };

	ArrayList<LatLng> latlngArr = new ArrayList<LatLng>();/////gps list
	ArrayList<String> cityNameArr = new ArrayList<String>();
	ArrayList<String> maxKeyList = new ArrayList<String>();

	private static NotificationManager mNM;
	private static Notification mNoti;
	private String mServiceNoti = "";

	public static int tem;
	private static Handler handler;
	private static boolean isRunning;
	private static int h=0,m=0,s=0;
	private static int bitmap_start_x=-1, bitmap_start_y=-1, arrange=-1 ;
	private static boolean isStartcount;

	private ImageButton stop,gomap;
	private static TextView mShowTimerView; 
	private GPSmap gm;
	private DrawingView D;

	private ImageButton btn1, btn2, btn3, btn4, btn5;
	private ImageButton startBtn,pause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		//Set GUI elements
		mShowTimerView = (TextView)findViewById(R.id.showTimerView);
		stop = (ImageButton)findViewById(R.id.stopBtn);	
		pause = (ImageButton)findViewById(R.id.pauseBtn);	
		gomap = (ImageButton)findViewById(R.id.gomapBtn);
		startBtn = (ImageButton)findViewById(R.id.startBtn);

		stop.setOnClickListener(this);
		pause.setOnClickListener(this);
		gomap.setOnClickListener(this);
		startBtn.setOnClickListener(this);

		D  = (DrawingView)findViewById(R.id.drawing);
		btn1=(ImageButton)findViewById(R.id.btn1);
		btn2=(ImageButton)findViewById(R.id.btn2);
		btn3=(ImageButton)findViewById(R.id.btn3);
		btn4=(ImageButton)findViewById(R.id.btn4);
		btn5=(ImageButton)findViewById(R.id.btn5);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		mShowTimerView.setText(h + "hour " + m + "min " + s + "sec");


		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();

		int isPause;
		//By notification
		if(isRunning){

			mServiceNoti = pref.getString("service-noti", "");
			Log.i("service-noti in trip",mServiceNoti);

			if (mServiceNoti.equals("true")){
				startBtn.setClickable(true);
				handler.removeMessages(0);
				stopMethod();
				//Show SNS page
				Intent intent = new Intent(StartActivity.this, SnsActivity.class);
				startActivity(intent);
				overridePendingTransition(0,0);
				finish();
			}
		}
		else{
			isPause = pref.getInt("PAUSE-UPDATE", 0);
			if(isPause==0){
				edit.putInt("PAUSE-UPDATE", 0);
				edit.commit();

			}

		}

		//isPause = pref.getInt("PAUSE-UPDATE", 0);
		//Log.d("isPause","ssss"+isPause);

		if(pref.getInt("PAUSE-UPDATE", 0)==0){
			Log.d("isPause","안바뀐대");
		}
		else if(pref.getInt("PAUSE-UPDATE", 0)==1){
			if(!isRunning && pref.getInt("PAUSE", 0)==0){//restart
				isRunning=true;
				handler.sendEmptyMessage(0);
			} 
			else if (isRunning && pref.getInt("PAUSE", 0)==1){//PAUSE
				isRunning=false;
				handler.removeMessages(0);
				setStartcount(false);
			}
			edit.putInt("PAUSE-UPDATE", 0);
			edit.commit();	
		}
	}
	
	private boolean isMyServiceRunning(Context ctx, String s_service_name) {
		ActivityManager manager = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (s_service_name.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	//GPS setting activity 
	private boolean chkGpsService() {
		String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {
			// If GPS turn OFF, pop up dialog 
			AlertDialog.Builder gsDialog = new 	AlertDialog.Builder(StartActivity.this); 
			gsDialog.setTitle("위치 서비스 설정");   
			gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 " +
					"위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?"); 
			gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int which) { 
					// Go to the GPS setting activity
					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS); 
					intent.addCategory(Intent.CATEGORY_DEFAULT); 
					startActivity(intent); 
				} 
			})
			.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).create().show();
			return false;

		} else { 
			return true; 
		} 
	}
	
	public void startSERVICE(){

		Log.d("!isStartcount()","ssss"+isStartcount());
		gm = new GPSmap();
		if (handler==null || !isStartcount()){
			Log.d("starthandler","starthandlerstarthandlerstarthandler");
			// 스타트 눌렀을 때, 시작. 시작되었다는 뜻. 한번 스타트누르고 핸들러 생성되면 여기 못들어온다.
			handler = new Handler() {
				public void handleMessage(Message msg) {
					tem++;
					Log.d("tem","!!!!!!!!!"+tem);
					mShowTimerView.setText(h + "hour " + m + "min " + s + "sec");
					handler.sendEmptyMessageDelayed(0,1000); 
					h=tem/3600;
					m=(tem%3600)/60;
					s=(tem%3600)%60;

					//canvas logic
					//	change the canvas state to ready if it's not
					if(!D.isReady()){
						Log.i("isReady",""+D.isReady());
						D.readyToDraw();}
					if(tem%20==0 && tem >0){
						Log.i("tem",tem+"초에 그리기시작하라.");

						//getLocation();
						gm.addGPSinBitmap(FusedLocationService3.currentLat,FusedLocationService3.currentLong);
						//gm.addGPSinBitmap(lat,lng);
						Log.i("dpem","애드후");
						//Add new GPS data in GPS model
						if(bitmap_start_x==-1 && bitmap_start_y==-1){
							bitmap_start_x = gm.getmGPSbitmap().get(0).getIdxX();
							bitmap_start_y = gm.getmGPSbitmap().get(0).getIdxY();
							return;
						}

						//call drawing method after second handler access
						D.Drawperminute(gm, bitmap_start_x, bitmap_start_y);
					}
				}
			};
		}

//		Log.d("!isStartcount()","falsetrue 나오길 : "+isStartcount()+isRunning);
		if(isRunning && !isStartcount()){
			Log.d("sendEmptyMessage","sendEmptyMessage");
			handler.sendEmptyMessage(0);
			setStartcount(true);
		}


	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if(v.getId() == startBtn.getId()){
			chkGpsService();
			//chkgpservice함수를 불러 gps를 키게 만들고 gps가 켜져있을 경우레만 start버튼을 클릭하였을 때 서비스가 실행되게 한다.
			String gps = android.provider.Settings.Secure.getString(getContentResolver(), 
					android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {}
			else{
				startBtn.setClickable(false);
				isRunning=true;
				//initial
				SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = pref.edit();
				edit.putInt("PAUSE", 0);
				edit.putInt("PAUSE-UPDATE", 0);
				edit.commit();
				Log.d("퓨즈드갈거야 퍼지몇이야","퍼지"+pref.getInt("PAUSE",0));
				startService(new Intent(StartActivity.this,FusedLocationService3.class));
				//startService(new Intent(StartActivity.this,RecognationService.class));
				D.setReady(false);
				setStartcount(false);		
				startSERVICE();
				notification();

			}
		}

		else if(v.getId() == btn1.getId()){
			Intent intent = new Intent(StartActivity.this, SnsActivity.class);
			startActivity(intent);
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == btn2.getId()){
			if (LoginFragment.isFBLogined()) {
				/* make the API call */
				startPickerActivity(PickerActivity.FRIEND_PICKER, LoginFragment.REQUEST_CODE_FOLLOWABLE, LoginFragment.PICK_TYPE_FRIENDS);
			}else{
				//이 경우는 페이스북 사용자가 아니니까 다른 추천을 해줘야함
				Toast.makeText(getApplicationContext(), "로그인 먼저하시오", Toast.LENGTH_SHORT).show();
			}
		}		
		else if(v.getId() == btn3.getId()){
			Intent startIntent = new Intent(StartActivity.this, StartActivity.class);
			startActivity(startIntent);
			overridePendingTransition(0,0);
			finish();
		}		
		else if(v.getId() == btn4.getId()){
			Intent searchIntent = new Intent(StartActivity.this, TopPathActivity.class);
			startActivity(searchIntent);
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == btn5.getId()){
			Intent profilIntent = new Intent(StartActivity.this,MyProfileActivity.class);
			startActivity(profilIntent);	
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == stop.getId()){
			//isRunning=false;
			//startBtn.setClickable(true);
			handler.removeMessages(0);
			stopMethod();
			Intent intent = new Intent(StartActivity.this, SnsActivity.class);
			startActivity(intent);
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == gomap.getId()){//route while traveling 
			Intent intent = new Intent(StartActivity.this, MapViewInservice.class);
			Bundle bund = new Bundle();
			//	Add Log Img name in bundle
			bund.putString("ID_Key", "current");
			//	Add GPS data in bundle
			SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
			bund.putStringArray("LAT", pref.getString("DBLAT", "").split(","));
			bund.putStringArray("LNG", pref.getString("DBLNG", "").split(","));
			intent.putExtras(bund);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);		
		}
		else if(v.getId() == pause.getId()){
			Log.i("STATE", "PAUSE");
			pauseMethod();
		}

	}

	private void pauseMethod(){

		if(!isRunning){//restart
			isRunning=true;
			handler.sendEmptyMessage(0);
		} 
		else{//PAUSE
			isRunning=false;
			handler.removeMessages(0);
			setStartcount(false);
		}

		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		if (pref.getInt("PAUSE", 0) == 0){
			edit.putInt("PAUSE", 1);
		}
		else{//1 : When pause, so playing
			edit.putInt("PAUSE", 0);
		}

		edit.commit();
	}

	private void stopMethod(){
		mNM.cancel(7777);

		//return to initial state
		bitmap_start_x=-1; bitmap_start_y=-1; arrange=-1;
		setStartTimer(-1);
		isRunning=false;
		h=0;m=0;s=0;
		//setStartcount(false);
		mShowTimerView.setText("0hour 0min 0sec");
		///


		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.putInt("STATE", 0);
		edit.putString("service-noti", "");
		edit.commit();

		//stop service
		//		if(!mServiceNoti.equals("true")){
		stopService(new Intent(StartActivity.this, FusedLocationService3.class));
		//		}
		//stopService(new Intent(StartActivity.this, RecognationService.class));

		String re_state[] = pref.getString("RECOG-STATE", "").split(",");
		String re_date[] = pref.getString("RECOG-DATE", "").split(",");

		pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		edit = pref.edit();
		//String latS = pref.getString("DBLAT", "");
		//String lngS = pref.getString("DBLNG", "");
		String lat[] = pref.getString("DBLAT", "").split(",");
		String lng[] = pref.getString("DBLNG", "").split(",");
		String date[] = pref.getString("DBDATE", "").split(",");
		edit.remove("DBLAT");
		edit.remove("DBLNG");
		edit.remove("DBDATE");
		for(int i = 0; i<lat.length;i++){
			latlngArr.add(new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lng[i])));
		}
		
		convertToLocation(); 
		findMaxCity();

		ArrayList<DbModel> arrDb = new ArrayList<DbModel>();
		int j = 0;
		for(int i = 0; i < lat.length; i++){
			DbModel m;
			if(j < re_date.length){
				if(date[i].compareTo(re_date[j]) < 0){
					m = new DbModel(lat[i], lng[i], "", date[i]);
					arrDb.add(m);
				}else if(date[i].compareTo(re_date[j]) == 0){
					m = new DbModel(lat[i], lng[i], re_state[j], date[i]);
					arrDb.add(m);
					j++;
				}else{
					double tmpLat;
					double tmpLng;
					if(i <= 1){
						tmpLat = Double.parseDouble(lat[0]);
						tmpLng = Double.parseDouble(lng[0]);
					}
					else{
						tmpLat = (Double.parseDouble(lat[i-1])+Double.parseDouble(lat[i]))/2.0;
						tmpLng = (Double.parseDouble(lng[i-1])+Double.parseDouble(lng[i]))/2.0;
					}
					m = new DbModel(Double.toString(tmpLat), Double.toString(tmpLng), re_state[j], re_date[j]);
					j++;
					arrDb.add(m);
					i--;
				}
			}else{
				m = new DbModel(lat[i], lng[i], "", date[i]);
				arrDb.add(m);
			}
		}
		Log.i("list-date", Integer.toString(date.length));
		Log.i("list-redate", Integer.toString(re_date.length));
		String latS = "";
		String lngS = "";
		String dateS = "";
		String stateS = "";
		for(int i = 0; i <arrDb.size();i++){
			Log.i("list", arrDb.get(i).getDate());
			Log.i("list", arrDb.get(i).getLat());
			Log.i("list", arrDb.get(i).getState());
			if(latS.equals("") && lngS.equals("") && dateS.equals("") && stateS.equals("")){
				latS += arrDb.get(i).getLat();
				lngS += arrDb.get(i).getLng();
				dateS += arrDb.get(i).getDate();
				stateS += arrDb.get(i).getState();
			}else{
				latS += ","+arrDb.get(i).getLat();
				lngS += ","+arrDb.get(i).getLng();
				dateS += ","+arrDb.get(i).getDate();
				stateS += ","+arrDb.get(i).getState();					
			}
		}
		//----- insert DB -----
		if(!lat.equals("")){
			DBAdapter db= new DBAdapter(getApplicationContext());
			db.open();
			long id = db.insertContact(latS, lngS, dateS, stateS);
			db.close();
			// ---get all contacts---
			db.open();
			Cursor c = db.getAllContacts(); 
			if (c.moveToFirst()) {
				do { 
					DisplayContact(c); 
				} while (c.moveToNext()); 
			}
			db.close();          
		}

		//Intent map=new Intent(getActivity(), MapActivity.class);
		//startActivity(map);
		//Intent intent = new Intent(StartActivity.this, SnsActivity.class);
		//startActivity(intent);
		//overridePendingTransition(0,0);
		//finish();
		//		Fragment mFragment = new StartActivity();
		//		FragmentTransaction ft = getFragmentManager().beginTransaction();
		//		getActivity().getActionBar().getTabAt(0).setTabListener(new TabListener(mFragment));
		//		//Replacing using the id of the container and not the fragment itself
		//		ft.replace(R.id.tabparent, mFragment);
		//		ft.addToBackStack(null);
		//		ft.commit();

	}

	public void notification(){ //notification method while service is running
		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);		

		mNoti = new NotificationCompat.Builder(getApplicationContext())
		.setWhen(System.currentTimeMillis())
		.setNumber(10)
		.setContentTitle("FootTrip")
		.setSmallIcon(R.drawable.f)
		.setTicker("Happy trip").setOngoing(true)
		.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), StartActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
		.setAutoCancel(true)
		.build();

		RemoteViews contentiew = new RemoteViews(getPackageName(), R.layout.notification_view);
		Intent intentPause = new Intent(Context.NOTIFICATION_SERVICE);
		Bundle pauseBundle = new Bundle();
		pauseBundle.putInt("noti", 1);
		intentPause.putExtras(pauseBundle);
		PendingIntent pausePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentPause,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentiew.setOnClickPendingIntent(R.id.pause, pausePendingIntent);
		Intent intentStop = new Intent(Context.NOTIFICATION_SERVICE);
		Bundle stopBundle = new Bundle();
		stopBundle.putInt("noti", 2);
		intentStop.putExtras(stopBundle);
		PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intentStop,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentiew.setOnClickPendingIntent(R.id.stop, stopPendingIntent);
		mNoti.contentView = contentiew;
		mNM.notify(7777, mNoti);
	}


	public void DisplayContact(Cursor c) {
		Log.e("DB", "id: " + c.getString(0) + "\n" + "LAT: " + c.getString(1) + "\n" + "LNG: " + c.getString(2));
	}

	public static void setStartTimer(int count) {
		tem=count;
	}

	public static boolean isStartcount() {
		return isStartcount;
	}

	public static void setStartcount(boolean _isStartcount) {
		isStartcount = _isStartcount;
	}

	public void findMaxCity(){

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


				for(int j = 0; j < region.length; j++){
					if(city.toLowerCase().contains(region[j].toLowerCase())){
						Log.d(city, "->"+region[j]);
						cityNameArr.add(region[j]);
						break;
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Here 1 represent max location result to returned, by documents it recommended 1 to 5

	}
	
	private void startPickerActivity(Uri data, int requestCode, String pick_type) {
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(this, PickerActivity.class);

		Bundle bd = new Bundle();
		bd.putString("pick_type", pick_type);

		intent.putExtras(bd);
		startActivityForResult(intent, requestCode);
	}
}



