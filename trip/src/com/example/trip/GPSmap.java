package com.example.trip;
import java.io.Serializable;
import java.util.LinkedList;

//import javax.xml.crypto.Data;

/**
 * @category Bitmap mapped from GPS value [n x m]
 * @see Data description.
 * 		n: latitude _ distance: 31m
 * 		m: longitude _ distance: 25m
 * */
public class GPSmap{

	private LinkedList<TripGPS> mGPSbitmap;
	
	private int mbitmapMaskRowSize;
	private int mbitmapMaskColSize;
	
	private int maxLat,minLat,maxLong,minLong;

	public GPSmap(){
		mGPSbitmap = new LinkedList<TripGPS>();
		mbitmapMaskRowSize = mbitmapMaskColSize = 100;

		maxLat = maxLong = Integer.MIN_VALUE;
		minLat = minLong = Integer.MAX_VALUE;
	}
	public GPSmap(double[] lats, double[] longs){
		mGPSbitmap = new LinkedList<TripGPS>();
		if(lats.length!=longs.length){
			//error report.
			return;
		}
		maxLat = maxLong = Integer.MIN_VALUE;
		minLat = minLong = Integer.MAX_VALUE;
		for(int i=0;i<lats.length;i++){
			TripGPS tgps = new TripGPS(lats[i],longs[i]);
			addGPSinBitmap(tgps);
			
			if(maxLat < lats[i]) maxLat = tgps.getIdxX();
			if(minLat > lats[i]) minLat = tgps.getIdxX();
			if(maxLong < longs[i]) maxLong = tgps.getIdxY();
			if(minLong > longs[i]) minLong = tgps.getIdxY();
		}
		mbitmapMaskRowSize = maxLat - minLat;
		mbitmapMaskColSize = maxLong - minLong;
	}
	
	public void addGPSinBitmap(double lat, double longi){
		TripGPS tgps = new TripGPS(lat,longi);
		mGPSbitmap.add(tgps);
		
		if(maxLat < lat) maxLat = tgps.getIdxX();
		if(minLat > lat) minLat = tgps.getIdxX();
		if(maxLong < longi) maxLong = tgps.getIdxY();
		if(minLong > longi) minLong = tgps.getIdxY();

		mbitmapMaskRowSize = maxLat - minLat;
		mbitmapMaskColSize = maxLong - minLong;
	}
	public void addGPSinBitmap(TripGPS gpsVal){
		mGPSbitmap.add(gpsVal);
	}

	public class TripGPS implements Serializable{
		//Bitmap (x,y) pair
		private final int bitmapX;
		private final int bitmapY;	

		//Real GPS value for (arc,min,sec)
		private int mLatArc;
		private int mLatMin;
		private int mLatSec;
		private int mLongArc;
		private int mLongMin;
		private int mLongSec;

		public TripGPS(double lat, double longi){
			mLatArc = (int) lat;
			mLatMin = (int)((lat-mLatArc)*60);
			mLatSec = (int)(((lat-mLatArc)*60 - mLatMin)*60);
			mLongArc = (int)longi;
			mLongMin = (int)((longi-mLongArc)*60);
			mLongSec = (int)(((longi-mLongArc)*60 - mLongMin)*60);

			bitmapX = (mLatArc+180)*3600 + mLatMin*360 + mLatSec;
			bitmapY = (mLongArc+180)*3600 + mLongMin*360 + mLongSec;
		}

		public int getIdxX(){ return bitmapX; }
		public int getIdxY(){ return bitmapY; }
	}
	
	public LinkedList<TripGPS> getmGPSbitmap() {
		return mGPSbitmap;
	}
	
	
	public void setmGPSbitmap(LinkedList<TripGPS> mGPSbitmap) {
		this.mGPSbitmap = mGPSbitmap;
	}


}