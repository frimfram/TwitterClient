package com.codepath.apps.basictwitter.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.codepath.apps.basictwitter.ProfileActivity;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeTimelineArrayAdapter extends TweetArrayAdapter {

	public HomeTimelineArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = super.getView(position, convertView, parent);
		Tweet tweet = getItem(position);
		ViewHolder holder = (ViewHolder)itemView.getTag();

		holder.ivProfileImage.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				if(v.getTag() != null) {
					User user = (User)v.getTag();
					Intent i = new Intent(getContext(), ProfileActivity.class);
					i.putExtra("profile_user", user);
					getContext().startActivity(i);
				}
			}
		});
		
		holder.ivPreviewImage = (ImageView)itemView.findViewById(R.id.ivPreviewImage);
		if(tweet.mediaUrl != null) {			
			holder.ivPreviewImage.setVisibility(View.VISIBLE);
			holder.ivPreviewImage.setBackgroundResource(android.R.color.transparent);
			DisplayMetrics metrics = itemView.getResources().getDisplayMetrics();
			int width = metrics.widthPixels - 70;
			holder.ivPreviewImage.getLayoutParams().width = width;			
			ImageLoader loader = ImageLoader.getInstance();
			loader.displayImage(tweet.mediaUrl, holder.ivPreviewImage);
		}else{
			holder.ivPreviewImage.setVisibility(View.GONE);
		}
		
		holder.retweetedIcon = (ImageView)itemView.findViewById(R.id.ivRetweetTop);
		holder.retweetedUser = (TextView)itemView.findViewById(R.id.tvRetweetUser);
		if(tweet.retweetedUser != null) {			
			holder.retweetedIcon.setVisibility(View.VISIBLE);			
			holder.retweetedUser.setVisibility(View.VISIBLE);
			holder.retweetedUser.setText(tweet.retweetedUser.getName() + " " + getContext().getResources().getString(R.string.retweeted));
		}else{
			holder.retweetedIcon.setVisibility(View.GONE);
			holder.retweetedUser.setVisibility(View.GONE);
		}
		
		return itemView;
	}

}
