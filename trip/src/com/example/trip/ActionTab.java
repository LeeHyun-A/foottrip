package com.example.trip;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



public class ActionTab extends ActionBarActivity {


	ActionBar ab;
	ActionBar.Tab firstTab;
	ActionBar.Tab secondTab;
	ActionBar.Tab thirdTab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actiontab);




		ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//NAVIGATION_MODE_TABS : 액션바에 여러 개의 탭을 배치하여 페이지를 전환한다.

		//firstTab
		//newTab을 하면 탭의 개수를 늘릴 수 있다. setText를 이용해 탭의 이름을 정해준다.
		firstTab = ab.newTab().setText("Tab1");
		Fragment frag = new StartTab();//이런식으로 내가 만든 fragment를 넣을 수 있다.
		firstTab.setTabListener(new TabListener(frag));
		ab.addTab(firstTab);//마지막에 tab을 추가 해줘야 한다.

		//secondTab
		//		secondTab = ab.newTab().setText("Tab2");
		//		frag = TabFragment.newInstance("Tab2");
		//		secondTab.setTabListener(new TabListener(frag));
		//		ab.addTab(secondTab);
		///webview
		secondTab = ab.newTab().setText("Tab2");
		Fragment frag2 = new ttt();
		secondTab.setTabListener(new TabListener(frag2));
		ab.addTab(secondTab);
		
		//third tab for log list
		thirdTab = ab.newTab().setText("Tab3");
		Fragment frag3 = new ListTmp();
		thirdTab.setTabListener(new TabListener(frag3));
		ab.addTab(thirdTab);
		


		if (savedInstanceState != null) {
			int seltab = savedInstanceState.getInt("seltab");
			ab.setSelectedNavigationItem(seltab);
		}
		Log.i("servicecheck", "false");
		//서비스가 만약 실행중이라면 stoptab화면으로 바로 바꿔줘야 한다.
		if(isMyServiceRunning(getApplicationContext(), "com.example.trip.FusedLocationService")){
			//탭 내에서  다른 프레그먼트로 교체하는 방법.(activity와는 조금 다름)
			Fragment mFragment = new StopTab();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			//첫번째탭에 listener를 set해준다.
			getActionBar().getTabAt(0).setTabListener(new TabListener(mFragment));
			//Replacing using the id of the container and not the fragment itself
			ft.replace(R.id.tabparent, mFragment);
			ft.addToBackStack(null);//프로젝트명 뭐임
			ft.commit();
			
		}

	}
	//서비스가 실행중인지 확인하는 코드
	private boolean isMyServiceRunning(Context ctx, String s_service_name) {
		ActivityManager manager = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (s_service_name.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;


	}

	public void setFirstListener(Fragment flag){
		firstTab.setTabListener(new TabListener(flag));
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("seltab", getActionBar().getSelectedNavigationIndex());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// XML로 옵션메뉴 추가 하기
		getMenuInflater().inflate(R.menu.menu, menu);


		return true;
	} 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.logout:
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("ID", "");
			edit.putString("PASSWORD", "");
			edit.commit();

			Intent intent = new Intent(ActionTab.this, MainActivity.class);
			startActivity(intent);

			android.os.Process.killProcess(android.os.Process.myPid());

			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}


}
