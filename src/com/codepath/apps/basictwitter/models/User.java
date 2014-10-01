package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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
	}

}
