package prohor33.redmadrobot.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
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
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("Hello world!");
		System.out.println("Hey");
		
				
        final Button button = (Button) findViewById(R.id.GiveMeCollage);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	System.out.println("button1 click");
            	
            	

	        	new AsyncTask<String, Void, String>(){
	        		
	        		  @Override
	        		  protected String doInBackground(String... urlStr){        			  
	        		    // do stuff on non-UI thread
	        			
	        			String exception_mess = new String();
	        			CollageMaker collage_maker = new CollageMaker();
	        			try {
	        				collage_maker.GimmeCollage(urlStr[0]);
	       				
	        			} catch(IOException e) {
	        				//System.out.println(e.getMessage());
	        				exception_mess = e.getMessage();
	        				//messageBox("GimmeCollage", e.getMessage());
	        			}
	        			return exception_mess;
	        		  }         
	
	        		  @Override
	        		  protected void onPostExecute(String result){
	        		    // do stuff on UI thread with the html
	
	        			 if (result.length() != 0) {
	        				 messageBox("Internet Connection Problem", result);
	        			 }
/*	        			 else {
	         				ImageView iv = new ImageView(ParentActivity.this);
	        				iv.setImageResource(R.drawable.beerbottle);
	        				RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout01);
	        				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	        				    RelativeLayout.LayoutParams.WRAP_CONTENT,
	        				    RelativeLayout.LayoutParams.WRAP_CONTENT);
	        				lp.addRule(RelativeLayout.BELOW, R.id.ButtonRecalculate);
	        				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	        				rl.addView(iv, lp);
	        			 }*/
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

	//*********************************************************
	//generic dialog, takes in the method name and error message
	//*********************************************************	
	private void messageBox(String method, String message)
	{
	    Log.d("EXCEPTION: " + method,  message);

	    AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
	    messageBox.setTitle(method);
	    messageBox.setMessage(message);
	    messageBox.setCancelable(false);
	    messageBox.setNeutralButton("OK", null);
	    messageBox.show();
	}	
}