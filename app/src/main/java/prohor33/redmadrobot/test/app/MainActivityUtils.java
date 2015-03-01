package prohor33.redmadrobot.test.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import prohor33.redmadrobot.test.R;
import prohor33.redmadrobot.test.collage_maker.CollageMaker;
import prohor33.redmadrobot.test.instagram_api.InstagramAPI;
import prohor33.redmadrobot.test.utility.ProgressDialogManager;
import prohor33.redmadrobot.test.utility.RoundButton;
import prohor33.redmadrobot.test.utility.Utils;

/**
 * Created by prohor on 01/03/15.
 */
public class MainActivityUtils {

    private static final String TAG = "MainActivityUtils";
    private static Activity mainActivity;

    public static void onCreate(Activity activity) {
        mainActivity = activity;

        final EditText nickEditText = (EditText) mainActivity.findViewById(R.id.editText);

        // TODO: remove
        nickEditText.setText("damedvedev");

        Button mainButton = (Button) mainActivity.findViewById(R.id.mainButton);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nickname = nickEditText.getText().toString();
                if (nickname.isEmpty()) {
                    Toast.makeText(mainActivity, mainActivity.getString(R.string.no_nickname_text),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                ProgressDialogManager.show(R.string.progress_load_preview_title,
                        R.string.progress_load_preview_message);

                InstagramAPI.with(new InstagramAPI.Listener() {
                    @Override
                    public void onSuccess() {
                        getUserMedia();
                    }

                    @Override
                    public void onFail(String error) {
                        Toast.makeText(mainActivity, error,
                                Toast.LENGTH_LONG).show();
                        ProgressDialogManager.dismiss();
                    }
                }).findUser(nickname);
            }
        });

        setupRoundButtons();
        showPreview(CollageMaker.isShowingCollage());
    }

    // private members only =========

    private static void getUserMedia() {
        InstagramAPI.with(new InstagramAPI.Listener() {
            @Override
            public void onSuccess() {
                generateCollagePreview();

                // debug
                Toast.makeText(mainActivity, "successfully find user",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(mainActivity, error,
                        Toast.LENGTH_LONG).show();
                ProgressDialogManager.dismiss();
            }
        }).fetchUserMedia();
    }

    private static void generateCollagePreview() {
        CollageMaker.with(new CollageMaker.Listener() {
            @Override
            public void onSuccess() {
                // debug
                Toast.makeText(mainActivity, "preview have been successfully generated",
                        Toast.LENGTH_LONG).show();
                ProgressDialogManager.dismiss();
                showPreview(true);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(mainActivity, error,
                        Toast.LENGTH_LONG).show();
                ProgressDialogManager.dismiss();
            }
        }).generateCollagePreview();
    }

    private static void generateCollage() {
        CollageMaker.with(new CollageMaker.Listener() {
            @Override
            public void onSuccess() {
                // debug
                Toast.makeText(mainActivity, "collage have been successfully generated",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(mainActivity, error,
                        Toast.LENGTH_LONG).show();
            }
        }).generateCollage();
    }

    private static void showPreview(boolean show) {
        Utils.throwIfNotUIThread();
        Log.d(TAG, "Show preview, show = " + show);
        CollageMaker.showCollage(show);
        RelativeLayout rlNicknameGroup = (RelativeLayout) mainActivity.findViewById(R.id.rlNicknameEditGroup);
        RelativeLayout rlCollageGroup = (RelativeLayout) mainActivity.findViewById(R.id.rlCollageGroup);

        ImageView ivCollage = (ImageView) mainActivity.findViewById(R.id.collageImageView);
        Bitmap collageBitmap = CollageMaker.getCollageBitmap();
        if (collageBitmap != null)
            ivCollage.setImageBitmap(collageBitmap);

        rlNicknameGroup.setVisibility(show ? View.GONE : View.VISIBLE);
        rlCollageGroup.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private static void setupRoundButtons() {
        RoundButton closeCollageBtn = (RoundButton) mainActivity.findViewById(R.id.close_collage_btn);
        closeCollageBtn.setColor(mainActivity.getResources().getColor(R.color.round_buttons_color));
        closeCollageBtn.setDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_close_collage_btn));
        closeCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rlCollageGroup = (RelativeLayout) mainActivity.findViewById(R.id.rlCollageGroup);
                showPreview(rlCollageGroup.getVisibility() == View.GONE);
            }
        });

        RoundButton shareCollageBtn = (RoundButton) mainActivity.findViewById(R.id.share_collage_btn);
        shareCollageBtn.setColor(mainActivity.getResources().getColor(R.color.round_buttons_color));
        shareCollageBtn.setDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_collage_share));
        shareCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Not implemented yet");
            }
        });

        RoundButton saveCollageBtn = (RoundButton) mainActivity.findViewById(R.id.save_collage_btn);
        saveCollageBtn.setColor(mainActivity.getResources().getColor(R.color.round_buttons_color));
        saveCollageBtn.setDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_collage_save));
        saveCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Not implemented yet");
            }
        });
    }
}
