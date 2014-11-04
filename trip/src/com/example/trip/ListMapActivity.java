package com.example.trip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
	private double testDist = 35;//10초에 35
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

		// 마커
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();


		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		Log.d("<<MAP>>", "SQL");
		DBAdapter db= new DBAdapter(getApplicationContext());
		// ---get all contacts---
		db.open();
		Cursor c = db.getContact(Long.parseLong(pref.getString("INDEX", "0")));
		Log.e("cursor", c.getString(1));
		//DisplayContact(c); 
		String str1[] = c.getString(1).split(",");
		String str2[] = c.getString(2).split(",");
		Log.e("STR1-len", Integer.toString(str1.length));
		double s = Double.parseDouble(str1[0]);
		Log.e("S", Double.toString(s));

		///////////////평균화하는 것 하지 않을 때 : coo -> coo2
		coo = new ArrayList<Coordinate>();
		for(int i=0;i<str1.length;i++){
			Log.e("FOR", str1[i]);
			/**@brief save the location received*/
			Coordinate c1 = new Coordinate(Double.parseDouble(str1[i])
					, Double.parseDouble(str2[i]));
			coo.add(c1);
			Log.e("FOR-STR", Double.toString(coo.get(i).getLat()));
		}
		////밑의 노이즈를 제거하는 함수를 부르기 전에, 정말 쌩뚱맞게 값이 떨어져 있는 것 제거해야 한다. 좌표 사이의 거리가 몇 키로미터 이상인 것 제거.

		deleteNoise();
		prevAvg();
		init();


	}
	//튄 값을  제거하는 함수
	private void deleteNoise(){//평균화 하는 것 하지 않을 때 coo->coo2
		for(int i = 0;i < coo.size() - 1; i++){
			//처음 좌표일 경우, 첫번째 좌표 제거
			//두번째가 마지막 좌표일 경우, 두번 째 좌표 제거
			//else, 위의 두 경우 제외한 나머지 경우. -> 포문이 0부터 돌기 때문에 뒤의 좌표를 제거하고 루프를 다시 돈다.(같은 인덱스 다시 한번 확인. 뒤의 좌표 제거했기 때문에)
			double dist = distance(coo.get(i).getLat(), coo.get(i).getLng(), coo.get(i+1).getLat(), coo.get(i).getLng());
			if(dist > testDist){//최고속도 : 10초에 35m   1분에 333미터  시속 20km
				if(i == 0){//만약 처음 좌표 두개의 간격일 경우. 첫번째 좌표 제거.
					coo.remove(i);
				}
				else if((i+1) == (coo.size()-1)){//만약 마지막 좌표 두개의 간격일 경우. 마지막 좌표 제거.
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
	//거리 구하는 부분
	public double distance(double P1_latitude, double P1_longitude,
			double P2_latitude, double P2_longitude) {
		if ((P1_latitude == P2_latitude) && (P1_longitude == P2_longitude)) {
			return 0;
		}
		double e10 = P1_latitude * Math.PI / 180;
		double e11 = P1_longitude * Math.PI / 180;
		double e12 = P2_latitude * Math.PI / 180;
		double e13 = P2_longitude * Math.PI / 180;
		/* 타원체 GRS80 */
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

		// 마커 설정.
		// 맵의 좌표에 마크를 추가하고, 마커 title 내용이 바로 보이게 합니다.
		//
		// .position(position) = 마커가 추가되는 좌표
		// .title(title) = 마커의 제목
		// .showInfoWindow() = 마커의 제목이 바로 보이게. (없으면 클릭했을때 마커의 제목이 표시됨)
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
				//좌표가 바다 한가운데 찍히는것을 방지하기 위해.초기화값.
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
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu items for use in the action bar
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.save, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

}

