package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnListFragmentInteractionListener;
import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends FragmentActivity implements OnListFragmentInteractionListener  {
	
	private UserTimelineFragment listFragment;
	private long userId;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		user = getIntent().getParcelableExtra("profile_user");
		if(user != null) {
			userId = user.uid;
			loadActionBar(user);
			loadFragment();
		}else{
			loadProfileInfo();
		}
	}

	private void loadProfileInfo() {
		TwitterApplication.getRestClient().getVerifiedCredentials(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject arg1) {
				user = User.fromJson(arg1);
				userId = user.uid;
				loadActionBar(user);
				loadFragment();
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
			}
		});		
	}
	
	private void loadActionBar(User user2) {
		getActionBar().setTitle("@" + user2.getScreenName());
		populateProfileHeader(user2);		
	}
	
	private void loadFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		listFragment = UserTimelineFragment.newInstance(userId);

		ft.replace(R.id.flUserTimeline, listFragment);
		ft.commit();
	}
	
	protected void populateProfileHeader(User u) {
		TextView tvName = (TextView)findViewById(R.id.tvName);
		TextView tvTagline = (TextView)findViewById(R.id.tvTagline);
		TextView tvFollowers = (TextView)findViewById(R.id.tvFollowers);
		TextView tvFollowing = (TextView)findViewById(R.id.tvFollowing);
		ImageView ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
		
		tvName.setText(u.getName());
		tvTagline.setText(u.getTagline());
		tvFollowers.setText(u.getFollowerCount() + " " + getResources().getString(R.string.followers_default));
		tvFollowing.setText(u.getFriendsCount() + " " + getResources().getString(R.string.following_default));
		
		ImageLoader.getInstance().displayImage(u.getProfileImageUrl(), ivProfileImage);
	}

	@Override
	public void onListItemClicked(Tweet tweet) {
		// TODO Auto-generated method stub
		
	}
}
