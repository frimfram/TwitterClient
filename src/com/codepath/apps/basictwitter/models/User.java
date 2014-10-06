package com.codepath.apps.basictwitter.models;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.basictwitter.LoginActivity;

@Table(name="User")
public class User extends Model implements Parcelable {
	
	@Column(name="uid")
	public long uid;
	@Column(name="name")
	public String name;
	@Column(name="screen_name")
	public String screenName;
	@Column(name="image_url")
	public String profileImageUrl;
	@Column(name="background_image_url")
	public String backgroundImageUrl;
	@Column(name="tagline")
	public String tagline;
	@Column(name="follower_count")
	public int followerCount;
	@Column(name="friends_count")
	public int friendsCount;
	
	@Column(name="status_count")
	public int statusCount;
	@Column(name="profile_banner_url")
	public String profileBannerUrl;
	@Column(name="profile_display_url")
	public String profileDisplayUrl;
	@Column(name="profile_url")
	public String profileUrl;
	@Column(name="location")
	public String location;
	
	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int followingCount) {
		this.friendsCount = followingCount;
	}

	public User() {
		super();
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getTagline() {
		return tagline;
	}
	
	public List<Tweet> tweets() {
		return getMany(Tweet.class, "User");
	}
	
	public static List<User> getAll() {
		return new Select().from(User.class).execute();
	}

	public static User fromJson(JSONObject json) {
		User u = new User();
		try {
			u.name = json.getString("name");
			u.uid = json.getLong("id");
			u.screenName = json.getString("screen_name");
			u.profileImageUrl = json.getString("profile_image_url");
			u.followerCount = json.getInt("followers_count");
			u.friendsCount = json.getInt("friends_count");
			u.tagline = json.getString("description");
			u.backgroundImageUrl = json.getString("profile_background_image_url");
			u.statusCount = json.getInt("statuses_count");
			u.profileBannerUrl = json.getString("profile_banner_url");
			if(json.has("entities")) {
				JSONObject entity = json.getJSONObject("entities");
				if(entity.has("url")) {
					JSONObject url = entity.getJSONObject("url");
					if(url.has("urls")) {
						JSONArray urls = url.getJSONArray("urls");
						u.profileDisplayUrl = ((JSONObject)urls.get(0)).getString("display_url");
						u.profileUrl = ((JSONObject)urls.get(0)).getString("url");						
					}
				}
			}
			u.location = json.getString("location");
		}catch(JSONException e){
			e.printStackTrace();
		}
		return u;
	}
	
	public static User get(long uid) {
		List<User> user = new Select().from(User.class).where("uid=?", uid).execute();
		if(user != null && user.size() > 0) {
			return user.get(0);
		}else{
			return null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(uid);
		out.writeString(name);
		out.writeString(screenName);
		out.writeString(profileImageUrl);
		out.writeString(backgroundImageUrl);
		out.writeString(tagline);
		out.writeInt(followerCount);
		out.writeInt(friendsCount);
		out.writeInt(statusCount);
		out.writeString(profileBannerUrl);
		out.writeString(profileDisplayUrl);
		out.writeString(profileUrl);
		out.writeString(location);
	}
	
    public static final Parcelable.Creator<User> CREATOR
    	= new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel in) {
		    return new User(in);
		}

		@Override
		public User[] newArray(int size) {
		    return new User[size];
		}
    };

	private User(Parcel in) {
		uid = in.readLong();
		name = in.readString();
		screenName = in.readString();
		profileImageUrl = in.readString();
		backgroundImageUrl = in.readString();
		tagline = in.readString();
		followerCount = in.readInt();
		friendsCount = in.readInt();
		statusCount = in.readInt();
		profileBannerUrl = in.readString();
		profileDisplayUrl = in.readString();
		profileUrl = in.readString(); 
		location = in.readString();
	}
	
	public static User fromSharedPreferences(SharedPreferences pref) {
		User user = new User();
		user.uid = pref.getLong(LoginActivity.SAVED_USER_ID, -1);
		user.name = pref.getString(LoginActivity.SAVED_USER_NAME, null);
		user.screenName = pref.getString(LoginActivity.SAVED_USER_SCREENNAME, null);
		user.profileImageUrl = pref.getString(LoginActivity.SAVED_USER_IMAGEURL, null);
		return user;		
	}


}
