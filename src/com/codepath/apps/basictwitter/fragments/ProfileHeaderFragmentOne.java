package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileHeaderFragmentOne extends Fragment {
	private User user;
	private int page;
	
	public static ProfileHeaderFragmentOne newInstance(int page, User user) {
		ProfileHeaderFragmentOne fragment = new ProfileHeaderFragmentOne();
		Bundle args = new Bundle();
		args.putParcelable("user", user);
		args.putInt("page", page);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		page = getArguments().getInt("page");
		user = getArguments().getParcelable("user");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_header_one, container, false);
		ImageView ivProfile = (ImageView)view.findViewById(R.id.ivProfileImage);
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfile);
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		tvName.setText(user.name);
		TextView tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
		tvScreenName.setText("@" + user.screenName);
		
		return view;
	}
}
