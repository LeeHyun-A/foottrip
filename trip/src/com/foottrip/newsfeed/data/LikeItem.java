package com.foottrip.newsfeed.data;

import java.io.Serializable;

public class LikeItem implements Serializable{
	private String profile_img;
	private String likeID;
	private String likeName;
	private boolean isFollow;
	
	public LikeItem() {
		super();
	}
	
	public LikeItem(String profile_img, String likeID, String likeName,boolean isFollow) {
		super();
		this.profile_img = profile_img;
		this.likeID = likeID;
		this.likeName = likeName;
		this.isFollow = isFollow;
	}
	
	public boolean isFollowable(){
		return isFollow;
	}
	public void setIsFollowable(boolean isFollow){
		this.isFollow = isFollow;
	}
	public String getProfile_img() {
		return profile_img;
	}
	public void setProfile_img(String profile_img) {
		this.profile_img = profile_img;
	}
	public String getLikeID() {
		return likeID;
	}
	public void setLikeID(String likeID) {
		this.likeID = likeID;
	}
	public String getLikeName() {
		return likeName;
	}
	public void setLikeName(String likeName) {
		this.likeName = likeName;
	}
}
