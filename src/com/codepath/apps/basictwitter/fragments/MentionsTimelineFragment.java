package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;

public class MentionsTimelineFragment extends TweetsPagingListFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void populateTimeline(long max) {
		if(max > 0) {
			max = max -1;
		}
		String maxStr = (max > 0) ? Long.toString(max) : null;
		startProgress();
		
		client.getMentionsTimeline(populateTimelineHandler, maxStr, null);
	}
	
	@Override
	protected void fetchNewerTweets(final String sinceId, String maxId) {
		fetchNewerHandler.setSinceId(sinceId);
		client.getMentionsTimeline(fetchNewerHandler, maxId, sinceId);		
	}
}
