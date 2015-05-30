package com.example.trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.codejava.server.FootTripJSONBuilder;
import request.codeJava.client.RequestMethods;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.foottrip.newsfeed.data.TopPlaceItem;
import com.foottrip.newsfeed.data.TopPlaceItem.PhotoInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TopPlaceActivity extends FragmentActivity implements OnClickListener{


	private TextView path_btn,place_btn;
	private ImageButton btn1,btn2,btn3,btn4,btn5,search_btn;
	private AutoCompleteTextView search = null;
	ArrayAdapter<String> adapter;
	private ArrayList<Integer> regionArr = null;
	private ArrayList<TopPlaceItem> topPlaceItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_top_place);

		Region r = new Region();

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, r.region);
		search = (AutoCompleteTextView)findViewById(R.id.editsearch);
		search.setThreshold(1);
		search.setAdapter(adapter);

		path_btn = (TextView)findViewById(R.id.path_btn);
		place_btn = (TextView)findViewById(R.id.place_btn);
		search_btn = (ImageButton)findViewById(R.id.search_btn);


		btn1 = (ImageButton)findViewById(R.id.btn1);
		btn2 = (ImageButton)findViewById(R.id.btn2);
		btn3 = (ImageButton)findViewById(R.id.btn3);
		btn4 = (ImageButton)findViewById(R.id.btn4);
		btn5 = (ImageButton)findViewById(R.id.btn5);

		search_btn.setOnClickListener(this);
		path_btn.setOnClickListener(this);
		place_btn.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == btn1.getId()){
			Intent intent = new Intent(TopPlaceActivity.this, SnsActivity.class);
			startActivity(intent);
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == btn2.getId()){
			Intent peopleIntent = new Intent(TopPlaceActivity.this,SearchPeopleActivity.class);
			startActivity(peopleIntent);
			overridePendingTransition(0,0);
			finish();

		}		
		else if(v.getId() == btn3.getId()){
			Intent startIntent = new Intent(TopPlaceActivity.this, StartActivity.class);
			startActivity(startIntent);
			overridePendingTransition(0,0);
			finish();
		}		
		else if(v.getId() == btn4.getId()){
			Intent searchIntent = new Intent(TopPlaceActivity.this, TopPathActivity.class);
			startActivity(searchIntent);
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == btn5.getId()){
			Intent profilIntent = new Intent(TopPlaceActivity.this,MyProfileActivity.class);
			startActivity(profilIntent);	
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == search_btn.getId()){
			if(search.getText().toString().equals("")){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Input text for searching")
				.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
				// Create the AlertDialog object and return it
				builder.create();
			}
			else{
				Region r = new Region();
				topPlaceItem = new ArrayList<TopPlaceItem>();
				int code = r.getRegionID(search.getText().toString());
				String topPlaceList = RequestMethods.HotPlaceRequest(Integer.toString(code));
				Log.i("place",topPlaceList);
				HashMap<String,String> dataMap = null;
				if(topPlaceList == null){
					Log.e("topPlaceList","null");
				}else{
					dataMap = FootTripJSONBuilder.jsonParser(topPlaceList);
					if(dataMap.get("accessable").equals("nack")){
						Log.e("request", "nack");
					}else{
						String JSONresult = dataMap.get("data");
						HashMap<String,String>topkMap = FootTripJSONBuilder.jsonParser(JSONresult);
						Iterator<String> keys = topkMap.keySet().iterator();
						while(keys.hasNext()){
							TopPlaceItem item = new TopPlaceItem();
							String key = keys.next();
							String[] temp = key.split("::");
							String value = topkMap.get(key);
							item.setCount(Integer.parseInt(temp[0]));
							item.setTopID(temp[1]);
							
							String[] temp2 = value.split(",");
							for(int i = 0; i<temp2.length;i++){
								String[] info = temp2[i].split("::");
								String[] gps = info[0].split("&");
								item.setPhotoInfo(info[1], gps[0], gps[1]);
								if(i==0){
									item.setLat(gps[0]);
									item.setLng(gps[1]);
								}
							}
							topPlaceItem.add(item);
							Log.e("request", "nack");
						}
						mapShow();
						getWindow().setSoftInputMode(
							    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
							);
					}
					
				}
			}

		}
		else if(v.getId() == path_btn.getId()){
			Intent pathIntent = new Intent(TopPlaceActivity.this, TopPathActivity.class);
			startActivity(pathIntent);
			overridePendingTransition(0,0);
			finish();

		}
		else if(v.getId() == place_btn.getId()){
			Intent placeIntent = new Intent(TopPlaceActivity.this, TopPlaceActivity.class);
			startActivity(placeIntent);
			overridePendingTransition(0,0);
			finish();

		}
	}
	private void mapShow(){
		Log.e("mth", "mapShow");

		GoogleMap map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.clear();

		//cluster 마다
		LatLng location = new LatLng(0,0);
		for(int i = 0; i < topPlaceItem.size(); i++){
			PhotoInfo photoInfo = topPlaceItem.get(i).getPhotoInfo().get(0);
			location = new LatLng(Double.parseDouble(photoInfo.getImageLat()), Double.parseDouble(photoInfo.getImageLng()));
			Log.e("location", location.toString());
			Marker marker = map.addMarker(new MarkerOptions()
			.position(location)
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))//cluster 마다 색 변환
			.title(topPlaceItem.get(i).getTopID()) //user name
			.snippet(location.toString()));
			map.setInfoWindowAdapter(new CustomInfoAdapterPlace(this, marker, photoInfo.getImagePath()));
		}
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));		

		//marker 클릭 후, 윈도우 클릭 시 리스너
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Toast.makeText(getApplicationContext(), marker.getId().toString(), Toast.LENGTH_SHORT).show();


			}
		});
	}

}
