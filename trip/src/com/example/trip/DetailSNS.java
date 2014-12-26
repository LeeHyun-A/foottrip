package com.example.trip;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class DetailSNS extends Activity{

	//sns부분에서 log사진 클릭하면 이 액티비티가 뜬다.게시글 더 보기 기능을 위한?
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("detail", "here");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_sns);
		WebView browser = (WebView) findViewById(R.id.webView1);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		browser.loadUrl("file:///android_asset/detail.html");
		
	}
	public class JavaScriptInterface {
		Context mContext;
		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;
		}
		/** Show a toast from the web page */
		@JavascriptInterface
		public void showToast(String toastMsg) {
			Toast.makeText(mContext, toastMsg, Toast.LENGTH_SHORT).show();
		}
	}
}
