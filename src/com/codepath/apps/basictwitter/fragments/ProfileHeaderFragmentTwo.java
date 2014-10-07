package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.User;

public class ProfileHeaderFragmentTwo extends Fragment {
	private User user;
	private int page;
	
	public static ProfileHeaderFragmentTwo newInstance(int page, User user) {
		ProfileHeaderFragmentTwo fragment = new ProfileHeaderFragmentTwo();
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
		View view = inflater.inflate(R.layout.fragment_profile_header_two, container, false);
		TextView tvTagline = (TextView) view.findViewById(R.id.tvTagline);
		tvTagline.setText(user.tagline);
		TextView tvWebUrl = (TextView) view.findViewById(R.id.tvWebUrl);
		tvWebUrl.setText(user.profileDisplayUrl);		
		return view;
	}
}
