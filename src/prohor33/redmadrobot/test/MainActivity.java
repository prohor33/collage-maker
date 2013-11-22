package prohor33.redmadrobot.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	protected static Bitmap current_collage_preview;
	protected static Bitmap current_collage;
	protected static ProgressDialog progress_dialog;
	protected static AsyncTask<String, Void, String> async_task;	
	protected static String nickname;
	protected static File collage_file;
	protected static MakeCollageTask make_collage_task;	
	protected static ImagesQuantity images_quantity;
	
	protected Handler mHandler = new Handler();
	
	protected Runnable mUpdateTimeTask = new Runnable() {
    public void run() {
      System.out.println("mUpdateTimeTask");
      if (make_collage_task == null || make_collage_task.isCancelled())        
        AdBuddiz.getInstance().showAd();      
      mHandler.postDelayed(this, (long)(30000 + Math.random()*20000));
    }
 };	
	
  protected enum ImagesQuantity {
    Im_3(0, 3),
    Im_6(1, 6),
    Im_9(2, 9),
    Im_12(3, 12),    
    Im_20(4, 20),
    Im_40(5, 40),
    Im_100(6, 100);
    
    public Integer index;
    public Integer value;    
    
    private ImagesQuantity(int index, int value) {
      this.index = index;      
      this.value = value;            
    }   
  };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AdBuddiz.getInstance().cacheAds(this);
		
		mHandler.removeCallbacks(mUpdateTimeTask);
    mHandler.postDelayed(mUpdateTimeTask, (long)(30000 + Math.random()*20000));
		
				
		if (make_collage_task != null)
		  make_collage_task.main_activity = this;
		
		if (current_collage_preview != null)		  
			CreateImageView(current_collage_preview);
				
		if (progress_dialog != null) {
		  
		  int p = progress_dialog.getProgress();
		  
      progress_dialog = onCreateProgressDialog();
      progress_dialog.setMax(images_quantity.value);
      progress_dialog.setProgress(p);      
		}
		
		if (images_quantity == null)
		  images_quantity = ImagesQuantity.Im_12;		
		
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
                
      	System.out.println("GiveMeCollage button click");
        
      	if (make_collage_task != null && !make_collage_task.isCancelled())
      	  make_collage_task.cancel(true);
      	
      	make_collage_task = new MakeCollageTask(MainActivity.this);
      	make_collage_task.execute(nickname);
     	
      }
        
    });
    
	}

  @Override
  protected void onStart() {
    super.onStart();
    AdBuddiz.getInstance().onStart(this);
  }
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
    //respond to menu item selection
	  
	  switch (item.getItemId()) {
      case R.id.action_settings:      
      
      onCreateAlertDialog().show();        
        
      return true;
      default:
      return super.onOptionsItemSelected(item);
	  }
	}
	

  /**
   * Showing Settings Dialog
   * */  
  protected AlertDialog onCreateAlertDialog() {
  
    List<String> listItems = new ArrayList<String>();

    int num = 5;    
    for (int i=0; i<num; i++) {
      listItems.add(ImagesQuantity.values()[i].value.toString()+" images");
    }
    
    final CharSequence[] colors_radio = listItems.toArray(new CharSequence[listItems.size()]);
    
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
    .setTitle("Collage size")
    .setSingleChoiceItems(colors_radio, images_quantity.index, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        Toast.makeText(getApplicationContext(),
            "The selected size is "+colors_radio[which], Toast.LENGTH_LONG).show();
    
        //dismissing the dialog when the user makes a selection.
        dialog.dismiss();
        
        images_quantity = ImagesQuantity.values()[which];
      }
    
    });
    AlertDialog alertdialog = builder.create();
    return alertdialog;    
    
  }
  
	
	//*********************************************************
	//generic dialog, takes in the method name and error message
	//*********************************************************	
	public void messageBox(String method, String message)
	{
	    Log.d("EXCEPTION: " + method,  message);

	    AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
	    messageBox.setTitle(method);
	    messageBox.setMessage(message);
	    messageBox.setCancelable(false);
	    messageBox.setNeutralButton("OK", null);
	    messageBox.show();
	}
	
	void CreateImageView (Bitmap image_preview) {
	  
	  int orientation = MainActivity.this.getResources().getConfiguration().orientation;
	  
    RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout01);
    
    int image_view_id = 91; // random number (for simplicity)
    int button_send_email_id = 81; // random number (for simplicity)
    
    // create new "Send By Email" button
    Button bt = (Button)rl.findViewById(button_send_email_id);
    if (bt != null)
      rl.removeView(bt);    
    
    bt = new Button(MainActivity.this);                  
    bt.setText("Share With Friends");
    
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
    ImageView iv = (ImageView)rl.findViewById(image_view_id);
    if (iv != null)
      rl.removeView(iv);
      
    iv = new ImageView(MainActivity.this);
    iv.setImageBitmap(image_preview);  	
    
    iv.setBackgroundResource(R.drawable.white_back);
    iv.setPadding(4, 4, 4, 4);
  	 	
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
			
		
  	// button "Give Me Collage"
  	Button gmc_button = (Button) findViewById(R.id.GiveMeCollage);  
  	gmc_button.setText("Give Me Another One");
  	gmc_button.setTextSize(15);
  	
  	
  	// set OnClick listener for "Send By Email" button
    Button send_email_button = (Button) findViewById(button_send_email_id);
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
              "! Here I have tiny present for you! =) (look at the attachments)\n\n" +
              "Done with \"Gimme Collage\" application. " +
              "Check it out on the "+
              "https://play.google.com/store/apps/details?id=prohor33.redmadrobot.test");

          emailIntent.putExtra(Intent.EXTRA_STREAM, u);
          startActivity(Intent.createChooser(emailIntent, "Share with friends..."));
          
        }
    });
    
    // text "Type instagram nickname"
    TextView tw = (TextView) findViewById(R.id.label);
    if (tw != null) {
      if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        tw.setTextSize(14);
    }
	}
	
	
  @Override
  public void onBackPressed() {    
    System.out.println("DemoActivity::onBackPressed()");    
    // reset view
    current_collage_preview = null;
    mHandler.removeCallbacks(mUpdateTimeTask);
    super.onBackPressed();
  }
  
  
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    if (progress_dialog != null)
      progress_dialog.dismiss();
  }  
  
  
  File savebitmap(Bitmap bmp) {
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
   * Showing Progress Dialog
   * */  
  protected ProgressDialog onCreateProgressDialog() {
    
    ProgressDialog pDialog = new ProgressDialog(this);
    pDialog.setMessage("Downloading images...");
    pDialog.setIndeterminate(false);
    pDialog.setMax(100);
    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    pDialog.setCancelable(true);
    pDialog.show();
    
    pDialog.setOnCancelListener(
        new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
              System.out.println("onCancel");
                async_task.cancel(true);
                progress_dialog = null;
            }
        }
    );
    
    return pDialog;
  }  
  
}