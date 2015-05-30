package request.codeJava.client;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.codejava.server.FootTripCommunication;
import net.codejava.server.FootTripJSONBuilder;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class RequestMethods {
	public static String joinRequest(String id, String pw, String fname, String lname, Map<String,String> extras){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.JOIN_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("E-mail", id);
			jsonObject.accumulate("user_password", pw);
			jsonObject.accumulate("first_name", fname);
			jsonObject.accumulate("last_name", lname);
			if(extras!=null){
				for(String extraKey : extras.keySet()) 
					jsonObject.accumulate(extraKey, extras.get(extraKey));
			}

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > "SERVER REPLIED:\n".length()){
				//Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String loginRequest(String id, String pw){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.LOGIN_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("E-mail", id);			//abcd1234@naver.com
			jsonObject.accumulate("user_password", pw);	//qwer1234!

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > 0){
				return resp;
				//Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String newspeedRequest(){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.NEWSFEED_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("Account", "abcd123@naver.com");//abcd1234@naver.com");
			jsonObject.accumulate("index", 0);

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "SERVER REPLIED:\n";
			for (String line : response) {
				resp += line+"\n";
			}
			if(resp.length() > "SERVER REPLIED:\n".length()){
				//	Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}

	public static String boardDetailRequest(String BoardID){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.DETAIL_VIEW_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("Account", "abcd123@naver.com");//abcd1234@naver.com");
			jsonObject.accumulate("Board_ID", BoardID);

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "SERVER REPLIED:\n";
			for (String line : response) {
				resp += line+"\n";
			}
			if(resp.length() > "SERVER REPLIED:\n".length()){
				//Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String likeBoardRequest(String user_ID, String board_ID){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.LIKE_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("Account", user_ID);
			jsonObject.accumulate("Board_ID", board_ID);

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			//좋아요랑 좋아요 취소 구분해서 뉴스피드 화면상에서 변화주어야함
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > 0){
				//				Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String likeDetailRequest(String Board_ID, String User_ID){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.LIKE_DETAIL_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("Board_ID", Board_ID);
			jsonObject.accumulate("Account", User_ID);

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}

			//Get JSON
			HashMap<String, String> dataMap = FootTripJSONBuilder.jsonParser(resp);
			String JSONresult = dataMap.get("data");

			if(resp.length() > 0){
				//				Toast.makeText(getApplicationContext(), str, 1000).show();
				return JSONresult;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			ex.printStackTrace();
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String commentRequest(String BoardID, String writeID, String comment){
		//파라미터 
		//1.어떤 게시글에 댓글 달았는지 알아야지
		//2.누가 댓글을 달았는지 알아야하지
		//3.댓글 내용
		//나중에 댓글에도 좋아요 하려면? 이중 마샬링하면 될듯?? 좋아요 xml리스트를 댓글xml안에 다가 집어넣으면 될거같어 
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.COMMENT_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("Board_ID", BoardID);
			jsonObject.accumulate("Account", writeID);
			jsonObject.accumulate("Comment", comment);

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > 0){
				//				Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String commentDetailRequest(String boardID){
		//파라미터 1.어떤 게시글의 댓글이 보고싶은 건지 알아야지 
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.COMMENT_DETAIL_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("Board_ID", boardID);

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			HashMap<String, String> dataMap = FootTripJSONBuilder.jsonParser(resp);
			String JSONresult = dataMap.get("data");	//return value

			if(resp.length() > 0){
				//				Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
				return JSONresult;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String boardWriteRequest(String content, ArrayList<String> Paths, int size, String userID,String LogData, String regionCode,
			ArrayList<String>imagesInfo){
		//request && respond
		try {
			//			Log.i("board", "here");
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.UPLOAD_BOARD_REQUEST);

			if(regionCode == null) regionCode = "0"; //set regionCode Seoul as default value. 
			// *2. Add additional information in the JSON
			jsonObject.accumulate("Account", userID);
			jsonObject.accumulate("content", content);
			jsonObject.accumulate("LogData", LogData);
			jsonObject.accumulate("RegionCode", regionCode);

			// *3. Add File or not
			//Make photo-meta data JSON
			JSONObject photometaJSON = new JSONObject();
			List<File> fileList = new ArrayList<File>();

			for(int i=0;i<size;i++){
				File f = new File(Paths.get(i));
				if(i==0){
					jsonObject.accumulate("LogImageFileName", f.getName());
					Log.d("Log image test",f.getName());
				}else{	//board images
					photometaJSON.accumulate(f.getName(), imagesInfo.get(i-1));
				}
				fileList.add(f);
			}
			jsonObject.accumulate("photo-meta", photometaJSON.toString());

			//Add file data in JSON(내비둬)
			int fileTypes = 1;	//temporary 3 (not always)
			jsonObject.accumulate("fileSize", fileList.size());
			jsonObject.accumulate("fileTypes", fileTypes);
			for(int i=0;i<fileTypes;i++){
				jsonObject.accumulate("type#"+(i)+"_nums",(i+1));
			}


			// 4. Call send-data function with converting JSONObject to JSON to String
			//sendScreen.setText(jsonObject.toString());
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, fileList);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line+"\n";
			}
			if(resp.length() > "SERVER REPLIED:\n".length()){ 
				Log.d("RESPOND",resp);
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String followUserRequest(Map<String,String> paramMap){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.FOLLOW_USER_REQUEST);

			// 2. Add additional information in the JSON
			for(String keyVal : paramMap.keySet()){
				jsonObject.accumulate(keyVal, paramMap.get(keyVal));
			}

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > "SERVER REPLIED:\n".length()){
				return resp;
				//Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static boolean checkAlreadyJoinRequest(String id){
		//Send 'true' if already joined FootTrip, 'false' if not.
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.JOIN_CHECK_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("E-mail", id);

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > 0){
				HashMap<String, String> responseMap = FootTripJSONBuilder.jsonParser(resp);

				return responseMap.get("accessable").equals("ack");
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return false;
	}
	public static String getFollowerReqeust(String userID){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.GET_FOLLOWER_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("userID", userID);			//abcd1234@naver.com

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > 0){
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String getFolloweeRequest(String userID){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.GET_FOLLOWEE_REQUEST);

			// 2. Add additional information in the JSON
			jsonObject.accumulate("userID", userID);			//abcd1234@naver.com

			// 3. Add File or not
			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > 0){
				return resp;
			}
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String uploadProfileImageRequest(String userID, String filePath){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.PROFILE_IMAGE_UPLOAD_REQUEST);

			// *2. Add additional information in the JSON
			jsonObject.accumulate("Account", userID);

			// *3. Add File or not
			String absDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
			List<File> fileList = new ArrayList<File>();
			//start
			fileList.add(new File(filePath)); // Example: "/storage/emulated/0/SendAnywhere/eunseong.png"
			jsonObject.accumulate("fileSize", fileList.size());

			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, fileList);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}

			if(resp.length() > 0) return resp;
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}

	public static String ProfileCardRequest(String userID){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.PROFILE_CARD_REQUEST);

			// *2. Add additional information in the JSON
			jsonObject.accumulate("userID", userID);

			// *3. Add File or not

			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}

			if(resp.length() > 0) return resp;
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}

		return null;
	}

	public static String CardDetailRequest(String userID, String cardRegionCode){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.CARD_DETAIL_REQUEST);

			// *2. Add additional information in the JSON
			jsonObject.accumulate("userID", userID);
			jsonObject.accumulate("CardCode", cardRegionCode);

			// *3. Add File or not

			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}

			if(resp.length() > 0) return resp;
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}

		return null;
	}


	public static String PersonQueryRequest(String userName){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.SEARCH_AS_PERSON_REQUEST);

			// *2. Add additional information in the JSON
			jsonObject.accumulate("name", userName);

			// *3. Add File or not

			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}
			if(resp.length() > 0) return resp;
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}

		return null;
	}

	public static String HotPlaceRequest(String cardRegionCode){

		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.HOT_PLACE_REQUEST);

			// *2. Add additional information in the JSON
			jsonObject.accumulate("RegionCode", cardRegionCode);

			// *3. Add File or not

			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}

			if(resp.length() > 0) return resp;
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}
		return null;
	}
	public static String SearchAsRegionRequest(String cardRegionCode){
		//request && respond
		try {
			// 1. Build jsonObject
			JSONObject jsonObject = FootTripJSONBuilder.getJSONobjBase(FootTripCommunication.SEARCH_AS_REGION_REQUEST);

			// *2. Add additional information in the JSON
			jsonObject.accumulate("RegionCode", cardRegionCode);

			// *3. Add File or not

			// 4. Call send-data function with converting JSONObject to JSON to String
			HashMap<String, String> map = FootTripJSONBuilder.jsonObjectToHashmap(jsonObject);
			List<String> response = FootTripCommunication.SendToServerByMultiUtil(jsonObject.toString(), map, null);

			// 5. Analyze server's response and toast it
			String resp = "";
			for (String line : response) {
				resp += line;
			}

			if(resp.length() > 0) return resp;
		}catch (Exception ex) {
			// handle exception here
			Log.d("ERROR", "JSON Communication error!");
			if(ex.getMessage()!=null) Log.d("content",ex.getMessage());
		}

		return null;
	}

	public static Bitmap LoadImageFromWebOperation(String url){
		url = FootTripCommunication.SERVER_URL + url;
		Bitmap micon = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 15;
		try{
			InputStream in = new java.net.URL(url).openStream();
			micon = BitmapFactory.decodeStream(in,null, options);
		}catch(Exception e){
			e.printStackTrace();
		}
		return micon;
	}
}
