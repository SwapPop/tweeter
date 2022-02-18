package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.CountTask;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;

public class CountHandler extends BackgroundTaskHandler<CountObserver> {
    public CountHandler(CountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, CountObserver observer) {
        int count = data.getInt(CountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
