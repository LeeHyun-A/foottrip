package com.example.trip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

public class PickerActivity extends FragmentActivity {
	public static final Uri FRIEND_PICKER = Uri.parse("picker://taggable_friends");
	
	private FriendPickerFragment friendPickerFragment;
	private boolean isResultOK = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pickers);

		//Get pick_type from the activity who called it
		Bundle activityBundle = getIntent().getExtras();
		String pick_type = activityBundle.getString("pick_type");
		
		Bundle args = new Bundle();
		args.putString(FriendPickerFragment.FRIEND_PICKER_TYPE_KEY, pick_type);
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragmentToShow = null;
		Uri intentUri = getIntent().getData();
		
		if (FRIEND_PICKER.equals(intentUri)) {
			if (savedInstanceState == null) {
				friendPickerFragment = new FriendPickerFragment(args);
			} else {
				friendPickerFragment = 
						(FriendPickerFragment) manager.findFragmentById(R.id.picker_fragment);
			}
			// Set the listener to handle errors
			friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
				@Override
				public void onError(PickerFragment<?> fragment,
						FacebookException error) {
					PickerActivity.this.onError(error);
				}
			});
			
//			friendPickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener(){
//				@Override
//				public void onSelectionChanged(PickerFragment<?> fragment) {
//					// TODO Auto-generated method stub
//					List<GraphUser> userList = friendPickerFragment.getSelection();
//					Log.d("Test",userList.size()+"");
//				}
//			});
			
			// Set the listener to handle button clicks
			friendPickerFragment.setOnDoneButtonClickedListener(
					new PickerFragment.OnDoneButtonClickedListener() {
						@Override
						public void onDoneButtonClicked(PickerFragment<?> fragment) {
							List<GraphUser> userList = friendPickerFragment.getSelection();
							ArrayList<String> IDlists = new ArrayList<String>();
							for(int i=0;i<userList.size();i++){
								IDlists.add(userList.get(i).getId());
//								Log.d("ID",userList.get(i).getId());
							}
							Intent in = new Intent();
							in.putExtra("selectUserList", (Serializable)IDlists);
							setResult(RESULT_OK, in); 
							isResultOK = true;
							
							finishActivity();
						}
					});
			fragmentToShow = friendPickerFragment;

		} else {
			// Nothing to do, finish
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		manager.beginTransaction()
		.replace(R.id.picker_fragment, fragmentToShow)
		.commit();
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    if (FRIEND_PICKER.equals(getIntent().getData())) {
	        try {
	            friendPickerFragment.loadData(false);
	        } catch (Exception ex) {
	            onError(ex);
	        }
	    }
	}

	private void onError(Exception error) {
		onError(error.getLocalizedMessage(), false);
	}

	private void onError(String error, final boolean finishActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ERROR").
		setMessage(error).
		setPositiveButton("OK", 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				if (finishActivity) {
					finishActivity();
				}
			}
		});
		builder.show();
	}

	private void finishActivity() {
		if(!isResultOK) setResult(RESULT_CANCELED, null);
		
		finish();
	}

}
