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
		//NAVIGATION_MODE_TABS : �׼ǹٿ� ���� ���� ���� ��ġ�Ͽ� �������� ��ȯ�Ѵ�.

		//firstTab
		//newTab�� �ϸ� ���� ������ �ø� �� �ִ�. setText�� �̿��� ���� �̸��� �����ش�.
		firstTab = ab.newTab().setText("Tab1");
		Fragment frag = new StartTab();//�̷������� ���� ���� fragment�� ���� �� �ִ�.
		firstTab.setTabListener(new TabListener(frag));
		ab.addTab(firstTab);//�������� tab�� �߰� ����� �Ѵ�.

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
		//���񽺰� ���� �������̶�� stoptabȭ������ �ٷ� �ٲ���� �Ѵ�.
		if(isMyServiceRunning(getApplicationContext(), "com.example.trip.FusedLocationService")){
			//�� ������  �ٸ� �����׸�Ʈ�� ��ü�ϴ� ���.(activity�ʹ� ���� �ٸ�)
			Fragment mFragment = new StopTab();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			//ù��°�ǿ� listener�� set���ش�.
			getActionBar().getTabAt(0).setTabListener(new TabListener(mFragment));
			//Replacing using the id of the container and not the fragment itself
			ft.replace(R.id.tabparent, mFragment);
			ft.addToBackStack(null);//������Ʈ�� ����
			ft.commit();
			
		}

	}
	//���񽺰� ���������� Ȯ���ϴ� �ڵ�
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

		// XML�� �ɼǸ޴� �߰� �ϱ�
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
