package com.example.trip;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ttt extends Fragment{
	private View rootView;
	WebView mWeb;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {             
		Log.e("here", "m.m2");

		rootView = inflater.inflate(R.layout.ttttt, container, false);
		Log.e("here", "m.m");



		mWeb = (WebView)rootView.findViewById(R.id.web);
		mWeb.setWebViewClient(new MyWebClient());
		WebSettings set = mWeb.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(true);
		mWeb.loadUrl("http://leehyuna419.appspot.com");
		
		return rootView;
	}



	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
