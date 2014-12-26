package com.example.trip;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SnsView extends Fragment{
	private View rootView;
	WebView mWeb;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {             

		rootView = inflater.inflate(R.layout.sns_view, container, false);
		mWeb = (WebView)rootView.findViewById(R.id.webView1);
		mWeb.getSettings().setJavaScriptEnabled(true);
		mWeb.addJavascriptInterface(new JavaScriptInterface(getActivity()), "Android");
		mWeb.loadUrl("file:///android_asset/sns.jsp");
		
		return rootView;
	}


	public class JavaScriptInterface {
		
		Context mContext;
		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			Log.e("interface", "here");
			mContext = c;
		}
		/** Show a toast from the web page */
		@JavascriptInterface
		public void detailInfo() {
			Intent intent = new Intent(getActivity(), DetailSNS.class);
			startActivity(intent);
		}
		
		public void doWrite(){
			//activiey
			Log.e("do", "write");
			Intent intent = new Intent(getActivity(), WriteActivity.class);
			Log.e("do", "write2");
			startActivity(intent);
			Log.e("do", "write3");
		}
	}

	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
