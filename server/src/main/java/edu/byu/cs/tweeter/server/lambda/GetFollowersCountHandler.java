package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetFollowersCountHandler implements RequestHandler<GetFollowersCountRequest, GetFollowersCountResponse> {

    @Override
    public GetFollowersCountResponse handleRequest(GetFollowersCountRequest request, Context context) {
        UserService service = new UserService();
        return service.getFollowersCount(request);
    }
}
