package com.foottrip.newsfeed.data;

import java.io.Serializable;

public class CommentItem implements Serializable{
	private String userID;
	private String userName;
	private String profile_img;
	private String timeStamp;
	private String comment;
	public CommentItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CommentItem(String userID, String userName, String profile_img,
			String timeStamp, String comment) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.profile_img = profile_img;
		this.timeStamp = timeStamp;
		this.comment = comment;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProfile_img() {
		return profile_img;
	}
	public void setProfile_img(String profile_img) {
		this.profile_img = profile_img;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
