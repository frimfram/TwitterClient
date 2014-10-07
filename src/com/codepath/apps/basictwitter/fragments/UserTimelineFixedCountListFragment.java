package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;
import org.json.JSONArray;
import android.os.Bundle;
import android.widget.Toast;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TweetArrayAdapter;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFixedCountListFragment extends TweetsListFragment {
	TwitterClient client;
	private long userId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		userId = getArguments().getLong("user_id", 0);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		startProgress();
		populateTimeline();
	}

	private void populateTimeline() {
		startProgress();
		
		client.getUserTimeline(new JsonHttpResponseHandler() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JSONArray json) {
				ArrayList<Tweet> list = Tweet.fromJSONArray(json);
				if(list != null && list.size() > 0) {				
					for(int i=0;i<3; i++) {
						Tweet t = list.get(i);
						aTweets.add(t);
					}
				}
				stopProgress();
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				stopProgress();
				arg0.printStackTrace();
				Toast.makeText(getActivity(), getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
			}
		}, null, null, Long.toString(userId));
	}
	//static creator
	public static UserTimelineFixedCountListFragment newInstance(long userId) {
		UserTimelineFixedCountListFragment fragment = new UserTimelineFixedCountListFragment();
		Bundle args = new Bundle();
		args.putLong("user_id", userId);
		fragment.setArguments(args);
		return fragment;
	}
}
