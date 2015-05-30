package com.example.trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foottrip.newsfeed.data.ListCardDetailData;

import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CardListActivity extends Activity{

	//card list
	private ListView mListView = null;
	private ListCardAdapter mAdapter = null;
	private String boardID = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cardlist);
				
		//dynamic add card list 
		mListView = (ListView) findViewById(R.id.cardlist); 
		mAdapter = new ListCardAdapter(this);
		mListView.setAdapter(mAdapter);
		 
		Intent intent = getIntent();
		String userID = intent.getExtras().getString("userID");
		String regionCode = Integer.toString(intent.getExtras().getInt("regionCode"));
		String cardlist = RequestMethods.CardDetailRequest(userID,regionCode);
		Log.e("cardlist-detail", cardlist);
		HashMap<String, String> dataMap = FootTripJSONBuilder.jsonParser(cardlist);
		String JSONresult = dataMap.get("data");	//return value
		//JSON String --> JSONArray Object
		
		JSONArray contentArr = null;
		Region r = new Region();
		try {
			contentArr = new JSONArray(JSONresult);
			for(int i=0;i<contentArr.length();i++){
				JSONObject content = (JSONObject)contentArr.get(i);

				Drawable d = new BitmapDrawable(getResources(), RequestMethods.LoadImageFromWebOperation(content.get("MAPIMAGE").toString()));
				String regionName = r.getRegionName(Integer.parseInt(regionCode));
				boardID = content.get("BOARDID").toString();
				String date = boardID.toString().substring(userID.length()+1);
				date = date.substring(0, 4) +"." + date.substring(4, 6) +"." + date.substring(6, 8);
				String userName = content.get("USERNAME").toString();
				String text = content.get("CONTENT").toString();
				
				mAdapter.addItem(d, regionName, date, userName, text, boardID);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		mListView.setOnItemClickListener(new OnItemClickListener() {
		 
		    @Override
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
		        ListCardDetailData mData = mAdapter.mListData.get(position);
		        Intent detailIntent = new Intent(getApplicationContext().getApplicationContext(),DetailSNS.class);
				Bundle detailBundle = new Bundle();
				detailBundle.putString("BOARDID", mData.mBoardID);
				detailIntent.putExtras(detailBundle);
				startActivity(detailIntent);
		    }
		});
		
	}	

	/***
	 * @author LeeHyun-A
	 * for card list view
	 */

	public class ListCardAdapter extends BaseAdapter{
		private Context mContext = null;
		private ArrayList<ListCardDetailData> mListData = new ArrayList<ListCardDetailData>();

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
		public void addItem(Drawable img, String mRegion, String mDate, String mName, String mContent, String mBoardID){
		    ListCardDetailData addInfo = null;
		    addInfo = new ListCardDetailData();
		    addInfo.mImg = img;
		    addInfo.mRegion = mRegion;
		    addInfo.mDate = mDate;
		    addInfo.mName = mName;
		    addInfo.mContent = mContent;
		    addInfo.mBoardID = mBoardID;
		             
		    mListData.add(addInfo);
		}
		 
		public void remove(int position){
		    mListData.remove(position);
		    dataChange();
		}
		 
		public void sort(){
		    Collections.sort(mListData, ListCardDetailData.ALPHA_COMPARATOR);
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
				convertView = inflater.inflate(R.layout.listview_card_detail, null);

				holder.mRegion = (TextView) convertView.findViewById(R.id.regionname);
				holder.mImg = (ImageView) convertView.findViewById(R.id.cardimg);
				holder.mDate = (TextView) convertView.findViewById(R.id.date);
				holder.mName = (TextView) convertView.findViewById(R.id.name);
				holder.mContent = (TextView) convertView.findViewById(R.id.content);
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}

			ListCardDetailData mData = mListData.get(position);

			if (mData.mImg != null) {
				holder.mImg.setVisibility(View.VISIBLE);
				holder.mImg.setImageDrawable(mData.mImg);
			}else{
				holder.mImg.setVisibility(View.GONE);
			}
			holder.mRegion.setText(mData.mRegion);
			holder.mDate.setText(mData.mDate);
			holder.mName.setText(mData.mName);
			holder.mContent.setText(mData.mContent);

			
			return convertView;
		}
	}
	private class ViewHolder{
		public TextView mRegion;
		public ImageView mImg;
	    public TextView mDate;
	    public TextView mName;
	    public TextView mContent;
	}


}