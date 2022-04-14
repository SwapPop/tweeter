package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.BatchFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersQueueRequest;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersQueueHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        FollowService followService = new FollowService();
        for (SQSEvent.SQSMessage msg : input.getRecords()){
            GetFollowersQueueRequest request = JsonSerializer.deserialize(msg.getBody(), GetFollowersQueueRequest.class);
            followService.batchFollowers(request.getStatus());
        }
        return null;
    }
}
