package com.example.trip;

import java.util.Date;

public class FootTripDate {
	public static String DateToINTString(Date d){
		return to2Dig(""+(1900+d.getYear()))+to2Dig(""+(d.getMonth()+1))+to2Dig(""+d.getDate())+to2Dig(""+d.getHours())+to2Dig(""+d.getMinutes())+to2Dig(""+d.getSeconds());
	}
	public static String DateToString(Date d){
		return to2Dig(""+(1900+d.getYear()))+"-"+to2Dig(""+(d.getMonth()+1))+"-"+to2Dig(""+d.getDate())+" "+to2Dig(""+d.getHours())+":"+to2Dig(""+d.getMinutes())+":"+to2Dig(""+d.getSeconds());
	}
	public static String INTDATEtoDATE(String ds){
		StringBuffer strtmp = new StringBuffer();
		strtmp.append(ds.subSequence(0, 4));
		strtmp.append("-");
		strtmp.append(ds.subSequence(4, 6));
		strtmp.append("-");
		strtmp.append(ds.subSequence(6, 8));
		strtmp.append(" ");
		strtmp.append(ds.subSequence(8, 10));
		strtmp.append(":");
		strtmp.append(ds.subSequence(10, 12));
		strtmp.append(":");
		strtmp.append(ds.subSequence(12, 14));
		
		return strtmp.toString();
	}
	public static Date StringToDate(String sd){
		int y,mo,d,h,mi,s;
		String[] part = sd.split(" ");
		String[] Ds = part[0].split("-");
		String[] Ss = part[1].split(":");
		y = Integer.parseInt(Ds[0])-1900;
		mo = Integer.parseInt(Ds[1])-1;
		d = Integer.parseInt(Ds[2]);
		h = Integer.parseInt(Ss[0]);
		mi = Integer.parseInt(Ss[1]);
		s = Integer.parseInt(Ss[2]);
		return new Date(y, mo, d, h, mi, s);
	}
	public static String to2Dig(String s){
		if(s!=null && s.length()==1){
			s = "0" + s;
		}return s;
	}
	public static void main(String[] args){
		System.out.println(FootTripDate.INTDATEtoDATE("20150525212221"));
	}
}