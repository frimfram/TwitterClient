package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnListFragmentInteractionListener;
import com.codepath.apps.basictwitter.fragments.UserTimelineFixedCountListFragment;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.viewpagerindicator.CirclePageIndicator;

public class ProfileActivity extends FragmentActivity implements OnListFragmentInteractionListener  {
	
	private UserTimelineFixedCountListFragment listFragment;
	private long userId;
	private User user;
	
	private ProfileHeaderPagerAdapter adapterViewPage;
	ViewPager vpHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		user = getIntent().getParcelableExtra("profile_user");
		if(user != null) {
			userId = user.uid;
			loadActionBar(user);
			loadFragment();
			loadViewPager();
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
				loadViewPager();
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
		listFragment = UserTimelineFixedCountListFragment.newInstance(userId);

		ft.replace(R.id.flUserTimeline, listFragment);
		ft.commit();
	}
	
	private void loadViewPager() {
		vpHeader = (ViewPager)findViewById(R.id.vpProfileHeader);
		adapterViewPage = new ProfileHeaderPagerAdapter(getSupportFragmentManager(), 2, user);
		vpHeader.setAdapter(adapterViewPage);
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);

        indicator.setViewPager(vpHeader);
        
		if(user.profileBannerUrl != null) {
			ImageLoader.getInstance().loadImage(user.profileBannerUrl,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {}

						@SuppressLint("NewApi") @Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap bitmap) {
							Drawable d = new BitmapDrawable(getResources(), bitmap);
							vpHeader.setBackground(d);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {}
					});
		}
	}
	
	protected void populateProfileHeader(User u) {
		TextView tvFollowers = (TextView)findViewById(R.id.tvFollowers);
		TextView tvFollowing = (TextView)findViewById(R.id.tvFollowing);
		TextView tvTweets = (TextView)findViewById(R.id.tvTweets);
		tvFollowers.setText(Integer.toString(u.getFollowerCount()));
		tvFollowing.setText(Integer.toString(u.getFriendsCount()));
		tvTweets.setText(Integer.toString(u.statusCount));
	}

	@Override
	public void onListItemClicked(Tweet tweet) {
		// TODO Auto-generated method stub
		
	}
}
