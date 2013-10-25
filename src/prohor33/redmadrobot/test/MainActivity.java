package prohor33.redmadrobot.test;

import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	protected static Bitmap current_collage_preview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("Hey");
		
		if (current_collage_preview != null) {
			System.out.println("Create image view!");
			CreateImageView(current_collage_preview);
		}
				
        final Button button = (Button) findViewById(R.id.GiveMeCollage);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	System.out.println("button1 click");
            	           	

	        	new AsyncTask<String, Void, String>(){
	        		
	        		Bitmap collage;
	        		
	        		  @Override
	        		  protected String doInBackground(String... urlStr){        			  
	        		    // do stuff on non-UI thread
	        			
	        			String exception_mess = new String();
	        			CollageMaker collage_maker = new CollageMaker();
	        			try {
	        				collage = collage_maker.GimmeCollage(urlStr[0]);	        				
	       				
	        			} catch(IOException e) {
	        				exception_mess = e.getMessage();
	        			}
	        			return exception_mess;
	        		  }         
	
	        		  @Override
	        		  protected void onPostExecute(String result){
	        		    // do stuff on UI thread with the html
	
	        			 if (result.length() != 0) {
	        				 messageBox("Internet Connection Problem", result);
	        			 }
	        			 else {
	        				 
	        				Bitmap preview;
	         				if (collage.getWidth() > 480) { // too big
	         					System.out.println("comress collgae to make preview");
	         					float coef = collage.getWidth() / 480;
	         					preview = Bitmap.createScaledBitmap(collage,
	         							(int)coef*collage.getWidth(), (int)coef*collage.getHeight(), false);
	         				}
	         				else {
	         					preview = collage;
	         				}	        				 
	         				current_collage_preview = preview;
	         				
	        				System.out.println("Start changing view...");
	        				CreateImageView(preview);

	        			 }
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
	
	private void CreateImageView (Bitmap image_preview) {
		// create image view
		ImageView iv = new ImageView(MainActivity.this);
		iv.setImageBitmap(image_preview);
		
		int image_view_id = 91;	// random number (for simplicity)
		iv.setId(image_view_id);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout01);
		
		System.out.println("1.0");
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		    RelativeLayout.LayoutParams.WRAP_CONTENT,
		    RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		System.out.println("1.1");
		
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		System.out.println("1.2");
		
		lp.setMargins(0, 60, 0, 0);
		rl.addView(iv, lp);
		
		System.out.println("1.3");
		
		// edit text reposition
		EditText et = (EditText) findViewById(R.id.entry);	        				
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
			    RelativeLayout.LayoutParams.WRAP_CONTENT,
			    RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2 = (RelativeLayout.LayoutParams) et.getLayoutParams();		        			
		lp2.addRule(RelativeLayout.ABOVE, image_view_id);		        			
		et.setLayoutParams(lp2);
		
		System.out.println("1.4");
		
		// create new button
			Button bt = new Button(MainActivity.this);	        				
			bt.setText("Send by email");        				
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
		    RelativeLayout.LayoutParams.WRAP_CONTENT,
		    RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp3.addRule(RelativeLayout.BELOW, image_view_id);
		lp3.addRule(RelativeLayout.ALIGN_RIGHT);
		lp3.setMargins(0, 10, 0, 0);       				
		lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl.addView(bt, lp3);	
		
		System.out.println("1.5");
	}
}