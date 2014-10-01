package com.codepath.apps.basictwitter.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name="Tweet")
public class Tweet extends Model implements Parcelable {
	
	@Column(name="uid")
	public long uid;
	@Column(name="body")
	public String body;
	@Column(name="created_at")
	public String createdAt;	
	@Column(name="User")
	public User user;
	@Column(name="favorite_count")
	public int favoriteCount;
	@Column(name="retweet_count")
	public int retweetCount;
	@Column(name="media_url")
	public String mediaUrl;
	@Column(name="media_width")
	public int mediaWidth;
	@Column(name="media_height")
	public int mediaHeight;
	
	public long userId;
	
	public Tweet() {
		super();
	}
	
	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}

	public static Tweet fromJson(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		//extract values from the json to populate the member variables
		try {
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
			tweet.userId = tweet.user.uid;
			tweet.retweetCount = jsonObject.getInt("retweet_count");
			tweet.favoriteCount = jsonObject.getInt("favorite_count");
			JSONObject entity = jsonObject.getJSONObject("entities");
			if(entity != null && entity.has("media")) {
				JSONArray media = entity.getJSONArray("media");
				if(media != null && media.length() > 0) {
					JSONObject mediaObj = media.getJSONObject(0);
					tweet.mediaUrl = mediaObj.getString("media_url");
					JSONObject size = mediaObj.getJSONObject("sizes");
					if(size != null) {
						JSONObject medInfo = size.getJSONObject("medium");
						if(medInfo != null) {
							tweet.mediaWidth = medInfo.getInt("w");
							tweet.mediaHeight = medInfo.getInt("h");
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return tweet;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
		for(int i=0; i< jsonArray.length();i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			Tweet tweet = Tweet.fromJson(tweetJson);
			if(tweet != null) {
				tweets.add(tweet);				
			}
		}
		return tweets;

	}
	
	public static List<Tweet> getAll() {
		return new Select().from(Tweet.class).orderBy("uid DESC").execute();
	}
	
	@Override
	public String toString() {
		return getBody() + " - " + getUser().getScreenName();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(uid);
		out.writeString(body);
		out.writeString(createdAt);
		out.writeParcelable(user, PARCELABLE_WRITE_RETURN_VALUE);
		out.writeInt(favoriteCount);
		out.writeInt(retweetCount);
		out.writeString(mediaUrl);
		out.writeInt(mediaWidth);
		out.writeInt(mediaHeight);
		out.writeLong(userId);
	}
	
    public static final Parcelable.Creator<Tweet> CREATOR
    	= new Parcelable.Creator<Tweet>() {
		@Override
		public Tweet createFromParcel(Parcel in) {
		    return new Tweet(in);
		}

		@Override
		public Tweet[] newArray(int size) {
		    return new Tweet[size];
		}
    };

	private Tweet(Parcel in) {
		uid = in.readLong();
		body = in.readString();
		createdAt = in.readString();
		user = in.readParcelable(User.class.getClassLoader());
		favoriteCount = in.readInt();
		retweetCount = in.readInt();
		mediaUrl = in.readString();
		mediaWidth = in.readInt();
		mediaHeight = in.readInt();
		userId = in.readLong();
	}
}
