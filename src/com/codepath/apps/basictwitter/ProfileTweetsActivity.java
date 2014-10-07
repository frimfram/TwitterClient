package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnListFragmentInteractionListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ProfileTweetsActivity extends FragmentActivity implements OnListFragmentInteractionListener {
	
	private UserTimelineFragment listFragment;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_tweets);
		user = getIntent().getParcelableExtra("profile_user");
		if(user != null) {
			getActionBar().setTitle("@" + user.getScreenName());
			loadFragment();
		}else{
			loadProfileInfo();
		}
	}
	
	private void loadFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		listFragment = UserTimelineFragment.newInstance(user.uid);
		ft.replace(R.id.flContainer, listFragment);
		ft.commit();
	}
	
	private void loadProfileInfo() {
		TwitterApplication.getRestClient().getVerifiedCredentials(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject arg1) {
				user = User.fromJson(arg1);
				getActionBar().setTitle("@" + user.getScreenName());
				loadFragment();
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
			}
		});		
	}

	@Override
	public void onListItemClicked(Tweet tweet) {
		showDetailsActivity(tweet);		
	}
	
	private void showDetailsActivity(Tweet tweet) {
	    Intent detailsIntent = new Intent(this, DetailsActivity.class);
	    SharedPreferences pref = getSharedPreferences(LoginActivity.SAVED_USER_PROFILE, MODE_PRIVATE);
		User loggedInUser = User.fromSharedPreferences(pref);
	    if(loggedInUser != null) {
	    	detailsIntent.putExtra("user", loggedInUser);
	    }
	    detailsIntent.putExtra("tweet", tweet);
	    startActivityForResult(detailsIntent, TimelineActivity.DETAILS_ACTIVITY_REQUEST_CODE);	
	}
}
