package com.example.trip;

import request.codeJava.client.RequestMethods;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.foottrip.newsfeed.app.AppController;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoAdapterPlace implements InfoWindowAdapter {

	private Context mContext = null;
	private Marker mMarker;
	/** Window の View. */
	private final View mWindow;
	private String mImagePath;


	public CustomInfoAdapterPlace(Context context, Marker marker, String imagePath) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		mWindow = inflater.inflate(R.layout.item_marker_place, null);
		mMarker = marker;
		mImagePath = imagePath;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		render(marker, mWindow);
		return mWindow;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	/**
	 * InfoWindow を表示する.
	 * @param marker {@link Marker}
	 * @param view {@link View}
	 */
	private void render(Marker marker, View view) {
		if (marker.equals(mMarker)) {
			// 画像
			BitmapDrawable d = new BitmapDrawable(mContext.getResources(), RequestMethods.LoadImageFromWebOperation(mImagePath));
			ImageView badge = (ImageView) view.findViewById(R.id.badge);

			badge.setVisibility(View.VISIBLE);
			
			badge.setImageDrawable(d);
			//badge.setRotation((float) 90.0);
		}
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView snippet = (TextView) view.findViewById(R.id.snippet);
		title.setText(marker.getTitle());
		snippet.setText(marker.getSnippet());


	}

}