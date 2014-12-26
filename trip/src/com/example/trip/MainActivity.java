package com.example.trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	private Button sign, login;
	private EditText email, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//시계만냅두고 타이틀바 없애는 것.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		if(pref.getString("ID", "") != "" && pref.getString("PASSWORD","") != ""){
			Intent intent = new Intent(MainActivity.this, ActionTab.class);
			startActivity(intent);

			finish();

		}

		sign = (Button) findViewById(R.id.sign);
		login = (Button) findViewById(R.id.login);
		sign.setOnClickListener(this);
		login.setOnClickListener(this);
		email = (EditText)findViewById(R.id.email);
		password= (EditText)findViewById(R.id.password);





	}

	public void login(){
		int login_state = -1;
		//get server db_id db_password
		// TODO Auto-generated method stub
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//request respond
//		swdmtest 1234
		try {
			String url = "http://192.9.83.209:8089/footrip/login";
			HttpPost post = new HttpPost(url);

			List<NameValuePair> p = new ArrayList<NameValuePair>();
			p.add(new BasicNameValuePair("login", "user-data"));
			p.add(new BasicNameValuePair("E-mail", email.getText().toString()));
			p.add(new BasicNameValuePair("user_password", password.getText().toString()));
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(p,HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePOST = httpClient.execute(post);  
			HttpEntity resEntity = responsePOST.getEntity();

			String responseString = EntityUtils.toString(resEntity, "UTF-8");
			System.out.println(responseString);
			//[{"accessable":"ack","login":"login-respond"}]
			responseString = responseString.substring(2, responseString.length() - 2);//remove []
			String list[] = responseString.split(",");
			HashMap<String, String> hm = new HashMap<String, String>();
			for(int i = 0; i < list.length; i++){
				String keyVal[] = list[i].split(":");//"key" "val"
				hm.put(keyVal[0].substring(1,keyVal[0].length()-1), keyVal[1].substring(1,keyVal[1].length()-1));
			}
			
			if(hm.get("accessable").equals("ack")){
				//if ack -> login 서버에 id password가 제대로 돼있을 경우.
				login_state = 1;
			}
			// handle response here...
		}catch (Exception ex) {
			// handle exception here
		} finally {
			httpClient.getConnectionManager().shutdown();
		}



		//db에서 아이디 체크.현재는 디파인 된 값으로 체크.
		//if login_state = 1 : ack / -1 : nack
		if(login_state == 1){
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("ID", email.getText().toString());
			edit.putString("PASSWORD", password.getText().toString());
			edit.commit();

			Intent intent = new Intent(MainActivity.this, ActionTab.class);
			startActivity(intent);

			finish();
		}
		else{
			Log.i("login", "fail");
			AlertDialog dialBox = loginFailDialog();
			dialBox.show();
		}


	}


	private AlertDialog loginFailDialog(){
		AlertDialog myQuittingDialogBox =
				new AlertDialog.Builder(this)
		//set message, title, and icon
		.setMessage("Check your ID and Password.")
		.setIcon(R.drawable.ic_launcher)
		.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//whatever should be done when answering "YES" goes here

			}
		})
		.create();//setPositiveButton

		return myQuittingDialogBox;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == login.getId()) {
			if(email.getText().toString().length() == 0 || password.getText().toString().length() == 0){
				AlertDialog dialBox = loginFailDialog();
				dialBox.show();
			}
			else
				login();
		} else if (v.getId() == sign.getId()) {
			Intent intent = new Intent(MainActivity.this, JoinActivity.class);
			startActivity(intent);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
