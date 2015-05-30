package com.example.trip;

import java.util.HashMap;
import net.codejava.server.FootTripJSONBuilder;
import request.codeJava.client.RequestMethods;
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
		//Initiate the settings.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
		if(!pref.getString("ID", "").equals("") && !pref.getString("PASSWORD","").equals("")){
			Intent intent = new Intent(MainActivity.this, SnsActivity.class);
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
		String responseString = RequestMethods.loginRequest(email.getText().toString(), password.getText().toString());
		
		System.out.println(responseString);
		
		//Handle response here...
		//Interpret JSON response
		HashMap<String, String> responseMap = FootTripJSONBuilder.jsonParser(responseString);

		if(responseMap.get("accessable").equals("ack")){
			//If 'Ack', then login and store id & password in android SharedPreference.
			login_state = 1;
		}

		//if login_state = 1 : ack  ||  -1 : nack
		if(login_state == 1){
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("ID", email.getText().toString());
			edit.putString("PASSWORD", password.getText().toString());
			if(responseMap.get("profile_img")!=null)
				edit.putString("PROFILE_IMAGE_PATH", responseMap.get("profile_img").toString());
			edit.putString("USER_NAME", responseMap.get("user_name").toString());
			edit.commit();

			//
			Intent intent = new Intent(MainActivity.this, SnsActivity.class);
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
		AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this) //set message, title, and icon
		.setMessage("Check your ID and Password.")
		.setIcon(R.drawable.ic_launcher)
		.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//whatever should be done when answering "YES" goes here

			}
		}).create();//setPositiveButton

		return myQuittingDialogBox;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == login.getId()) {
			if(email.getText().toString().length() == 0 || password.getText().toString().length() == 0){
				AlertDialog dialBox = loginFailDialog();
				dialBox.show();
			}else login();
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
