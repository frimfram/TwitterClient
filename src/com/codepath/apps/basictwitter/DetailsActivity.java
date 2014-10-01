package com.codepath.apps.basictwitter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailsActivity extends Activity {
	
	private Tweet tweet;
	private User loggedInUser;
	
	public static int COMPOSE_ACTIVITY_REQUEST_CODE = 39;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.tweet_title));

		if(getIntent().getExtras() != null) {
			tweet = (Tweet)getIntent().getExtras().getParcelable("tweet");
			if(tweet != null) {
				((TextView)findViewById(R.id.tvUserNameD)).setText(tweet.user.getName());
				((TextView)findViewById(R.id.tvUserScreenNameD)).setText("@" + tweet.user.getScreenName());
				ImageView iv = (ImageView)findViewById(R.id.ivProfileImageD);
				iv.setImageResource(android.R.color.transparent);
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(tweet.user.getProfileImageUrl(), iv);
				((TextView)findViewById(R.id.tvTweetBodyD)).setText(tweet.body);
				((TextView)findViewById(R.id.tvCreatedAtD)).setText(getDateString(tweet.createdAt));
				((TextView)findViewById(R.id.tvRetweetCount)).setText(Integer.toString(tweet.retweetCount));
				((TextView)findViewById(R.id.tvFavoriteCount)).setText(Integer.toString(tweet.favoriteCount));
				ImageView ivMedia = (ImageView)findViewById(R.id.ivMedia);
				ivMedia.setImageResource(android.R.color.transparent);
				if(tweet.mediaUrl != null) {
					DisplayMetrics metrics = this.getResources().getDisplayMetrics();
					int viewWidth = metrics.widthPixels - 20;
					int picWidth = tweet.mediaWidth;
					int picHeight = tweet.mediaHeight;
					int viewHeight = (picHeight*viewWidth)/picWidth;
					ivMedia.getLayoutParams().width = viewWidth;
					ivMedia.getLayoutParams().height = viewHeight;
					imageLoader.displayImage(tweet.mediaUrl, ivMedia);
				}
			}
		}
		loggedInUser = (User)getIntent().getExtras().getParcelable("user");
		
	}
	
	public void onReplyClicked(View v) {
    	Intent composeIntent = new Intent(this, ComposeActivity.class);
    	if(loggedInUser != null) {
    		composeIntent.putExtra("user", loggedInUser);
    	}
    	composeIntent.putExtra("toBeRepliedTweet", tweet);
    	startActivityForResult(composeIntent, COMPOSE_ACTIVITY_REQUEST_CODE);		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == COMPOSE_ACTIVITY_REQUEST_CODE) {
			String tweetText = data.getExtras().getString("text");
			postStatusUpdate(tweetText);
		}
	}
	
	public void postStatusUpdate(String tweetText) {
		TwitterApplication.getRestClient().postReplyStatusUpdate(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.d("debug", arg1.toString());
				super.onFailure(arg0, arg1);
				Toast.makeText(DetailsActivity.this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
			}			
		}, tweetText, loggedInUser, tweet);
	}
	
	private String getDateString(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		String result = "";
		try {
			Date date = sf.parse(rawJsonDate);
			result = new SimpleDateFormat("h:m a   dd MMM yy").format(date);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
