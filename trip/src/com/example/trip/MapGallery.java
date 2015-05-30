package com.example.trip;

import java.io.File;
import java.util.ArrayList;

import com.foottrip.newsfeed.data.MapGalleryItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MapGallery extends Activity implements OnClickListener {
	
	private GridView gridView;
	private ImageAdapter imageAdapter;
	private ArrayList<MapGalleryItem> mapLists;
	private String selectedMap;
	private int prePosition;
	private int count;

	private ImageButton ok_btn,back_btn;
	Bundle extra;
	Intent intent;
	Intent intentGoToWrite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);        
		setContentView(R.layout.map_main);
		
		ok_btn = (ImageButton)findViewById(R.id.ok_btn);
		back_btn = (ImageButton)findViewById(R.id.back_btn);
		ok_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		
		extra = new Bundle();
		intent = new Intent();
		intentGoToWrite = new Intent(MapGallery.this, WriteActivity.class);
		
		intent = getIntent();
		gridView = (GridView) findViewById(R.id.grid_view);
		

		mapLists = new ArrayList<MapGalleryItem>();
		mapLists = (ArrayList<MapGalleryItem>)intent.getSerializableExtra("MAPLISTS");

		imageAdapter = new ImageAdapter(this, mapLists);
		gridView.setAdapter(imageAdapter);	
		setProgressBarIndeterminateVisibility(true); 
        loadImages();

	}

    /**
     * Load images.
     */
    private void loadImages() {
        final Object data = getLastNonConfigurationInstance();
        if (data == null) {
            new LoadImagesFromSDCard().execute();
        } else {
            final LoadedImage[] photos = (LoadedImage[]) data;
            if (photos.length == 0) {
                new LoadImagesFromSDCard().execute();
            }
            for (LoadedImage photo : photos) {
                addImage(photo);
            }
        }
    }
    /**
     * Add image(s) to the grid view adapter.
     * 
     * @param value Array of LoadedImages references
     */
    private void addImage(LoadedImage... value) {
        for (LoadedImage image : value) {
            imageAdapter.addPhoto(image);
            imageAdapter.notifyDataSetChanged();
        }
    } 
    /**
     * Save bitmap images into a list and return that list. 
     * 
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        final GridView grid = gridView;
        final int count = grid.getChildCount();
        final LoadedImage[] list = new LoadedImage[count];
        for (int i = 0; i < count; i++) {
            final ImageView v = (ImageView) grid.getChildAt(i);
            list[i] = new LoadedImage(((BitmapDrawable) v.getDrawable()).getBitmap());
        }
        return list;
    }
    /**
     * Async task for loading the images from the SD card. 
     * 
     * @author Mihai Fonoage
     *
     */
    class LoadImagesFromSDCard extends AsyncTask<Object, LoadedImage, Object> {
        
        /**
         * Load images from SD Card in the background, and display each image on the screen. 
         *  
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Object doInBackground(Object... params) {
            //setProgressBarIndeterminateVisibility(true); 
            Bitmap bitmap = null;
            Bitmap newBitmap = null;         

           
            int size = mapLists.size();
            // If size is 0, there are no images on the SD Card.
            if (size == 0) {
                //No Images available, post some message to the user
            }
            for (int i = 0; i < size; i++) {
                //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            	BitmapFactory.Options option = new BitmapFactory.Options();
    			if(new File(mapLists.get(i).getMapImagePath()).length() > 100000){
    				option.inSampleSize = 10;	
    			}else{
    				option.inSampleSize = 2;
    			}
				bitmap = BitmapFactory.decodeFile(mapLists.get(i).getMapImagePath(),option);
				publishProgress(new LoadedImage(bitmap));
            }
  
            return null;
        }
        /**
         * Add a new LoadedImage in the images grid.
         *
         * @param value The image.
         */
        @Override
        public void onProgressUpdate(LoadedImage... value) {
            addImage(value);
        }
        /**
         * Set the visibility of the progress bar to false.
         * 
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Object result) {
            setProgressBarIndeterminateVisibility(false);
        }
    }

	public void onClick(View view){
		if(view.getId() == R.id.back_btn){
			Toast.makeText(getApplicationContext(), "Finish the job", Toast.LENGTH_SHORT).show();
			finish();
		}else if(view.getId() == R.id.ok_btn){			 
				intentGoToWrite.putExtra("ITEMVALUE", selectedMap);
				startActivity(intentGoToWrite);
        		finish();
		}

	}


	public class ImageAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private Context mContext;
		private ArrayList<MapGalleryItem>mImageList;
        private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

		public ImageAdapter(Context c, ArrayList<MapGalleryItem>mapLists){
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mContext = c;
			mImageList = mapLists;
		}

		public void addPhoto(LoadedImage photo) { 
            photos.add(photo); 
        } 
		public int getCount() { 
			return photos.size();
			//return mImageList.size();
		} 

		public Object getItem(int position) { 
			return photos.get(position); 
		} 
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_map, null);
				holder.imageview = (ImageView)convertView.findViewById(R.id.cover);
				holder.date = (TextView)convertView.findViewById(R.id.date);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			holder.date.setText(mapLists.get(position).getDate());
			holder.imageview.setId(position);
			holder.imageview.setLongClickable(true);
			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);		
			holder.imageview.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View arg0) {
					int id = arg0.getId();
					Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
					i.putExtra("imgPath",mapLists.get(id).getMapImagePath());
					startActivity(i);
					return true;
				}
			});

			holder.imageview.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int id = v.getId();
					ImageView prev = (ImageView)findViewById(prePosition);
					prev.setAlpha(255);
					prePosition = id;
					ImageView img = (ImageView)v;
					img.setAlpha(128);
					selectedMap = mapLists.get(id).getMapName();
					notifyDataSetChanged();
				}
			});
			
			holder.imageview.setImageBitmap(photos.get(position).getBitmap());
			return convertView;
		}
		class ViewHolder {
			ImageView imageview;
			TextView date;
			boolean checked;
			int id;
		}

	}
	/**
     * A LoadedImage contains the Bitmap loaded for the image.
     */
    private static class LoadedImage {
        Bitmap mBitmap;
        LoadedImage(Bitmap bitmap) {
            mBitmap = bitmap;
        }
        public Bitmap getBitmap() {
            return mBitmap;
        }
    }

}
