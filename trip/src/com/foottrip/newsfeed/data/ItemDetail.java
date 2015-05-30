package com.foottrip.newsfeed.data;

import java.io.Serializable;

public class ItemDetail implements Serializable{
	private String imgUrl;
	private String imgTag;
	
	public ItemDetail() {
		super();
	}
	public ItemDetail(String imgUrl, String imgTag){
		super();
		this.imgUrl = imgUrl;
		this.imgTag = imgTag;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getImgTag() {
		return imgTag;
	}
	public void setImgTag(String imgTag) {
		this.imgTag = imgTag;
	}
	
	

}
