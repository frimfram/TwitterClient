package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;

public class UserTimelineFragment extends TweetsPagingListFragment {
	
	private long userId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userId = getArguments().getLong("user_id", 0);
	}
	
	@Override
	protected void populateTimeline(long max) {
		if(max > 0) {
			max = max -1;
		}
		String maxStr = (max > 0) ? Long.toString(max) : null;
		startProgress();
		
		client.getUserTimeline(populateTimelineHandler, maxStr, null, Long.toString(userId));
	}
	
	@Override
	protected void fetchNewerTweets(final String sinceId, String maxId) {
		fetchNewerHandler.setSinceId(sinceId);
		client.getMentionsTimeline(fetchNewerHandler, maxId, sinceId);
		
	}
	
	//static creator
	public static UserTimelineFragment newInstance(long userId) {
		UserTimelineFragment fragment = new UserTimelineFragment();
		Bundle args = new Bundle();
		args.putLong("user_id", userId);
		fragment.setArguments(args);
		return fragment;
	}

}
