package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

public interface FollowDAO {
    FollowingResponse getFollowees(FollowingRequest request);

    FollowersResponse getFollowers(FollowersRequest request);

    GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request);

    GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request);

    IsFollowerResponse isFollower(IsFollowerRequest request);

    FollowResponse follow(FollowRequest request);

    UnfollowResponse unfollow(UnfollowRequest request);
}
