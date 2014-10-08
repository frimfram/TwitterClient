package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.adapters.HomeTimelineArrayAdapter;
import com.codepath.apps.basictwitter.fragments.TweetsPagingListFragment.TweetAddAsyncTask;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchResultTimelineFragment extends TweetsPagingListFragment {

	String query;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		query = getArguments().getString("query");
	}
	
	@Override
	protected void initArrayAdapter() {
		aTweets = new HomeTimelineArrayAdapter(getActivity(), tweets);
	}

	protected JsonHttpResponseHandler populateSearchTimelineHandler = new JsonHttpResponseHandler() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(JSONObject jsonObject) {
			JSONArray json;
			try {
				json = jsonObject.getJSONArray("statuses");
				ArrayList<Tweet> list = Tweet.fromJSONArray(json);
				if(list != null && list.size() > 0) {				
					Tweet last = list.get(list.size()-1);
					maxId = last.getUid();
					addAll(list);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			stopProgress();
		}
		
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			stopProgress();
			arg0.printStackTrace();
			Toast.makeText(getActivity(), getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
		}
	};	
	
	@Override
	protected void populateTimeline(long max) {
		if(max > 0) {
			max = max -1;
		}
		String maxStr = (max > 0) ? Long.toString(max) : null;
		startProgress();
		
		client.getTweetSearchResults(populateSearchTimelineHandler, maxStr, null, query);
	}
	
	@Override
	protected void fetchNewerTweets(String sinceId, String maxId) {
		fetchNewerHandler.setSinceId(sinceId);		
		client.getTweetSearchResults(fetchNewerHandler, maxId, sinceId, query);		
	}
	
	//static creator
	public static SearchResultTimelineFragment newInstance(String queryString) {
		SearchResultTimelineFragment fragment = new SearchResultTimelineFragment();
		Bundle bundle = new Bundle();
		bundle.putString("query", queryString);
		fragment.setArguments(bundle);
		return fragment;
	}

	public void performNewSearch(String queryStr) {
		aTweets.clear();
		aTweets.notifyDataSetChanged();
		maxId = 0;
		query = queryStr;
		onLoadMoreToList(false);		
	}

}
