package com.example.trip;

import java.util.ArrayList;

import com.google.android.gms.internal.lo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListTmp extends Fragment{
	ArrayList<String> list = new ArrayList<String>();
	ListView listView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {             
		View rootView = inflater.inflate(R.layout.listtmp, container, false);

		listView = (ListView)rootView.findViewById(R.id.list);

		final DBAdapter db= new DBAdapter(getActivity().getApplicationContext());
		// ---get all contacts---
		db.open();
		Cursor c = db.getAllContacts(); 
		if (c.moveToFirst()) {
			do { 
				//DisplayContact(c); 
				Log.i("db-list", c.getString(0));
				if(!list.contains(c.getString(0)))
					list.add(c.getString(0));
			} while (c.moveToNext()); 
		}
		db.close(); 			

		String str[] = new String[list.size()];
		str = list.toArray(str);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,str);

		listView.setAdapter(aa);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//¸ÊÀ» ¶ç¿î´Ù.
				Log.i("<<ITEM>>", "CLICK");
				SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = pref.edit();
//				edit.putInt("STATE", 1);
				edit.putString("INDEX", list.get(arg2));
				edit.commit();
				Log.i("<<ITEM>>", list.get(arg2));


				Intent map=new Intent(getActivity().getApplicationContext(), ListMapActivity.class);
				startActivity(map);
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
				adb.setTitle("Delete?");
				adb.setMessage("Are you sure you want to delete " + listView.getItemAtPosition(position).toString());
				final int positionToRemove = position;
				adb.setNegativeButton("Cancel", null);
				adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						list.remove(listView.getItemAtPosition(positionToRemove).toString());
						db.open();
						db.deleteContact(Integer.parseInt(listView.getItemAtPosition(positionToRemove).toString()));
						db.close();

						//refresh
						Fragment mFragment = new ListTmp();
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						getActivity().getActionBar().getTabAt(2).setTabListener(new TabListener(mFragment));
						ft.replace(R.id.tabparent, mFragment);
						ft.addToBackStack(null);
						ft.commit();
					}});
				adb.show();
				return false;
			}
		});

		return rootView;
	}


}
