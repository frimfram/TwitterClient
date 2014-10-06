package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.helper.EndlessScrollListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/*
 * Tweets list fragment with Pull-to-refresh, Infinite Scrolling and DB persistence
 */
public abstract class TweetsPagingListFragment extends TweetsListFragment {
	protected TwitterClient client;
	protected long maxId = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(isOffline) {
			fetchTweetsFromDB();
		}
	}
	
	protected void initView(View v) {
		super.initView(v);
		lvTweets.setTextPullToRefresh(getResources().getString(R.string.pull_down));
		lvTweets.setTextReleaseToRefresh(getResources().getString(R.string.release));
		lvTweets.setTextRefreshing(getResources().getString(R.string.loading));		
	}
	
	protected void initListViewEventListener() {
		super.initListViewEventListener();		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				onLoadMoreToList(false);
			}
		});
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				onLoadMoreToList(true);
			}
		});	
		
	}
	protected void onLoadMoreToList(boolean isRefreshTop) {
		if(isRefreshTop) {
			//from pull-to-refresh
			initiateFetchingNewerTweets();
		}else{
			//from scrolling
			populateTimeline(maxId);
		}
		
	}

	protected JsonHttpResponseHandler populateTimelineHandler = new JsonHttpResponseHandler() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(JSONArray json) {
			ArrayList<Tweet> list = Tweet.fromJSONArray(json);
			if(list != null && list.size() > 0) {				
				Tweet last = list.get(list.size()-1);
				maxId = last.getUid();
				addAll(list);
			}
			stopProgress();
			TweetAddAsyncTask saveTask = new TweetAddAsyncTask();
			saveTask.execute(list);
		}
		
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			stopProgress();
			arg0.printStackTrace();
			Toast.makeText(getActivity(), getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
		}
	};
	
	protected abstract void populateTimeline(long max);

	protected void initiateFetchingNewerTweets() {
		String sinceIdStr = getMostRecentTweetId();
		fetchNewerTweets(sinceIdStr, null);
	}
		
	protected class RefreshJsonHttpResponseHandler extends JsonHttpResponseHandler {
		private long since;
		private String sinceId;
		
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(JSONArray json) {
			ArrayList<Tweet> list = Tweet.fromJSONArray(json);
			if(list != null && list.size() > 0) {
				addAllToFront(list);

				Tweet last = list.get(list.size()-1);
				long topMaxId = last.getUid();
				topMaxId = topMaxId - 1;
				if(topMaxId > since) {
					fetchNewerTweets(sinceId, Long.toString(topMaxId));
				}
				TweetAddAsyncTask saveTask = new TweetAddAsyncTask();
				saveTask.execute(list);
				
			}else{
				refreshCompleted(true);
			}
		}
		
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			Log.d("debug", arg1.toString());
			super.onFailure(arg0, arg1);
			refreshCompleted(false);
			Toast.makeText(getActivity(), getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
		}
		
		public void setSinceId(String sinceid) {
			sinceId = sinceid;
			since = Long.parseLong(sinceId);
		}
	};
	
	protected RefreshJsonHttpResponseHandler fetchNewerHandler = new RefreshJsonHttpResponseHandler();
	
	protected abstract void fetchNewerTweets(final String sinceId, String maxId);
	
	protected void refreshCompleted(boolean notify) {
		super.refreshCompleted(notify);
		lvTweets.onRefreshComplete();		
	}
	
	protected void fetchTweetsFromDB() {
		TweetFetchAsyncTask task = new TweetFetchAsyncTask();
		task.execute();
	}
	
	protected class TweetFetchAsyncTask extends AsyncTask<Void, Void, List<Tweet>> {

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
				addAll(result);
				stopProgress();
			}
		}
		
		@Override
		protected void onPreExecute() {
			startProgress();
		}
		
	}	
	protected class TweetAddAsyncTask extends AsyncTask<ArrayList<Tweet>, Void, Void> {
		@Override
		protected Void doInBackground(ArrayList<Tweet>... params) {
			try {
				ArrayList<Tweet> list = params != null ? params[0] : null;
				for(Tweet tweet : list) {
					if(Tweet.get(tweet.uid) == null) {
						long userId = tweet.user.uid;
						User user = User.get(userId);
						if(user == null) {
							user = tweet.getUser();
							user.save();
						}
						tweet.user = user;
						tweet.save();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}
	
	protected class TweetDeleteAsyncTask extends AsyncTask<Void, Void, Void> {
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
}
