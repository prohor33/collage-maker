package prohor33.redmadrobot.test.utility;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by prohor on 01/03/15.
 */
public class ProgressDialogManager {

    private static final String TAG = "ProgressDialogManager";
    private static ProgressDialogManager instance;
    private Context context;
    private WeakReference<ProgressDialog> currentProgress;
    private String currentTitle;
    private String currentMessage;
    private boolean active = false;

    public static synchronized ProgressDialogManager getInstance() {
        if (instance == null) {
            instance = new ProgressDialogManager();
        }
        return instance;
    }

    public static ProgressDialogManager with(Context context) {
        getInstance().context = context;
        return instance;
    }

    public static void show(int title, int message) {
        getInstance().showImpl(title, message);
    }
    private void showImpl(int title, int message) {
//        Log.d(TAG, "show progress dialog");
        currentTitle = context.getString(title);
        currentMessage = context.getString(message);
        createDialog();
        active = true;
    }

    public static void restore() {
        getInstance().restoreImpl();
    }
    private void restoreImpl() {
        if (active) {
//            Log.d(TAG, "restore progress dialog");
            createDialog();
        }
    }

    public static void onPause() {
        getInstance().dismissImpl(true);
    }

    public static void dismiss() {
        getInstance().dismissImpl(false);
    }
    private void dismissImpl(boolean pause) {
        if (active) {
            ProgressDialog progressDialog = currentProgress.get();
            if (progressDialog != null) {
//                Log.d(TAG, "dismiss progress dialog");
                progressDialog.dismiss();
            }
            currentProgress = null;
            if (!pause)
                active = false;
        }
    }

    // private members only ============
    private void createDialog() {
        if (context == null)
            throw new RuntimeException("Please, use with() method to set context");

        currentProgress = new WeakReference<>(ProgressDialog.show(context,
                currentTitle, currentMessage, true));
    }
}
