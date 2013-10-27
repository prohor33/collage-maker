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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
	
  protected enum ImagesQuantity {
    Im_3(3),
    Im_6(6),
    Im_9(9),
    Im_12(12),    
    Im_20(20),
    Im_40(40),
    Im_100(100);
        
    public Integer value;
    
    private int GetIndex() {
      // so stupid =(
      // TODO: make it right way      
      switch(this) {
      case Im_3: return 0;
      case Im_6: return 1;
      case Im_9: return 2;
      case Im_12: return 3;
      case Im_20: return 4;
      case Im_40: return 5;
      case Im_100: return 6;        
      }
      return -1;
    }
    
    private static ImagesQuantity GetByIndex(int index) {
      // TODO: make it right way
      switch(index) {
      case 0: return Im_3;
      case 1: return Im_6;
      case 2: return Im_9;
      case 3: return Im_12;
      case 4: return Im_20;
      case 5: return Im_40;
      case 6: return Im_100;        
      }
      return Im_3;
    }
    
    private ImagesQuantity(int value) {
            this.value = value;
    }   
  };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("Hey");
		
		if (make_collage_task != null)
		  make_collage_task.main_activity = this;
		
		if (current_collage_preview != null) {
			System.out.println("Create image view!");
			CreateImageView(current_collage_preview);
		}
		
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
        	         
      	make_collage_task = new MakeCollageTask(MainActivity.this);
      	make_collage_task.execute(nickname);
        	
        }
    });
    
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
   * Showing Alert Dialog
   * */  
  protected AlertDialog onCreateAlertDialog() {
  
    final CharSequence[] colors_radio={
        ImagesQuantity.Im_3.value.toString()+" images",
        ImagesQuantity.Im_6.value.toString()+" images",
        ImagesQuantity.Im_9.value.toString()+" images",
        ImagesQuantity.Im_12.value.toString()+" images",
        ImagesQuantity.Im_20.value.toString()+" images",
        ImagesQuantity.Im_40.value.toString()+" images",
        ImagesQuantity.Im_100.value.toString()+" images",
        };
    
    
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
    .setTitle("Collage size")
    .setSingleChoiceItems(colors_radio, images_quantity.GetIndex(), new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        Toast.makeText(getApplicationContext(),
            "The selected size is "+colors_radio[which], Toast.LENGTH_LONG).show();
    
        //dismissing the dialog when the user makes a selection.
        dialog.dismiss();
        
        images_quantity = ImagesQuantity.GetByIndex(which);
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