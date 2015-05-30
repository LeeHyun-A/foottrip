package com.example.trip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ListMapActivity extends FragmentActivity {

	GoogleMap mGoogleMap;
	Marker marker;
	private int count;
	private double testDist = 35;
	ArrayList<Coordinate> coo;
	ArrayList<Coordinate> coo2;

	private class Coordinate{
		private Double lat;
		private Double lng;
		public Coordinate(double lat, double lng){
			this.lat = lat;
			this.lng = lng;
		}
		public Double getLat() {
			return lat;
		}
		public void setLat(Double lat) {
			this.lat = lat;
		}
		public Double getLng() {
			return lng;
		}
		public void setLng(Double lng) {
			this.lng = lng;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listmapview);

		// ��Ŀ
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();


		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		Log.d("<<MAP>>", "SQL");
		DBAdapter db= new DBAdapter(getApplicationContext());
		// ---get all contacts---
		db.open();
		Cursor c = db.getContact(Long.parseLong(pref.getString("INDEX", "0")));
		Log.e("lat", c.getString(1));
		Log.e("lng", c.getString(2));
		//DisplayContact(c); 
		String str1[] = c.getString(1).split(",");
		String str2[] = c.getString(2).split(",");
//		Log.e("STR1-len", Integer.toString(str1.length));
		double s = Double.parseDouble(str1[0]);
//		Log.e("S", Double.toString(s));

		///////////////���ȭ�ϴ� �� ���� ���� �� : coo -> coo2
		coo = new ArrayList<Coordinate>();
		for(int i=0;i<str1.length;i++){
			Log.e("FOR", str1[i]);
			/**@brief save the location received*/
			Coordinate c1 = new Coordinate(Double.parseDouble(str1[i])
					, Double.parseDouble(str2[i]));
			coo.add(c1);
			Log.e("FOR-STR", Double.toString(coo.get(i).getLat()));
		}
		////���� ����� �����ϴ� �Լ��� �θ��� ����, ���� �߶׸°� ���� ������ �ִ� �� �����ؾ� �Ѵ�. ��ǥ ������ �Ÿ��� �� Ű�ι��� �̻��� �� ����.

		deleteNoise();
		prevAvg();
		init();
		//CaptureMapScreen();

	}
	//Ƥ ����  �����ϴ� �Լ�
	private void deleteNoise(){//���ȭ �ϴ� �� ���� ���� �� coo->coo2
		for(int i = 0;i < coo.size() - 1; i++){
			//ó�� ��ǥ�� ���, ù��° ��ǥ ����
			//�ι�°�� ������ ��ǥ�� ���, �ι� ° ��ǥ ����
			//else, ���� �� ��� ������ ������ ���. -> ������ 0���� ���� ������ ���� ��ǥ�� �����ϰ� ������ �ٽ� ����.(���� �ε��� �ٽ� �ѹ� Ȯ��. ���� ��ǥ �����߱� ������)
			double dist = distance(coo.get(i).getLat(), coo.get(i).getLng(), coo.get(i+1).getLat(), coo.get(i).getLng());
			if(dist > testDist){//�ְ�ӵ� : 10�ʿ� 35m   1�п� 333����  �ü� 20km
				if(i == 0){//���� ó�� ��ǥ �ΰ��� ������ ���. ù��° ��ǥ ����.
					coo.remove(i);
				}
				else if((i+1) == (coo.size()-1)){//���� ������ ��ǥ �ΰ��� ������ ���. ������ ��ǥ ����.
					coo.remove(i+1);
				}
				else{
					coo.remove(i);
				}
				i--;
				continue;								
			}
		}
	}
	//�Ÿ� ���ϴ� �κ�
	public double distance(double P1_latitude, double P1_longitude,
			double P2_latitude, double P2_longitude) {
		if ((P1_latitude == P2_latitude) && (P1_longitude == P2_longitude)) {
			return 0;
		}
		double e10 = P1_latitude * Math.PI / 180;
		double e11 = P1_longitude * Math.PI / 180;
		double e12 = P2_latitude * Math.PI / 180;
		double e13 = P2_longitude * Math.PI / 180;
		/* Ÿ��ü GRS80 */
		double c16 = 6356752.314140910;
		double c15 = 6378137.000000000;
		double c17 = 0.0033528107;
		double f15 = c17 + c17 * c17;
		double f16 = f15 / 2;
		double f17 = c17 * c17 / 2;
		double f18 = c17 * c17 / 8;
		double f19 = c17 * c17 / 16;
		double c18 = e13 - e11;
		double c20 = (1 - c17) * Math.tan(e10);
		double c21 = Math.atan(c20);
		double c22 = Math.sin(c21);
		double c23 = Math.cos(c21);
		double c24 = (1 - c17) * Math.tan(e12);
		double c25 = Math.atan(c24);
		double c26 = Math.sin(c25);
		double c27 = Math.cos(c25);
		double c29 = c18;
		double c31 = (c27 * Math.sin(c29) * c27 * Math.sin(c29))
				+ (c23 * c26 - c22 * c27 * Math.cos(c29))
				* (c23 * c26 - c22 * c27 * Math.cos(c29));
		double c33 = (c22 * c26) + (c23 * c27 * Math.cos(c29));
		double c35 = Math.sqrt(c31) / c33;
		double c36 = Math.atan(c35);
		double c38 = 0;
		if (c31 == 0) {
			c38 = 0;
		} else {
			c38 = c23 * c27 * Math.sin(c29) / Math.sqrt(c31);
		}
		double c40 = 0;
		if ((Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))) == 0) {
			c40 = 0;
		} else {
			c40 = c33 - 2 * c22 * c26
					/ (Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38)));
		}
		double c41 = Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))
				* (c15 * c15 - c16 * c16) / (c16 * c16);
		double c43 = 1 + c41 / 16384
				* (4096 + c41 * (-768 + c41 * (320 - 175 * c41)));
		double c45 = c41 / 1024 * (256 + c41 * (-128 + c41 * (74 - 47 * c41)));
		double c47 = c45
				* Math.sqrt(c31)
				* (c40 + c45
						/ 4
						* (c33 * (-1 + 2 * c40 * c40) - c45 / 6 * c40
								* (-3 + 4 * c31) * (-3 + 4 * c40 * c40)));
		double c50 = c17
				/ 16
				* Math.cos(Math.asin(c38))
				* Math.cos(Math.asin(c38))
				* (4 + c17
						* (4 - 3 * Math.cos(Math.asin(c38))
								* Math.cos(Math.asin(c38))));
		double c52 = c18
				+ (1 - c50)
				* c17
				* c38
				* (Math.acos(c33) + c50 * Math.sin(Math.acos(c33))
						* (c40 + c50 * c33 * (-1 + 2 * c40 * c40)));
		double c54 = c16 * c43 * (Math.atan(c35) - c47);
		// return distance in meter
		return c54;
	}

	private void prevAvg() {
		// TODO Auto-generated method stub
		double latTotal = 0, lngTotal = 0;
		coo2 = new ArrayList<Coordinate>();
		for(int i = 0 ;i < coo.size(); i++){
			if(i == 0){
				Coordinate c = new Coordinate(coo.get(i).getLat(), coo.get(i).getLng());
				coo2.add(c);
			}
			else if(i == 1){
				latTotal += coo.get(i).getLat() + coo.get(i-1).getLat();
				lngTotal += coo.get(i).getLng() + coo.get(i-1).getLng();

				Coordinate c = new Coordinate(latTotal/2.0, lngTotal/2.0);
				coo2.add(c);
			}
			else if(i == 2){
				latTotal += coo.get(i).getLat() + coo.get(i-1).getLat() + coo.get(i-2).getLat();
				lngTotal += coo.get(i).getLng() + coo.get(i-1).getLng() + coo.get(i-2).getLng();

				Coordinate c = new Coordinate(latTotal/3.0, lngTotal/3.0);
				coo2.add(c);
			}
			else{
				latTotal += coo.get(i).getLat() + coo.get(i-1).getLat() + coo.get(i-2).getLat() + coo.get(i-3).getLat();
				lngTotal += coo.get(i).getLng() + coo.get(i-1).getLng() + coo.get(i-2).getLng() + coo.get(i-3).getLng();

				Coordinate c = new Coordinate(latTotal/4.0, lngTotal/4.0);
				coo2.add(c);
			}
			latTotal = lngTotal = 0;

			Log.i("avg-coo", Double.toString(coo2.get(i).getLat())+","+Double.toString(coo2.get(i).getLng()));

			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = geocoder.getFromLocation(coo2.get(i).getLat(),coo2.get(i).getLng()	, 1);

				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}



	private void init() {
		/**@brief show marker with location**/

		// ��Ŀ ����.
		// ���� ��ǥ�� ��ũ�� �߰��ϰ�, ��Ŀ title ������ �ٷ� ���̰� �մϴ�.
		//
		// .position(position) = ��Ŀ�� �߰��Ǵ� ��ǥ
		// .title(title) = ��Ŀ�� ����
		// .showInfoWindow() = ��Ŀ�� ������ �ٷ� ���̰�. (������ Ŭ�������� ��Ŀ�� ������ ǥ�õ�)
		// mGoogleMap.addMarker(new
		// MarkerOptions().position(position).title(title)).showInfoWindow();
		Intent getI = getIntent();

		String title = getI.getStringExtra("title");
		LatLng position = new LatLng(coo2.get(0).getLat(), coo2.get(0).getLng());

		Log.d("QQQ", position.latitude+" : " +position.longitude);
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(ListMapActivity.this);

		//		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
		//				.findFragmentById(R.id.map)).getMap();

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));



		PolylineOptions rectOptions = new PolylineOptions();
		String tmp="";
		for(int i=0;i<coo2.size();i++){
			Log.d("COO2", Double.toString(coo2.get(i).getLat()));
			position = new LatLng(coo2.get(i).getLat(), coo2.get(i).getLng());
			tmp+="count : "+(i+1);
			tmp+=" lat : "+coo2.get(i).getLat();
			tmp+=" lng : "+coo2.get(i).getLng()+"\n";
			if(position.latitude == 0 && position.longitude == 0){
				//��ǥ�� �ٴ� �Ѱ�� �����°��� �����ϱ� ����.�ʱ�ȭ��.
				Log.d("position", "0,0");
			}
			else{
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

//				Log.d("LATLON", position.latitude+" : " +position.longitude);
				mGoogleMap.addMarker(
						new MarkerOptions().position(position).title(tmp))
						.showInfoWindow();
				rectOptions.add(position);
			}
			tmp = "";
		}
		rectOptions.color(Color.RED);
		Polyline polyline = mGoogleMap.addPolyline(rectOptions);
		mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			public boolean onMarkerClick(Marker marker) {

				return false;
			}
		});
	}


	public void CaptureMapScreen() 
	{
		
		final String deviceIndependentRootAddress = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
		File file  = new File(deviceIndependentRootAddress + "/FOOTTRIP/LOG");
		Log.d("tests", "save start");
		if(!file.exists()){
			file.mkdir(); 
			Log.d("mkdir","make directory");
		}

	SnapshotReadyCallback callback = new SnapshotReadyCallback() {
	            Bitmap bitmap;

	            
	            
	            @Override
	            public void onSnapshotReady(Bitmap snapshot) {
	                // TODO Auto-generated method stub
	                bitmap = snapshot;
	                try {

	                    FileOutputStream out = new FileOutputStream(deviceIndependentRootAddress + "/FOOTTRIP/LOG/"+ "MyMapScreen" + System.currentTimeMillis()
	                        + ".png");

	                    // above "/mnt ..... png" => is a storage path (where image will be stored) + name of image you can customize as per your Requirement

	                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        };

	        mGoogleMap.snapshot(callback);

	        // myMap is object of GoogleMap +> GoogleMap myMap;
	        // which is initialized in onCreate() => 
	        // myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_pass_home_call)).getMap();
	}	
	
	
}

