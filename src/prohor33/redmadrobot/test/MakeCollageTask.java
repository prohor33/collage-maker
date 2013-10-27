package prohor33.redmadrobot.test;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class MakeCollageTask extends AsyncTask<String, Void, String> {
  
  public MakeCollageTask(MainActivity main_activity) {
    this.main_activity = main_activity;
  }
  
  Bitmap collage;
  MainActivity main_activity;
  
  @Override
  protected void onPreExecute(){
    
    main_activity.async_task = this;
    
    main_activity.progress_dialog = main_activity.onCreateProgressDialog();
    main_activity.progress_dialog.setMax(main_activity.images_quantity.value);
    
  }
  

  @Override
  protected String doInBackground(String... urlStr){                
      // do stuff on non-UI thread
      
    String exception_mess = new String();
    CollageMaker collage_maker = new CollageMaker(main_activity.images_quantity.value, this);
    
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

    main_activity.progress_dialog.dismiss();
    main_activity.progress_dialog = null;             
    
   if (result.length() != 0) {
     main_activity.messageBox("Internet Connection Problem", result);
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
     
     main_activity.current_collage_preview = preview;
     main_activity.current_collage = collage;
    
     main_activity.collage_file = main_activity.savebitmap(collage);
    
     System.out.println("Start changing view...");
    
     main_activity.CreateImageView(preview);

    }
  }
}
