package com.example.trip;

import java.util.HashMap;
import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;
import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class JoinActivity extends Activity implements OnClickListener {
	private Button join;
	private EditText email, password, firstname, lastname;
	int join_state = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�ð踸���ΰ� Ÿ��Ʋ�� ���ִ� ��.
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
			//�̸��� ����
			// �̸��� ����
			// String e= "mailto:"+emailaddress.getText().toString();
			// Intent myActivity2 = new Intent(Intent.ACTION_SENDTO,
			// Uri.parse(e));
			// startActivity(myActivity2);
			
			
			//���� ����.
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
		//get server db_id db_password
		String responseString = RequestMethods.joinRequest(email.getText().toString(), password.getText().toString(), firstname.getText().toString(), lastname.getText().toString(), null);

		System.out.println(responseString);

		//Handle response here...
		//Interpret JSON response
		HashMap<String, String> responseMap = FootTripJSONBuilder.jsonParser(responseString);
		if(responseMap.get("accessable").equals("ack")){
			//If 'Ack', then login and store id & password in android SharedPreference.
			join_state = 1;
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