package prohor33.redmadrobot.test.instagram_api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import prohor33.redmadrobot.test.app.R;

/**
 * Created by prohor on 25/02/15.
 */
public class InstagramAPI {

    /**
     * Interface for catching result of operations.
     */
    public interface Listener {
        public abstract void onSuccess();
        public abstract void onFail(String error);
    }

    private static final String TAG = "InstagramAPI";
    private static final String API_URL = "https://api.instagram.com/v1/";

    private static final int WHAT_ERROR = -1;
    private static final int WHAT_FINALIZE = 0;

    // support classes
    private static InstagramAPI instance;
    private Listener listener;
    private Storage storage;
    private Loader loader;
    private Parser parser;

    // external data
    private static Context context;
    private String clientId;
    private String redirectUrl;

    public static void putContext(Context c) {
        context = c;
    }

    private InstagramAPI() {
        clientId = context.getString(R.string.instagram_client_id);
        redirectUrl = context.getString(R.string.instagram_redirect_uri);

        listener = mDefaultListener;

        storage = new Storage(context);
        storage.restoreAccessToken();

        loader = new Loader(API_URL);

        parser = new Parser();
    }

    public static synchronized InstagramAPI getInstance() {
        if (instance == null) {
            instance = new InstagramAPI();
        }
        return instance;
    }

    public static InstagramAPI with(Listener listener) {
        getInstance().attachListener(listener);
        return instance;
    }

    public static InstagramAPI with() {
        return with(null);
    }

    public static void findUser(String nickname) {
        getInstance().findUserImpl(nickname);
    }
    public void findUserImpl(final String nickname) {
        final Listener listener = this.listener;
        Log.d(TAG, "Find user ...");
        new Thread() {
            @Override
            public void run() {
                int what = WHAT_FINALIZE;
                try {
                    String answer = loader.fetchUserID(clientId, nickname);
                    storage.selfUserInfo = parser.parseUserSearch(answer);
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage(what, 0, 0, listener));
            }
        }.start();
    }

    public static void fetchUserMedia() {
        getInstance().fetchUserMediaImpl();
    }
    public void fetchUserMediaImpl() {
        final Listener listener = this.listener;
        Log.d(TAG, "Fetch user media ...");
        new Thread() {
            @Override
            public void run() {
                int what = WHAT_FINALIZE;
                try {
                    String answer = loader.fetchUserMedia(clientId, storage.selfUserInfo.id);
                    parser.parseUserMedia(storage.imageInfos, answer);
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage(what, 0, 0, listener));
            }
        }.start();
    }

    public static ArrayList<Storage.ImageInfo> getImages() {
        return getInstance().storage.imageInfos;
    }

    public static Storage.UserInfo getUserInfo() {
        return getInstance().storage.selfUserInfo;
    }

    // private members only

    // this func must control that listener is not null.
    private void attachListener(Listener listener) {
        if (listener == null) {
            this.listener = mDefaultListener;
        } else {
            this.listener = listener;
        }
    }

    // default Listener
    private Listener mDefaultListener = new Listener() {
        @Override
        public void onSuccess() {}

        @Override
        public void onFail(String error) {}
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Listener listener = (Listener) msg.obj;
            if (msg.what == WHAT_ERROR) {
                listener.onFail("InstagramAPI error: " + msg.toString());
            } else {
                listener.onSuccess();
            }
        }
    };
}
