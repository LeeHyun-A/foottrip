package com.example.trip;

public class DbModel {
	private String lat;
	private String lng;
	private String state;
	private String date;
	
	public DbModel(String lat, String lng, String state, String date){
		this.lat = lat;
		this.lng = lng;
		this.state = state;
		this.date = date;		
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
