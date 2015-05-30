package com.foottrip.newsfeed.data;

import java.io.Serializable;
import java.util.ArrayList;

public class TopPlaceItem implements Serializable{
	private String topID;
	private int count;
	private String lat;
	private String lng;
	private ArrayList<PhotoInfo> PhotoInfo;
	public TopPlaceItem() {
		super();
		this.PhotoInfo = new ArrayList<PhotoInfo>();
		// TODO Auto-generated constructor stub
	}
	

	public String getTopID() {
		return topID;
	}
	public void setTopID(String topID) {
		this.topID = topID;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public ArrayList<PhotoInfo> getPhotoInfo() {
		return PhotoInfo;
	}
	public void setPhotoInfo(String imagePath, String imageLat, String imageLng) {
		this.PhotoInfo.add(new PhotoInfo(imagePath,imageLat,imageLng));
	}
	
	public class PhotoInfo implements Serializable{
		private String imagePath;
		private String imageLat;
		private String imageLng;
		public PhotoInfo(String imagePath, String imageLat, String imageLng) {
			super();
			this.imagePath = imagePath;
			this.imageLat = imageLat;
			this.imageLng = imageLng;
		}
		public String getImagePath() {
			return imagePath;
		}
		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}
		public String getImageLat() {
			return imageLat;
		}
		public void setImageLat(String imageLat) {
			this.imageLat = imageLat;
		}
		public String getImageLng() {
			return imageLng;
		}
		public void setImageLng(String imageLng) {
			this.imageLng = imageLng;
		}
		
		
	}
}
