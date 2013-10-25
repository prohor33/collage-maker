package prohor33.redmadrobot.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("Hello world!");
		System.out.println("Hey");
		
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	System.out.println("button1 click");
            	

        	new AsyncTask<String, Void, String>(){
        		
        		  @Override
        		  protected String doInBackground(String... urlStr){        			  
        		    // do stuff on non-UI thread
        			CollageMaker collage_maker = new CollageMaker();
        			collage_maker.GimmeCollage(urlStr[0]);
        			return new String();
        		  }         

        		  @Override
        		  protected void onPostExecute(String result){
        		    // do stuff on UI thread with the html

        		  }
        		}.execute("tom");
            	
            }
        });		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}