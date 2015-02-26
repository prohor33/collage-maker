package prohor33.redmadrobot.test.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import prohor33.redmadrobot.test.R;
import prohor33.redmadrobot.test.collage_maker.CollageMaker;
import prohor33.redmadrobot.test.instagram_api.InstagramAPI;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                InstagramAPI.with(new InstagramAPI.Listener() {
                    @Override
                    public void onSuccess() {
                        getUserMedia();
                    }

                    @Override
                    public void onFail(String error) {
                        Toast.makeText(getApplicationContext(), error,
                                Toast.LENGTH_LONG).show();
                    }
                }).findUser(nickname);
            }
        });
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
                generateCollage();

                // debug
                Toast.makeText(getApplicationContext(), "successfully find user",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getApplicationContext(), error,
                        Toast.LENGTH_LONG).show();
            }
        }).fetchUserMedia();
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
}
