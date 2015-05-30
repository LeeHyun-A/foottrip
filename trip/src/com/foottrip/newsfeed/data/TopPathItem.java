package com.foottrip.newsfeed.data;

import java.io.Serializable;
import java.util.ArrayList;

public class TopPathItem implements Serializable{
	private String clusterID;
	private ArrayList<BoardInfo> boardInfo;
	
	
	public TopPathItem() {
		super();
		this.boardInfo = new ArrayList<BoardInfo>();
	}
	

	public String getClusterID() {
		return clusterID;
	}


	public void setClusterID(String clusterID) {
		this.clusterID = clusterID;
	}

	public ArrayList<BoardInfo> getBoardInfo() {
		return boardInfo;
	}

	public void setBoardInfo(String boardID, ArrayList<String>lat, ArrayList<String>lng) {
		this.boardInfo.add(new BoardInfo(boardID,lat,lng)); 
	}

	public class BoardInfo implements Serializable{
		private String boardID;
		private ArrayList<String> lat;
		private ArrayList<String> lng;
		
		public BoardInfo(String boardID, ArrayList<String> lat, ArrayList<String> lng){
			super();
			this.boardID = boardID;
			this.lat = lat;
			this.lng = lng;
		}

		public String getBoardID() {
			return boardID;
		}

		public void setBoardID(String boardID) {
			this.boardID = boardID;
		}

		public ArrayList<String> getLng() {
			return lng;
		}

		public void setLng(ArrayList<String> lng) {
			this.lng = lng;
		}

		public ArrayList<String> getLat() {
			return lat;
		}

		public void setLat(ArrayList<String> lat) {
			this.lat = lat;
		}
	
		
	}

}
