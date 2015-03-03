package prohor33.redmadrobot.test.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.widget.Toast;

import prohor33.redmadrobot.test.app.R;


/**
 * Created by prohor on 01/03/15.
 */
public class Utils {
    public static void throwIfNotUIThread() {
        if (Looper.myLooper() != Looper.getMainLooper())
            throw new RuntimeException("Not UI thread, but should be");
    }

    public static boolean checkInternetConnection(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void notifyNoConnection(Context context) {
        Toast.makeText(context, context.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT).show();
    }

    public static boolean checkAndNotifyConnection(Activity activity) {
        boolean connected = checkInternetConnection(activity);
        if(!connected) notifyNoConnection(activity);
        return connected;
    }
}
