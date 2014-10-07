package com.codepath.apps.basictwitter;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.codepath.apps.basictwitter.fragments.UsersListFragment;
import com.codepath.apps.basictwitter.fragments.UsersListFragment.OnListFragmentInteractionListener;
import com.codepath.apps.basictwitter.models.User;

public class UsersActivity extends FragmentActivity implements OnListFragmentInteractionListener {
	private String type;
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		if(getIntent().getExtras() != null) {
			user = getIntent().getExtras().getParcelable("user");
			type = getIntent().getExtras().getString("type");
			ActionBar actionBar = getActionBar();
			actionBar.setTitle(type);
		}
		loadFragment();
	}
	
	private void loadFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment listFragment = UsersListFragment.newInstance(type, user);
		ft.replace(R.id.flContainer, listFragment);
		ft.commit();
	}

	@Override
	public void onListItemClicked(User user) {
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("profile_user", user);
		startActivity(i);	
	}
}
