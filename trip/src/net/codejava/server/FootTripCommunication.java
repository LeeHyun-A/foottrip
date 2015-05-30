package net.codejava.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class FootTripCommunication {
	//Server Information
	public static final String SERVER_IP_ADDRESS = "203.249.127.244";
	public static final int PORT_NUMBER = 8089;
	public static final String SERVER_URL = "http://"+ SERVER_IP_ADDRESS +":"+ PORT_NUMBER +"/TripServer/";

	//REQUEST from client to server
	public static final int JOIN_REQUEST = 221;
	public static final int LOGIN_REQUEST = 222;
	public static final int NEWSFEED_REQUEST = 223;
	public static final int DETAIL_VIEW_REQUEST = 224;
	public static final int LIKE_REQUEST = 225;
	public static final int LIKE_DETAIL_REQUEST = 226;
	public static final int COMMENT_REQUEST = 227;
	public static final int COMMENT_DETAIL_REQUEST = 228;
	public static final int UPLOAD_BOARD_REQUEST = 229;
	public static final int PROFILE_IMAGE_UPLOAD_REQUEST = 230;
	public static final int FOLLOW_USER_REQUEST = 231;
	public static final int REGISTER_GCM_REQUEST = 232;
	public static final int UNREGISTER_GCM_REQUEST = 233;
	public static final int JOIN_CHECK_REQUEST = 234;
	public static final int GET_FOLLOWER_REQUEST = 235;
	public static final int GET_FOLLOWEE_REQUEST = 236;
	public static final int PROFILE_CARD_REQUEST = 237;
	public static final int CARD_DETAIL_REQUEST = 238;
	public static final int SEARCH_AS_REGION_REQUEST = 239;
	public static final int SEARCH_AS_PERSON_REQUEST = 240;
	public static final int HOT_PLACE_REQUEST = 241;
	public static final int BOARD_LIST_REQUEST = 242;
	
	//RESPONSE from server to client
	public static final int JOIN_RESPONSE = 1221;
	public static final int LOGIN_RESPONSE = 1222;
	public static final int NEWSFEED_RESPONSE = 1223;
	public static final int DETAIL_VIEW_RESPONSE = 1224;
	public static final int LIKE_RESPONSE = 1225;
	public static final int LIKE_DETAIL_RESPONSE = 1226;
	public static final int COMMENT_RESPONSE = 1227;
	public static final int COMMENT_DETAIL_RESPONSE = 1228;
	public static final int UPLOAD_BOARD_RESPONSE = 1229;
	public static final int PROFILE_IMAGE_UPLOAD_RESPONSE = 1230;
	public static final int FOLLOW_USER_RESPONSE = 1231;
	public static final int REGISTER_GCM_RESPONSE = 1232;
	public static final int UNREGISTER_GCM_RESPONSE = 1233;
	public static final int JOIN_CHECK_RESPONSE = 1234;
	public static final int GET_FOLLOWER_RESPONSE = 1235;
	public static final int GET_FOLLOWEE_RESPONSE = 1236;
	public static final int PROFILE_CARD_RESPONSE = 1237;
	public static final int CARD_DETAIL_RESPONSE = 1238;
	public static final int SEARCH_AS_REGION_RESPONSE = 1239;
	public static final int SEARCH_AS_PERSON_RESPONSE = 1240;
	public static final int HOT_PLACE_RESPONSE = 1241;
	public static final int BOARD_LIST_RESPONSE = 1242;
	
	public static final int ERROR_RESPONSE = 9999;
	
	/**
	 * @purpose To get server address for each purpose
	 * @param int
	 * @return String : Server address
	 */
	public static String ServerAddressByProtocolNum(int protocol){
		switch(protocol){
		case NEWSFEED_REQUEST:
			return SERVER_URL+"FootTripNewsfeed";
		case DETAIL_VIEW_REQUEST:
			return SERVER_URL+"FootTripBoardDetail";
		case REGISTER_GCM_REQUEST:
			return SERVER_URL+"RegisterServlet";
		case UNREGISTER_GCM_REQUEST:
			return SERVER_URL+"UnregisterServlet";
		default:
			return SERVER_URL+"FootTripServer";
		}
	}
	
	/**
	 * @param JSON string
	 * @return JSON object given HashMap
	 */
	public static HashMap<String, String> jsonInterpretor(String jsonStr){
		JSONObject jsonObj = (JSONObject)JSONValue.parse(jsonStr);
		if(jsonObj==null || jsonObj.isEmpty()) return null;
		HashMap<String, String> JsonMap = new HashMap<String, String>();
		Set<String> keySet = jsonObj.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()){
			String key = it.next();
			JsonMap.put(key, jsonObj.get(key).toString());
		}
		return JsonMap;
	}
	
	/**
	 * @param clssifier object (from given JSON string)
	 * @return classifier (from given string)
	 */
	public static int getClassifier(Object obj){
		try{
			return Integer.parseInt(obj.toString());
		}catch(Exception e){
			//You shouldn't have called this method.
			System.err.println("NAN error");
		}
		return -1;
	}
	
	/**
	 * @author EunSeong
	 * @param  Send data to Server (Directly!!!)
	 * @return Response from server
	 */
	public static List<String> SendToServerByMultiUtil(String jsonStr, HashMap<String, String> jsonMap, List<File> fileList){
		String charset = "UTF-8";
		//Add multipart all files as standard form.
		try {
			//Initialize multi-part-utility
			MultipartUtility multipart = new MultipartUtility(ServerAddressByProtocolNum(UPLOAD_BOARD_REQUEST), charset);
			multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");
            
            //Add data in multi-part-utility
            multipart.addFormField("JSON", jsonStr);

            if(Boolean.parseBoolean(jsonMap.get("hasFile"))){
				//Add file as standard form
				int fileSize = Integer.parseInt(jsonMap.get("fileSize"));
				int index=0;	//Index for "fileList" to separate files to each part of group
				for(int i=0;i<fileSize;i++){
					multipart.addFilePart("file#"+(i), fileList.get(index++));
				}
			}
            return multipart.finish();
		}catch (IOException ex) {
            System.err.println(ex);
        }
        
        //In this case, there are some problem in the networking.
        return null;
	}
	
//	Codes to be deleted(not be used)
	/**
	 * @param decodedString
	 * @return bitmap (from given string)
	 */
	public static String BitmapToString(Bitmap bitmap){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
			byte [] b = baos.toByteArray();
			String temp = Base64.encodeToString(b, Base64.DEFAULT);
			return temp;
		}catch(Exception e){
			System.err.println("Error: BitmapToString error");
			e.getMessage();
			return null;
		}
	}
	/**
	 * @param encodedString
	 * @return bitmap (from given string)
	 */
	public static Bitmap StringToBitMap(String encodedString){
		try{
			byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			return bitmap;
		}catch(Exception e){
			System.err.println("Error: StringToBitmap error");
			e.getMessage();
			return null;
		}
	}
}
