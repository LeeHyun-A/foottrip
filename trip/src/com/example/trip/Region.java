package com.example.trip;

import java.util.HashMap;

public class Region {
	static final String[] region = { "서울","부산","인천","대구","대전","광주","울산","수원","창원","성남","고양","용인","부천","청주",
		"안산","전주","안양","천안","남양주","포항","김해","화성","의정부","시흥","구미","제주","평택","진주","광명",
		"파주","원주","익산","아산","군포","춘천","여수","경산","군산","순천","경주","양산","목포","거제",
		"광주","김포","강릉","충주","이천","양주","구리","오산","안성","안동","서산","의왕","당진","포천",
		"하남","광양","제천","서귀포","통영","김천","공주","논산","정읍","영주","사천","밀양","상주","보령",
		"영천","동두천","동해","김제","속초","남원","나주","문경","삼척","과천","태백","계룡","세종","여주" };
	HashMap<Integer,String> regionMap = null;

	public Region(){
		//Initiate Region data
		int regID = 0;
		regionMap = new HashMap<Integer,String>();

		for(String regName : region){
			regionMap.put(regID++, regName);
		}

	}

	/* *
	 * @author ieunseong
	 * @return region name
	 * @exception return null if there are no matching region key
	 * */
	public String getRegionName(int key){
		if(key <0 || key > region.length)
			try {
				throw new Exception("Region key is out of range!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return regionMap.get(key);
	}

	/* *
	 * @author ieunseong
	 * @return key value of region name
	 * @exception return -1 if there are no matching region name
	 * */
	public int getRegionID(String regionName){
		for(int i=0;i<region.length;i++){
			if(regionName.equals(getRegionName(i))){
				return i;
			}
		}
		throw new IllegalArgumentException("That region name is not available");
	}
}
