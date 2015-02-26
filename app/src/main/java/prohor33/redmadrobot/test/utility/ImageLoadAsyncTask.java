package prohor33.redmadrobot.test.utility;

import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by prohor on 26/02/15.
 */
public class ImageLoadAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private SimpleAsyncListener mSimpleAsyncListener;
    private String mErrorString;
    private ProgressDialog mProgressDialog;
    private boolean canceled = false;

    public ImageLoadAsyncTask(SimpleAsyncListener simpleAsyncListener) {
        super();
        mSimpleAsyncListener = simpleAsyncListener;
    }

    public void cancel() {
        canceled = true;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return mSimpleAsyncListener.doInBackground();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (canceled)
            return;

        if(result) {
            mSimpleAsyncListener.onSuccess();
        } else {
            mSimpleAsyncListener.onError(mErrorString);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
