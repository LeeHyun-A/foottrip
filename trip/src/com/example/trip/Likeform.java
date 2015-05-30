package com.example.trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.trip.SnsActivity.NewsFeedAdapter;
import com.example.trip.SnsActivity.ViewHolder;
import com.foottrip.newsfeed.app.AppController;
import com.foottrip.newsfeed.app.CircledNetworkImageView;
import com.foottrip.newsfeed.data.CardItem;
import com.foottrip.newsfeed.data.LikeItem;

import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;
import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Likeform extends Activity{

	// 리스트뷰에 보여질 문자열 배열로 할당
	
	private ArrayList<LikeItem> likeItems;
	private ListView listView;
	private LikeListAdapter likeAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_like);
		
		listView = (ListView)findViewById(R.id.like_listview);

		Bundle bund = getIntent().getExtras();
		String likeJSON = bund.getString("LIKEINFO");	
		Log.i("LIKE INFO : ", likeJSON);
		likeItems = new ArrayList<LikeItem>();
		if(likeJSON!=null){
			//Get JSON
			HashMap<String, String> LikeListMap = FootTripJSONBuilder.jsonParser(likeJSON);
			int size = Integer.parseInt(LikeListMap.get("size"));
			for(int i=0;i<size;i++){			
				HashMap<String, String> LikeMap = FootTripJSONBuilder.jsonParser(LikeListMap.get("list"+i).toString());
				LikeItem item = new LikeItem();
				item.setLikeID(LikeMap.get("Like_ID"));
				item.setProfile_img(FootTripCommunication.SERVER_URL+LikeMap.get("Liker_Profile_img"));
				item.setLikeName(LikeMap.get("Liker_name"));
				if(LikeMap.get("IS_FOLLOW").equalsIgnoreCase("true")){
					item.setIsFollowable(true);
				}else{
					item.setIsFollowable(false);
				}
				
				likeItems.add(item);
			}
			likeAdapter = new LikeListAdapter(this,likeItems);
			listView.setAdapter(likeAdapter);
			likeAdapter.notifyDataSetChanged();
		}
	}
	
	public class LikeListAdapter extends BaseAdapter{
		private Activity activity;
		private List<LikeItem> Items;
		private LayoutInflater inflater;
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		
		public LikeListAdapter(Activity activity, List<LikeItem> Items) {
			super();
			this.activity = activity;
			this.Items = Items;
		}
		@Override
		public int getCount() {
			return Items.size();
		}
		@Override
		public Object getItem(int location) {
			return Items.get(location);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (inflater == null)
				inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();
			if(convertView == null){
				convertView = inflater.inflate(R.layout.item_like, parent, false);
				holder.profile_img = (CircledNetworkImageView) convertView.findViewById(R.id.profile_img);
				holder.like_name = (TextView) convertView.findViewById(R.id.like_name);
				holder.follow_btn = (ImageButton) convertView.findViewById(R.id.follow_btn);
				convertView.setTag(holder);             
			}
			LikeItem item = Items.get(position);
			holder = (ViewHolder) convertView.getTag();
			final int pos = position;
			final String myID = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");

			holder.profile_img.setImageUrl(item.getProfile_img(), imageLoader);
			holder.like_name.setText(item.getLikeName());
			
			if(item.getLikeID().equals(myID))
				holder.follow_btn.setVisibility(View.INVISIBLE);
			
			if(item.isFollowable())
				holder.follow_btn.setImageResource(R.drawable.unfollow_btn);
			
			holder.follow_btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ImageButton btn = (ImageButton)v;
					HashMap<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("ID_Type", "foottrip");
					paramMap.put("myID", myID);
					paramMap.put("followID", likeItems.get(pos).getLikeID());
					switch(v.getId()){
					case R.id.follow_btn:
						if(likeItems.get(pos).isFollowable()){
							btn.setImageResource(R.drawable.unfollow_btn);
							Items.get(pos).setIsFollowable(false);
							likeAdapter.notifyDataSetChanged();
							String resp = RequestMethods.followUserRequest(paramMap);
						}else{
							btn.setImageResource(R.drawable.follow_btn);
							Items.get(pos).setIsFollowable(true);
							likeAdapter.notifyDataSetChanged();
							String resp = RequestMethods.followUserRequest(paramMap);
						}				
						break;
					}
				}
			});

			
			return convertView;
		}
	}
	public static class ViewHolder {
		CircledNetworkImageView profile_img;
		TextView like_name;
		ImageButton follow_btn;
	}
}
