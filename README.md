# Twitter Client

## Overview
A simple Twitter client that supports viewing a Twitter timeline and composing a new tweet

animated gif:

![Alt text](https://github.com/frimfram/TwitterClient/blob/master/enhancedtwitter_project2.gif "Twitter Client")

## Features completed
- User can sign in to Twitter using OAuth login
- User can view the tweets from their home timeline
- User should be displayed the username, name, and body for each tweet
- User should be displayed the relative timestamp for each tweet "8m", "7h"
- User can view more tweets as they scroll with infinite pagination
- Links in tweets are clickable and will launch the web browser
- User can compose a new tweet
- User can click a “Compose” icon in the Action Bar on the top right
- User can then enter a new tweet and post this to twitter
- User is taken back to home timeline with new tweet visible in timeline
- User can see a counter with total number of characters left for tweet
- User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
- User can open the twitter app offline and see last loaded tweets
- Tweets are persisted into sqlite and can be displayed from the local DB
- User can tap a tweet to display a "detailed" view of that tweet
- User can select "reply" from detail view to respond to a tweet
- Improve the user interface and theme the app to feel "twitter branded"
- User can see embedded image media within the tweet detail view
- Compose activity is replaced with a modal overlay - not replaced but have the compose fragment come up when the user click on "What's happening" at the bottom of timeline.

Enhanced version features:
- User can switch between Timeline and Mention views using tabs.
- User can view their home timeline tweets.
- User can view the recent mentions of their username.
- User can scroll to bottom of either of these lists and new tweets will load ("infinite scroll")
- Implement tabs in a gingerbread-compatible approach
- User can navigate to view their own profile
- User can see picture, tagline, # of followers, # of following, and tweets on their profile.
- User can click on the profile image in any tweet to see another user's profile.
- User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
- Profile view should include that user's timeline
- User can view following / followers list through the profile
- Robust error handling, check if internet is available, handle error cases, network failures
- When a network request is sent, user sees an indeterminate progress indicator
- Advanced: User can "reply" to any tweet on their home timeline
The user that wrote the original tweet is automatically "@" replied in compose
- User can click on a tweet to be taken to a "detail view" of that tweet
- User can take favorite (and unfavorite) or reweet actions on a tweet
- Improve the user interface and theme the app to feel twitter branded
- User can search for tweets matching a particular query and see results

Hours spent:  30

![Alt text](https://github.com/frimfram/TwitterClient/blob/master/enhancedtwitter_project.gif "Twitter Client")


