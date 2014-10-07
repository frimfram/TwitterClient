package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.helper.EndlessScrollListener;
import com.codepath.apps.basictwitter.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TweetsListFragment extends Fragment {
	protected ArrayList<Tweet> tweets;
	protected ArrayAdapter<Tweet> aTweets;
	protected PullToRefreshListView lvTweets;
	protected ProgressBar progressBar;
	
	private OnListFragmentInteractionListener listener;
	protected boolean isOffline;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (OnListFragmentInteractionListener)activity;
		}catch(ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnListFragmentInteractionListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//non-view initialization		
		super.onCreate(savedInstanceState);
		tweets = new ArrayList<Tweet>();
		initArrayAdapter();
	}
	
	protected void initArrayAdapter() {
		aTweets = new TweetArrayAdapter(getActivity(), tweets);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate the layout
		View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
		//assign view references
		initView(v);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(!isOffline) {
			initListViewEventListener();
		}
	}
	
	protected void initView(View v) {
		progressBar = (ProgressBar)v.findViewById(R.id.pbLoading);
		lvTweets = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(aTweets);		
	}
	
	protected void initListViewEventListener() {
		lvTweets.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Tweet tweet = getTweetAtPosition(position);
				listener.onListItemClicked(tweet);
			}		
		});
	}

	//encapsulate the internal data
	public void addAll(List<Tweet> tweets) {
		for(Tweet tweet : tweets) {
			aTweets.add(tweet);
		}
		//aTweets.addAll(tweets);
	}
	
	public void addAllToFront(List<Tweet> list) {
		if(tweets != null) {
			tweets.addAll(0, list);
		}
	}
	
	public void addToFront(Tweet tweet) {
		if(tweet != null) {
			aTweets.insert(tweet, 0);
		}
	}
	
	public void startProgress() {
		progressBar.setVisibility(ProgressBar.VISIBLE);
	}
	
	public void stopProgress() {
		progressBar.setVisibility(ProgressBar.INVISIBLE);
	}
	
	public String getMostRecentTweetId() {
		String sinceIdStr = null;
		if(aTweets.getCount() > 0) {
			long sinceId = aTweets.getItem(0).getUid();
			sinceIdStr = Long.toString(sinceId);
		}
		return sinceIdStr;
	}
	
	protected void refreshCompleted(boolean notify) {
		if(notify) {
			aTweets.notifyDataSetChanged();
		}		
	}
	
	public int getTweetCount() {
		return aTweets != null ? aTweets.getCount() : 0;
	}
	
	public Tweet getTweetAtPosition(int position) {
		return aTweets.getItem(position);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}
	
	//listener interface
	public interface OnListFragmentInteractionListener {
		public void onListItemClicked(Tweet tweet);
	}
	
	
	protected void onLoadMoreToList(boolean isRefreshTop) {
		
	}
}
