package com.example.trip;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.view.View;

public class TabListener implements ActionBar.TabListener {
	private Fragment mFragment;
	private View rootView;

	public TabListener(Fragment fragment) {
		mFragment = fragment;
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.add(R.id.tabparent, mFragment, "tag");
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
}    
