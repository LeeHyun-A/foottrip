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
	private ArrayList<DataSet> mLogPathList;

	public RecordModel(){
		mPhotoList = new ArrayList<DataSet>();
		mVideoList = new ArrayList<DataSet>();
		mVoiceList = new ArrayList<DataSet>();
		mLogPathList = new ArrayList<DataSet>();

	}
	//getter & setter
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
	
	public DataSet getPhotoList(DataSet ds) {
		for(int i=0;i<mPhotoList.size();i++){
			if(mPhotoList.get(i).isDataSetSame(ds)) return mPhotoList.get(i);
		}
		return null;
	}

	public void addPhotoList(String dPath, String dGps, Date dDate){
		mPhotoList.add(new DataSet(dPath, dGps, dDate));
	}

	public DataSet getVideoList(DataSet ds) {
		for(int i=0;i<mVideoList.size();i++){
			if(mVideoList.get(i).isDataSetSame(ds)) return mVideoList.get(i);
		}
		return null;
	}
	public void addVideoList(String dPath, String dGps, Date dDate){
		mVideoList.add(new DataSet(dPath, dGps, dDate));
	}
	public DataSet getVoiceList(DataSet ds) {
		for(int i=0;i<mVoiceList.size();i++){
			if(mVoiceList.get(i).isDataSetSame(ds)) return mVoiceList.get(i);
		}
		return null;
	}
	public void addVoiceList(String dPath, String dGps, Date dDate) {
		mVoiceList.add(new DataSet(dPath, dGps, dDate));
	}
	public DataSet getLogPathList(DataSet ds) {
		for(int i=0;i<mLogPathList.size();i++){
			if(mLogPathList.get(i).isDataSetSame(ds)) return mLogPathList.get(i);
		}
		return null;
	}
	public void addLogPathList(String dPath, String dGps, Date dDate) {
		mLogPathList.add(new DataSet(dPath, dGps, dDate));
	}

	/**
	 * @author Hyun-a
	 * @category data type model 
	 */
	public class DataSet implements Serializable{
		private String mDataPath;		//data's absolute path(ê¸°ê¸°ë³„ë¡œ ?‹¤ë¦?) 
		private String mGps;			//user location when data was made(lat/lng)
		private Date mTime;				//the time which indicate when data was made

		public DataSet(String path, String gps, Date time){
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
		public Date getTime(){
			return mTime;
		}
		public String getTimeByStr() {
			return mTime.toString();
		}
		public void setTime(Date time) {
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
