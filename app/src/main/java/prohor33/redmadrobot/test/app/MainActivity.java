package prohor33.redmadrobot.test.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import prohor33.redmadrobot.test.R;
import prohor33.redmadrobot.test.collage_maker.CollageMaker;
import prohor33.redmadrobot.test.instagram_api.InstagramAPI;
import prohor33.redmadrobot.test.utility.RoundButton;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: handle progress bar

        InstagramAPI.putContext(MainActivity.this);

        final EditText nickEditText = (EditText) findViewById(R.id.editText);

        // TODO: remove
        nickEditText.setText("damedvedev");

        Button mainButton = (Button) findViewById(R.id.mainButton);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nickname = nickEditText.getText().toString();
                if (nickname.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_nickname_text),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // TODO: progress bar style is horrible
                if (progressDialog == null)
                    progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle(getString(R.string.progress_load_preview_title));
                progressDialog.setMessage(getString(R.string.progress_load_preview_message));
                progressDialog.setCancelable(false);
                progressDialog.show();

                InstagramAPI.with(new InstagramAPI.Listener() {
                    @Override
                    public void onSuccess() {
                        getUserMedia();
                    }

                    @Override
                    public void onFail(String error) {
                        Toast.makeText(getApplicationContext(), error,
                                Toast.LENGTH_LONG).show();
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                }).findUser(nickname);
            }
        });

        setupRoundButtons();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // private members only =========

    private void getUserMedia() {
        InstagramAPI.with(new InstagramAPI.Listener() {
            @Override
            public void onSuccess() {
                generateCollagePreview();

                // debug
                Toast.makeText(getApplicationContext(), "successfully find user",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getApplicationContext(), error,
                        Toast.LENGTH_LONG).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        }).fetchUserMedia();
    }

    private void generateCollagePreview() {
        CollageMaker.with(new CollageMaker.Listener() {
            @Override
            public void onSuccess() {
                // debug
                Toast.makeText(getApplicationContext(), "preview have been successfully generated",
                        Toast.LENGTH_LONG).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
                showPreview(true);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getApplicationContext(), error,
                        Toast.LENGTH_LONG).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        }).generateCollagePreview();
    }

    private void generateCollage() {
        CollageMaker.with(new CollageMaker.Listener() {
            @Override
            public void onSuccess() {
                // debug
                Toast.makeText(getApplicationContext(), "collage have been successfully generated",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getApplicationContext(), error,
                        Toast.LENGTH_LONG).show();
            }
        }).generateCollage();
    }

    private void showPreview(boolean show) {
        RelativeLayout rlNicknameGroup = (RelativeLayout) findViewById(R.id.rlNicknameEditGroup);
        RelativeLayout rlCollageGroup = (RelativeLayout) findViewById(R.id.rlCollageGroup);

        ImageView ivCollage = (ImageView) findViewById(R.id.collageImageView);
        Bitmap collageBitmap = CollageMaker.getCollageBitmap();
        if (collageBitmap != null)
            ivCollage.setImageBitmap(collageBitmap);

        rlNicknameGroup.setVisibility(show ? View.GONE : View.VISIBLE);
        rlCollageGroup.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setupRoundButtons() {
        RoundButton closeCollageBtn = (RoundButton) findViewById(R.id.close_collage_btn);
        closeCollageBtn.setColor(getResources().getColor(R.color.round_buttons_color));
        closeCollageBtn.setDrawable(getResources().getDrawable(R.drawable.ic_close_collage_btn));
        closeCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rlCollageGroup = (RelativeLayout) findViewById(R.id.rlCollageGroup);
                showPreview(rlCollageGroup.getVisibility() == View.GONE);
            }
        });

        RoundButton shareCollageBtn = (RoundButton) findViewById(R.id.share_collage_btn);
        shareCollageBtn.setColor(getResources().getColor(R.color.round_buttons_color));
        shareCollageBtn.setDrawable(getResources().getDrawable(R.drawable.ic_collage_share));
        shareCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Not implemented yet");
            }
        });

        RoundButton saveCollageBtn = (RoundButton) findViewById(R.id.save_collage_btn);
        saveCollageBtn.setColor(getResources().getColor(R.color.round_buttons_color));
        saveCollageBtn.setDrawable(getResources().getDrawable(R.drawable.ic_collage_save));
        saveCollageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Not implemented yet");
            }
        });
    }
}
