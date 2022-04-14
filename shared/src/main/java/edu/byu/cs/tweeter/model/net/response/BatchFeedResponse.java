package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;

/**
 * A paged response for a {@link FollowingRequest}.
 */
public class BatchFeedResponse extends PagedResponse {

    private List<String> followersAliases;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful. Sets the
     * success and more pages indicators to false.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public BatchFeedResponse(String message) {
        super(false, message, false);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param followersAliases the followers to be included in the result.
     * @param hasMorePages an indicator of whether more data is available for the request.
     */
    public BatchFeedResponse(List<String> followersAliases, boolean hasMorePages) {
        super(true, hasMorePages);
        this.followersAliases = followersAliases;
    }

    /**
     * Returns the followees for the corresponding request.
     *
     * @return the followees.
     */
    public List<String> getFollowersAliases() {
        return followersAliases;
    }

    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        BatchFeedResponse that = (BatchFeedResponse) param;

        return (Objects.equals(followersAliases, that.followersAliases) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(followersAliases);
    }
}
