package com.foottrip.newsfeed.data;

import java.io.Serializable;

import android.graphics.Bitmap;

public class GalleryItem implements Serializable{
	private String imagePath;
	private String imageGPS;
	private String imageTime;
	private boolean checkable;
	private boolean used;
	
	public GalleryItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GalleryItem(String imagePath, String imageGPS, String imageTime,
			boolean checkable, boolean used) {
		super();
		this.imagePath = imagePath;
		this.imageGPS = imageGPS;
		this.imageTime = imageTime;
		this.checkable = checkable;
		this.used = used;
	}
	public String getImageTime() {
		return imageTime;
	}
	public void setImageTime(String imageTime) {
		this.imageTime = imageTime;
	}
	public String getImageGPS() {
		return imageGPS;
	}
	public void setImageGPS(String imageGPS) {
		this.imageGPS = imageGPS;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isCheckable() {
		return checkable;
	}
	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}
	
	public boolean isUsed(){
		return used;
	}
	public void setUsed(boolean used){
		this.used = used;
	}
	
}
