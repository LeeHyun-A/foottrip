package com.example.trip;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StopTab extends Fragment implements OnClickListener{

	private ImageView logo;
	private Button stop,pause;
	private View rootView;
	private Context mContext;
	private Handler handler;
	private int h=0,s=0,m=0;
	public static int tem;
	private TextView showTimer; 
	boolean isRunning=true;
	private float screenWidth;
	private float screenHeight;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {             
		rootView = inflater.inflate(R.layout.stoptab, container, false);
		mContext = rootView.getContext();
		showTimer = (TextView)rootView.findViewById(R.id.showTimer);
		init();

		logo = (ImageView)rootView.findViewById(R.id.imageView1);
		stop = (Button)rootView.findViewById(R.id.stop);	
		stop.setOnClickListener(this);
		pause = (Button)rootView.findViewById(R.id.pause);	
		pause.setOnClickListener(this);

		/** designate location **/
		LayoutParams params = (LayoutParams) logo.getLayoutParams();
		logo.setX(0);
		logo.setY(0);
		//		params.width = (int) screenWidth;
		//		params.height = (int) screenHeight / 2;
		logo.setLayoutParams(params);

		params = (LayoutParams) showTimer.getLayoutParams();
		showTimer.setX(screenWidth*2/ 5);
		showTimer.setY(screenHeight / 2);
		showTimer.setLayoutParams(params);

		params = (LayoutParams) stop.getLayoutParams();
		stop.setX(screenWidth / 6);
		stop.setY(screenHeight * 4 / 6);
		stop.setLayoutParams(params);	

		params = (LayoutParams) pause.getLayoutParams();
		pause.setX(screenWidth*4 / 6);
		pause.setY(screenHeight * 4 / 6);
		pause.setLayoutParams(params);	

		handler = new Handler() {
			public void handleMessage(Message msg) {
				tem++;
				showTimer.setText(h + "시간 " +s + "분 " +m + "초");
				handler.sendEmptyMessageDelayed(0,1000); // 1초에 한번 UP, 1000 = 1 초
				h=tem/3600;
				s=(tem%3600)/60;
				m=(tem%3600)%60;
			}
		};
		if(isRunning)
			handler.sendEmptyMessage(0);
		/*else{
			handler.removeMessages(0);
			//showTimer.setText("0시간 0분 0초");
		}*/

		stop = (Button)rootView.findViewById(R.id.stop);
		pause = (Button)rootView.findViewById(R.id.pause);
		stop.setOnClickListener(this);
		pause.setOnClickListener(this);

		return rootView;
	}

	private void init() {
		// receive window size
		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == stop.getId()){

			tem=-1;
			isRunning=false;
			SharedPreferences pref = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			edit.putInt("STATE", 0); //service 종료 시에 맵을 보여주는 것을 구분해 주기 위해.
			edit.commit();
			//일단 서비스를 멈춘다.
			getActivity().stopService(new Intent(getActivity(), FusedLocationService.class));

			/*
			 * 서비스가 종료된 후 얻어진 좌표값이 있을 경우 서버 한 번에 저장.
			 * rowid 중 이전에 인서트 했던 것이 지워졌어도 계속해서 다음꺼로 넣어지는 듯하다.(1인서트햇다가 지우고 새로운 데이터 인서트 하면 rowid 2임)
			*/
			pref = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
			String lat = pref.getString("DBLAT", "");
			String lng = pref.getString("DBLNG", "");
			if(!lat.equals("")){
				DBAdapter db= new DBAdapter(mContext.getApplicationContext());
				db.open(); 
				long id = db.insertContact(lat, lng);
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
			/*저장하기 위한 화면.map띄워주는 것.*/
			//	         Intent map=new Intent(getActivity(), MapActivity.class);
			//	         startActivity(map);

			/**	         탭 내에서  다른 프레그먼트로 교체하는 방법.(activity와 조금 다름)
			 *	         스타트 화면으로  바꿔줌.         
			 **/
			Fragment mFragment = new StartTab();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			//첫번째탭에 listener를 set해준다.
			getActivity().getActionBar().getTabAt(0).setTabListener(new TabListener(mFragment));
			//Replacing using the id of the container and not the fragment itself
			ft.replace(R.id.tabparent, mFragment);
			ft.addToBackStack(null);
			ft.commit();
		}
		else if(v.getId() == pause.getId()){
			Log.i("STATE", "PAUSE");
			if(!isRunning){
				isRunning=true;
				handler.sendEmptyMessage(0);
			}
			else{
				isRunning=false;
				handler.removeMessages(0);
			}

			SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			//서비스가 처음부터 다시 실행되는 것을 방지하기 위해 상태저장.
			if (pref.getInt("PAUSE", 0) == 0){
				//When service playing, so pause
				edit.putInt("PAUSE", 1);
			}
			else{//When pause, so playing
				edit.putInt("PAUSE", 0);
			}

			edit.commit();//do alwase!!
		}

	}

	public void DisplayContact(Cursor c) {
		Log.e("DB", "id: " + c.getString(0) + "\n" + "Name: " + c.getString(1) + "\n" + "Email: " + c.getString(2));
	}

	public static void setStartTimer(int count) {
		tem=count;
	}

}
