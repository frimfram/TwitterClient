package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.User;
import com.codepath.oauth.OAuthLoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

	public static final String SAVED_USER_PROFILE = "userProfile";
	public static final String SAVED_USER_ID = "user_uid";
	public static final String SAVED_USER_NAME = "user_name";
	public static final String SAVED_USER_SCREENNAME = "user_screenname";
	public static final String SAVED_USER_IMAGEURL = "user_profileimage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		saveLoggedInUser();
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}
	
	private void saveLoggedInUser() {
		TwitterApplication.getRestClient().getVerifiedCredentials(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {
				if(json != null) {
					User loggedInUser = User.fromJson(json);
					//save to shared preferences
					SharedPreferences pref = getSharedPreferences(SAVED_USER_PROFILE, MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putLong(SAVED_USER_ID, loggedInUser.getUid());
					editor.putString(SAVED_USER_NAME, loggedInUser.getName());
					editor.putString(SAVED_USER_SCREENNAME, loggedInUser.getScreenName());
					editor.putString(SAVED_USER_IMAGEURL, loggedInUser.getProfileImageUrl());
					editor.commit();
					Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
					startActivity(i);
				}else{
					showError();
				}
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				arg0.printStackTrace();
				showError();
			}			
		});
	}
	
	private void showError() {
		Toast.makeText(this, getResources().getString(R.string.user_fetch_failed), Toast.LENGTH_LONG).show();
	}
}
