package com.example.trip;

import java.util.ArrayList;
import java.util.HashMap;

import net.codejava.server.FootTripJSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class FolloweeActivity extends Activity implements OnItemClickListener{

	private String data = "";

	// 리스트뷰에 보여질 문자열 배열로 할당
	private String[] prfPath;
	private String userId = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follow);

		
		userId = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ID", "");
		data = RequestMethods.getFolloweeRequest(userId);

		HashMap<String, String> dataMap = FootTripJSONBuilder.jsonParser(data);
		String JSONresult = dataMap.get("data");	//return value
		//JSON String --> JSONArray Object
		JSONArray contentArr = null;

		ArrayList<profile> follweeList = new ArrayList<profile>();

		try {
			contentArr = new JSONArray(JSONresult);
			prfPath = new String[contentArr.length()];

			for(int i=0;i<contentArr.length();i++){
				JSONObject content = (JSONObject)contentArr.get(i);

				Log.i("JSONSON",content.toString());
				prfPath[i] = (String) content.get("PROFILEIMG");
				follweeList.add(new profile(R.drawable.ic_launcher, content.get("USERNAME").toString()));

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		//customAdapter
		ProfilerAdapter myProfilerAdapter = new ProfilerAdapter(this, follweeList);
		// list와 adapter 연결
		ListView list = (ListView) findViewById(R.id.listview);
		list.setAdapter(myProfilerAdapter);


		// 리스트뷰 선택 시 이벤트를 설정한다.
		list.setOnItemClickListener(this);


	}


	// 리스트뷰 선택 시 이벤트 처리 메소드
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// arg1는 현재 리스트에 뿌려지고 있는 정보
		// arg2는 현재 리스트에 뿌려지고 있는 해당 id 값

		// 현재 리스트뷰에 있는 해당 값을 보기//TextView tv = (TextView) arg1;
		// 현재 리스트뷰에 나오는 문자열 확인
		//a.setText("선택된 값 : " + tv.getText());

		//		//새창열기 - 좋아요한사람의 사이트로 넘어가기 위한 코드
		//		Intent it = new Intent(getApplicationContext(), likepersonsite.class);
		//		it.putExtra("name", name[arg2]);
		//		startActivity(it);

	}

	private class profile{//멤버정보를 갖고 있는 클래스
		int image;
		String name;
		//친구 추가를 위한 링크 정보 추가해야함 ★

		public profile(int _image, String _name){
			image = _image;
			name = _name;
			//친구 추가를 위한 링크 정보도 받아와야함★
		}
	}

	//customAdapter
	private class ProfilerAdapter extends BaseAdapter{
		LayoutInflater mInflater;
		ArrayList<profile> arSrc;
		Context _context;

		public ProfilerAdapter(Context context, ArrayList<profile> personListItem){
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = personListItem;
			_context =context;
		}

		public int getCount(){
			return arSrc.size();
		}
		public Object getItem(int position){
			return arSrc.get(position);
		}
		public long getItemId(int position){
			return position;
		}

		public View getView (int position, View convertView, ViewGroup parent){
			int res = R.layout.item_follow;
			convertView = mInflater.inflate(res,parent, false);

			//프로필사진
			ImageView image = (ImageView) convertView.findViewById(R.id.image);
			if(prfPath[position]==null) image.setImageBitmap(RequestMethods.LoadImageFromWebOperation("default_profile.jpg"));
			else image.setImageBitmap(RequestMethods.LoadImageFromWebOperation(prfPath[position]));
			Log.e("image", position+" : "+prfPath[position]);
			//이름
			TextView txt = (TextView) convertView.findViewById(R.id.nameTxt);
			txt.setText(arSrc.get(position).name);

			
			/*view*/
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			image.getLayoutParams().width = display.getWidth()/6;
			image.getLayoutParams().height = display.getWidth()/6;

			return convertView;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade, R.anim.slide_out_left);
	}
}
