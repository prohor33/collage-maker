package prohor33.redmadrobot.test.utility;

/**
 * Created by prohor on 26/02/15.
 */
public interface SimpleAsyncListener {
    void onSuccess();
    void onError(String error);
    Boolean doInBackground();
}
