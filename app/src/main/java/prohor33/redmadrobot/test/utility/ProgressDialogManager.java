package prohor33.redmadrobot.test.utility;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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
    private int currentProgressValue;
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
        currentProgressValue = 0;
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

    public static boolean isCanceled() {
        return !getInstance().active;
    }

    public static void setNewTarget(int message) {
        getInstance().setNewTargetImpl(message);
    }
    private void setNewTargetImpl(int message) {
        if (active) {
            ProgressDialog progress = currentProgress.get();
            currentMessage = context.getString(message);
            progress.setMessage(currentMessage);
        }
    }

    public static void setProgress(int value) {
        getInstance().setProgressImpl(value);
    }
    private void setProgressImpl(int value) {
        if (active) {
            currentProgressValue = value;
            currentProgress.get().setProgress(value);
        }
    }

    // private members only ============
    private void createDialog() {
        if (context == null)
            throw new RuntimeException("Please, use with() method to set context");

        ProgressDialog progress = new ProgressDialog(context);
        currentProgress = new WeakReference<>(progress);
        progress.setTitle(currentTitle);
        progress.setMessage(currentMessage);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setCancelable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // not necessary
            progress.setProgressNumberFormat("");
        }
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                active = false;
            }
        });
        progress.show();
        progress.setProgress(currentProgressValue);
    }
}
