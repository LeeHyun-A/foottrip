package com.example.trip;

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

	private Bundle save;

	//DB
	private String DbId = "a";
	private String DbPassword = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.save = savedInstanceState;
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

		//db에서 아이디 체크.현재는 디파인 된 값으로 체크.
		Log.i("ID", email.getText().toString());
		if((email.getText().toString()).equals(DbId) && (password.getText().toString()).equals(DbPassword)){
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
