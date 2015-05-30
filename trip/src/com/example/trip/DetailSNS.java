package com.example.trip;

import static com.example.trip.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.trip.CommonUtilities.EXTRA_MESSAGE;
import static com.example.trip.CommonUtilities.SENDER_ID;
import static com.example.trip.CommonUtilities.SERVER_URL;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import request.codeJava.client.RequestMethods;

import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.trip.SnsActivity.NewsFeedAdapter;
import com.foottrip.newsfeed.app.AppController;
import com.foottrip.newsfeed.data.CardItem;
import com.foottrip.newsfeed.data.ItemDetail;

public class DetailSNS extends Activity{

	private static final String TAG = DetailSNS.class.getSimpleName();
	private CardItem cardItem;
	private ArrayList<ItemDetail> itemDetail;
	private String URL_DETAIL;
	static Animation anim;

	private String boardID;
	Intent intentFromSNS;
	TextView header;
	NetworkImageView map_img;
	NetworkImageView profile_img;
	TextView user_name;
	TextView timestamp;
	ImageButton map_btn;
	TextView content;
	TextView loveCnt;
	TextView replyCnt;
	ImageButton loveBtn;
	ImageButton replyBtn;
	ImageButton shareBtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_detail);

		header = (TextView)findViewById(R.id.header);
		map_img = (NetworkImageView)findViewById(R.id.map_img);
		profile_img = (NetworkImageView)findViewById(R.id.profile_img);
		user_name = (TextView)findViewById(R.id.user_name);
		timestamp = (TextView)findViewById(R.id.timestamp);
		map_btn = (ImageButton)findViewById(R.id.map_btn);
		content = (TextView)findViewById(R.id.content);
		
		loveCnt = (TextView)findViewById(R.id.loveCnt);
		replyCnt = (TextView)findViewById(R.id.replyCnt);

		loveBtn = (ImageButton)findViewById(R.id.loveBtn);
		replyBtn = (ImageButton)findViewById(R.id.reply);
		shareBtn = (ImageButton)findViewById(R.id.share);
		
		intentFromSNS = new Intent();
		intentFromSNS = getIntent();
		boardID = intentFromSNS.getStringExtra("BOARDID");

		String message = "?userID="+getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "") + "&boardID="+boardID;
		URL_DETAIL = FootTripCommunication.ServerAddressByProtocolNum(FootTripCommunication.DETAIL_VIEW_REQUEST) + message;
		itemDetail = new ArrayList<ItemDetail>();

		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		cache.clear();
		Entry entry = cache.get(URL_DETAIL);
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
					URL_DETAIL, null, new Response.Listener<JSONObject>() {
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

		}
	}

	/**
	 * Parsing json reponse and passing the data to feed view list adapter
	 * */
	private void parseJsonFeed(JSONObject response) {

		Log.i("Json resp",response.toString());
		//JSON String을 JSON obj로 parsing하기위한 작업 line#2
		HashMap<String, String> dataMap = FootTripJSONBuilder.jsonParser(response.toString());
		HashMap<String, String> feedObj = FootTripJSONBuilder.jsonParser(dataMap.get("data"));
		
		cardItem = new CardItem();
		cardItem.setId(feedObj.get("BOARDID"));			//board id
		cardItem.setName(feedObj.get("USERNAME"));		//user name
		cardItem.setUserID(feedObj.get("USERID"));		//user id
		cardItem.setContent(feedObj.get("CONTENT"));		//content
		cardItem.setHit(feedObj.get("HITCNT"));			//hit count
		cardItem.setIsLove(Boolean.parseBoolean(feedObj.get("LOVECHK")));		//love check
		cardItem.setLove(feedObj.get("LOVECNT"));			//love count
		cardItem.setReply(feedObj.get("REPLYCNT"));		//reply count
		cardItem.setTimestamp(feedObj.get("TIMESTAMP"));	//time stamp
		cardItem.setRegion(feedObj.get("REGION"));
		//set profile image		
		String profileImg = feedObj.get("PROFILEIMG");
		cardItem.setProfileImg(FootTripCommunication.SERVER_URL+profileImg);

		//set image list
		String imagestr = feedObj.get("IMAGELIST");
		String[] imgList = null;
		ArrayList<String> imageArrayList = new ArrayList<String>();
		if(imagestr.contains(","))
			imgList = imagestr.split(",");
		else{
			imgList = new String[1];
			imgList[0] = imagestr;
		}
		for(int j=0;j<imgList.length;j++){
			ItemDetail dItem = new ItemDetail();
			imageArrayList.add(FootTripCommunication.SERVER_URL+imgList[j]);
			dItem.setImgUrl(FootTripCommunication.SERVER_URL+imgList[j]);
			dItem.setImgTag("text");
			itemDetail.add(dItem);
		}

		cardItem.setImageList(imageArrayList);

		//set map image 
		String mapImg = feedObj.get("MAPIMAGE");
		cardItem.setMapImg(FootTripCommunication.SERVER_URL+mapImg);

		//set gps data
		String gpsData =feedObj.get("GPSDATA");
		ArrayList<String> gpsArrayList = new ArrayList<String>();
		String[] tmpGpsData = null;

		if(gpsData != null){
			tmpGpsData = gpsData.split(":");
			gpsData = tmpGpsData[1];
		}
		gpsArrayList.add(gpsData);
		cardItem.setGpsData(gpsArrayList);
		
		Typeface font = Typeface.createFromAsset(getAssets(),"fonts/magic.ttf");
		header.setText(cardItem.getRegion());
		header.setTypeface(font);
		user_name.setText(cardItem.getName());
		user_name.setTypeface(font);
		timestamp.setText(cardItem.getTimestamp());
		timestamp.setTypeface(font);
		content.setText(cardItem.getContent());
		content.setTypeface(font);
		loveCnt.setText("좋아요 "+cardItem.getLove());
		loveCnt.setTypeface(font);
		replyCnt.setText("댓글 "+cardItem.getReply());
		replyCnt.setTypeface(font);
		final int lovID = loveCnt.getId();
		
		loveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageButton btn = (ImageButton)v;
				TextView txt = (TextView) findViewById(lovID);
				switch(v.getId()){
				case R.id.loveBtn:
					if(cardItem.isLove()){
						btn.setImageResource(R.drawable.unlove);
						cardItem.setIsLove(false);
						int num = Integer.parseInt(cardItem.getLove());
						if(num != 0)
							num-=1;
						txt.setText("좋아요 "+num);
						String user_ID = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");
						RequestMethods.likeBoardRequest(user_ID, cardItem.getId());	
					}
					else{
						btn.setImageResource(R.drawable.love);
						anim = AnimationUtils.loadAnimation(getBaseContext(),R.anim.scale);
						v.startAnimation(anim);
						cardItem.setIsLove(true);
						int num = Integer.parseInt(cardItem.getLove())+1;
						txt.setText("좋아요 "+num);
						String user_ID = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");
						RequestMethods.likeBoardRequest(user_ID, cardItem.getId());
					}
					break;
				}
			}
		});
	
		replyBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				String commentData = RequestMethods.commentDetailRequest(cardItem.getId());
				Intent intent = new Intent(getBaseContext(),commentform.class);
				Bundle bund = new Bundle();
				bund.putString("CommentInfo", commentData);
				bund.putString("boardID", cardItem.getId());
				intent.putExtras(bund);
				startActivity(intent);

			}
		});
		
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		map_img.setImageUrl(cardItem.getMapImg(), imageLoader);
		profile_img.setImageUrl(cardItem.getProfileImg(), imageLoader);
		LinearLayout layout = (LinearLayout) findViewById(R.id.imglist);
		int imgCNT = cardItem.getImageList().size();
		Display display = ((WindowManager) getApplicationContext()
				.getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
		int dWidth = display.getWidth();
		
		for(int i=0;i<imgCNT;i++){
			View view = (View) getLayoutInflater().inflate(R.layout.item_detail, null);
			NetworkImageView img = (NetworkImageView) view.findViewById(R.id.cardimg);
			
			TextView tag = (TextView) view.findViewById(R.id.img_tag);
			img.setImageUrl(cardItem.getImageList().get(i),imageLoader);
			img.setRotation((float)90);
			img.getLayoutParams().width = dWidth;
			img.getLayoutParams().height = dWidth;
			
			layout.addView(view);
		}
	}
}
