package com.foottrip.newsfeed.data;

import java.text.Collator;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

public class ListCardDetailData {
	 /**
     * 리스트 정보를 담고 있을 객체 생성
     */
    // regionImg
    public Drawable mImg;
    // region
    public String mRegion;
    // content
    public String mDate;
    public String mName;
    public String mContent;
    public String mBoardID;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<ListCardDetailData> ALPHA_COMPARATOR = new Comparator<ListCardDetailData>() {
        private final Collator sCollator = Collator.getInstance();
         
        @Override
        public int compare(ListCardDetailData mListDate_1, ListCardDetailData mListDate_2) {
            return sCollator.compare(mListDate_1.mDate, mListDate_2.mDate);
        }
    };
}
