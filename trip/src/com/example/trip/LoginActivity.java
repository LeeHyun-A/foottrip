package com.example.trip;

import static com.example.trip.CommonUtilities.SENDER_ID;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.android.gcm.GCMRegistrar;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

public class LoginActivity extends FragmentActivity {
	private LoginFragment loginFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//show app's hash key
		try {
			PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
					"com.example.trip", PackageManager.GET_SIGNATURES); //Your package name here
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {}
		
		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			loginFragment = new LoginFragment();
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment).commit();
		} else {
			// Or set the fragment from restored state info
			loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		/*
		 * Typically, an application registers automatically, so options
		 * below are disabled. Uncomment them if you want to manually
		 * register or unregister the device (you will also need to
		 * uncomment the equivalent options on options_menu.xml).
		 */
		/*
		 */
		case R.id.options_register:
			GCMRegistrar.register(this, SENDER_ID);
			return true;
		case R.id.options_unregister:
			GCMRegistrar.unregister(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}