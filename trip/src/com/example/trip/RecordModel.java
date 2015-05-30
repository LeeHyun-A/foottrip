package com.example.trip;

/**
 * @category Recording travel Data Model
 * @content a record data include file information which is created when users record their trip 
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RecordModel implements Serializable{
	private ArrayList<DataSet> mPhotoList;
	private ArrayList<DataSet> mVideoList;
	private ArrayList<DataSet> mVoiceList;
	private String mLogPaths;
	private String mRegionCode;
//	Ignoe below two variables!
	private ArrayList<DataSet> mLogPathList;
	private ArrayList<DbModel> mDbList;

	public RecordModel(){
		mPhotoList = new ArrayList<DataSet>();
		mVideoList = new ArrayList<DataSet>();
		mVoiceList = new ArrayList<DataSet>();
		mLogPathList = new ArrayList<DataSet>();
	}
	//getter & setter
	public String getLogPaths() {
		return mLogPaths;
	}
	public void setLogPaths(String mLogPaths) {
		this.mLogPaths = mLogPaths;
	}
	public void setRegionCode(String mRegionCode){
		this.mRegionCode = mRegionCode;
	}
	public String getRegionCode(){
		return mRegionCode;
	}
	public ArrayList<DataSet> getPhotoLists(){
		return mPhotoList;
	}
	public ArrayList<DataSet> getVideoLists(){
		return mVideoList;
	}public ArrayList<DataSet> getVoiceLists(){
		return mVoiceList;
	}public ArrayList<DataSet> getLogPathLists(){
		return mLogPathList;
	}
	public ArrayList<DbModel> getDbLists(){
		return mDbList;
	}
	public void setDbList(ArrayList<DbModel> dbList){
		mDbList = dbList;
	}
	
	
	public DataSet getPhotoList(DataSet ds) {
		for(int i=0;i<mPhotoList.size();i++){
			if(mPhotoList.get(i).isDataSetSame(ds)) return mPhotoList.get(i);
		}
		return null;
	}

	public void addPhotoList(String dPath, String dGps, String dDate){
		mPhotoList.add(new DataSet(dPath, dGps, dDate));
	}

	public DataSet getVideoList(DataSet ds) {
		for(int i=0;i<mVideoList.size();i++){
			if(mVideoList.get(i).isDataSetSame(ds)) return mVideoList.get(i);
		}
		return null;
	}
	public void addVideoList(String dPath, String dGps, String dDate){
		mVideoList.add(new DataSet(dPath, dGps, dDate));
	}
	public DataSet getVoiceList(DataSet ds) {
		for(int i=0;i<mVoiceList.size();i++){
			if(mVoiceList.get(i).isDataSetSame(ds)) return mVoiceList.get(i);
		}
		return null;
	}
	public void addVoiceList(String dPath, String dGps, String dDate) {
		mVoiceList.add(new DataSet(dPath, dGps, dDate));
	}
	public DataSet getLogPathList(DataSet ds) {
		for(int i=0;i<mLogPathList.size();i++){
			if(mLogPathList.get(i).isDataSetSame(ds)) return mLogPathList.get(i);
		}
		return null;
	}
	public void addLogPathList(String dPath, String dGps, String dDate) {
		mLogPathList.add(new DataSet(dPath, dGps, dDate));
	}
	
	
	
	/**
	 * @author Hyun-a
	 * @category data type model 
	 */
	public class DataSet implements Serializable{
		private String mDataPath;		//data's absolute path(기기별로 ?���?) 
		private String mGps;			//user location when data was made(lat/lng)
		private String mTime;				//the time which indicate when data was made

		public DataSet(String path, String gps, String time){
			mDataPath = path;
			mGps = gps;
			mTime = time;
		}

		public String getPath() {
			return mDataPath;
		}
		public void setPath(String path) {
			this.mDataPath = path;
		}
		public String getGps() {
			return mGps;
		}
		public void setGps(String gps) {
			this.mGps = gps;
		}
		public String getTime(){
			return mTime;
		}
		public String getTimeByStr() {
			return mTime.toString();
		}
		public void setTime(String time) {
			this.mTime = time;
		}
		public boolean isDataSetSame(DataSet targetDS){
			if(!mDataPath.equals(targetDS.mDataPath)) return false;
			if(!mGps.equals(targetDS.mGps)) return false;
			if(!mTime.equals(targetDS.mTime)) return false;

			return true;
		}
	}
}
