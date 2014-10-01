package com.codepath.apps.basictwitter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {
	
	private TextView tvCount;
	private EditText etText;
	private Button tweetButton;
	Tweet toBeRepliedTweet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
		int backgroundColor = getResources().getColor(android.R.color.background_light);
		actionBar.setBackgroundDrawable(new ColorDrawable(backgroundColor));
		actionBar.setCustomView(R.layout.actionbar_title);
		View custView = actionBar.getCustomView();
		
		if(getIntent().getExtras() != null) {
			User user = (User)getIntent().getExtras().getParcelable("user");
			if(user != null) {
				((TextView)custView.findViewById(R.id.tvUserNameD)).setText(user.getName());
				((TextView)custView.findViewById(R.id.tvUserScreenNameD)).setText("@" + user.getScreenName());
				ImageView iv = (ImageView)custView.findViewById(R.id.ivProfileImageD);
				iv.setImageResource(android.R.color.transparent);
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(user.getProfileImageUrl(), iv);
			}
		}
		
		etText = (EditText)findViewById(R.id.etTweetText);
		toBeRepliedTweet = (Tweet)getIntent().getExtras().getParcelable("toBeRepliedTweet");
		if(toBeRepliedTweet != null) {
			String screenName = "@" + toBeRepliedTweet.getUser().screenName;
			etText.setText(screenName);
			etText.setSelection(etText.getText().length());
		}
		etText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {	
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String text = (s != null) ? s.toString() : null;
				if(text != null && text.length() >0) {
					tvCount.setText(Integer.toString(140-text.length()));
					if(text.length() <= 140) {					  
					  enableButton(tweetButton);
					  tvCount.setTextColor(getResources().getColor(android.R.color.primary_text_light));
					}else{
						disableButton(tweetButton);
					  tvCount.setTextColor(getResources().getColor(android.R.color.holo_red_light));
					}
				}else{
					tvCount.setText(Integer.toString(140));
					tvCount.setTextColor(getResources().getColor(android.R.color.primary_text_light));
					disableButton(tweetButton);
				}				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem tweetItem = menu.findItem(R.id.action_tweet);
		View v = tweetItem.getActionView();
		tweetButton = (Button)v.findViewById(R.id.btnTweet);
		tweetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onTweetButtonClicked();				
			}
		});
		disableButton(tweetButton);
		
		MenuItem countItem = menu.findItem(R.id.action_textcount);
		View v2 = countItem.getActionView();
		tvCount = (TextView)v2.findViewById(R.id.tvTextCount);

		return super.onPrepareOptionsMenu(menu);
	}
	
	private void disableButton(Button b) {
		b.setEnabled(false);
	}
	
	private void enableButton(Button b) {
		b.setEnabled(true);
		b.getBackground().setColorFilter(null);
	}
	
	private void onTweetButtonClicked() {
		String text = etText.getText().toString();
		if(validate(text)) {
			Intent data = new Intent();
			data.putExtra("text", text);
			setResult(RESULT_OK, data);
			finish();
		}else{
			etText.setError(getResources().getString(R.string.error_notext));
		}		
	}

	private boolean validate(String text) {
		if(text == null || text.length() == 0) {
			return false;
		}
		if(text.length() > 140) {
			return false;
		}
		return true;
	}
}
