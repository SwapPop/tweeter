package edu.byu.cs.tweeter.client.model.service.observer;

import java.security.spec.ECField;

public interface ServiceObserver {

    void handleFailure(String message);
    void handleException(Exception exception);
}
