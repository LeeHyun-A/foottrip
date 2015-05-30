
package com.example.trip;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.foottrip.newsfeed.app.AppController;
import com.foottrip.newsfeed.app.CTextView;
import com.foottrip.newsfeed.app.CircledNetworkImageView;
import com.foottrip.newsfeed.data.CommentItem;

import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;
import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class commentform extends Activity{
	// 리스트뷰에 보여질 문자열 배열로 할당
	private String mBoardID = null;

	private EditText addcomE;
	private ImageButton addcomB;

	private ArrayList<CommentItem> commentItems;
	private ListView listView;

	private CommentListAdapter commentAdapter;

	private static class TIME_MAXIMUM{
		public static final int SEC = 60;
		public static final int MIN = 60;
		public static final int HOUR = 24;
		public static final int DAY = 30;
		public static final int MONTH = 12;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comment);

		Bundle bund = getIntent().getExtras();
		String CommentJSON = bund.getString("CommentInfo");
		mBoardID = bund.getString("boardID");
		addcomE = (EditText) findViewById(R.id.editcomment);
		addcomB = (ImageButton) findViewById(R.id.addcomment);

		//Defensive code!
		if(mBoardID==null) return;

		commentItems = new ArrayList<CommentItem>();
		listView = (ListView) findViewById(R.id.comment_listview);

		if(CommentJSON==null){
			commentAdapter = new CommentListAdapter(this,commentItems);
			listView.setAdapter(commentAdapter);
			//commentAdapter.notifyDataSetChanged();
		}else{
			//Success to getting comment info
			HashMap<String, String> CommentListMap = FootTripJSONBuilder.jsonParser(CommentJSON);
			int size = Integer.parseInt(CommentListMap.get("Comment_size"));
			for(int i=0;i<size;i++){
				Log.d("content",CommentListMap.get("list"+i).toString());
				HashMap<String, String> CommentMap = FootTripJSONBuilder.jsonParser(CommentListMap.get("list"+i).toString());

				CommentItem item = new CommentItem();
				item.setUserID(CommentMap.get("comment_user_ID"));
				item.setUserName(CommentMap.get("comment_user_name"));
				item.setProfile_img(FootTripCommunication.SERVER_URL+CommentMap.get("comment_user_profile_img"));
				item.setTimeStamp(CommentMap.get("comment_write_time"));
				item.setComment(CommentMap.get("comment"));

				commentItems.add(item);
			}
			commentAdapter = new CommentListAdapter(this,commentItems);
			listView.setAdapter(commentAdapter);
			//commentAdapter.notifyDataSetChanged();	
		}
		//댓글 입력에대한 listener
		addcomB.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//프로필, 이름은 임시로 해놨고 댓글작성창에서 string 받아와서 리스트에 추가하고
				String comment = addcomE.getText().toString();
				addcomE.setText("");

				//[서버에다 댓글 정보 업로드하는 로직]
				SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
				String mUserID = pref.getString("ID", "");

				String commentEnrollResult = RequestMethods.commentRequest(mBoardID, mUserID, comment);
				HashMap<String, String> CommentMap = FootTripJSONBuilder.jsonParser(commentEnrollResult);

				String mUserName = CommentMap.get("USERNAME");
				String mProfile_img = FootTripCommunication.SERVER_URL+CommentMap.get("PROFILEIMG");
				String mWriteTime = CommentMap.get("TIMESTAMP");

				CommentItem item = new CommentItem(mUserID,mUserName,mProfile_img,mWriteTime,comment);
				commentItems.add(item);
				commentAdapter.notifyDataSetChanged();

			}});
	}
	public class CommentListAdapter extends BaseAdapter{
		private Activity activity;

		private List<CommentItem> Items;

		private LayoutInflater inflater;

		ImageLoader imageLoader = AppController.getInstance().getImageLoader();


		public CommentListAdapter(Activity activity, List<CommentItem> Items) {
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
				convertView = inflater.inflate(R.layout.item_comment, parent, false);

				holder.profile_img = (CircledNetworkImageView)convertView.findViewById(R.id.profile_img);
				holder.username_comment = (TextView)convertView.findViewById(R.id.username_comment);
				holder.timestamp = (TextView)convertView.findViewById(R.id.timestamp);
				convertView.setTag(holder);             

			}
			Typeface font = Typeface.createFromAsset(getAssets(),"fonts/magic.ttf");
			CommentItem item = Items.get(position);
			holder = (ViewHolder) convertView.getTag();
			final int pos = position;

			holder.profile_img.setImageUrl(item.getProfile_img(),imageLoader);
			String fulltext = "";
			String subtext = "";

			fulltext = item.getUserName()+"  "+item.getComment();
			subtext = item.getUserName();
			holder.username_comment.setText(fulltext, TextView.BufferType.SPANNABLE);
			

			Spannable str = (Spannable) holder.username_comment.getText();
			int i = fulltext.indexOf(subtext);
			int color =  0xffff7011;
			str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			holder.username_comment.setTypeface(font);
			String formatTime = item.getTimeStamp();
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date;
			try {
				date = format.parse(formatTime);
				formatTime = formatTimeString(date);
				holder.timestamp.setText(formatTime);
				holder.timestamp.setTypeface(font);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			return convertView;

		}

	}

	public class ViewHolder {
		CircledNetworkImageView profile_img;
		TextView username_comment;
		TextView timestamp;
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
