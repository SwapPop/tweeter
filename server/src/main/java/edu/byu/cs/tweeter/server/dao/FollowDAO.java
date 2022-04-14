package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.BatchFeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

public interface FollowDAO {
    FollowingResponse getFollowees(FollowingRequest request);

    FollowersResponse getFollowers(FollowersRequest request);

    IsFollowerResponse isFollower(IsFollowerRequest request);

    FollowResponse follow(FollowRequest request, User userAlias);

    UnfollowResponse unfollow(UnfollowRequest request, String userAlias);

    BatchFeedResponse getAllFollowersAliases(String alias, int limit, String lastFollowerAlias);
}
