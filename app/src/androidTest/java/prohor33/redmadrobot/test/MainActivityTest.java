package prohor33.redmadrobot.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import prohor33.redmadrobot.test.app.MainActivity;
import prohor33.redmadrobot.test.utility.RoundButton;

/**
 * Created by prohor on 03/03/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2 {

    private final static String TAG = "MainActivityTest";
    private final static int SHOW_PREVIEW_TIME = 10;
    private final static int SAVE_COLLAGE_TIME = 30;
    private MainActivity mainActivity;
    private EditText nickEditText;
    private Button mainButton;
    private RoundButton closeCollageBtn;
    private RoundButton shareCollageBtn;
    private RoundButton saveCollageBtn;
    // create  a signal to let us know when our task is done.
    private final CountDownLatch signal = new CountDownLatch(1);

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

//        setActivityInitialTouchMode(true);

        mainActivity = (MainActivity) getActivity();
        nickEditText = (EditText) mainActivity.findViewById(R.id.editText);
        mainButton = (Button) mainActivity.findViewById(R.id.gimmeCollageButton);
        closeCollageBtn = (RoundButton) mainActivity.findViewById(R.id.close_collage_btn);
        shareCollageBtn = (RoundButton) mainActivity.findViewById(R.id.share_collage_btn);
        saveCollageBtn = (RoundButton) mainActivity.findViewById(R.id.save_collage_btn);
    }

    @MediumTest
    public void testMainChain() {
        testShowMePreview("damedvedev", false);
        testSaveCollage(true);
    }

    @MediumTest
    public void testShowPreviewDifferentNames() {
        testShowMePreview("nick", true);
        testShowMePreview("ivan", true);
        testShowMePreview("artemka", true);
        testShowMePreview("evgeniy", true);
        testShowMePreview("123", true);
    }

    // private members only ==============

    private void testSaveCollage(final boolean save) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d("UI thread", "I am the UI thread");
                if (save) {
                    saveCollageBtn.performClick();
                } else {
                    shareCollageBtn.performClick();
                }
            }
        });

        /* The testing thread will wait here until the UI thread releases it
         * above with the countDown() or 30 seconds passes and it times out.
         */
        try {
            signal.await(SAVE_COLLAGE_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.d(TAG, "exception occurs: " + e);
        }
    }

    private void testShowMePreview(final String nickname, boolean close) {

        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d("UI thread", "I am the UI thread");
                nickEditText.setText(nickname);
                mainButton.performClick();
            }
        });

        /* The testing thread will wait here until the UI thread releases it
         * above with the countDown() or 30 seconds passes and it times out.
         */
        try {
            signal.await(SHOW_PREVIEW_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.d(TAG, "exception occurs: " + e);
        }

        if (close) {
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    RelativeLayout rlCollageGroup = (RelativeLayout) mainActivity.findViewById(R.id.rlCollageGroup);

                    if (rlCollageGroup.getVisibility() == View.VISIBLE)
                        closeCollageBtn.performClick();
                }
            });
        }
    }
}
