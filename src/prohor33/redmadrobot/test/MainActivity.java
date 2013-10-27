package prohor33.redmadrobot.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
	protected static Bitmap current_collage;
	protected static ProgressDialog progress_dialog;
	protected static AsyncTask<String, Void, String> async_task;
	protected static MainActivity main_activity;
	protected static String nickname;
	protected static File collage_file;
	protected int collage_size;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("Hey");
		
		main_activity = this;
		
		if (current_collage_preview != null) {
			System.out.println("Create image view!");
			CreateImageView(current_collage_preview);
		}
		
		if (progress_dialog != null) {
      progress_dialog = onCreateProgressDialog();	  
		}
				
    final Button button = (Button) findViewById(R.id.GiveMeCollage);
    button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          
        EditText edit_nickname = (EditText) findViewById(R.id.entry);  
        nickname = edit_nickname.getText().toString();
        
        if (nickname.isEmpty()) {
          AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          builder.setTitle("Field is empty");
          builder.setMessage("Please, type in nickname in the blank field");
          builder.setPositiveButton("OK", null);
          builder.show();
          return;
        }
        
        collage_size = 6;
        
      	System.out.println("GiveMeCollage button click");
        	         


      	new AsyncTask<String, Void, String>(){
      		
      		Bitmap collage;
      		      		
            @Override
            protected void onPreExecute(){
              
              async_task = this;
              
              progress_dialog = onCreateProgressDialog();
              progress_dialog.setMax(collage_size);
              
            }
            
      		
      		  @Override
      		  protected String doInBackground(String... urlStr){        			  
      		    // do stuff on non-UI thread
      		          		    
      		    
      			String exception_mess = new String();
      			CollageMaker collage_maker = new CollageMaker(collage_size, progress_dialog);
      			
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

              progress_dialog.dismiss();
              progress_dialog = null;      		    
      		    
      			 if (result.length() != 0) {
      				 messageBox("Internet Connection Problem", result);
      			 }
      			 else {
      				 
      			   Bitmap preview;
       				 if (collage.getWidth() > 450) { // too big
       					 System.out.println("comress collgae to make preview");
       					 float coef = 450.0f / collage.getWidth();
       					 preview = Bitmap.createScaledBitmap(collage,
       							(int)(coef*collage.getWidth()), (int)(coef*collage.getHeight()), false);
       				 }
       				 else
       					 preview = collage;      				 
       				 
       				 current_collage_preview = preview;
       				 current_collage = collage;
       				
       				 collage_file = savebitmap(collage);
       				
      				 System.out.println("Start changing view...");
      				
      				 main_activity.CreateImageView(preview);

      			  }
      		  }
      		}.execute(nickname);
        	
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
	  
	  int orientation = MainActivity.this.getResources().getConfiguration().orientation;
	  
    RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout01);
    
    int image_view_id = 91; // random number (for simplicity)
    
    // create new "Send By Email" button
    Button bt = new Button(MainActivity.this);                  
    bt.setText("Share With Friends");
    int button_send_email_id = 81; // random number (for simplicity)
    bt.setId(button_send_email_id);    
    RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    
    lp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);   
    
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
      lp3.setMargins(0, 0, 0, 20);
    }
    else {
      lp3.addRule(RelativeLayout.ALIGN_RIGHT, image_view_id);
      lp3.setMargins(0, 0, 50, 20);
    }
    rl.addView(bt, lp3);	  
  	  
    
  	// create image view
  	ImageView iv = new ImageView(MainActivity.this);
  	iv.setImageBitmap(image_preview);  	
  	 	
  	iv.setId(image_view_id);
  	
  	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
  	    RelativeLayout.LayoutParams.WRAP_CONTENT,
  	    RelativeLayout.LayoutParams.WRAP_CONTENT);
  	
  	lp.addRule(RelativeLayout.CENTER_VERTICAL);
  	lp.addRule(RelativeLayout.ABOVE, button_send_email_id);
  	
  	if (orientation == Configuration.ORIENTATION_PORTRAIT) {
  	  lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
  	  lp.setMargins(0, 100, 0, 20);
  	}
  	else {
      lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      lp.setMargins(0, 20, 10, 20);
  	}
  	
  	rl.addView(iv, lp);
  	
  	
  	// edit text reposition
  	EditText et = (EditText) findViewById(R.id.entry);	        				
  	RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
  		    RelativeLayout.LayoutParams.WRAP_CONTENT,
  		    RelativeLayout.LayoutParams.WRAP_CONTENT);
  	lp2 = (RelativeLayout.LayoutParams) et.getLayoutParams();
  	if (orientation == Configuration.ORIENTATION_PORTRAIT)
  	  lp2.addRule(RelativeLayout.ABOVE, image_view_id);
  	else {
  	  lp2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
  	  lp2.setMargins(10, 0, 0, 0);
  	  et.setEms(9);
  	  et.setTextSize(20);
  	}
  	et.setLayoutParams(lp2);
			
		
  	// edit button "Give Me Collage"
  	Button gmc_button = (Button) findViewById(R.id.GiveMeCollage);  
  	gmc_button.setText("Give Me Another One");
  	gmc_button.setTextSize(15);
  	
  	
    final Button send_email_button = (Button) findViewById(button_send_email_id);
    send_email_button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        
          System.out.println("Trying to send email");
          
          Uri u = null;
          if (collage_file == null) {
            System.out.println("Collage file is null");
            return;
          }
          u = Uri.fromFile(collage_file);

          Intent emailIntent = new Intent(Intent.ACTION_SEND);
          emailIntent.setType("image/*");
          emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Photo Collage For You");
          emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, " + nickname + 
              "! Here I have tiny present for you! =) (look at the attachments)");
          emailIntent.putExtra(Intent.EXTRA_STREAM, u);
          startActivity(Intent.createChooser(emailIntent, "Share with friends..."));
          
        }
    });  	
  	  	
	}
	
	
  @Override
  public void onBackPressed() {    
    System.out.println("DemoActivity::onBackPressed()");    
    // reset view
    current_collage_preview = null;    
    super.onBackPressed();
  }
  
  
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    if (progress_dialog != null)
      progress_dialog.dismiss();
  }  
  
  
  private File savebitmap(Bitmap bmp) {
    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    OutputStream outStream = null;
    String temp = new String(nickname + "_collage");
    File file = new File(extStorageDirectory, temp + ".png");
    if (file.exists()) {
      file.delete();
      file = new File(extStorageDirectory, temp + ".png");
    }
    
    try {
      outStream = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
      outStream.flush();
      outStream.close();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return file;
  }
  
  
  /**
   * Showing Dialog
   * */  
  protected ProgressDialog onCreateProgressDialog() {
    
    ProgressDialog pDialog = new ProgressDialog(this);
    pDialog.setMessage("Downloading file. Please wait...");
    pDialog.setIndeterminate(false);
    pDialog.setMax(100);
    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    pDialog.setCancelable(true);
    pDialog.show();
    return pDialog;
  }  
  
}