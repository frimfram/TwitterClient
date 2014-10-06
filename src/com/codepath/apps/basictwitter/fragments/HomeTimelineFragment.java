package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;
import com.codepath.apps.basictwitter.HomeTimelineArrayAdapter;

public class HomeTimelineFragment extends TweetsPagingListFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initArrayAdapter() {
		aTweets = new HomeTimelineArrayAdapter(getActivity(), tweets);
	}
	
	@Override
	protected void populateTimeline(long max) {
		if(max > 0) {
			max = max -1;
		}
		String maxStr = (max > 0) ? Long.toString(max) : null;
		startProgress();
		
		client.getHomeTimeline(populateTimelineHandler, maxStr, null);
	}
	
	@Override
	protected void fetchNewerTweets(String sinceId, String maxId) {
		fetchNewerHandler.setSinceId(sinceId);		
		client.getHomeTimeline(fetchNewerHandler, maxId, sinceId);		
	}	
}
