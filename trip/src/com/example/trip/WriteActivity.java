package com.example.trip;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WriteActivity extends Activity implements OnClickListener {
	private ImageButton ok_btn,back_btn,map_btn,picture_btn,video_btn,tag_btn,mic_btn;
	private EditText content_text;

	private final int REQ_CODE_SELECT_IMAGE = 100;
	Bitmap profilBit = null;
	String filePath;


	private Uri mImageCaptureUri;
	private ImageView mPhotoImageView;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_write);

		ok_btn = (ImageButton)findViewById(R.id.ok_btn);
		back_btn = (ImageButton)findViewById(R.id.back_btn);
		picture_btn = (ImageButton)findViewById(R.id.picture_btn);
		video_btn = (ImageButton)findViewById(R.id.video_btn);
		tag_btn = (ImageButton)findViewById(R.id.tag_btn);
		map_btn = (ImageButton)findViewById(R.id.map_btn);
		mic_btn = (ImageButton)findViewById(R.id.mic_btn);
		content_text = (EditText)findViewById(R.id.content_text);
		//mPhotoImageView = (ImageView) findViewById(R.id.image);

		ok_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		picture_btn.setOnClickListener(this);
		video_btn.setOnClickListener(this);
		tag_btn.setOnClickListener(this);
		map_btn.setOnClickListener(this);
		mic_btn.setOnClickListener(this);

	}

	public void onClick(View view){
		if(view.getId() == R.id.back_btn){
			Toast.makeText(getApplicationContext(), "back", Toast.LENGTH_SHORT).show();			
		}else if(view.getId() == R.id.ok_btn){
			Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();			

		}else if(view.getId() == R.id.map_btn){
			Toast.makeText(getApplicationContext(), "map", Toast.LENGTH_SHORT).show();			

		}else if(view.getId() == R.id.picture_btn){
			doTakeAlbumAction();
		}else if(view.getId() == R.id.video_btn){
			Toast.makeText(getApplicationContext(), "video", Toast.LENGTH_SHORT).show();			

		}else if(view.getId() == R.id.mic_btn){
			Toast.makeText(getApplicationContext(), "mic", Toast.LENGTH_SHORT).show();			

		}else if(view.getId() == R.id.tag_btn){
			Toast.makeText(getApplicationContext(), "tag", Toast.LENGTH_SHORT).show();			

		}
	}

	private void doTakeAlbumAction(){
		Intent intent = new Intent(Intent.ACTION_PICK);                
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQ_CODE_SELECT_IMAGE); 
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 


		Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

		if(requestCode == REQ_CODE_SELECT_IMAGE) 
		{ 
			if(resultCode==Activity.RESULT_OK) 
			{     
				try { 
					//Uri?��?�� ?��미�? ?��름을 ?��?��?��?��. 
					//String name_Str = getImageNameToUri(data.getData()); 

					//?��미�? ?��?��?���? 비트맵으�? 받아?��?��. 
					LinearLayout layout = (LinearLayout) findViewById(R.id.content_layout);
					LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT
							);
					Bitmap image_bitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
					ImageView image = new ImageView(this);
					
					//ImageView image = (ImageView)findViewById(R.id.image); 
					//배치?��?��?? ImageView?�� set 
					image.setImageBitmap(image_bitmap);   
					layout.addView(image);
					//Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();


				} catch (FileNotFoundException e) { 
					// TODO Auto-generated catch block 
					e.printStackTrace(); 
				} catch (IOException e) { 
					// TODO Auto-generated catch block 
					e.printStackTrace(); 
				} catch (Exception e)
				{
					e.printStackTrace();
				} 
			}     
		}   
	}
	//
	//	/**
	//	 * ?��범에?�� ?��미�? �??��?���?
	//	 */
	//	private void doTakeAlbumAction()
	//	{
	//		// ?���? ?���?
	//		Intent intent = new Intent(Intent.ACTION_PICK);
	//		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
	//		startActivityForResult(intent, PICK_FROM_ALBUM);
	//	}
	//
	//	@Override
	//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	//	{
	//		if(resultCode != RESULT_OK)
	//		{
	//			return;
	//		}
	//
	//		switch(requestCode)
	//		{
	//		case CROP_FROM_CAMERA:
	//		{
	//			// ?���??�� ?�� ?��?��?�� ?��미�?�? ?���? 받습?��?��.
	//			// ?��미�?뷰에 ?��미�?�? 보여�??��거나 �?�??��?�� ?��?�� ?��?��?��
	//			// ?��?�� ?��?��?�� ?��?��?��?��?��.
	//			final Bundle extras = data.getExtras();
	//
	//			if(extras != null)
	//			{
	//				Bitmap photo = extras.getParcelable("data");
	//				mPhotoImageView.setImageBitmap(photo);
	//			}
	//
	//			// ?��?�� ?��?�� ?��?��
	//			File f = new File(mImageCaptureUri.getPath());
	//
	//			if(f.exists())
	//			{
	//				f.delete();
	//			}
	//
	//			break;
	//		}
	//
	//		case PICK_FROM_ALBUM:
	//		{
	//			// ?��?��?�� 처리�? 카메?��?? 같으�?�? ?��?��  break?��?�� 진행?��?��?��.
	//			// ?��?�� 코드?��?��?�� �??�� ?��리적?�� 방법?�� ?��?��?��?���? 바랍?��?��.
	//
	//			mImageCaptureUri = data.getData();
	//		}
	//
	//		case PICK_FROM_CAMERA:
	//		{
	//			// ?��미�?�? �??��?�� ?��?��?�� 리사?��즈할 ?��미�? ?��기�?? 결정?��?��?��.
	//			// ?��?��?�� ?��미�? ?���? ?��?��리�??��?��?�� ?��출하�? ?��?��?��.
	//
	//			Intent intent = new Intent("com.android.camera.action.CROP");
	//			intent.setDataAndType(mImageCaptureUri, "image/*");
	//
	//			intent.putExtra("outputX", 90);
	//			intent.putExtra("outputY", 90);
	//			intent.putExtra("aspectX", 1);
	//			intent.putExtra("aspectY", 1);
	//			intent.putExtra("scale", true);
	//			intent.putExtra("return-data", true);
	//			startActivityForResult(intent, CROP_FROM_CAMERA);
	//
	//			break;
	//		}
	//		}
	//	}

}
