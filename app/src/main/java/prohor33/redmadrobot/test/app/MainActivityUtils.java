package prohor33.redmadrobot.test.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import prohor33.redmadrobot.test.R;
import prohor33.redmadrobot.test.collage_maker.CollageMaker;
import prohor33.redmadrobot.test.instagram_api.InstagramAPI;
import prohor33.redmadrobot.test.utility.ProgressDialogManager;
import prohor33.redmadrobot.test.utility.RoundButton;
import prohor33.redmadrobot.test.utility.SimpleAsyncListener;
import prohor33.redmadrobot.test.utility.SimpleAsyncTask;
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
        putGimmeCollageButtonActive(!nickEditText.getText().toString().isEmpty());
        nickEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                putGimmeCollageButtonActive(!nickEditText.getText().toString().isEmpty());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
        });

        Button mainButton = (Button) mainActivity.findViewById(R.id.gimmeCollageButton);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();

                final String nickname = nickEditText.getText().toString();
                if (nickname.isEmpty()) {
                    Toast.makeText(mainActivity, mainActivity.getString(R.string.no_nickname_text),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!Utils.checkAndNotifyConnection(mainActivity))
                    return;

                ProgressDialogManager.show(R.string.progress_load_preview_title,
                        R.string.progress_load_preview_message);

                InstagramAPI.with(new InstagramAPI.Listener() {
                    @Override
                    public void onSuccess() {
                        nickEditText.setText(InstagramAPI.getUserInfo().username);
                        getUserMedia();
                    }

                    @Override
                    public void onFail(String error) {
                        Log.d(TAG, "findUser onFail:" + error);
                        Toast.makeText(mainActivity,
                                mainActivity.getString(R.string.no_user_with_such_nickname),
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
            }

            @Override
            public void onFail(String error) {
                Log.d(TAG, "fetchUserMedia onFail:" + error);
                Toast.makeText(mainActivity,
                        mainActivity.getString(R.string.user_have_no_public_media),
                        Toast.LENGTH_LONG).show();
                ProgressDialogManager.dismiss();
            }
        }).fetchUserMedia();
    }

    private static void generateCollagePreview() {
        CollageMaker.with(new CollageMaker.Listener() {
            @Override
            public void onSuccess() {
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

    private static void generateCollage(final boolean share) {
        ProgressDialogManager.show(R.string.progress_load_preview_title,
                R.string.progress_load_preview_message);

        CollageMaker.with(new CollageMaker.Listener() {
            @Override
            public void onSuccess() {
                saveCollage(share);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(mainActivity, error,
                        Toast.LENGTH_LONG).show();
                ProgressDialogManager.dismiss();
            }
        }).generateCollage();
    }

    private static void showPreview(boolean show) {
        Utils.throwIfNotUIThread();
        CollageMaker.showCollage(show);
        RelativeLayout rlNicknameGroup = (RelativeLayout) mainActivity.findViewById(R.id.rlNicknameEditGroup);
        RelativeLayout rlCollageGroup = (RelativeLayout) mainActivity.findViewById(R.id.rlCollageGroup);

        ImageView ivCollage = (ImageView) mainActivity.findViewById(R.id.collageImageView);
        Bitmap collageBitmap = CollageMaker.getCollageBitmap();
        if (collageBitmap != null)
            ivCollage.setImageBitmap(collageBitmap);
        onCollageLayoutSizeChanged();

        rlNicknameGroup.setVisibility(show ? View.GONE : View.VISIBLE);
        rlCollageGroup.setVisibility(show ? View.VISIBLE : View.GONE);

        FrameLayout flMain = (FrameLayout) mainActivity.findViewById(R.id.flMain);
        if (show) {
            flMain.getBackground().setColorFilter(
                    mainActivity.getResources().getColor(R.color.main_back_color_tint),
                    PorterDuff.Mode.SRC_ATOP);
        } else {
            flMain.getBackground().setColorFilter(null);
        }
    }

    private static void setupRoundButtons() {
        RoundButton closeCollageBtn = (RoundButton) mainActivity.findViewById(R.id.close_collage_btn);
        closeCollageBtn.setColor(mainActivity.getResources().getColor(R.color.round_buttons));
        closeCollageBtn.setDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_close_collage_btn));
        closeCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rlCollageGroup = (RelativeLayout) mainActivity.findViewById(R.id.rlCollageGroup);
                showPreview(rlCollageGroup.getVisibility() == View.GONE);
            }
        });

        RoundButton shareCollageBtn = (RoundButton) mainActivity.findViewById(R.id.share_collage_btn);
        shareCollageBtn.setColor(mainActivity.getResources().getColor(R.color.round_buttons));
        shareCollageBtn.setDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_collage_share));
        shareCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateCollage(true);
            }
        });

        RoundButton saveCollageBtn = (RoundButton) mainActivity.findViewById(R.id.save_collage_btn);
        saveCollageBtn.setColor(mainActivity.getResources().getColor(R.color.round_buttons));
        saveCollageBtn.setDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_collage_save));
        saveCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateCollage(false);
            }
        });
    }

    private static void onCollageLayoutSizeChanged() {
        final ImageView ivCollage = (ImageView) mainActivity.findViewById(R.id.collageImageView);
        ViewTreeObserver vto = ivCollage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                updateCollageLayoutSize();

                ViewTreeObserver obs = ivCollage.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });
    }

    private static void updateCollageLayoutSize() {
        float aspect_ratio = CollageMaker.getCollageImageAspectRatio();

        ImageView ivCollage = (ImageView) mainActivity.findViewById(R.id.collageImageView);
        RelativeLayout parent = (RelativeLayout) ivCollage.getParent();

        int max_width = parent.getWidth();
        int max_height = parent.getHeight();
//        Log.d(TAG, "aspect_ratio = " + aspect_ratio);
//        Log.d(TAG, "max_w = " + max_width);
//        Log.d(TAG, "max_h = " + max_height);
        int collage_w, collage_h;
        if (max_height > aspect_ratio * max_width) {
            collage_w = max_width;
            collage_h = (int)(aspect_ratio * max_width);
        } else {
            collage_h = max_height;
            collage_w = (int)(max_height / aspect_ratio);
        }

//        Log.d(TAG, "collage_h = " + collage_h);
//        Log.d(TAG, "collage_w = " + collage_w);

        if (mainActivity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE &&
                aspect_ratio < 1.0f) {
            final float special_coef = 0.66f;
            collage_h *= special_coef;
            collage_w *= special_coef;
        }


        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) ivCollage.getLayoutParams();

        layoutParams.width = collage_w;
        layoutParams.height = collage_h;
        ivCollage.setLayoutParams(layoutParams);
    }

    private static void saveCollage(final boolean share) {

        new SimpleAsyncTask(new SimpleAsyncListener() {
            File file;

            @Override
            public void onSuccess() {
                ProgressDialogManager.dismiss();

                if (share) {
                    openShareDialog(file);
                } else {
                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    extStorageDirectory += "/" + file.getName();
                    Toast.makeText(mainActivity,
                            mainActivity.getString(R.string.main_activity_toast_collage_saved_to)
                                    + extStorageDirectory,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                ProgressDialogManager.dismiss();
            }

            @Override
            public Boolean doInBackground() {
                Bitmap bmpImage = CollageMaker.getCollageBitmap();
                if (bmpImage == null)
                    return false;

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                OutputStream outStream;
                String temp = new String("collage");
                file = new File(extStorageDirectory, temp + ".png");
                if (file.exists()) {
                    file.delete();
                    file = new File(extStorageDirectory, temp + ".png");
                }

                try {
                    outStream = new FileOutputStream(file);
                    bmpImage.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    Log.v(TAG, "Error saving file: " + e.toString());
                    return false;
                }
                return true;
            }
        }).execute();
    }

    private static void openShareDialog(File fileResult) {
        if (fileResult == null) {
            Log.v(TAG, "Null file pointer");
            return;
        }

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                mainActivity.getString(R.string.main_activity_collage_mail_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                mainActivity.getString(R.string.main_activity_collage_mail_text));

        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileResult));
        mainActivity.startActivity(Intent.createChooser(sharingIntent,
                mainActivity.getString(R.string.main_activity_share_chooser)));
    }

    private static void hideKeyboard() {
        // Check if no view has focus:
        View view = mainActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private static void putGimmeCollageButtonActive(boolean active) {
        Button button = (Button) mainActivity.findViewById(R.id.gimmeCollageButton);
        button.setBackground(mainActivity.getDrawable(active ?
                R.drawable.gimme_button_back_active : R.drawable.gimme_button_back));
        button.setActivated(active);
    }
}
