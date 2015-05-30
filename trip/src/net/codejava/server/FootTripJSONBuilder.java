package net.codejava.server;

import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class FootTripJSONBuilder {
	public static JSONObject getJSONobjBase(int msg_type){
		JSONObject jsonObj = new JSONObject();
		try{
			if(msg_type < 1000) jsonObj.accumulate("request_from", "footrip_client");
			else jsonObj.accumulate("response_from", "footrip_server");

			switch(msg_type){
			case FootTripCommunication.JOIN_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.JOIN_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.LOGIN_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.LOGIN_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.NEWSFEED_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.NEWSFEED_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.DETAIL_VIEW_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.DETAIL_VIEW_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.LIKE_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.LIKE_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.LIKE_DETAIL_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.LIKE_DETAIL_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;	
			case FootTripCommunication.COMMENT_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.COMMENT_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.COMMENT_DETAIL_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.COMMENT_DETAIL_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.UPLOAD_BOARD_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.UPLOAD_BOARD_REQUEST);
				jsonObj.accumulate("hasFile", true);
				break;
			case FootTripCommunication.PROFILE_IMAGE_UPLOAD_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.PROFILE_IMAGE_UPLOAD_REQUEST);
				jsonObj.accumulate("hasFile", true);
				break;
			case FootTripCommunication.FOLLOW_USER_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.FOLLOW_USER_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.JOIN_CHECK_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.JOIN_CHECK_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.GET_FOLLOWER_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.GET_FOLLOWER_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.GET_FOLLOWEE_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.GET_FOLLOWER_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.PROFILE_CARD_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.PROFILE_CARD_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.CARD_DETAIL_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.CARD_DETAIL_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.SEARCH_AS_REGION_REQUEST: 
				jsonObj.accumulate("category", FootTripCommunication.SEARCH_AS_REGION_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.SEARCH_AS_PERSON_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.SEARCH_AS_PERSON_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.HOT_PLACE_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.HOT_PLACE_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.BOARD_LIST_REQUEST:
				jsonObj.accumulate("category", FootTripCommunication.BOARD_LIST_REQUEST);
				jsonObj.accumulate("hasFile", false);
				break;
				
			case FootTripCommunication.JOIN_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.JOIN_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.LOGIN_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.LOGIN_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.NEWSFEED_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.NEWSFEED_RESPONSE);
				jsonObj.accumulate("hasFile", true);
				break;
			case FootTripCommunication.DETAIL_VIEW_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.DETAIL_VIEW_RESPONSE);
				jsonObj.accumulate("hasFile", true);
				break;
			case FootTripCommunication.LIKE_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.LIKE_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.LIKE_DETAIL_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.LIKE_DETAIL_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.COMMENT_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.COMMENT_RESPONSE);
				//Afterward, if we use profile images in the comments then we should change this to 'true'
				jsonObj.accumulate("hasFile", false);	
				break;
			case FootTripCommunication.COMMENT_DETAIL_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.COMMENT_DETAIL_RESPONSE);
				//Afterward, if we use profile images in the comments then we should change this to 'true'
				jsonObj.accumulate("hasFile", false);	
				break;
			case FootTripCommunication.UPLOAD_BOARD_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.UPLOAD_BOARD_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.PROFILE_IMAGE_UPLOAD_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.PROFILE_IMAGE_UPLOAD_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.FOLLOW_USER_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.FOLLOW_USER_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.JOIN_CHECK_RESPONSE: 
				jsonObj.accumulate("category", FootTripCommunication.JOIN_CHECK_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.GET_FOLLOWER_RESPONSE: 
				jsonObj.accumulate("category", FootTripCommunication.GET_FOLLOWER_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.GET_FOLLOWEE_RESPONSE: 
				jsonObj.accumulate("category", FootTripCommunication.GET_FOLLOWEE_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.PROFILE_CARD_RESPONSE: 
				jsonObj.accumulate("category", FootTripCommunication.PROFILE_CARD_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.CARD_DETAIL_RESPONSE: 
				jsonObj.accumulate("category", FootTripCommunication.CARD_DETAIL_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.SEARCH_AS_REGION_RESPONSE: 
				jsonObj.accumulate("category", FootTripCommunication.SEARCH_AS_REGION_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.SEARCH_AS_PERSON_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.SEARCH_AS_PERSON_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.HOT_PLACE_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.HOT_PLACE_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
			case FootTripCommunication.BOARD_LIST_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.BOARD_LIST_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;
				
			case FootTripCommunication.ERROR_RESPONSE:
				jsonObj.accumulate("category", FootTripCommunication.ERROR_RESPONSE);
				jsonObj.accumulate("hasFile", false);
				break;

			default:
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return jsonObj;
	}
	
	/**
	 * For android platform
	 * @param JSON string
	 * @return JSON object given HashMap
	 */
	public static HashMap<String, String> jsonParser(String jsonStr){
		HashMap<String, String> JsonMap = null;
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			if(jsonObj==null || jsonObj.length()==0) return null;
			
			JsonMap = new HashMap<String, String>();
			Iterator<String> it = jsonObj.keys();
			while(it.hasNext()){
				String key = it.next();
				JsonMap.put(key, jsonObj.getString(key));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JsonMap;
	}
	
	/**
	 * @param JSON string
	 * @return JSON object given HashMap
	 */
	public static HashMap<String, String> jsonObjectToHashmap(JSONObject jsonObj){
		if(jsonObj==null || jsonObj.length()==0) return null;
		
		HashMap<String, String> JsonMap = null; 
		try {
			JsonMap = new HashMap<String, String>();
			Iterator<String> it = jsonObj.keys();
			while(it.hasNext()){
				String key = it.next();
				JsonMap.put(key, jsonObj.getString(key));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JsonMap;
	}
}