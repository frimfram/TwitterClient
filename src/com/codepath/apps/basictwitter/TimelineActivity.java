package com.codepath.apps.basictwitter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnListFragmentInteractionListener;
import com.codepath.apps.basictwitter.listeners.SupportFragmentTabListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;

public class TimelineActivity extends ActionBarActivity 
	implements OnListFragmentInteractionListener {

	private EditText etComposeTrigger;
	private User loggedInUser;
	
	public static int COMPOSE_ACTIVITY_REQUEST_CODE = 59;
	public static final int DETAILS_ACTIVITY_REQUEST_CODE = 58;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		getSupportActionBar().setTitle("");
		initUser();
		initView();
		setupTabs();
		if(((TwitterApplication)getApplication()).isOffline()) {
			Toast.makeText(this, getResources().getString(R.string.offline), Toast.LENGTH_LONG).show();
		}
	}

	private void initView() {
		etComposeTrigger = (EditText)findViewById(R.id.etFragmentTrigger);
		etComposeTrigger.setOnTouchListener(new OnTouchListener() {			
			@SuppressLint("ClickableViewAccessibility") @Override
			public boolean onTouch(View v, MotionEvent event) {
				showComposeInActivity();
				return true;
			}
		});
	}

	private void setupTabs() {
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		android.support.v7.app.ActionBar.Tab tab1 = actionBar
			.newTab()
			.setText(getResources().getString(R.string.home_cap))
			.setTag("HomeTimelineFragment")
			.setTabListener(
				new SupportFragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "home",
						HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		android.support.v7.app.ActionBar.Tab tab2 = actionBar
			.newTab()
			.setText(getResources().getString(R.string.mentions_cap))
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new SupportFragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "mentions",
			    		MentionsTimelineFragment.class));

		actionBar.addTab(tab2);
	}	
	
	@Override
	public void onListItemClicked(Tweet tweet) {
		showDetailsActivity(tweet);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timeline, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try {
		MenuItem edit = menu.findItem(R.id.action_compose);
		if(((TwitterApplication)getApplication()).isOffline()) {
			edit.setVisible(false);
		}else{
			edit.setVisible(true);
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_compose) {
			showComposeInActivity();
		}else if(id==R.id.action_profile) {
			showProfileActivity();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showComposeInActivity() {
    	Intent composeIntent = new Intent(this, ComposeActivity.class);
    	if(loggedInUser != null) {
    		composeIntent.putExtra("user", loggedInUser);
    	}
    	startActivityForResult(composeIntent, COMPOSE_ACTIVITY_REQUEST_CODE);
	}
	
	private void showProfileActivity() {
		Intent i = new Intent(this, ProfileActivity.class);
		startActivity(i);
	}
	
	private void showDetailsActivity(Tweet tweet) {
	    Intent detailsIntent = new Intent(this, DetailsActivity.class);
	    if(loggedInUser != null) {
	    	detailsIntent.putExtra("user", loggedInUser);
	    }
	    detailsIntent.putExtra("tweet", tweet);
	    startActivityForResult(detailsIntent, DETAILS_ACTIVITY_REQUEST_CODE);  	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == COMPOSE_ACTIVITY_REQUEST_CODE) {
			Tweet tweet = data.getExtras().getParcelable("tweet");
			if(tweet != null) {
				TweetsListFragment homeFragment = (TweetsListFragment)getSupportFragmentManager().findFragmentByTag("home");
				if(homeFragment != null) {
					homeFragment.addToFront(tweet);
				}
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}	
	
	private void initUser() {		
		SharedPreferences pref = getSharedPreferences(LoginActivity.SAVED_USER_PROFILE, MODE_PRIVATE);
		loggedInUser = User.fromSharedPreferences(pref);
	}

}
