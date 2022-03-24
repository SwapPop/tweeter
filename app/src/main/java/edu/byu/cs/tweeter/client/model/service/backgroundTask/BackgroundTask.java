package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.util.FakeData;

public abstract class BackgroundTask implements Runnable{

    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";
    private static final String LOG_TAG = "BackgroundTask";


    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public BackgroundTask(Handler messageHandler) {
        this.messageHandler = messageHandler;

    }

    @Override
    public final void run() {
        try {
            runTask();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    protected abstract void runTask();

    public void sendSuccessMessage() {
        Bundle msgBundle = createBundle(true);
        loadSuccessBundle(msgBundle);
        sendMessage(msgBundle);
    }

    public void sendFailedMessage(String message) {
        Bundle msgBundle = createBundle(false);
        msgBundle.putString(MESSAGE_KEY, message);
        sendMessage(msgBundle);
    }

    public void sendExceptionMessage(Exception exception) {
        Bundle msgBundle = createBundle(false);
        msgBundle.putSerializable(EXCEPTION_KEY, exception);
        sendMessage(msgBundle);
    }

    //TODO: REMOVE THIS FROM EXISTENCE
    protected FakeData getFakeData() {
        return new FakeData();
    }

    protected abstract void loadSuccessBundle(Bundle msgBundle);

    @NonNull
    private Bundle createBundle(boolean b) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, b);
        return msgBundle;
    }

    private void sendMessage(Bundle msgBundle) {
        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }
}
