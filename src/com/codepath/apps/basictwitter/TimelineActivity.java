package com.codepath.apps.basictwitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.basictwitter.ComposeDialog.OnFragmentInteractionListener;
import com.codepath.apps.basictwitter.helper.EndlessScrollListener;
import com.codepath.apps.basictwitter.helper.NetworkHelper;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends FragmentActivity implements OnFragmentInteractionListener {
	
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private PullToRefreshListView lvTweets;
	private ProgressBar progressBar;
	private EditText etFragmentTrigger;
	private ComposeDialog composeDialog;
	private android.support.v4.app.FragmentManager fm;
	
	private long maxId = 0;
	
	public static int COMPOSE_ACTIVITY_REQUEST_CODE = 59;
	public static final int DETAILS_ACTIVITY_REQUEST_CODE = 58;
	
	public static final String SAVED_USER_PROFILE = "userProfile";
	public static final String SAVED_USER_ID = "uid";
	
	public User loggedInUser;
	
	public boolean isOfflineMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		getActionBar().setTitle(getResources().getString(R.string.home));
		initView();
		SharedPreferences pref = getSharedPreferences(SAVED_USER_PROFILE, MODE_PRIVATE);
		long savedUserUid = pref.getLong(SAVED_USER_ID, -1);
		
		if(NetworkHelper.isNetworkAvailable(this)) {
			client = TwitterApplication.getRestClient();
	
			if(savedUserUid < 0) {
				getLoggedInUser();
			}else{
				loggedInUser = User.get(savedUserUid);
			}
			new TweetDeleteAsyncTask().execute();
			
			initListViewEventListener();
			
			fm = getSupportFragmentManager();
			composeDialog = ComposeDialog.newInstance();
		}else{
			//off-line mode where the tweets from sqlite is shown
			isOfflineMode = true;
			if(savedUserUid > -1) {
				loggedInUser = User.get(savedUserUid);
			}
			fetchTweetsFromDB();
			etFragmentTrigger.setVisibility(View.GONE);
			Toast.makeText(this, getResources().getString(R.string.offline), Toast.LENGTH_SHORT).show();
		}
	}

	private void initListViewEventListener() {
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				populateTimeline(maxId);
			}
		});
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				initiateFetchingNewerTweets();
			}
		});	
		
		etFragmentTrigger.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				etFragmentTrigger.setVisibility(View.GONE);
				showComposeDialogFragment();
				return true;
			}
		});
	}

	private void initView() {
		progressBar = (ProgressBar)findViewById(R.id.pbLoading);
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		lvTweets.setTextPullToRefresh(getResources().getString(R.string.pull_down));
		lvTweets.setTextReleaseToRefresh(getResources().getString(R.string.release));
		lvTweets.setTextRefreshing(getResources().getString(R.string.loading));
		
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		
		//attach click listener for the list
		lvTweets.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showDetailsActivity(position);
			}		
		});
		etFragmentTrigger = (EditText)findViewById(R.id.etFragmentTrigger);
	}
	
	private void fetchTweetsFromDB() {
		TweetFetchAsyncTask task = new TweetFetchAsyncTask();
		task.execute();
	}
	
	private class TweetFetchAsyncTask extends AsyncTask<Void, Void, List<Tweet>> {

		@Override
		protected List<Tweet> doInBackground(Void... params) {
			List<Tweet> tweets = null;
			try {
				tweets = Tweet.getAll();				
			}catch(Exception e){
				e.printStackTrace();
			}
			return tweets;
		}
		
		@Override
		protected void onPostExecute(List<Tweet> result) {
			if(result != null) {
				aTweets.addAll(result);
			}
			progressBar.setVisibility(ProgressBar.INVISIBLE);
		}
		
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		
	}
	
	private class TweetDeleteAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				new Delete().from(Tweet.class).execute();
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private class TweetAddAsyncTask extends AsyncTask<ArrayList<Tweet>, Void, Void> {
		@Override
		protected Void doInBackground(ArrayList<Tweet>... params) {
			try {
				ArrayList<Tweet> list = params != null ? params[0] : null;
				for(Tweet tweet : list) {
					long userId = tweet.userId;
					User user = User.get(userId);
					if(user == null) {
						user = tweet.getUser();
						user.save();
					}
					tweet.user = user;
					tweet.save();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	public void populateTimeline(long max) {
		if(max > 0) {
			max = max -1;
		}
		String maxStr = (max > 0) ? Long.toString(max) : null;
		progressBar.setVisibility(ProgressBar.VISIBLE);
		
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JSONArray json) {
				ArrayList<Tweet> list = Tweet.fromJSONArray(json);
				if(list != null && list.size() > 0) {				
					Tweet last = list.get(list.size()-1);
					maxId = last.getUid();
					aTweets.addAll(list);
					TweetAddAsyncTask saveTask = new TweetAddAsyncTask();
					saveTask.execute(list);
				}
				progressBar.setVisibility(ProgressBar.INVISIBLE);
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.d("debug", arg1.toString());
				super.onFailure(arg0, arg1);
				progressBar.setVisibility(ProgressBar.INVISIBLE);
				Toast.makeText(TimelineActivity.this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
			}
		}, maxStr, null);
	}
	private void initiateFetchingNewerTweets() {
		String sinceIdStr = null;
		if(aTweets.getCount() > 0) {
			long sinceId = aTweets.getItem(0).getUid();
			sinceIdStr = Long.toString(sinceId);
		}
		fetchNewerTweets(sinceIdStr, null);
	}
	private void fetchNewerTweets(final String sinceId, String maxId) {
		final long since = Long.parseLong(sinceId);
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JSONArray json) {
				ArrayList<Tweet> list = Tweet.fromJSONArray(json);
				if(list != null && list.size() > 0) {
					tweets.addAll(0, list);

					Tweet last = list.get(list.size()-1);
					long topMaxId = last.getUid();
					topMaxId = topMaxId - 1;
					if(topMaxId > since) {
						fetchNewerTweets(sinceId, Long.toString(topMaxId));
					}
					TweetAddAsyncTask saveTask = new TweetAddAsyncTask();
					saveTask.execute(list);
					
				}else{
					aTweets.notifyDataSetChanged();
					lvTweets.onRefreshComplete();
				}
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.d("debug", arg1.toString());
				super.onFailure(arg0, arg1);
				lvTweets.onRefreshComplete();
				Toast.makeText(TimelineActivity.this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
			}
		}, maxId, sinceId);
		
	}
	
	public void postStatusUpdate(String tweet) {
		client.postStatusUpdate(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {
				if(json != null) {					
					Tweet tweet = Tweet.fromJson(json);
					if(tweet != null) {
						aTweets.insert(tweet, 0);
					}					
				}
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.d("debug", arg1.toString());
				super.onFailure(arg0, arg1);
				Toast.makeText(TimelineActivity.this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
			}			
		}, tweet);
	}
	
	public void getLoggedInUser() {
		client.getVerifiedCredentials(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {
				if(json != null) {
					loggedInUser = User.fromJson(json);
					loggedInUser.save();
					//save to shared preferences
					SharedPreferences pref = getSharedPreferences(SAVED_USER_PROFILE, MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putLong(SAVED_USER_ID, loggedInUser.getUid());
					editor.commit();
				}
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.d("debug", arg1.toString());
				super.onFailure(arg0, arg1);
				Toast.makeText(TimelineActivity.this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
			}			
		});
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
		if(isOfflineMode) {
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
	private void showDetailsActivity(int position) {
		if(position >=0 && position < aTweets.getCount()) {
	    	Intent detailsIntent = new Intent(this, DetailsActivity.class);
	    	if(loggedInUser != null) {
	    		detailsIntent.putExtra("user", loggedInUser);
	    	}
	    	Tweet tweet = aTweets.getItem(position);
	    	detailsIntent.putExtra("tweet", tweet);
	    	startActivityForResult(detailsIntent, DETAILS_ACTIVITY_REQUEST_CODE);
    	}   	
	}	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == COMPOSE_ACTIVITY_REQUEST_CODE) {
			String tweetText = data.getExtras().getString("text");
			postStatusUpdate(tweetText);
		}
	}
	
	@Override
	public void onFragmentInteraction(String text) {
		//update the status	
		etFragmentTrigger.setVisibility(View.VISIBLE);
		if(text != null) {
			postStatusUpdate(text);
		}
	}
	
	private void showComposeDialogFragment() {
		if(!composeDialog.isVisible()) {
			composeDialog.show(fm, "fragment_compose_twitter");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}
