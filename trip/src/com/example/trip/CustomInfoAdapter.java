package com.example.trip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoAdapter implements InfoWindowAdapter {
	 
	private Context mContext = null;
	private Marker mMarker;
   /** Window の View. */
   private final View mWindow;
   
   
   public CustomInfoAdapter(Context context, Marker marker) {
   	mContext = context;
   	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
       mWindow = inflater.inflate(R.layout.item_marker, null);
       mMarker = marker;
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
       // ここでどの Marker がタップされたか判別する
//       if (marker.equals(mMarker)) {
//           // 画像
//           ImageView badge = (ImageView) view.findViewById(R.id.badge);
//           badge.setImageResource(R.drawable.ic_launcher);
//       }
       TextView title = (TextView) view.findViewById(R.id.title);
       TextView snippet = (TextView) view.findViewById(R.id.snippet);
       title.setText(marker.getTitle());
       snippet.setText(marker.getSnippet());
       
       
//       LinearLayout layout = (LinearLayout)view.findViewById(R.id.info_layout);
//       layout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Toast.makeText(mContext, arg0.getId()+"", Toast.LENGTH_SHORT).show();
//			}
//		});
   }

}