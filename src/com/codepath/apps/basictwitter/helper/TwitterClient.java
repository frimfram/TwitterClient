package com.codepath.apps.basictwitter.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "H5oZvfJGuMsV4PHWUC2t9G3xI";       // Change this
	public static final String REST_CONSUMER_SECRET = "QnftaJlPxQmJ3VxDntmwJEH9vkaUzlInisOsy94L5NcJ3TScYG"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)
	
	public static final String PAGE_SIZE = "20";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}
	
	public void getHomeTimeline(AsyncHttpResponseHandler handler, String maxId, String sinceId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		invokeTimelineCall(handler, maxId, sinceId, apiUrl, null);
	}
	
	public void getMentionsTimeline(AsyncHttpResponseHandler handler, String maxId, String sinceId) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		invokeTimelineCall(handler, maxId, sinceId, apiUrl, null);
	}

	public void getUserTimeline(AsyncHttpResponseHandler handler, String maxId, String sinceId, String userId) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", userId);
		invokeTimelineCall(handler, maxId, sinceId, apiUrl, params);
	}
	
	public void getTweetSearchResults(AsyncHttpResponseHandler handler, String maxId, String sinceId, String query) {
		String apiUrl = getApiUrl("search/tweets.json");
		Map<String, String> params = new HashMap<String, String>();
		try {
			query = URLEncoder.encode(query, "UTF-8");
			params.put("q", query);
			params.put("result_type", "popular");
			invokeTimelineCall(handler, maxId, sinceId, apiUrl, params);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void invokeTimelineCall(AsyncHttpResponseHandler handler, String maxId, String sinceId, String apiUrl, Map<String, String>additionParams) {
		RequestParams params = new RequestParams();
		params.put("count", PAGE_SIZE);
		if(maxId != null) {
			params.put("max_id", maxId);
		}
		if(sinceId != null) {
			params.put("since_id", sinceId);
		}
		if(additionParams != null) {
			for(String key: additionParams.keySet()) {
				params.put(key, additionParams.get(key));
			}
		}
		client.get(apiUrl,  params, handler);  //pass null instead of params if nothing
		Log.d("TwitterClient", "API Request: maxId: " + maxId);		
	}
	
	public void postStatusUpdate(AsyncHttpResponseHandler handler, String text) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = null;
		
		if(text != null) {
			params = new RequestParams();
			params.put("status", text);
		}
		client.post(apiUrl,  params, handler);
	}
	
	public void getVerifiedCredentials(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl,  null, handler); 		
	}

	public void postReplyStatusUpdate(
			JsonHttpResponseHandler jsonHttpResponseHandler, String text,
			User loggedInUser, Tweet tweet) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = null;
		
		if(text != null && tweet != null) {
			params = new RequestParams();
			params.put("status", text);
			params.put("in_reply_to_status_id", Long.toString(tweet.uid));
		}
		client.post(apiUrl,  params, jsonHttpResponseHandler);		
	}
	
	public void retrieveFriendsList(JsonHttpResponseHandler handler,
			User user, long cursor) {
		String apiUrl = getApiUrl("friends/list.json");
		RequestParams params = null;
		if(user != null) {
			params = new RequestParams();
			params.put("user_id", Long.toString(user.uid));
			if(cursor > -1) {
				params.put("cursor", Long.toString(cursor));
			}
		}
		client.get(apiUrl,  params, handler);
	}
	
	public void retrieveFollowersList(JsonHttpResponseHandler handler,
			User user, long cursor) {
		String apiUrl = getApiUrl("followers/list.json");
		RequestParams params = null;
		if(user != null) {
			params = new RequestParams();
			params.put("user_id", Long.toString(user.uid));
			if(cursor > -1) {
				params.put("cursor", Long.toString(cursor));
			}
		}
		client.get(apiUrl,  params, handler);
	}

	public void switchFavorited(
			JsonHttpResponseHandler jsonHttpResponseHandler, User loggedInUser, Tweet tweet) {
		String apiUrl = getApiUrl("favorites/create.json");
		if(tweet.favorited) {
			apiUrl = getApiUrl("favorites/destroy.json");
		}
		RequestParams params = null;
		
		if(tweet != null) {
			params = new RequestParams();
			params.put("id", Long.toString(tweet.uid));
		}
		client.post(apiUrl,  params, jsonHttpResponseHandler);		
	}
}