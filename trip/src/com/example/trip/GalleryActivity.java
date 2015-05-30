package com.example.trip;

import java.io.File;
import java.util.ArrayList;

import com.foottrip.newsfeed.data.GalleryItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryActivity extends Activity implements OnClickListener {


	private GridView gridView;
	private ImageAdapter imageAdapter;
	private ArrayList<GalleryItem> imageList;
	private ArrayList<GalleryItem> imageLists;
	private int count;

	private ImageButton ok_btn,back_btn;
	Bundle extra;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);        
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_gallery);
		
		ok_btn = (ImageButton)findViewById(R.id.ok_btn);
		back_btn = (ImageButton)findViewById(R.id.back_btn);
		ok_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		
		extra = new Bundle();
		intent = new Intent();
		intent = getIntent();
		gridView = (GridView) findViewById(R.id.grid_view);
		
		imageList = new ArrayList<GalleryItem>();
		imageLists = new ArrayList<GalleryItem>();
		imageLists = (ArrayList<GalleryItem>)intent.getSerializableExtra("imageLists");
		ArrayList<String>photoPathList = new ArrayList<String>();
		photoPathList = intent.getStringArrayListExtra("photoPathList");
		count=0;
		Log.i("gallerySize","item : " + imageLists.size());
		
		for(int i=0;i<photoPathList.size();i++){
			GalleryItem imageItem = new GalleryItem();
			Log.i("gallery","here");
			imageItem.setImagePath(photoPathList.get(i));
			imageItem.setCheckable(false);
			//imageLists.get(i).setCheckable(true);
			imageList.add(imageItem);
			count++;
		}
		imageAdapter = new ImageAdapter(this, imageList);
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
            Bitmap resized = null;
            int size = imageList.size();
            // If size is 0, there are no images on the SD Card.
            if (size == 0) {
                //No Images available, post some message to the user
            }
            Display display = ((WindowManager) getApplicationContext()
					.getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
			int dWidth = display.getWidth();
            
            
            for (int i = 0; i < size; i++) {
            	
                //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            	BitmapFactory.Options option = new BitmapFactory.Options();
    			if(new File(imageList.get(i).getImagePath()).length() > 100000){
    				option.inSampleSize = 10;	
    			}else{
    				option.inSampleSize = 2;
    			}
    			
				bitmap = BitmapFactory.decodeFile(imageList.get(i).getImagePath(),option);
				resized = Bitmap.createScaledBitmap(bitmap, dWidth, dWidth, true);
				int rotation = WriteActivity.getExifOrientation(imageList.get(i).getImagePath());
				if(rotation != 0){
					int w = resized.getWidth();
					int h = resized.getHeight();
					Matrix matrix = new Matrix();
					matrix.postRotate(rotation);
					resized = Bitmap.createBitmap(resized, 0, 0, w, h, matrix, true);
				}
				publishProgress(new LoadedImage(resized));
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
			if(count == 0){
				Toast.makeText(getApplicationContext(),
                        "Please select at least one image",
                            Toast.LENGTH_LONG).show();
			}else{
				
				intent.putExtra("imageList",imageList);
				setResult(RESULT_OK, intent); 
				
        		finish();
        		
			}

		}

	}


	public class ImageAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private Context mContext;
		private ArrayList<GalleryItem>mImageList;
        private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

		public ImageAdapter(Context c, ArrayList<GalleryItem>imageList){
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mContext = c;
			mImageList = imageList;
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
				convertView = mInflater.inflate(R.layout.item_gallery, null);
				holder.imageview = (ImageView)convertView.findViewById(R.id.cover);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			holder.imageview.setId(position);
			holder.imageview.setLongClickable(true);
			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.imageview.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View arg0) {
					int id = arg0.getId();
					Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
					i.putExtra("imgPath",imageList.get(id).getImagePath());
					startActivity(i);
					return true;
				}
			});

			holder.imageview.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int id = v.getId();
					ImageView img = (ImageView)v;
					if(imageLists.get(id).isCheckable()){
						img.setAlpha(255);
						imageList.get(id).setCheckable(false);
						imageLists.get(id).setCheckable(false);
					}else{
						img.setAlpha(128);
						imageList.get(id).setCheckable(true);
						imageLists.get(id).setCheckable(true);
					}
					notifyDataSetChanged();
				}
			});
			
			holder.imageview.setImageBitmap(photos.get(position).getBitmap());
			if(!imageLists.get(position).isCheckable())
				holder.imageview.setAlpha(255);
			else
				holder.imageview.setAlpha(128);
			return convertView;
		}
		class ViewHolder {
			ImageView imageview;
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
