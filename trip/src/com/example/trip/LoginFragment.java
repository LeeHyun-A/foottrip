package com.example.trip;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.codejava.server.FootTripJSONBuilder;
import request.codeJava.client.RequestMethods;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class LoginFragment extends Fragment{
	private static final String TAG = LoginFragment.class.getSimpleName();
	private UiLifecycleHelper uiHelper;
	private final List<String> permissions;
	
	//Truth value for checking facebook user already logged in using their facebook account
	//It's hard to check if facebook user already logged in our 'FootTrip' application when we don't use it.
	private boolean isAlreadyLogined = false;

	//GUI Data
	private Button sign, login;
	private EditText email, password;
	
	//FB Request strings
	public static final int REQUEST_CODE = 8007;
	public static final int REQUEST_CODE_TAGGABLE = 8008;
	public static final int REQUEST_CODE_FOLLOWABLE = 8009;
	public static final String PICK_TYPE_FRIENDS = "FRIENDS";
	public static final String PICK_TYPE_TAGGABLE_FRIENDS = "TAGGABLE_FRIENDS";
	
	//Facebook user data
	HashMap<String, String> userInfoMap = null;
	
	private List<GraphUser> selectedUsers;
	
	public LoginFragment() {
		permissions = Arrays.asList("user_status,user_friends,public_profile,user_birthday,email");
	}
	public List<GraphUser> getSelectedUsers() { return selectedUsers; }
	public void setSelectedUsers(List<GraphUser> users) { selectedUsers = users; }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);

		//show app's hash key
		try {
			PackageInfo info = getActivity().getApplicationContext().getPackageManager().getPackageInfo(
					"com.example.trip", PackageManager.GET_SIGNATURES); //Your package name here
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);

		//initiate userInfoHashMap<k,v> k=v=String
		userInfoMap = new HashMap<String, String>();
		
		//change login button
		LoginButton FBloginButton = (LoginButton) view.findViewById(R.id.authButton);
		FBloginButton.setFragment(this);
		FBloginButton.setReadPermissions(permissions);
		
		if(isFootTripLogined()){
			Intent intent = new Intent(getActivity(), SnsActivity.class);
			startActivity(intent);
			getActivity().finish();
		}

		sign = (Button) view.findViewById(R.id.sign);
		login = (Button) view.findViewById(R.id.login);
		sign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), JoinActivity.class);
				startActivity(intent);
			}
		});
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(email.getText().toString().length() == 0 || password.getText().toString().length() == 0){
					AlertDialog dialBox = loginFailDialog();
					dialBox.show();
				}else login();
				
			}
		});
		email = (EditText)view.findViewById(R.id.email);
		password= (EditText)view.findViewById(R.id.password);

		return view;
	}
	
	//getting user data from facebook and send request to the foottrip server
	@SuppressWarnings("deprecation")
	private HashMap<String,String> getFacebookUserData(){
		final HashMap<String, String> userMap = new HashMap<String, String>();
		
		// If session is opened, then try to get friend list
		if (isFBLogined()) {
			Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {
				public void onCompleted(GraphUser user, Response response) {
					if (response != null) {
						// do something with <response> now
						try{
							userMap.put("fbID", user.getId());	//facebook ID
							userMap.put("Name", user.getName());
							userMap.put("First_Name", user.getFirstName());
							userMap.put("Last_Name", user.getLastName());
							userMap.put("Gender", user.getProperty("gender").toString());
							userMap.put("E-mail", user.getProperty("email").toString());
							userMap.put("Birthday", user.getBirthday());
							userMap.put("Locale", user.getProperty("locale").toString());
//							//Test result
//							Toast.makeText(getActivity().getApplicationContext(), userMap.get("Name"), Toast.LENGTH_LONG).show();
						} catch(Exception e) {
							e.printStackTrace();
							Log.d("LOG_TAG", "Exception e");
						}
						
						//[Login Option #1] - Login with Facebook account!
						//1. Getting information from Facebook(페북 연동아닌거 처리 로직 추가해야함)
						//2. Add User data in sharedPreference! 'pref'!!
						SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
						SharedPreferences.Editor edit = pref.edit();
						edit.putString("SESSION_TYPE", "Facebook");
						edit.putString("ID", userMap.get("E-mail"));
						edit.putString("fbID", userMap.get("fbID"));
//						profile image를 facebook에서 get해서 처리하는 logic 추가해야됨!!
//						if(responseMap.get("profile_img")!=null)
//							edit.putString("PROFILE_IMAGE_PATH", responseMap.get("profile_img").toString());
						edit.putString("USER_NAME", userMap.get("Name"));
						edit.commit();
						
						//3. Check facebook account is already registered in our FootTrip server
						boolean isAlreadyJoined = RequestMethods.checkAlreadyJoinRequest(userMap.get("E-mail"));
						//FootTrip 서버에 등록되어있지 않다면, 가입처리하고 밑의 내용 진행!
						if(!isAlreadyJoined){
							HashMap<String, String> extraMap = new HashMap<String, String>();
							for(String key : userMap.keySet()) extraMap.put(key, userMap.get(key));
							extraMap.remove("E-mail"); extraMap.remove("First_Name"); extraMap.remove("Last_Name");
							
							String responseString = RequestMethods.joinRequest(userMap.get("E-mail"), "", userMap.get("First_Name"), userMap.get("Last_Name"), extraMap);
							HashMap<String, String> responseMap = FootTripJSONBuilder.jsonParser(responseString);	//Interpret JSON response
							if(responseMap.get("accessable").equals("ack")){
								Toast.makeText(getActivity().getApplicationContext(), "Debug::FB join success!", Toast.LENGTH_SHORT).show();
								//Enroll additional data automatically
								//서버에 보낼 내용(비밀번호는 나중에 마이페이지에서 등록하는 걸로(비번 수정 기능))
//								userMap.get("E-mail"); 	// FootTrip ID
//								userMap.get("fbID"); 		// facebook ID key
//								userMap.get("First_Name");
//								userMap.get("Last_Name");
//								userMap.get("Gender"); 
//								userMap.get("Birthday");
							}else Toast.makeText(getActivity().getApplicationContext(), "Debug::error in join", Toast.LENGTH_SHORT).show();
						}
						
						//4. Start FootTrip Application with parameter which will be used for registration
						Intent EnrollPushIntent = new Intent(getActivity(), SnsActivity.class);
						Bundle bund = new Bundle();
						bund.putSerializable("param_hashmap", userMap);
						EnrollPushIntent.putExtras(bund);
						isAlreadyLogined = true; //set true
						
						//5. Start activity
						startActivity(EnrollPushIntent);
						getActivity().finish();
						
					}
				}
			});
		}else{
			Toast.makeText(getActivity().getApplicationContext(), "로그인 먼저하시오", Toast.LENGTH_SHORT).show();
		}
		
		return userMap;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == REQUEST_CODE) && (resultCode == LoginActivity.RESULT_OK)){
			uiHelper.onActivityResult(requestCode, resultCode, data);
		} else if (requestCode == REQUEST_CODE_FOLLOWABLE) {
			// Do nothing for now
			Toast.makeText(getActivity().getApplicationContext(), "No response", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");

			if(!isAlreadyLogined){	//check facebook user already logged in our application
				//[Login Option #1] - Login with Facebook account!
				Toast.makeText(getActivity().getApplicationContext(), "hello facebook user~!", Toast.LENGTH_SHORT).show();
				
				//1. Getting information from Facebook(페북 연동아닌거 처리 로직 추가해야함)
				getFacebookUserData();
			}
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
			//Assign login repetition preventing value 'false'
			isAlreadyLogined = false;
			//Remove preference data.
			SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
			pref.edit().clear().commit();
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	//It is must not be used for checking if user logged in FootTrip application.
	//It's only for Facebook API. Use isFootTripLogined(), instead!
	public static boolean isFBLogined(){
		if(Session.getActiveSession()==null) return false;
		return Session.getActiveSession().getState().isOpened();
	}
	
	//It is user for checking if user logged in FootTrip application.
	private boolean isFootTripLogined(){
		SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
		String sessionType = pref.getString("SESSION_TYPE",null);
		if(sessionType == null) return false;
		
		if(sessionType.equals("FootTrip")) 
			return (pref.getString("ID", null)!=null && pref.getString("PASSWORD",null)!=null);
		else if(sessionType.equals("FaceBook"))
			return (pref.getString("ID", null)!=null);
		
		return false;
	}
	
	public void login(){
		//get server db_id db_password
		String responseString = RequestMethods.loginRequest(email.getText().toString(), password.getText().toString());
		
		System.out.println(responseString);
		
		//Handle response here...
		//Interpret JSON response
		HashMap<String, String> responseMap = FootTripJSONBuilder.jsonParser(responseString);

		//If 'Ack', then login and store id & password in android SharedPreference.
		if(responseMap.get("accessable").equals("ack")){ 
			//[Login Option #2] - FootTrip 본연의 log in!
			//1. Getting information from Facebook(풋트립 접근이므로 input에서 정보 가져와~~)
			HashMap<String, String> userMap = new HashMap<String, String>();
			userMap.put("E-mail",email.getText().toString()); 	// FootTrip ID
			userMap.put("Name", responseMap.get("user_name").toString());

			//2. Add User data in sharedPreference! 'pref'!!
			SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("SESSION_TYPE", "FootTrip");
			edit.putString("ID", userMap.get("E-mail"));
			edit.putString("PASSWORD", password.getText().toString());
			if(responseMap.get("profile_img")!=null)
				edit.putString("PROFILE_IMAGE_PATH", responseMap.get("profile_img").toString());
			edit.putString("USER_NAME", userMap.get("Name"));
			edit.commit();
			
			//3. Check facebook account is already registered in our FootTrip server(풋트립 접근이므로 skip!)
			
			//4. Start FootTrip Application with parameter which will be used for registration
			Intent EnrollPushIntent = new Intent(getActivity(), SnsActivity.class);
			Bundle bund = new Bundle();
			bund.putSerializable("param_hashmap", userMap);
			EnrollPushIntent.putExtras(bund);
			
			//5. Start activity
			startActivity(EnrollPushIntent);
			getActivity().finish();
		}else{
			Log.i("login", "fail");
			AlertDialog dialBox = loginFailDialog();
			dialBox.show();
		}
	}

	private AlertDialog loginFailDialog(){
		AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity()) //set message, title, and icon
		.setMessage("Check your ID and Password.")
		.setIcon(R.drawable.ic_launcher)
		.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//whatever should be done when answering "YES" goes here

			}
		}).create();//setPositiveButton

		return myQuittingDialogBox;
	}
}
