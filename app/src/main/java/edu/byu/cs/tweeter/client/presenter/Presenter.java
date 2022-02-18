package edu.byu.cs.tweeter.client.presenter;

public abstract class Presenter {
    protected Presenter.View view;

    public Presenter(View view) {
        this.view = view;
    }

    public interface View{
        void displayMessage(String message);
    }

    protected abstract String getActionString();
}
