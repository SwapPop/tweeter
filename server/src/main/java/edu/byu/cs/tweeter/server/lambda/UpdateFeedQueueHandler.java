package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.BatchFeedRequest;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeedQueueHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent input, Context context) {

        StatusService statusService = new StatusService();
        for (SQSEvent.SQSMessage msg : input.getRecords()) {
            BatchFeedRequest request = JsonSerializer.deserialize(msg.getBody(), BatchFeedRequest.class);
            statusService.batchFeedUpdate(request);
        }
        return null;
    }
}
