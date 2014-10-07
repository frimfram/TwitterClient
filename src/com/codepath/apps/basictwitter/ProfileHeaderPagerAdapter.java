package com.codepath.apps.basictwitter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.basictwitter.fragments.ProfileHeaderFragmentOne;
import com.codepath.apps.basictwitter.fragments.ProfileHeaderFragmentTwo;
import com.codepath.apps.basictwitter.models.User;

public class ProfileHeaderPagerAdapter extends FragmentPagerAdapter {
	int pageCount;
	User user;

	public ProfileHeaderPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	public ProfileHeaderPagerAdapter(FragmentManager fm, int pagesCount, User userObj) {
		super(fm);
		pageCount = pagesCount;
		user = userObj;
	}
	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0: // Fragment # 0 - This will show FirstFragment
			return ProfileHeaderFragmentOne.newInstance(0, user);
		case 1:
			return ProfileHeaderFragmentTwo.newInstance(1, user);
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return pageCount;
	}

}
