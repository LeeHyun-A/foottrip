package com.example.trip;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class FullImageActivity extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_full_image);
 
        // get intent data
        Intent i = getIntent();
 
        // Selected image id
        String imgPath = i.getExtras().getString("imgPath");
        Bitmap src = BitmapFactory.decodeFile(imgPath);
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        imageView.setImageBitmap(src);
    }

}
