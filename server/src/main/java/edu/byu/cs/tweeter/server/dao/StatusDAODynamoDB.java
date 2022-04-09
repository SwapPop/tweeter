package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class StatusDAODynamoDB implements StatusDAO{

    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse();
    }

    public StoryResponse getStory(StoryRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatuses = getDummyStatuses();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusIndex = getStatusesStartingIndex(request.getLastStatusPost(), allStatuses);

                for(int limitCounter = 0; statusIndex < allStatuses.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatuses.size();
            }
        }

        return new StoryResponse(responseStatuses, hasMorePages);
    }

    public FeedResponse getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatuses = getDummyStatuses();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusIndex = getStatusesStartingIndex(request.getLastStatusPost(), allStatuses);

                for(int limitCounter = 0; statusIndex < allStatuses.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatuses.size();
            }
        }

        return new FeedResponse(responseStatuses, hasMorePages);
    }

    private int getStatusesStartingIndex(String lastStatusPost, List<Status> allStatuses) {

        int statusIndex = 0;

        if(lastStatusPost != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatusPost.equals(allStatuses.get(i).getPost())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusIndex = i + 1;
                    break;
                }
            }
        }

        return statusIndex;
    }

    /**
     * Returns the list of dummy status data. This is written as a separate method to allow
     * mocking of the statuses.
     *
     * @return the statuses.
     */
    List<Status> getDummyStatuses() {
        return getFakeData().getFakeStatuses();
    }


    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
