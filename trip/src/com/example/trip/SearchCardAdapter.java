package com.example.trip;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class SearchCardAdapter extends BaseAdapter{
	private Context mContext = null;
	private ArrayList<SearchCardData> mListData = new ArrayList<SearchCardData>();

	public SearchCardAdapter(Context mContext) {
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
		SearchCardData addInfo = null;
		addInfo = new SearchCardData();
		addInfo.mImg = img;
		addInfo.mRegion = mRegion;


		mListData.add(addInfo);
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

		SearchCardData mData = mListData.get(position);

		//image
		if (mData.mImg != null) {
			holder.mImg.setVisibility(View.VISIBLE);
			holder.mImg.setImageDrawable(mData.mImg);
		}else{
			holder.mImg.setVisibility(View.GONE);
		}
		//text
		holder.mRegion.setText(mData.mRegion);
		//star
		ImageView item = new ImageView(mContext);
		item.setImageResource(R.drawable.star);



		return convertView;
	}
}
class ViewHolder{
	public TextView mRegion;
	public ImageView mImg;
	public GridView mStar;
}

