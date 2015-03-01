package prohor33.redmadrobot.test.utility;

import android.os.Looper;

/**
 * Created by prohor on 01/03/15.
 */
public class Utils {
    public static void throwIfNotUIThread() {
        if (Looper.myLooper() != Looper.getMainLooper())
            throw new RuntimeException("Not UI thread, but should be");
    }
}
