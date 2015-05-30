package com.example.trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.foottrip.newsfeed.app.AppController;
import com.foottrip.newsfeed.app.CircledNetworkImageView;

public class MyProfileActivity extends Activity implements OnClickListener{

	private Button tripBtn, followerBtn, followeeBtn, moreBtn;
	private LinearLayout profileLayout;
	private CircledNetworkImageView profileImg;
	private int starNum = 0;
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;

	private ImageButton btn1, btn2, btn3, btn4, btn5;
	private Uri mImageCaptureUri;
	private static int mImageState = 0;// 0 is profileImg, 1 is backgroundImg

	Typeface font;
	//card list
	private ListView mListView = null;
	private ListCardAdapter mAdapter = null;
	private String userID;
	private String mUserName = "";
	private ArrayList<Integer> regionArr = null;
	private String mProfileImg = "";

	ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);


		/*view*/
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		LinearLayout profilel = (LinearLayout)findViewById(R.id.profile_layout);
		profilel.getLayoutParams().height = display.getWidth()/3 + 40;
		ImageView iv = (ImageView) findViewById(R.id.profile_image);
		iv.getLayoutParams().width = display.getWidth()/3;
		iv.getLayoutParams().height = display.getWidth()/3;
		LinearLayout ll = (LinearLayout)findViewById(R.id.namelayout);
		LinearLayout.LayoutParams params = null;
		ll.setX(display.getWidth()/8);
		ll.setY(display.getWidth()/3/4);
		TextView name = (TextView)findViewById(R.id.name);
		name.setTextSize(display.getWidth()/5/10);

		userID = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");

		tripBtn = (Button)findViewById(R.id.tripbtn);
		followerBtn = (Button)findViewById(R.id.followerbtn);
		followeeBtn = (Button)findViewById(R.id.followingbtn);
		moreBtn = (Button)findViewById(R.id.morebtn);
		profileLayout = (LinearLayout)findViewById(R.id.profile_layout);
		profileImg = (CircledNetworkImageView)findViewById(R.id.profile_image);

		tripBtn.setOnClickListener(this);
		followerBtn.setOnClickListener(this);
		followeeBtn.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
		profileLayout.setOnClickListener(this);
		profileImg.setOnClickListener(this);
		
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
		mListView = (ListView) findViewById(R.id.cardlist);
		mAdapter = new ListCardAdapter(this);
		mListView.setAdapter(mAdapter);

		/**ProfileCardRequest : request card list with server*/
		String cardlist = RequestMethods.ProfileCardRequest(userID);

		if(cardlist == null){
			Log.e("cardlist", "null");
		}
		HashMap<String, String> dataMap = FootTripJSONBuilder.jsonParser(cardlist);
		if(dataMap.get("accessable").equals("nack")){
			Log.e("request", "nack");
		}
		String JSONresult = dataMap.get("data");	//return value
		//JSON String --> JSONArray Object
		JSONArray contentArr = null;
		Region r = new Region();
		font = Typeface.createFromAsset(getAssets(),"fonts/magic.ttf");

		regionArr = new ArrayList<Integer>();
		mUserName = dataMap.get("USER_NAME");
		mProfileImg = dataMap.get("PROFILE_IMAGE");
		Log.e("profilepath", mProfileImg);
		profileImg.setImageUrl(FootTripCommunication.SERVER_URL+mProfileImg,imageLoader);
		profileImg.setRotation((float)90.0);
		try {
			name.setText(mUserName);
			name.setTypeface(font);
			if(JSONresult!=null){
				contentArr = new JSONArray(JSONresult);

				for(int i=0;i<contentArr.length();i++){
					JSONObject content = (JSONObject)contentArr.get(i);
					Drawable d = new BitmapDrawable(getResources(), RequestMethods.LoadImageFromWebOperation(content.get("CARD_IMAGE_PATH").toString()));
					starNum = Integer.parseInt(content.get("STAR_NUM").toString());
					mAdapter.addItem(d, (content.get("CARD_NAME").toString()));
					Log.e("regionCode", Integer.toString(r.getRegionID(content.get("CARD_NAME").toString())));
					regionArr.add(r.getRegionID(content.get("CARD_NAME").toString()));//region code with order of card list
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id){
				ListCardData mData = mAdapter.mListData.get(position);
				Intent i = new Intent(MyProfileActivity.this, CardListActivity.class);
				i.putExtra("userID", userID);
				i.putExtra("regionCode", regionArr.get(position));
				startActivity(i);
				overridePendingTransition(android.R.anim.slide_in_left, R.anim.slide_out_left);

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == tripBtn.getId()){

		}else if(v.getId() == followerBtn.getId()){
			Intent i = new Intent(MyProfileActivity.this, FollowerActivity.class);
			startActivity(i);
			overridePendingTransition(android.R.anim.slide_in_left, R.anim.slide_out_left);

		}else if(v.getId() == followeeBtn.getId()){
			Intent i = new Intent(MyProfileActivity.this, FolloweeActivity.class);
			startActivity(i);
			overridePendingTransition(android.R.anim.slide_in_left, R.anim.slide_out_left);

		}else if(v.getId() == moreBtn.getId()){
			//more button
			CharSequence[] items = {"프로필 사진 바꾸기", "커버 사진 바꾸기"};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(getLayoutInflater().inflate(R.layout.dialog_layout, null));
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if(item == 0) {
						mImageState = 0;
						doTakeAlbumAction();
					} else if(item == 1) {
						mImageState = 1;
						Log.e("culver", mImageState+"");
						doTakeAlbumAction();
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});

			AlertDialog dialog = builder.create();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			dialog.show();
		}
		else if(v.getId() == profileLayout.getId()){
			//profile layout background
			CharSequence[] items = {"사진 업로드", "커버 사진 보기"};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(getLayoutInflater().inflate(R.layout.dialog_layout, null));
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if(item == 0) {

					} else if(item == 1) {

					} 
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});

			AlertDialog dialog = builder.create();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			dialog.show();
		}

		else if(v.getId() == profileImg.getId()){
			//if click profile image
			CharSequence[] items = {"새 사진 찍기", "앨범에서 선택", "프로필 사진 보기"};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(getLayoutInflater().inflate(R.layout.dialog_layout, null));
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if(item == 0) {
						//get a image from camera
						//						doTakePhotoAction();
					} else if(item == 1) {
						mImageState = 0;
						doTakeAlbumAction();
					} else if(item == 2) {
						ImageView iv = new ImageView(MyProfileActivity.this);
						iv.setImageBitmap(RequestMethods.LoadImageFromWebOperation(mProfileImg));
						iv.setScaleType(ImageView.ScaleType.FIT_XY);
						iv.setAdjustViewBounds(true);
						Dialog dialog1 = new Dialog(MyProfileActivity.this);
						dialog1.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
						dialog1.setContentView(iv);
						dialog1.show();
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});

			AlertDialog dialog = builder.create();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			dialog.show();
		}
		
		else if(v.getId() == btn1.getId()){
			Intent intent = new Intent(MyProfileActivity.this, SnsActivity.class);
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
			Intent startIntent = new Intent(MyProfileActivity.this, StartActivity.class);
			startActivity(startIntent);
			overridePendingTransition(0,0);
			finish();
		}		
		else if(v.getId() == btn4.getId()){
			Intent searchIntent = new Intent(MyProfileActivity.this, TopPathActivity.class);
			startActivity(searchIntent);
			overridePendingTransition(0,0);
			finish();
		}
		else if(v.getId() == btn5.getId()){
			Intent profilIntent = new Intent(MyProfileActivity.this,MyProfileActivity.class);
			startActivity(profilIntent);	
			overridePendingTransition(0,0);
			finish();
		}
	}

	/**
	 * 앨범에서 이미지 가져오기
	 */
	private void doTakeAlbumAction()
	{
		// 앨범 호출
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK)
		{
			return;
		}

		switch(requestCode)
		{

		case PICK_FROM_ALBUM:
		{
			mImageCaptureUri = data.getData();
			Log.e("mImageCaptureUri", getRealPathFromURI(mImageCaptureUri));
			String response = RequestMethods.uploadProfileImageRequest(userID, getRealPathFromURI(mImageCaptureUri));
			Log.e("profile_upload", response);
			if(mImageState == 0){
				Log.e("ex", mImageState+"");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				Bitmap photo = BitmapFactory.decodeFile(getRealPathFromURI(mImageCaptureUri), options);
				profileImg.setImageBitmap(photo);
			}
			else if(mImageState == 1){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				Bitmap photo = BitmapFactory.decodeFile(getRealPathFromURI(mImageCaptureUri), options);
				BitmapDrawable viewBg = new BitmapDrawable(getResources(), photo);
				profileLayout.setBackgroundDrawable(viewBg);
			}
		}
		break;
		}
	}


	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( contentUri,
				proj, // Which columns to return
				null,       // WHERE clause; which rows to return (all rows)
				null,       // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/***
	 * @author LeeHyun-A
	 * for card list view
	 */
	public class ListCardAdapter extends BaseAdapter{
		private Context mContext = null;
		private ArrayList<ListCardData> mListData = new ArrayList<ListCardData>();

		public ListCardAdapter(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		public void addItem(Drawable img, String mRegion){
			ListCardData addInfo = null;
			addInfo = new ListCardData();
			addInfo.mImg = img;
			addInfo.mRegion = mRegion;
			mListData.add(addInfo);
		}

		public void remove(int position){
			mListData.remove(position);
			dataChange();
		}

		public void sort(){
			Collections.sort(mListData, ListCardData.ALPHA_COMPARATOR);
			dataChange();
		}

		public void dataChange(){
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listview_card_region, null);

				holder.mRegion = (TextView) convertView.findViewById(R.id.regionname);
				holder.mImg = (ImageView) convertView.findViewById(R.id.cardimg);

				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}

			ListCardData mData = mListData.get(position);

			//image
			if (mData.mImg != null) {
				holder.mImg.setVisibility(View.VISIBLE);
				holder.mImg.setImageDrawable(mData.mImg);
			}else{
				holder.mImg.setVisibility(View.GONE);
			}
			//text
			holder.mRegion.setText(mData.mRegion);
			holder.mRegion.setTypeface(font);

			return convertView;
		}
	}
	private class ViewHolder{
		public TextView mRegion;
		public ImageView mImg;
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