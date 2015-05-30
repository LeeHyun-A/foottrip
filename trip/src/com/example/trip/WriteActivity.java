package com.example.trip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.codejava.server.FootTripJSONBuilder;
import request.codeJava.client.RequestMethods;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.trip.RecordModel.DataSet;
import com.foottrip.newsfeed.data.GalleryItem;

public class WriteActivity extends Activity implements OnClickListener {
	private ImageButton ok_btn,back_btn,picture_btn,video_btn,picture_plus_btn,tag_btn,video_plus_btn;
	public EditText content_text;
	public ArrayList<String> images;
	public ArrayList<String> imagesInfo;
	private ArrayList<GalleryItem> imageLists;
	private boolean flag;
	private final int REQ_CODE_CUSTOM_GALARY = 10;
	private final int REQ_CODE_SELECT_IMAGE = 100;
	Bitmap profilBit = null;
	String filePath;
	String readDataItem;
	private String logFilePath;
	private String logData;
	private String regionCode;
	private ArrayList<String> photos;
	private ArrayList<String> photosGPS;
	private ArrayList<String> photosTime;
	private ArrayList<String> movies;


	Intent intentFromSNS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_write);

		intentFromSNS = new Intent();
		intentFromSNS = getIntent();

		readDataItem = intentFromSNS.getStringExtra("ITEMVALUE");
		logFilePath = readDataItem.replace("LogList_","").replace(".dat",".png");
		
		/*
		 * button 
		 * */
		ok_btn = (ImageButton)findViewById(R.id.ok_btn);
		back_btn = (ImageButton)findViewById(R.id.back_btn);
		picture_btn = (ImageButton)findViewById(R.id.picture_btn);
		picture_plus_btn = (ImageButton)findViewById(R.id.picture_plus_btn);
		tag_btn = (ImageButton)findViewById(R.id.tag_btn);

		content_text = (EditText)findViewById(R.id.content_text);
		ok_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		picture_btn.setOnClickListener(this);
		picture_plus_btn.setOnClickListener(this);
		tag_btn.setOnClickListener(this);

		images = new ArrayList<String>();
		imagesInfo = new ArrayList<String>();
		imageLists = new ArrayList<GalleryItem>();
		flag = true;

		readDataWithList(readDataItem);
	}

	public void onClick(View view){

		if(view.getId() == R.id.back_btn){
			Toast.makeText(getApplicationContext(), "Finish the job", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		else if(view.getId() == R.id.ok_btn){
			Toast.makeText(getApplicationContext(), "Pressed ok button", Toast.LENGTH_SHORT).show();
			//Get user account from shared-preference
			SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
			String UserID = pref.getString("ID", "");

			//Set Log Image
			String absPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
			logFilePath = absPath + "/FOOTTRIP/logimg/" + logFilePath;
			images.add(0, logFilePath);

			for(GalleryItem item : imageLists){
				if(item.isCheckable()){
					images.add(item.getImagePath());
					imagesInfo.add(item.getImageGPS()+","+FootTripDate.INTDATEtoDATE(item.getImageTime()));
				}
			}
			//Send server board data
			String content = content_text.getText().toString();
			String resultStr = RequestMethods.boardWriteRequest(content,images,images.size(),UserID,logData,regionCode,imagesInfo);

			//The work of checking if writing board data in server was finished.
			HashMap<String, String> responseMap = FootTripJSONBuilder.jsonParser(resultStr);
			if(responseMap.get("accessable")!=null && responseMap.get("accessable").equals("ack")){
				Toast.makeText(getApplicationContext(), "Success to write!", Toast.LENGTH_LONG).show();
				finish();
			}
		}
		
		else if(view.getId() == R.id.picture_btn){
			getImageToCustomGalary(photos,photosGPS,photosTime);
		}
		else if(view.getId() == R.id.picture_plus_btn){
			doTakeAlbumAction();			
		}else if(view.getId() == R.id.tag_btn){
			Toast.makeText(getApplicationContext(), "tag", Toast.LENGTH_SHORT).show();
		}
	}

	public String[] getRecordFileNames(){
		// Gets the files in the directory
		File fileDirectory = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/FOOTTRIP/");
		// Lists all the files into an array
		File[] dirFiles = fileDirectory.listFiles();

		String[] datStr = null;
		if(dirFiles == null) return null;
		if (dirFiles.length != 0) {
			datStr = new String[dirFiles.length];
			// loops through the array of files, outputing the name to console
			for (int ii = 0; ii < dirFiles.length; ii++) {
				//				String fileOutput = dirFiles[ii].toString();
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
				String movieStr = "";
				
				logData = RM.getLogPaths();
				regionCode = RM.getRegionCode();
				Log.i("log :",regionCode);
				
				
				photos = new ArrayList<String>();
				photosGPS = new ArrayList<String>();
				photosTime = new ArrayList<String>();
				
				for(int i=0;i<RM.getPhotoLists().size();i++){
					DataSet ds = RM.getPhotoLists().get(i);
					photos.add(ds.getPath());
					photosGPS.add(ds.getGps());
					photosTime.add(ds.getTimeByStr());
					
					str += "Photo File #"+(i+1) +
							"\nfile address: "+ ds.getPath() +
							"\nposition: "+ ds.getGps() + 
							"\ntime: "+ ds.getTimeByStr() + "\n";
				}
				
				for(int i=0;i<RM.getVideoLists().size();i++){
					DataSet ds = RM.getVideoLists().get(i);
					movieStr += "***\nVideo File #"+(i+1) +
							"\nfile address: "+ ds.getPath() +
							"\nposition: "+ ds.getGps() + 
							"\ntime: "+ ds.getTimeByStr() + "\n";
				}

				
				
				String data[] = str.split("\n");
				String movieData[] = movieStr.split("\n");
//				if(data.length > 0){
//					photos = new ArrayList<String>();
//				}
				
//				for(int i = 1; i < data.length; i=i+5){
//					Log.i("data"+i, data[i]);
//					String tmp = data[i].substring(14);
//					photos.add(tmp);
//
//				}
				
				if(movieData.length > 0){
					movies = new ArrayList<String>();
				}
				for(int i = 1; i < movieData.length; i=i+5){
					Log.i("movie data"+i, movieData[i]);
					String tmp = movieData[i].substring(14);
					movies.add(tmp);

				}

				fos.close();
				objectin.close();
			}catch (Exception e) { 
				// TODO: handle exception
				Log.e("File error #2", e.getMessage());
			}
		}
	}

	private void doTakeAlbumAction(){
		Intent intent = new Intent(Intent.ACTION_PICK);                
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQ_CODE_SELECT_IMAGE); 
	}

	private void getImageToCustomGalary(ArrayList<String>photoPathList,ArrayList<String>gps,ArrayList<String>time){
		Intent customGalaryIntent = new Intent(WriteActivity.this,GalleryActivity.class);

		if(flag){
			for(int i=0; i<photoPathList.size();i++){
				GalleryItem item = new GalleryItem();
				item.setImagePath(photoPathList.get(i));
				item.setImageGPS(gps.get(i));
				item.setImageTime(time.get(i));
				item.setCheckable(false);
				item.setUsed(false);
				imageLists.add(item);
			}
			flag = false;
			customGalaryIntent.putExtra("photoPathList", photoPathList);
			customGalaryIntent.putExtra("imageLists", imageLists);
		}else{
			customGalaryIntent.putExtra("photoPathList", photoPathList);
			customGalaryIntent.putExtra("imageLists", imageLists);
		}

		startActivityForResult(customGalaryIntent, REQ_CODE_CUSTOM_GALARY);
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) { 
		if(requestCode == REQ_CODE_SELECT_IMAGE) 
		{ 
			if(resultCode==Activity.RESULT_OK) 
			{     
				try { 
					LinearLayout layout = (LinearLayout) findViewById(R.id.content_layout);
					LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT
							);
					Bitmap image_bitmap = Images.Media.getBitmap(getContentResolver(), intent.getData());
					ImageView image = new ImageView(this);

					image.setImageBitmap(image_bitmap);   
					layout.addView(image);

				} catch (FileNotFoundException e) { 
					e.printStackTrace(); 
				} catch (IOException e) { 
					e.printStackTrace(); 
				} catch (Exception e){
					e.printStackTrace();
				} 
			}     
		}

		else if(requestCode == REQ_CODE_CUSTOM_GALARY){
			if(resultCode==Activity.RESULT_OK){
				ArrayList<GalleryItem> imageList = (ArrayList<GalleryItem>)intent.getSerializableExtra("imageList");
				imageLists = (ArrayList<GalleryItem>)intent.getSerializableExtra("imageLists");
				LinearLayout layout = (LinearLayout) findViewById(R.id.content_layout);

				for(int i=0; i<layout.getChildCount();i++){
					if(layout.getChildAt(i)!=content_text){
						layout.removeView(layout.getChildAt(i));
						i--;
					}
				}


				for(int i=0;i<imageLists.size();i++){
					try { 
						LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT
								);
						
						Display display = ((WindowManager) getApplicationContext()
								.getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
						int dWidth = display.getWidth();

						if(imageLists.get(i).isCheckable()){
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 15;
							
							Bitmap src = BitmapFactory.decodeFile(imageList.get(i).getImagePath(),options);
							Bitmap resized = Bitmap.createScaledBitmap(src, dWidth, dWidth, true);
							src.recycle();
							int rotation = WriteActivity.getExifOrientation(imageList.get(i).getImagePath());
							if(rotation != 0){
								int w = resized.getWidth();
								int h = resized.getHeight();
								Matrix matrix = new Matrix();
								matrix.postRotate(rotation);
								//Bitmap tmp = resized;
								resized = Bitmap.createBitmap(resized, 0, 0, w, h, matrix, true);
								//tmp.recycle();
							}
							resized = Bitmap.createScaledBitmap(resized, resized.getWidth(), resized.getHeight(), false);

							ImageView image = new ImageView(this);
							image.setImageBitmap(resized);  
							image.setPadding(10, 10, 10, 10);
							layout.addView(image);
						}

					} catch (Exception e){
						e.printStackTrace();
					} 
				}
			}
		}
	}

	public static int getRotation(int exif_orientation){
		int orientation;

		switch(exif_orientation){
		case ExifInterface.ORIENTATION_ROTATE_90:
			orientation = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			orientation = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			orientation = 270;
			break;
		case ExifInterface.ORIENTATION_NORMAL:
		default:
			orientation = 0;
		}
		return orientation;
	}

	public static int getExifOrientation(String filePath){
		if(filePath == null) return 0;

		int orientation=0;
		try{
			ExifInterface exif = new ExifInterface(filePath);
			int exifOrientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
			orientation = getRotation(exifOrientation);
		}catch(Throwable t){}

		return orientation;
	}	
}