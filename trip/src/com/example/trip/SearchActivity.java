package com.example.trip;

import java.util.ArrayList;
import java.util.HashMap;

import net.codejava.server.FootTripJSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SearchActivity extends Activity implements OnClickListener{

	//search list
	private ListView mListView = null;
	private SearchCardAdapter mAdapter = null;

	private ImageButton btn1, btn2, btn3, btn4, btn5;
	private String userID = "gmlwo22@nate.com";
	private String mUserName = "";
	private ArrayList<Integer> regionArr = null;
	private String mProfileImg = "";
	private int starNum = 0;

	//search
	private AutoCompleteTextView search = null;
	private TextView tripBtn;
	private TextView peopleBtn;
	private ImageButton searchBtn;

	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_trip);


		//initilaze this array with your data
		Region r = new Region();
//		AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, r.region);

		
		
//		Region r = new Region();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, r.region);
		search = (AutoCompleteTextView)findViewById(R.id.editsearch);
		search.setThreshold(1);
		search.setAdapter(adapter);
//		search.setSelection(5);
		tripBtn = (TextView)findViewById(R.id.trip);
		peopleBtn = (TextView)findViewById(R.id.people);
		searchBtn = (ImageButton)findViewById(R.id.searchbtn);

		tripBtn.setOnClickListener(this);
		peopleBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		
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


		/**dynamic add card list */
		mListView = (ListView) findViewById(R.id.search_card_list);
		mAdapter = new SearchCardAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == tripBtn.getId()){

		}
//		else if(v.getId() == peopleBtn.getId()){
//			Intent i = new Intent(SearchActivity.this, SearchActivityPeople.class);
//			startActivity(i);
//			overridePendingTransition(0,0);
//			finish();
//		}
		else if(v.getId() == btn1.getId()){
			Intent intent = new Intent(SearchActivity.this, SnsActivity.class);
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
			Intent startIntent = new Intent(SearchActivity.this, StartActivity.class);
			startActivity(startIntent);
			overridePendingTransition(0,0);
			finish();
		}		
		else if(v.getId() == btn4.getId()){
			Intent searchIntent = new Intent(SearchActivity.this, SearchActivity.class);
			startActivity(searchIntent);
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == btn5.getId()){
			Intent profilIntent = new Intent(SearchActivity.this,MyProfileActivity.class);
			startActivity(profilIntent);	
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == searchBtn.getId()){
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
				int code = r.getRegionID(search.getText().toString());
				//				Log.e("cardlist", ""+RequestMethods.SearchAsRegionRequest(Integer.toString(code)));
				String cardlist = RequestMethods.SearchAsRegionRequest(Integer.toString(code));
				HashMap<String, String> dataMap = null;
				if(cardlist == null){
					Log.e("cardlist", "null");
				}
				else{
					dataMap = FootTripJSONBuilder.jsonParser(cardlist);
					mAdapter = new SearchCardAdapter(this);
					mListView.setAdapter(mAdapter);

					if(dataMap.get("accessable").equals("nack")){
						Log.e("request", "nack");
					}else{
						String JSONresult = dataMap.get("data");	//return value
						//JSON String --> JSONArray Object
						JSONArray contentArr = null;
						regionArr = new ArrayList<Integer>();
						try {
							contentArr = new JSONArray(JSONresult);
							for(int i=0;i<contentArr.length();i++){
								JSONObject content = (JSONObject)contentArr.get(i);
								Drawable d = new BitmapDrawable(getResources(), RequestMethods.LoadImageFromWebOperation(content.get("CARD_IMAGE_PATH").toString()));
								mUserName = content.get("USER_NAME").toString();
								mProfileImg = content.get("PROGILE_IMAGE_PATH").toString();
								Log.e("profilepath", ""+mProfileImg);
								//starNum = Integer.parseInt(content.get("STAR_NUM").toString());
								mAdapter.addItem(d, (content.get("CARD_NAME").toString()));
								Log.e("regionCode", Integer.toString(r.getRegionID(content.get("CARD_NAME").toString())));
								regionArr.add(r.getRegionID(content.get("CARD_NAME").toString()));//region code with order of card list
							}
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
						mAdapter.notifyDataSetChanged();
						mListView.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View v, int position, long id){
								//이 카드 주인의 프로필화면으로 넘어감
								Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
							}
						});
					}
				}

			}
		}

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
