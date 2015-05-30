package com.example.trip;

import static com.example.trip.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.trip.CommonUtilities.EXTRA_MESSAGE;
import static com.example.trip.CommonUtilities.SENDER_ID;
import static com.example.trip.CommonUtilities.SERVER_URL;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;
import net.codejava.server.ServerUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import request.codeJava.client.RequestMethods;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.widget.LoginButton;
import com.foottrip.newsfeed.app.AppController;
import com.foottrip.newsfeed.app.CircledNetworkImageView;
import com.foottrip.newsfeed.data.CardItem;
import com.foottrip.newsfeed.data.MapGalleryItem;
import com.google.android.gcm.GCMRegistrar;

public class SnsActivity extends Activity implements OnClickListener{

	private ImageButton btn1, btn2, btn3, btn4, btn5;
	private static final String TAG = SnsActivity.class.getSimpleName();
	private ListView listView;
	private NewsFeedAdapter nfAdapter;
	private ArrayList<CardItem> cardItems;
	static Animation anim;
	private String URL_FOOTTRIP;
	
	private static class TIME_MAXIMUM{
		public static final int SEC = 60;
		public static final int MIN = 60;
		public static final int HOUR = 24;
		public static final int DAY = 30;
		public static final int MONTH = 12;
	}

	//variables for push function
	AsyncTask<Void, Void, Void> mRegisterTask;
	public static HashMap<String,String> paramMap = null;
	Cache cache;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Defensive code! checking if string value is valid.
		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set
		GCMRegistrar.checkManifest(this);
		setContentView(R.layout.activity_sns);
		String message = "?userID="+getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");
		URL_FOOTTRIP = FootTripCommunication.ServerAddressByProtocolNum(FootTripCommunication.NEWSFEED_REQUEST)+message;

		listView = (ListView) findViewById(R.id.timeline);
		cardItems = new ArrayList<CardItem>();



		cache = AppController.getInstance().getRequestQueue().getCache();
		cache.clear();
		Entry entry = cache.get(URL_FOOTTRIP);


		if (entry != null) {
			// fetch the data from cache
			try {
				String data = new String(entry.data, "UTF-8");
				try {
					parseJsonFeed(new JSONObject(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} else {
			// making fresh volley request and getting json
			JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
					URL_FOOTTRIP, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					VolleyLog.d(TAG, "Response: " + response.toString());
					if (response != null) {
						parseJsonFeed(response);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.d(TAG, "Error: " + error.getMessage());
				}
			});

			// Adding request to volley request queue
			AppController.getInstance().addToRequestQueue(jsonReq);
			nfAdapter = new NewsFeedAdapter(this,cardItems);
			listView.setAdapter(nfAdapter);
		}

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


		//get facebook user data which will be used for Parameter to server(FootTrip용은 인위적으로 만드는 작업 추가해야함~)
		Bundle bund = getIntent().getExtras();
		paramMap = new HashMap<String, String>();
		if(bund!=null){
			HashMap<String, String> bundMap = (HashMap<String, String>) bund.getSerializable("param_hashmap");
			for(String key : bundMap.keySet()){
				paramMap.put(key, bundMap.get(key));
			}
		}
		registDevice();	
	}


	/**
	 * Parsing json reponse and passing the data to feed view list adapter
	 * */
	private void parseJsonFeed(JSONObject response) {
		try {
			Log.i("Json resp",response.toString());
			//JSON String을 JSON obj로 parsing하기위한 작업 line#2
			HashMap<String, String> dataMap = FootTripJSONBuilder.jsonParser(response.toString());
			String JSONresult = dataMap.get("data");	//return value

			//JSON String --> JSONArray Object
			JSONArray feedArray = new JSONArray(JSONresult);

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);
				CardItem item = new CardItem();
				item.setId(feedObj.getString("BOARDID"));			//board id
				item.setName(feedObj.getString("USERNAME"));		//user name
				item.setUserID(feedObj.getString("USERID"));		//user id
				item.setContent(feedObj.getString("CONTENT"));		//content
				item.setHit(feedObj.getString("HITCNT"));			//hit count
				item.setIsLove(feedObj.getBoolean("LOVECHK"));		//love check
				item.setLove(feedObj.getString("LOVECNT"));			//love count
				item.setReply(feedObj.getString("REPLYCNT"));		//reply count
				item.setTimestamp(feedObj.getString("TIMESTAMP"));	//time stamp
				item.setRegion(feedObj.getString("REGION"));
				//set profile image		
				String profileImg = feedObj.isNull("PROFILEIMG") ? null : feedObj.getString("PROFILEIMG");
				item.setProfileImg(FootTripCommunication.SERVER_URL+profileImg);

				//set image list
				String imagestr = feedObj.isNull("IMAGELIST") ? null : feedObj.getString("IMAGELIST");
				String[] imgList = null;
				ArrayList<String> imageArrayList = new ArrayList<String>();

				if(imagestr.contains(","))
					imgList = imagestr.split(",");
				else{
					imgList = new String[1];
					imgList[0] = imagestr;
				}
				//Toast.makeText(getApplicationContext(), image, Toast.LENGTH_LONG).show();
				for(int j=0;j<imgList.length;j++){
					imageArrayList.add(FootTripCommunication.SERVER_URL+imgList[j]);
				}

				item.setImageList(imageArrayList);

				//set map image 
				String mapImg = feedObj.isNull("MAPIMAGE") ? null : feedObj.getString("MAPIMAGE");
				item.setMapImg(FootTripCommunication.SERVER_URL+mapImg);

				//set gps data
				String gpsData = feedObj.isNull("GPSDATA") ? null : feedObj.getString("GPSDATA");
				ArrayList<String> gpsArrayList = new ArrayList<String>();
				String[] tmpGpsData = null;

				if(gpsData != null){
					tmpGpsData = gpsData.split(":");
					gpsData = tmpGpsData[1];
				}
				gpsArrayList.add(gpsData);
				item.setGpsData(gpsArrayList);

				cardItems.add(item);
			}
			nfAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public class NewsFeedAdapter extends BaseAdapter{
		private Activity activity;
		private LayoutInflater inflater;
		private List<CardItem> cardItems;
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();

		public NewsFeedAdapter(Activity activity, List<CardItem> cardItems) {
			//super(context, R.layout.timeline_item, R.id.header);
			this.activity = activity;
			this.cardItems = cardItems;
		}
		@Override
		public int getCount() {
			return cardItems.size();
		}
		@Override
		public Object getItem(int location) {
			return cardItems.get(location);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (inflater == null)
				inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();
			if(convertView == null){
				convertView = inflater.inflate(R.layout.item_timeline, parent, false);
				ViewHolder holder = new ViewHolder();
				holder.header = (TextView) convertView.findViewById(R.id.header);
				holder.userName = (TextView) convertView.findViewById(R.id.user_name);
				holder.profileImg = (CircledNetworkImageView) convertView.findViewById(R.id.profile_img);
				holder.timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				holder.loveBtn = (ImageButton) convertView.findViewById(R.id.loveBtn);
				holder.replyBtn = (ImageButton) convertView.findViewById(R.id.reply);
				holder.shareBtn = (ImageButton) convertView.findViewById(R.id.share);
				holder.img1 = (NetworkImageView) convertView.findViewById(R.id.image1);
				holder.img2 = (NetworkImageView) convertView.findViewById(R.id.image2);
				holder.img3 = (NetworkImageView) convertView.findViewById(R.id.image3);
				holder.img4 = (NetworkImageView) convertView.findViewById(R.id.image4);
				holder.mapImg = (NetworkImageView) convertView.findViewById(R.id.map_img);
				holder.mapBtn = (ImageButton) convertView.findViewById(R.id.map_btn);
				holder.loveCnt = (TextView) convertView.findViewById(R.id.loveCnt);
				holder.replyCnt = (TextView) convertView.findViewById(R.id.replyCnt);
				convertView.setTag(holder);             
			}
			CardItem item = cardItems.get(position);
			ViewHolder holder = (ViewHolder) convertView.getTag();

			final int pos = position;
			final String tmpBoardID = item.getId();
			final String myID = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");
			/*
			 * Set timeline item
			 * */
			Typeface font = Typeface.createFromAsset(getAssets(),"fonts/magic.ttf");
			holder.header.setText("#"+item.getRegion());
			holder.header.setTypeface(font);
			holder.userName.setText(item.getName());
			holder.userName.setTypeface(font);
			String formatTime = item.getTimestamp();
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date;
			try {
				date = format.parse(formatTime);
				formatTime = formatTimeString(date);
				holder.timeStamp.setText(formatTime);
				holder.timeStamp.setTypeface(font);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(!TextUtils.isEmpty(item.getContent())){
				holder.content.setText(item.getContent());
				holder.content.setVisibility(View.VISIBLE);
				holder.content.setTypeface(font);
			}else{
				holder.content.setVisibility(View.GONE);
			}

			if(item.isLove())
				holder.loveBtn.setImageResource(R.drawable.love);

			holder.loveCnt.setText("좋아요 "+item.getLove());
			holder.loveCnt.setTypeface(font);
			holder.loveCnt.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String likeData = RequestMethods.likeDetailRequest(tmpBoardID,myID);
					Intent intent = new Intent(getApplicationContext().getApplicationContext(),Likeform.class);
					Bundle bund = new Bundle();
					bund.putString("LIKEINFO", likeData);
					intent.putExtras(bund);
					startActivity(intent);
				}
			});



			holder.loveBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ImageButton btn = (ImageButton)v;
					switch(v.getId()){
					case R.id.loveBtn:
						if(cardItems.get(pos).isLove()){
							btn.setImageResource(R.drawable.unlove);
							String user_ID = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");
							RequestMethods.likeBoardRequest(user_ID, tmpBoardID);
							int num = Integer.parseInt(cardItems.get(pos).getLove());
							if(num != 0)
								num-=1;
							cardItems.get(pos).setIsLove(false);
							cardItems.get(pos).setLove(Integer.toString(num));
							nfAdapter.notifyDataSetChanged();
						}
						else{
							btn.setImageResource(R.drawable.love);
							anim = AnimationUtils.loadAnimation(activity,R.anim.scale);
							v.startAnimation(anim);
							String user_ID = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");
							RequestMethods.likeBoardRequest(user_ID, tmpBoardID);
							int num = Integer.parseInt(cardItems.get(pos).getLove())+1;
							cardItems.get(pos).setIsLove(true);
							cardItems.get(pos).setLove(Integer.toString(num));
							nfAdapter.notifyDataSetChanged();

						}
						break;
					}
				}
			});

			holder.mapBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(activity, MapViewInservice.class);
					Bundle bund = new Bundle();

					String[] tmp = null;
					tmp = cardItems.get(pos).getGpsData().get(0).split(",");
					int len = tmp.length;
					String[] lat = new String[len/2];
					String[] lng = new String[len/2];
					int cnt = 0;
					for(int i = 0; i<tmp.length; i+=2){
						lat[cnt] = tmp[i];
						lng[cnt] = tmp[i+1];
						cnt++;
					}
					bund.putStringArray("LAT",lat);
					bund.putStringArray("LNG",lng);
					intent.putExtras(bund);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);	
				}
			});

			holder.replyBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {	
					String commentData = RequestMethods.commentDetailRequest(tmpBoardID);
					Intent intent = new Intent(activity,commentform.class);
					Bundle bund = new Bundle();
					bund.putString("CommentInfo", commentData);
					bund.putString("boardID", tmpBoardID);
					intent.putExtras(bund);
					startActivity(intent);

				}
			});

			holder.shareBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					Toast.makeText(activity, "To do list", Toast.LENGTH_SHORT).show();
				}
			});
			holder.profileImg.setImageUrl(item.getProfileImg(),imageLoader);
			holder.mapImg.setImageUrl(item.getMapImg(),imageLoader);			
			holder.img1.setImageUrl(item.getImageList().get(0), imageLoader);
			holder.img1.setRotation((float)90);
			holder.img2.setImageUrl(item.getImageList().get(1), imageLoader);
			holder.img2.setRotation((float)90);
			holder.img3.setImageUrl(item.getImageList().get(2), imageLoader);
			holder.img3.setRotation((float)90);
			holder.img4.setImageUrl(item.getImageList().get(3), imageLoader);
			holder.img4.setRotation((float)90);

			holder.img1.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Intent detailIntent = new Intent(getApplicationContext().getApplicationContext(),DetailSNS.class);
					Bundle detailBundle = new Bundle();
					detailBundle.putString("BOARDID", tmpBoardID);
					detailIntent.putExtras(detailBundle);
					startActivity(detailIntent);
				}
			});
			holder.img2.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Intent detailIntent = new Intent(getApplicationContext().getApplicationContext(),DetailSNS.class);
					Bundle detailBundle = new Bundle();
					detailBundle.putString("BOARDID", tmpBoardID);
					detailIntent.putExtras(detailBundle);
					startActivity(detailIntent);
				}
			});
			holder.img3.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Intent detailIntent = new Intent(getApplicationContext().getApplicationContext(),DetailSNS.class);
					Bundle detailBundle = new Bundle();
					detailBundle.putString("BOARDID", tmpBoardID);
					detailIntent.putExtras(detailBundle);
					startActivity(detailIntent);
				}
			});
			holder.img4.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Intent detailIntent = new Intent(getApplicationContext().getApplicationContext(),DetailSNS.class);
					Bundle detailBundle = new Bundle();
					detailBundle.putString("BOARDID", tmpBoardID);
					detailIntent.putExtras(detailBundle);
					startActivity(detailIntent);
				}
			});
			holder.replyCnt.setText("댓글 "+item.getReply());
			holder.replyCnt.setTypeface(font);
			return convertView;
		}
	}
	public static class ViewHolder {
		TextView header;
		TextView content,userName,timeStamp;
		TextView loveCnt,replyCnt;
		ImageButton loveBtn,replyBtn,shareBtn,mapBtn;
		Button showMore;
		CircledNetworkImageView profileImg;
		NetworkImageView mapImg;
		NetworkImageView img1,img2,img3,img4;
		RelativeLayout image_list;
		String boardId;
		int previousTop = 0;
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1://btn1.getId():
			Intent intent = new Intent(SnsActivity.this, SnsActivity.class);
			startActivity(intent);
			overridePendingTransition(0,0);
			finish();
			break;
		case R.id.btn2:
			if (LoginFragment.isFBLogined()) {
				/* make the API call */
				startPickerActivity(PickerActivity.FRIEND_PICKER, LoginFragment.REQUEST_CODE_FOLLOWABLE, LoginFragment.PICK_TYPE_FRIENDS);
			}else{
				//이 경우는 페이스북 사용자가 아니니까 다른 추천을 해줘야함
				Toast.makeText(getApplicationContext(), "로그인 먼저하시오", Toast.LENGTH_SHORT).show();
			}

			break;			
		case R.id.btn3:
			Intent startIntent = new Intent(SnsActivity.this, StartActivity.class);
			startActivity(startIntent);
			overridePendingTransition(0,0);
			finish();

			break;		
		case R.id.btn4:
			Intent searchIntent = new Intent(SnsActivity.this, TopPathActivity.class);
			startActivity(searchIntent);
			overridePendingTransition(0,0);
			finish();
			break;	
		case R.id.btn5:
			Intent profilIntent = new Intent(getApplicationContext().getApplicationContext(),MyProfileActivity.class);
			
			startActivity(profilIntent);	
			overridePendingTransition(0,0);
			finish();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == LoginFragment.REQUEST_CODE_FOLLOWABLE) && (resultCode == RESULT_OK)){
			ArrayList<String> userLists = (ArrayList<String>) data.getSerializableExtra("selectUserList");
			if(userLists!=null){
				for(String userID : userLists){
					final HashMap<String,String> paramsMap = new HashMap<String, String>();

					SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
					paramsMap.put("ID_Type", "facebook");
					paramsMap.put("myID", pref.getString("ID", ""));
					paramsMap.put("followID", userID);

					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							//Send follow request to the server.
							String followedJSON = RequestMethods.followUserRequest(paramsMap);
							//Add logic
							if(followedJSON!=null && followedJSON.trim().length()>0){
								HashMap<String, String> responseMap = FootTripJSONBuilder.jsonParser(followedJSON);

								if(responseMap.get("accessable").equals("nack")){
									Log.d("Follow Result","Fail");
								}
							}
							return null;
						}
					}.execute(null, null, null);
				}
				Toast.makeText(getApplicationContext(), userLists.toString(), Toast.LENGTH_LONG).show();
			}
		} else if (requestCode == LoginFragment.REQUEST_CODE_FOLLOWABLE) {
			// Do nothing for now
			Toast.makeText(getApplicationContext(), "No response", Toast.LENGTH_LONG).show();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}


	/*
	 * menu item
	 * 1) go to write activity
	 * 2) logout
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.pencil:
			final ArrayList<MapGalleryItem> mapItems = getRecordFileNames();
			if(mapItems != null){
				Intent goToMapIntent = new Intent(SnsActivity.this,MapGallery.class);
				goToMapIntent.putExtra("MAPLISTS", mapItems);
				startActivity(goToMapIntent);
			}else{
				Toast.makeText(this, "파일 생성 안함", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.logout:
			LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			View view = inflater.inflate(R.layout.activity_main, null);
			LoginButton FBloginButton = (LoginButton) view.findViewById(R.id.authButton);
			if(FBloginButton==null) Log.i("ERROR:FB","view 못가져옴...");
			FBloginButton.logInAndOutFB();

			//Remove preference data.
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			pref.edit().clear().commit();

			Intent intent = new Intent(SnsActivity.this, LoginActivity.class);
			finish();
			startActivity(intent);

			/*
			 */
			break;
			//		case R.id.options_register:
			//			GCMRegistrar.register(this, SENDER_ID);
			//			return true;
		case R.id.options_unregister:
			GCMRegistrar.unregister(this);
			return true;
			//		case R.id.options_exit:
			//			finish();
			//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public ArrayList<MapGalleryItem> getRecordFileNames(){
		// Gets the files in the directory
		File fileDirectory = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/FOOTTRIP/");
		// Lists all the files into an array
		File[] dirFiles = fileDirectory.listFiles();
		ArrayList<MapGalleryItem> datStr = new ArrayList<MapGalleryItem>();
		if(dirFiles == null)
			return null;
		if(dirFiles.length != 0){
			for(int i=0;i<dirFiles.length;i++){
				if(dirFiles[i].getName().equals("logimg"))
					continue;
				else{
					MapGalleryItem item = new MapGalleryItem();
					item.setMapName(dirFiles[i].getName().split("/FOOTTRIP/")[0]);
					item.setDate(item.getMapName().replace("LogList_", "").replace(".dat", ""));
					item.setMapImagePath(fileDirectory.getAbsolutePath()+"/logimg/"+item.getDate()+".png");
					item.setCheckable(false);
					Log.i("test",item.getMapName()+"\n"+item.getMapImagePath()+"\n"+item.getDate());
					datStr.add(item);
				}
			}
		}
		return datStr;
	}

	public void registDevice(){
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		String regId = GCMRegistrar.getRegistrationId(this);
		paramMap.put("regId", regId);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
			Log.i("regidzero","이게뭐람. 도와줘~!");
		} else {
			// Device is already registered on GCM, check server.
			if(GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Log.d("ERROR::regist",getString(R.string.already_registered));
			} 
			// Try to register again, but not in the UI thread.
			// It's also necessary to cancel the thread onDestroy(),
			// hence the use of AsyncTask instead of a raw thread.
			final Context context = this;
			mRegisterTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					boolean registered =
							ServerUtilities.register(context, paramMap);
					// At this point all attempts to register with the app
					// server failed, so we need to unregister the device
					// from GCM - the app will try to register again when
					// it is restarted. Note that GCM will send an
					// unregistered callback upon completion, but
					// GCMIntentService.onUnregistered() will ignore it.
					if (!registered) {
						GCMRegistrar.unregister(context);
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					mRegisterTask = null;
				}

			};
			mRegisterTask.execute(null, null, null);
		}
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(
					getString(R.string.error_config, name));
		}
	}
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			Log.d("New_Message",newMessage);
		}
	};
	private void startPickerActivity(Uri data, int requestCode, String pick_type) {
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(this, PickerActivity.class);

		Bundle bd = new Bundle();
		bd.putString("pick_type", pick_type);

		intent.putExtras(bd);
		startActivityForResult(intent, requestCode);
	}
	public static long getMinutesDifference(long timeStart,long timeStop){
		long diff = timeStop - timeStart;
		long diffMinutes = diff / (60 * 1000);
		return  diffMinutes;
	}
	public static String formatTimeString(Date tempDate) {

		long curTime = System.currentTimeMillis();
		long regTime = tempDate.getTime();
		long diffTime = (curTime - regTime) / 1000;

		String msg = null;
		if (diffTime < TIME_MAXIMUM.SEC) {
			// sec
			msg = "방금 전";
		} else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
			// min
			msg = diffTime + "분 전";
		} else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
			// hour
			msg = (diffTime) + "시간 전";
		} else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
			// day
			msg = (diffTime) + "일 전";
		} else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
			// day
			msg = (diffTime) + "달 전";
		} else {
			msg = (diffTime) + "년 전";
		}
		return msg;
	}
}