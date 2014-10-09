package com.codepath.apps.basictwitter.adapters;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	
	protected static class ViewHolder {
		ImageView ivProfileImage;
		TextView tvUserName;
		TextView tvScreenName;
		TextView tvBody;
		TextView tvCreatedAt;
		TextView tvRetweetCount;
		TextView tvFavoriteCount;
		
		ImageView ivPreviewImage;
		ImageView retweetedIcon;
		TextView retweetedUser;
		
		ImageView ivReply;
		ImageView ivFavorite;
		ImageView ivRetweet;
	}

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);

		ViewHolder vh;
		if(convertView == null) {
			vh = new ViewHolder();
			LayoutInflater inflator = LayoutInflater.from(getContext());
			convertView = inflator.inflate(R.layout.tweet_item, parent, false);
			vh.ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfileImageD);
			vh.tvUserName = (TextView)convertView.findViewById(R.id.tvUserNameD);
			vh.tvScreenName = (TextView)convertView.findViewById(R.id.tvUserScreenNameD);
			vh.tvBody = (TextView)convertView.findViewById(R.id.tvBody);
			vh.tvCreatedAt = (TextView)convertView.findViewById(R.id.tvCreatedAt);
			vh.tvRetweetCount = (TextView)convertView.findViewById(R.id.tvRetweetOnList);
			vh.tvFavoriteCount = (TextView)convertView.findViewById(R.id.tvFavoriteOnList);
			vh.ivFavorite = (ImageView)convertView.findViewById(R.id.ivFavorite);
			vh.ivReply = (ImageView)convertView.findViewById(R.id.ivReply);
			vh.ivRetweet = (ImageView)convertView.findViewById(R.id.ivRetweet);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}		
		
		vh.ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), vh.ivProfileImage);
		vh.ivProfileImage.setTag(tweet.getUser());
		vh.ivReply.setTag(tweet);
		vh.tvUserName.setText(tweet.getUser().getName());
		vh.tvScreenName.setText("@" + tweet.getUser().getScreenName());
		vh.tvBody.setText(tweet.getBody());
		vh.tvCreatedAt.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
		vh.tvRetweetCount.setText(Integer.toString(tweet.retweetCount));
		vh.tvFavoriteCount.setText(Integer.toString(tweet.favoriteCount));
		if(tweet.favorited) {
			vh.ivFavorite.setImageResource(R.drawable.staron);
		}else{
			vh.ivFavorite.setImageResource(R.drawable.star);
		}
		return convertView;
	}
	
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();

			if(relativeDate != null) {
				String delims = "[ ]+";
				String[] tokens = relativeDate.split(delims);
				if(tokens != null && tokens.length > 0) {
					String secondText = "";
					if(tokens.length>1) {
						if(tokens[0].startsWith("in")) {
							tokens[0] = "0m";
						}else{
							secondText =  Character.valueOf(tokens[1].charAt(0)).toString();
							if((!isNumeric(tokens[0])) && tokens[1].length() > 1) {
								secondText = secondText + Character.valueOf(tokens[1].charAt(1)).toString();
							}
						}
					}
					relativeDate =  tokens[0] + secondText;
				}
			}
		} catch (Exception e) {
			relativeDate = "";
			e.printStackTrace();
		}
	 
		return relativeDate;
	}
	
	private boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
	
	

}
