package prohor33.redmadrobot.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class CollageMaker {
  
  public CollageMaker(int max_collage_size,
      MakeCollageTask make_collage_task) {
    
    this.max_collage_size = max_collage_size;
    this.make_collage_task = make_collage_task;
  }
  
  class NoImagesSuchABigSize extends Exception
  {
        //Parameterless Constructor
        public NoImagesSuchABigSize() {}

        //Constructor that accepts a message
        public NoImagesSuchABigSize(String message)
        {
           super(message);
        }
   }
  
  private int max_collage_size;
  private MakeCollageTask make_collage_task;
  
  protected enum LoadingPercent {
    LoadHtmlCodeFunc(25),
    FindAndSortAllImageLinksFunc(10),
    MakeCollageFromBitmapesFunc(100-25-10);
    
    private int value;
    
    private LoadingPercent(int value) {
            this.value = value;
    }   
  };
  
  protected void SetProgress(LoadingPercent func, float percent) {
    int progress = 0;
    
    switch (func) {
    case MakeCollageFromBitmapesFunc:
      progress += (int)(LoadingPercent.FindAndSortAllImageLinksFunc.value*percent);
      percent = 1.0f;
    case FindAndSortAllImageLinksFunc:
      progress += (int)(LoadingPercent.FindAndSortAllImageLinksFunc.value*percent);
      percent = 1.0f;
    case LoadHtmlCodeFunc:
      progress += (int)(LoadingPercent.LoadHtmlCodeFunc.value*percent);
      percent = 1.0f;
      break;
    }
    
    make_collage_task.main_activity.progress_dialog.setProgress(progress);    
  }
  
  protected enum ImageSize {
    UNDEF('0'),
    LARGE('8'),
    MEDIUM('6'),
    SMALL('5');
    
    private char value;
    
    private ImageSize(char value) {
            this.value = value;
    }   
  };
	
  
	public Bitmap GimmeCollage(String user_name) throws IOException {
		String html_code = LoadHtmlCode("http://www.instagram.com/" + user_name);
	    
		if (html_code.isEmpty()) {
	    	throw new IOException("No internet connection or wrong nickname");	    	
	    }
	    
		SortedMap<Integer, List<String>> photo_map;
		photo_map = FindAndSortAllImageLinks(html_code);
		
		if (photo_map.size() < 1) {
	    	throw new IOException("Error: There is no photos on the "
	    		+ user_name + "'s page");
	    }
		
		
		int collage_size_x = 3;

    if (max_collage_size >= 6)
      collage_size_x = 3;
    if (max_collage_size >= 10)
      collage_size_x = 4;
    if (max_collage_size >= 20)
      collage_size_x = 5;
    if (max_collage_size >= 40)
      collage_size_x = 8;
    if (max_collage_size >= 100)
      collage_size_x = 10;
    
    Bitmap collage = null;
    ImageSize image_size = ImageSize.UNDEF;
    
    boolean exception_occure = false;
    while (true) {
      try {
        collage = MakeCollageFromBitmapes(photo_map,
            max_collage_size, collage_size_x, ImageSize.UNDEF);
      } catch(NoImagesSuchABigSize e) {
        exception_occure = true;
        if (image_size == ImageSize.MEDIUM)
          image_size = ImageSize.SMALL;        
        if (image_size == ImageSize.UNDEF)
          image_size = ImageSize.MEDIUM;
      }
      
      if (!exception_occure)
        break;
    }
		
		return collage;
	}
	
	
	protected String LoadHtmlCode(String link) throws IOException {
	    String html_code = new String();
	    try{
	    	URL url = new URL(link);
	    	URLConnection connection = (URLConnection)url.openConnection();
	    	
	    	// to download desktop page
	    	connection.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)" +
	    			" AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4" );
	    	
	    	//SetProgress(LoadingPercent.LoadHtmlCodeFunc, 0.1f);
	    	
	    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	    	//SetProgress(LoadingPercent.LoadHtmlCodeFunc, 0.8f);
	    	
	    	String inputLine;

	    	while ((inputLine = in.readLine()) != null)
	    		html_code += inputLine;

	    	//SetProgress(LoadingPercent.LoadHtmlCodeFunc, 0.9f);
	    	
	    	in.close();
	    } catch (Exception e) {
	    	throw new IOException(e.getMessage());	        
	    }
	    return html_code.toString();		
	}
	
	protected SortedMap<Integer, List<String>> FindAndSortAllImageLinks(String html_code) {
	    int curr_index = 0;
	    SortedMap<Integer, List<String>> my_photo_map = new TreeMap<Integer, List<String>>();
	    while (true) {
	      
	      //SetProgress(LoadingPercent.FindAndSortAllImageLinksFunc, (float)curr_index/html_code.length());
	      
	    	int ind_link = html_code.indexOf("\"link\":\"", curr_index);
  			if (ind_link < 0)
  				break;            		    	
  	    	int ind_link_end = html_code.indexOf("\"", ind_link+8);
  	    	String link = html_code.substring(ind_link+8, ind_link_end);
  
  			int ind_likes = html_code.indexOf("\"likes\":{\"count\":", ind_link);
  			int ind_data = html_code.indexOf(",\"data\":", ind_likes);	            			
  			curr_index = ind_data;
   			
  			String likes_q = html_code.substring(ind_likes+17, ind_data);	            			
  			Integer likes = Integer.valueOf(likes_q);
  			
  			if (my_photo_map.containsKey(likes))
  			  my_photo_map.get(likes).add(link);
  			else {  			   
  			  my_photo_map.put(likes, new ArrayList<String>());
  			  my_photo_map.get(likes).add(link);
  			}
	    }		
	    return my_photo_map;
	}
	
	protected Bitmap FindAndLoadImage(String link_source,
	    ImageSize image_size_level)
	    throws IOException, NoImagesSuchABigSize {
			
		// need to replace all \/ -> /
		link_source = link_source.replaceAll("\\\\", "");
		
		String html_source = LoadHtmlCode(link_source);
		if (html_source.isEmpty()) {
			throw new IOException("Lost internet connection");			
		}
		
		String fnd_str = new String("<meta property=\"og:image\" content=\"");
		int image_link_start = html_source.indexOf(fnd_str);
		image_link_start += fnd_str.length();
		
		int image_link_end = html_source.indexOf(
				"\"", image_link_start);
		
		String image_link = html_source.substring(image_link_start,
				image_link_end);
		
		Bitmap bitmap = null;
		
		StringBuilder image_link_b = new StringBuilder(image_link);
		
		image_link_b.setCharAt(image_link.length()-5, image_size_level.value);
		image_link = image_link_b.toString(); 
		
		System.out.println("Link: "+image_link);
		
		boolean exception_occure = false;
		try {
		  bitmap = loadBitmap(image_link);
		} catch(IOException e) {
      if (image_size_level != ImageSize.SMALL &&
          (e.getMessage().contains("8") || e.getMessage().contains("6"))) {
        throw new NoImagesSuchABigSize();        
      }  		  
      else {
        throw e;
      }
		}
		
		return bitmap;
	}
	
	protected static Bitmap loadBitmap(String url) throws IOException {
		Bitmap bitmap = null;		
		bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());		   		
	    return bitmap;
	}
	
	protected Bitmap MakeCollageFromBitmapes(SortedMap<Integer, List<String>> photo_map,
			int collage_size, int size_x, ImageSize image_size_level) throws IOException, NoImagesSuchABigSize {
		
	  // we should count how many photos do we have
	  int photos_q = 0;
	  for (int i=0; i<photo_map.size(); i++) {
	    List<String> list = (List<String>)photo_map.values().toArray()[photo_map.size()-1-i];
	    photos_q += list.size();  
	  }
	  
		collage_size = photos_q >= collage_size ? collage_size : photos_q;
		
		int size_y;
		if (collage_size < size_x) {
		  size_y = 1;
		  size_x = collage_size;
		}
		else
		  size_y = collage_size / size_x;
		
		if (image_size_level == ImageSize.UNDEF) {
  		image_size_level = ImageSize.SMALL;
  		
      if (collage_size <= 9)
        image_size_level = ImageSize.MEDIUM;		
  		if (collage_size <= 3)
  		  image_size_level = ImageSize.LARGE;
		}
				
		int image_size = 640;
		switch (image_size_level) {
		case LARGE:
		  image_size = 640;
		  break;
		case MEDIUM:
		  image_size = 306;
		  break;
		case SMALL:
		  image_size = 150;
		  break;
		}		
		
		Bitmap bg = Bitmap.createBitmap(image_size * size_x,
				image_size * size_y, Config.RGB_565);
			            
    Canvas comboImage = new Canvas(bg);
    
    Bitmap image;
    int i = 0;
    int list_i = 0;
    int photos_i = 0;
    for (int x=0; x<size_x; x++) {
    	for (int y=0; y<size_y; y++) {
    	  
    		if (photo_map.size()-1-i >= 0) {
    		  
    		  List<String> list = (List<String>)photo_map.values().toArray()[photo_map.size()-1-i];
    		  
    		  image = FindAndLoadImage(list.get(list_i), image_size_level);
    		  list_i++;
    		  photos_i++;
    		  if (list_i >= list.size()) {
    		    list_i = 0;
    		    i++;
    		  }
    		  
					comboImage.drawBitmap(image, image_size*x, image_size*y, null);
					
					//SetProgress(LoadingPercent.MakeCollageFromBitmapesFunc, (float)photos_i/collage_size);
					make_collage_task.main_activity.progress_dialog.setProgress(photos_i);
					
    		}	    		
    	}
    }
	
    return bg;
	}
	
}
