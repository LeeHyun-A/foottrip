package com.example.trip;



import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trip.RecordModel.DataSet;

public class StartTab extends Fragment implements OnClickListener {

	private Button start;
	private View rootView;
	private Context mContext;

	private Button btn;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {             
		rootView = inflater.inflate(R.layout.starttab, container, false);
		//
		mContext = rootView.getContext();




		start = (Button)rootView.findViewById(R.id.start);	
		start.setOnClickListener(this);
		btn = (Button)rootView.findViewById(R.id.button1);
		btn.setOnClickListener(this);


		ImageView img = (ImageView)rootView.findViewById(R.id.run);
		BitmapDrawable frame1 =(BitmapDrawable)getResources().getDrawable(R.drawable.run1);
		BitmapDrawable frame2 =(BitmapDrawable)getResources().getDrawable(R.drawable.run2);
		BitmapDrawable frame3 =(BitmapDrawable)getResources().getDrawable(R.drawable.run3);
		BitmapDrawable frame4 =(BitmapDrawable)getResources().getDrawable(R.drawable.run4);
		BitmapDrawable frame5 =(BitmapDrawable)getResources().getDrawable(R.drawable.run5);
		// Get the background, which has been compiled to an AnimationDrawable object.
		int reasonableDuration = 250;
		AnimationDrawable mframeAnimation = new AnimationDrawable();
		mframeAnimation.setOneShot(false); // loop continuously
		mframeAnimation.addFrame(frame1, reasonableDuration);
		mframeAnimation.addFrame(frame2, reasonableDuration);
		mframeAnimation.addFrame(frame3, reasonableDuration);
		mframeAnimation.addFrame(frame4, reasonableDuration);
		mframeAnimation.addFrame(frame5, reasonableDuration);
		img.setBackgroundDrawable(mframeAnimation);
		mframeAnimation.setVisible(true, true);
		mframeAnimation.start();


		return rootView;
	}





	//gps설정 화면으로 넘어가도록.
	private boolean chkGpsService() {

		String gps = android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

			// GPS OFF 일때 Dialog 표시 
			AlertDialog.Builder gsDialog = new 	AlertDialog.Builder(mContext); 
			gsDialog.setTitle("위치 서비스 설정");   
			gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?"); 
			gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int which) { 
					// GPS설정 화면으로 이동 
					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS); 
					intent.addCategory(Intent.CATEGORY_DEFAULT); 
					startActivity(intent); 


				} 
			})
			.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).create().show();
			return false;

		} else { 
			return true; 
		} 
	}




	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if(v.getId() == start.getId()){
			chkGpsService();

			//chkgpservice함수를 불러 gps를 키게 만들고 gps가 켜져있을 경우레만 start버튼을 클릭하였을 때 서비스가 실행되게 한다.
			String gps = android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {}
			else{
				getActivity().startService(new Intent(getActivity(),FusedLocationService.class));


				StopTab.setStartTimer(-1);
				//탭 내에서  다른 프레그먼트로 교체하는 방법.(activity와는 조금 다름)
				Fragment mFragment = new StopTab();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				//첫번째탭에 listener를 set해준다.
				getActivity().getActionBar().getTabAt(0).setTabListener(new TabListener(mFragment));
				//Replacing using the id of the container and not the fragment itself
				ft.replace(R.id.tabparent, mFragment);
				ft.addToBackStack(null);//프로젝트명 뭐임
				ft.commit();

				getActivity().finish();
			}
		}
		else if(v.getId() == btn.getId()){
			final String[] items = getRecordFileNames();
			if(items != null){ 
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("Make your selection");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// Do something with the selection
						readDataWithList(items[item]);
						//					Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_LONG).show();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}else{
				Toast.makeText(getActivity(), "파일 생성 안함", Toast.LENGTH_LONG).show();
			}

		}
	}
	public String[] getRecordFileNames(){
		// gets the files in the directory
		File fileDirectory = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/FOOTTRIP/");
		// lists all the files into an array
		File[] dirFiles = fileDirectory.listFiles();

		String[] datStr = null;
		if(dirFiles == null) return null;
		if (dirFiles.length != 0) {
			datStr = new String[dirFiles.length];
			// loops through the array of files, outputing the name to console
			for (int ii = 0; ii < dirFiles.length; ii++) {
				//String fileOutput = dirFiles[ii].toString();
				datStr[ii] = dirFiles[ii].getName().split("/FOOTTRIP/")[0];
			}
		}
		return datStr;
	}
	public void readDataWithList(String fileName){
		String deviceIndependentRootAddress = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
		File file  = new File(deviceIndependentRootAddress + "/FOOTTRIP");

		Log.d("errorTest",""+file.exists());
		if(file.exists()){
			try {						
				FileInputStream fos = new FileInputStream (deviceIndependentRootAddress + "/FOOTTRIP/"+fileName);

				ObjectInputStream objectin = new ObjectInputStream(fos);
				RecordModel RM = new RecordModel();

				RM = (RecordModel)objectin.readObject();
				String str = "";
				for(int i=0;i<RM.getPhotoLists().size();i++){
					DataSet ds = RM.getPhotoLists().get(i);
//					Log.d("file address", ""+ds.getPath());
//					Log.d("position",""+ds.getGps());
//					Log.d("time",""+ds.getTimeByStr());

					str += "Photo File #"+(i+1) +
							"\nfile address: "+ ds.getPath() +
							"\nposition: "+ ds.getGps() + 
							"\ntime: "+ ds.getTimeByStr() + "\n";
				}
				for(int i=0;i<RM.getVideoLists().size();i++){
					DataSet ds = RM.getVideoLists().get(i);
//					Log.d("file address", ""+ds.getPath());
//					Log.d("position",""+ds.getGps());
//					Log.d("time",""+ds.getTimeByStr());

					str += "***\nVideo File #"+(i+1) +
							"\nfile address: "+ ds.getPath() +
							"\nposition: "+ ds.getGps() + 
							"\ntime: "+ ds.getTimeByStr() + "\n";
				}
				for(int i=0;i<RM.getVoiceLists().size();i++){
					DataSet ds = RM.getVoiceLists().get(i);
//					Log.d("file address", ""+ds.getPath());
//					Log.d("position",""+ds.getGps());
//					Log.d("time",""+ds.getTimeByStr());

					str += "***\nVoice File #"+(i+1) +
							"\nfile address: "+ ds.getPath() +
							"\nposition: "+ ds.getGps() + 
							"\ntime: "+ ds.getTimeByStr() + "\n";
				}
				
				Log.d("read file test", str);
				Toast.makeText(rootView.getContext(), str, Toast.LENGTH_LONG).show();
				
				fos.close();
				objectin.close();
			}catch (Exception e) { // TODO: handle exception
				Log.e("File error #2", e.getMessage());
			}
		}
	}





}
