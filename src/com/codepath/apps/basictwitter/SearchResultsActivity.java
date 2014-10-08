package com.codepath.apps.basictwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.codepath.apps.basictwitter.fragments.SearchResultTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.OnListFragmentInteractionListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;

public class SearchResultsActivity extends FragmentActivity implements OnListFragmentInteractionListener {
	private SearchResultTimelineFragment listFragment;
	private SearchView searchView;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		query = getIntent().getStringExtra("query");
		getActionBar().setTitle("");
		loadFragment();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void loadFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		listFragment = SearchResultTimelineFragment.newInstance(query);
		ft.replace(R.id.flContainer, listFragment);
		ft.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView)searchItem.getActionView();
		if(query != null) {
			searchView.setQuery(query, false);
			searchView.clearFocus();
			searchView.setIconified(false);
		}
		searchView.setOnQueryTextListener(new OnQueryTextListener() {			
			@Override
			public boolean onQueryTextSubmit(String query) {
				SearchResultsActivity.this.query = query;
				listFragment.performNewSearch(query);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onListItemClicked(Tweet tweet) {
		showDetailsActivity(tweet);		
	}
	
	private void showDetailsActivity(Tweet tweet) {
	    Intent detailsIntent = new Intent(this, DetailsActivity.class);
	    SharedPreferences pref = getSharedPreferences(LoginActivity.SAVED_USER_PROFILE, MODE_PRIVATE);
		User loggedInUser = User.fromSharedPreferences(pref);
	    if(loggedInUser != null) {
	    	detailsIntent.putExtra("user", loggedInUser);
	    }
	    detailsIntent.putExtra("tweet", tweet);
	    startActivityForResult(detailsIntent, TimelineActivity.DETAILS_ACTIVITY_REQUEST_CODE);	
	}
}
