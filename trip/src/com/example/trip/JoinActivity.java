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
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class JoinActivity extends Activity implements OnClickListener {


	private Button join;
	private EditText email, password, firstname, lastname;
	int join_state = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//시계만냅두고 타이틀바 없애는 것.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.join);

		join = (Button) findViewById(R.id.joinbtn);		
		firstname = (EditText)findViewById(R.id.firstname);
		lastname = (EditText)findViewById(R.id.lasttname);
		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);
		
		join.setOnClickListener(this);
		
		
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == join.getId()) {
			//이메일 인증
			// 이메일 전송
			// String e= "mailto:"+emailaddress.getText().toString();
			// Intent myActivity2 = new Intent(Intent.ACTION_SENDTO,
			// Uri.parse(e));
			// startActivity(myActivity2);
			
			
			//서버 전송.
			sendServer();
			if(join_state == 1){//success join-> login
				AlertDialog dialBox = joinDialog("Welcome Foottrip~!");
				dialBox.show();				
			}
			else{//faild to join
				AlertDialog dialBox = joinDialog("Failed to sign in.");
				dialBox.show();
			}

		}

	}
	public void sendServer(){
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//request respond
		try {
			String url = "http://192.9.83.209:8089/footrip/login";
			HttpPost post = new HttpPost(url);

			List<NameValuePair> p = new ArrayList<NameValuePair>();
			p.add(new BasicNameValuePair("join", "user-data"));
			p.add(new BasicNameValuePair("E-mail", email.getText().toString()));
			p.add(new BasicNameValuePair("user_password", password.getText().toString()));
			p.add(new BasicNameValuePair("first_name", firstname.getText().toString()));
			p.add(new BasicNameValuePair("last-name", lastname.getText().toString()));
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(p,HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePOST = httpClient.execute(post);  
			HttpEntity resEntity = responsePOST.getEntity();

			String responseString = EntityUtils.toString(resEntity, "UTF-8");
			System.out.println(responseString);
			//[{"accessable":"ack","join":"join-respond"}]
			responseString = responseString.substring(2, responseString.length() - 2);//remove []
			String list[] = responseString.split(",");
			HashMap<String, String> hm = new HashMap<String, String>();
			for(int i = 0; i < list.length; i++){
				String keyVal[] = list[i].split(":");//"key" "val"
				hm.put(keyVal[0].substring(1,keyVal[0].length()-1), keyVal[1].substring(1,keyVal[1].length()-1));
			}
			
			if(hm.get("accessable").equals("ack")){
				//if ack -> login 서버에 id password가 제대로 돼있을 경우.
				join_state = 1;
			}
			// handle response here...
		}catch (Exception ex) {
			// handle exception here
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
	}
	private AlertDialog joinDialog(String str){
		AlertDialog myQuittingDialogBox =
				new AlertDialog.Builder(this)
		//set message, title, and icon
		.setMessage(str)
		.setIcon(R.drawable.ic_launcher)
		.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//whatever should be done when answering "YES" goes here
				finish();
			}
		})
		.create();//setPositiveButton

		return myQuittingDialogBox;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}