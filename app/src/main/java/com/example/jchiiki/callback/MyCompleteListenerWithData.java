package com.example.jchiiki.callback;

public interface MyCompleteListenerWithData {
    void onSuccess(long data);
    void onFailure(Exception e);

}
