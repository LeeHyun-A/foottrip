package com.example.trip;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity implements OnMapClickListener {
	GoogleMap mGoogleMap;
	String LogImgName;
	LatLngBounds mBound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		String coo1[] = {"37.514993", "37.514108", "37.513845","37.512823","37.512074","37.511351"};
		String coo2[] = {"127.015752", "127.015419", "127.015526", "127.015521","127.015585","127.015741"};

		Bundle bund = getIntent().getExtras();
		String tmpStr="";
		if(bund!=null) {
			LogImgName = bund.getString("ID_Key");
			coo1 = bund.getStringArray("LAT");
			coo2 = bund.getStringArray("LNG");
			for(int i=0;i<coo1.length;i++)
				tmpStr += coo1[i]+", "+coo2[i]+"\n";
		}
		Toast.makeText(getApplicationContext(), tmpStr, Toast.LENGTH_LONG).show();
		init(coo1, coo2);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				saveSnapshot();
			}
		}, 5000);

	}
	public void saveSnapshot(){
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
			Bitmap bitmap;

			@Override
			public void onSnapshotReady(Bitmap snapshot) {
				// TODO Auto-generated method stub
				bitmap = snapshot;
				try {
					FileOutputStream out;
					String deviceIndependentRootAddress = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
					File file  = new File(deviceIndependentRootAddress + "/FOOTTRIP/logimg");
					Log.d("tests", "save start");
					if(!file.exists()){
						file.mkdir();
						Log.d("mkdir","make directory");
					}

					out = new FileOutputStream(deviceIndependentRootAddress + "/FOOTTRIP/logimg/"+LogImgName+".png");

					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		mGoogleMap.snapshot(callback);
		//		finish();
	}

	public String toStr(int d){
		if(d<10) return "0"+d;
		return ""+d;
	}
	public void onMapClick(LatLng point) {
		Point screenPt = mGoogleMap.getProjection().toScreenLocation(point);

		Log.d("∏ ¡¬«•","¡¬«•: ¿ßµµ(" + String.valueOf(point.latitude) + "), ∞Êµµ(" + String.valueOf(point.longitude) + ")");
		Log.d("»≠∏È¡¬«•","»≠∏È¡¬«•: X(" + String.valueOf(screenPt.x) + "), Y(" + String.valueOf(screenPt.y) + ")");
	}

	private void init(String coo1[], String coo2[]) {

		Intent getI = getIntent();

		String title = getI.getStringExtra("title");
		//		String coordinates[] = { "37.517180", "127.041268" };

		int minLat = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int minLon = Integer.MAX_VALUE;
		int maxLon = Integer.MIN_VALUE;

		//Variable for setting zoom level
		LatLngBounds.Builder builder = LatLngBounds.builder();
		LatLng old_point = null;
		for(int i = 0 ; i < coo1.length; i++){
			LatLng position = new LatLng(Double.parseDouble(coo1[i]), Double.parseDouble(coo2[i]));
			builder.include(position);
			GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapActivity.this);

			maxLat = (int) Math.max(Double.parseDouble(coo1[i]), maxLat);
			minLat = (int) Math.min(Double.parseDouble(coo1[i]), minLat);
			maxLon = (int) Math.max(Double.parseDouble(coo2[i]), maxLon);
			minLon = (int) Math.min(Double.parseDouble(coo2[i]), minLon);

			mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			mGoogleMap.setOnMapClickListener(this);
			
			if(i == (coo1.length/2)){
				LatLng po = new LatLng((Double.parseDouble(coo1[0]) +Double.parseDouble(coo1[coo1.length-1]))/2.0, (Double.parseDouble(coo2[0]) +Double.parseDouble(coo2[coo2.length-1]))/2.0);
//				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(po,  ((maxLat + minLat)/2 + (maxLon + minLon)/2)/7));
//				mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(po, 10.0f));
			}
			//mGoogleMap.addMarker(new MarkerOptions().position(position).title(title)).showInfoWindow();
			if(i==coo1.length-1 || i==0){
				mGoogleMap.addMarker(new MarkerOptions().position(position).title(title)).showInfoWindow();
			}
			if(old_point!=null){
				PolylineOptions polylineOptions = new PolylineOptions();
				polylineOptions.add(old_point);
				polylineOptions.add(position);
				mGoogleMap.addPolyline(polylineOptions);
			}
			old_point = position;
		}
		
		mBound = builder.build();
		mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				// TODO Auto-generated method stub
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBound, 299));
			}
		});

		mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				return false;
			}
		});
	}
}