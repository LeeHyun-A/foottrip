package com.foottrip.newsfeed.data;

import java.io.Serializable;
import java.util.ArrayList;

public class CardItem implements Serializable{
	private String id, name, userID, profileImg, timestamp, love, reply, hit, content,mapImg, region;
	private ArrayList<String> imageList,gpsData;
	private boolean isLove;
	
	public CardItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CardItem(String id, String name, String profileImg,
			String timestamp, String love, String reply, String hit,
			String content, ArrayList<String> imageList,
			ArrayList<String> gpsData, boolean isLove,String mapImg,String userID, String region) {
		super();
		this.id = id;
		this.name = name;
		this.profileImg = profileImg;
		this.timestamp = timestamp;
		this.userID = userID;
		this.love = love;
		this.reply = reply;
		this.hit = hit;
		this.content = content;
		this.imageList = imageList;
		this.gpsData = gpsData;
		this.isLove = isLove;
		this.mapImg = mapImg;
		this.region = region;
	}
	public String getRegion(){
		return region;
	}
	public void setRegion(String region){
		this.region = region;
	}
	public String getUserID(){
		return userID;
	}
	public void setUserID(String userID){
		this.userID = userID;
	}

	public String getMapImg(){
		return mapImg;
	}
	public void setMapImg(String mapImg){
		this.mapImg = mapImg;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfileImg() {
		return profileImg;
	}

	public void setProfileImg(String profileImg) {
		this.profileImg = profileImg;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getLove() {
		return love;
	}

	public void setLove(String love) {
		this.love = love;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getHit() {
		return hit;
	}

	public void setHit(String hit) {
		this.hit = hit;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<String> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<String> imageList) {
		this.imageList = imageList;
	}

	public ArrayList<String> getGpsData() {
		return gpsData;
	}

	public void setGpsData(ArrayList<String> gpsData) {
		this.gpsData = gpsData;
	}

	public boolean isLove() {
		return isLove;
	}

	public void setIsLove(boolean isLove) {
		this.isLove = isLove;
	}
	
	
}
