package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        StatusService service = new StatusService();
        return service.postStatus(request);
    }
}
