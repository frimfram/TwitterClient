package com.codepath.apps.basictwitter.adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.R.id;
import com.codepath.apps.basictwitter.R.layout;
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserArrayAdapter extends ArrayAdapter<User> {
	
	protected static class ViewHolder {
		ImageView ivProfileImage;
		TextView tvUserName;
		TextView tvScreenName;
		TextView tvTagline;
	}

	public UserArrayAdapter(Context context, List<User> users) {
		super(context, 0, users);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User user = getItem(position);

		ViewHolder vh;
		if(convertView == null) {
			vh = new ViewHolder();
			LayoutInflater inflator = LayoutInflater.from(getContext());
			convertView = inflator.inflate(R.layout.user_item, parent, false);
			vh.ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfileImage);
			vh.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
			vh.tvScreenName = (TextView)convertView.findViewById(R.id.tvUserScreenName);
			vh.tvTagline = (TextView)convertView.findViewById(R.id.tvTagline);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}		
		
		vh.ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(user.getProfileImageUrl(), vh.ivProfileImage);
		vh.ivProfileImage.setTag(user);
		vh.tvUserName.setText(user.getName());
		vh.tvScreenName.setText("@" + user.getScreenName());
		if(user.tagline != null) {
			vh.tvTagline.setText(user.tagline);
		}
		return convertView;
	}
}
