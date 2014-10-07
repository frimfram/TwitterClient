package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.adapters.UserArrayAdapter;
import com.codepath.apps.basictwitter.helper.EndlessScrollListener;
import com.codepath.apps.basictwitter.helper.TwitterClient;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UsersListFragment extends Fragment {
	protected ArrayList<User> users;
	protected ArrayAdapter<User> aUsers;
	protected ListView lvUsers;
	protected ProgressBar progressBar;
	protected long previousCursor;
	protected long nextCursor = -1;
	
	private OnListFragmentInteractionListener listener;
	private String type;
	private User user;
	private TwitterClient client;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (OnListFragmentInteractionListener)activity;
		}catch(ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnListFragmentInteractionListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//non-view initialization		
		super.onCreate(savedInstanceState);
		users = new ArrayList<User>();
		client = TwitterApplication.getRestClient();
		initArrayAdapter();
		type = getArguments().getString("type");
		user = getArguments().getParcelable("user");
		
	}
	
	protected void initArrayAdapter() {
		aUsers = new UserArrayAdapter(getActivity(), users);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate the layout
		View v = inflater.inflate(R.layout.fragment_users_list, container, false);
		//assign view references
		initView(v);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initListViewEventListener();
	}
	
	protected void initView(View v) {
		progressBar = (ProgressBar)v.findViewById(R.id.pbLoading);
		lvUsers = (ListView) v.findViewById(R.id.lvUsers);
		lvUsers.setAdapter(aUsers);
		onLoadMoreToList(false);
	}
	
	protected void initListViewEventListener() {
		lvUsers.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				onLoadMoreToList(false);
			}
		});
		lvUsers.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User user = getUserAtPosition(position);
				listener.onListItemClicked(user);
			}		
		});
	}

	protected void onLoadMoreToList(boolean b) {
		if("Following".equalsIgnoreCase(type)) {
			client.retrieveFriendsList(populateUserHandler, user, nextCursor);
		}else{
			client.retrieveFollowersList(populateUserHandler, user, nextCursor);
		}
	}
	protected JsonHttpResponseHandler populateUserHandler = new JsonHttpResponseHandler() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(JSONObject json) {
			if(json.has("next_cursor")) {
				try {
					nextCursor = json.getLong("next_cursor");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if(json.has("users")) {
				JSONArray jsonArray;
				try {
					jsonArray = json.getJSONArray("users");
					ArrayList<User> list = User.fromJSONArray(jsonArray);
					if(list != null && list.size() > 0) {
						addAll(list);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			stopProgress();
		}
		
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			stopProgress();
			arg0.printStackTrace();
			Toast.makeText(getActivity(), getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
		}
	};
	
	//encapsulate the internal data
	public void addAll(List<User> users) {
		aUsers.addAll(users);
	}
	
	public void startProgress() {
		progressBar.setVisibility(ProgressBar.VISIBLE);
	}
	
	public void stopProgress() {
		progressBar.setVisibility(ProgressBar.INVISIBLE);
	}
	
	public int getUserCount() {
		return aUsers != null ? aUsers.getCount() : 0;
	}
	
	public User getUserAtPosition(int position) {
		return aUsers.getItem(position);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}
	
	//listener interface
	public interface OnListFragmentInteractionListener {
		public void onListItemClicked(User user);
	}
	
	//static creator
	public static UsersListFragment newInstance(String type, User user) {
		UsersListFragment fragment = new UsersListFragment();
		Bundle args = new Bundle();
		args.putString("type", type);
		args.putParcelable("user", user);
		fragment.setArguments(args);
		return fragment;
	}
}
