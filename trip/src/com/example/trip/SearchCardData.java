package com.example.trip;

import java.text.Collator;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

public class SearchCardData {
	 /**
     * 리스트 정보를 담고 있을 객체 생성
     */
    // regionImg
    public Drawable mImg;
    // region
    public String mRegion;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<SearchCardData> ALPHA_COMPARATOR = new Comparator<SearchCardData>() {
        private final Collator sCollator = Collator.getInstance();
         
        @Override
        public int compare(SearchCardData mListDate_1, SearchCardData mListDate_2) {
            return sCollator.compare(mListDate_1.mImg, mListDate_2.mRegion);
        }
    };
}
