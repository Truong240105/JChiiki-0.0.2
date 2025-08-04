package admin.callback;

public interface MyCompleteListenerWithData {
    void onSuccess(long data);
    void onFailure(Exception e);

}
