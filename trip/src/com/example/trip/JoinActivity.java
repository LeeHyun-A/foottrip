package com.example.trip;

import android.app.Activity;
import android.content.Context;
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

	private float screenWidth;
	private float screenHeight;
	private Button join;
	private Bundle save;
	private EditText email, password, name;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.save = savedInstanceState;
		//시계만냅두고 타이틀바 없애는 것.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.join);

		init();

		join = (Button) findViewById(R.id.joinbtn);
		
		name = (EditText)findViewById(R.id.name);
		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);
		
		join.setOnClickListener(this);
		
//		/** designate location **/
//		// logo
//		LayoutParams params = (LayoutParams) logo.getLayoutParams();
//		logo.setX(screenWidth / 8);
//		logo.setY(screenWidth / 8);
//		params.width = (int) screenWidth*6 / 8;
//		params.height = (int) screenHeight *3/8;
//		logo.setLayoutParams(params);
//		//email
//		email.setX(screenWidth/8);
//		email.setY(screenHeight*4/8);
//		emailaddress.setX(screenWidth*3/8);
//		emailaddress.setY(screenHeight*4/8);
//		params = (LayoutParams)emailaddress.getLayoutParams();
//		params.width = (int)screenWidth*4/8;
//		emailaddress.setLayoutParams(params);
//		//password
//		password.setX(screenWidth/8);
//		password.setY(screenHeight*5/8);
//		passwordinput.setX(screenWidth*3/8);
//		passwordinput.setY(screenHeight*5/8);
//		params = (LayoutParams)passwordinput.getLayoutParams();
//		params.width = (int)screenWidth*4/8;
//		passwordinput.setLayoutParams(params);
//		// join
//		params = (LayoutParams) join.getLayoutParams();
//		join.setX(screenWidth*5/8);
//		join.setY(screenHeight * 7/8);
//		params.width = (int) (screenWidth * 2 / 6 - 10);
//		params.height = (int) screenWidth / 6;
//		join.setLayoutParams(params);
//		
		
	}

	private void init() {
		// receive window size
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == join.getId()) {
			// 이메일 전송
			// String e= "mailto:"+emailaddress.getText().toString();
			// Intent myActivity2 = new Intent(Intent.ACTION_SENDTO,
			// Uri.parse(e));
			// startActivity(myActivity2);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}