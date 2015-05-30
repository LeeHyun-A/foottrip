package com.foottrip.newsfeed.data;

import java.io.Serializable;

public class MapGalleryItem implements Serializable{
	private String mapImagePath;
	private String mapName;
	private String date;
	private boolean checkable;
	public MapGalleryItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MapGalleryItem(String mapImagePath, String mapName, String date, boolean checkable) {
		super();
		this.mapImagePath = mapImagePath;
		this.mapName = mapName;
		this.date = date;
		this.checkable = checkable;
	}
	public String getMapImagePath() {
		return mapImagePath;
	}
	public void setMapImagePath(String mapImagePath) {
		this.mapImagePath = mapImagePath;
	}
	public String getMapName(){
		return mapName;
	}
	public void setMapName(String mapName){
		this.mapName = mapName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isCheckable() {
		return checkable;
	}
	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}
	
	

}
