package prohor33.redmadrobot.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class CollageMaker {
	
	public String GimmeCollage(String user_name) throws IOException {
		String html_code = LoadHtmlCode("http://www.instagram.com/" + user_name);
	    
		if (html_code.isEmpty()) {
	    	throw new IOException("No internet connection or wrong nickname");	    	
	    }
	    
		SortedMap<Integer, String> photo_map;
		photo_map = FindAndSortAllImageLinks(html_code);
		
		if (photo_map.size() < 1) {
	    	throw new IOException("Error: There is no photos on the "
	    		+ user_name + "'s page");
	    }
		
		Bitmap collage = MakeCollageFromBitmapes(photo_map, 6, 2, 0.5f);
		
		return new String();
	}
	
	protected String LoadHtmlCode(String link) throws IOException {
	    String html_code = new String();
	    try{
	    	URL url = new URL(link);
	    	URLConnection connection = (URLConnection)url.openConnection();
	    	
	    	// to download desktop page
	    	connection.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)" +
	    			" AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4" );
	    	
	    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	    	String inputLine;

	    	while ((inputLine = in.readLine()) != null)
	    		html_code += inputLine;

	    	in.close();
	    } catch (Exception e) {
	    	throw new IOException(e.getMessage());	        
	    }
	    return html_code.toString();		
	}
	
	protected SortedMap<Integer, String> FindAndSortAllImageLinks(String html_code) {
	    int curr_index = 0;
	    SortedMap<Integer, String> my_photo_map = new TreeMap<Integer, String>();
	    while (true) {
	    	int ind_link = html_code.indexOf("\"link\":\"", curr_index);
			if (ind_link < 0)
				break;            		    	
	    	int ind_link_end = html_code.indexOf("\"", ind_link+8);
	    	String link = html_code.substring(ind_link+8, ind_link_end);
	    	System.out.println("link = " + link);
			int ind_likes = html_code.indexOf("\"likes\":{\"count\":", ind_link);
			int ind_data = html_code.indexOf(",\"data\":", ind_likes);	            			
			curr_index = ind_data;
//    			System.out.println("ind_likes = " + ind_likes +
//    					" ind_data = " + ind_data);    			
			String likes_q = html_code.substring(ind_likes+17, ind_data);	            			
			Integer likes = Integer.valueOf(likes_q);	            			
			System.out.println("likes = " + likes);
			
			my_photo_map.put(likes, link);
	    }
	    for(Map.Entry<Integer, String> entry : my_photo_map.entrySet()) {
	        System.out.println(entry.getValue());
	    } // outputs from smaller to bigger		
	    return my_photo_map;
	}
	
	protected Bitmap FindAndLoadImage(String link_source) throws IOException {
			
		// need to replace all \/ -> /
		link_source = link_source.replaceAll("\\\\", "");
		
		String html_source = LoadHtmlCode(link_source);
		if (html_source.isEmpty()) {
			throw new IOException("Lost internet connection");			
		}
		
		String fnd_str = new String("<meta property=\"og:image\" content=\"");
		int image_link_start = html_source.indexOf(fnd_str);
		image_link_start += fnd_str.length();
		System.out.println("image_link_start: "+image_link_start);
		
		int image_link_end = html_source.indexOf(
				"\"", image_link_start);
		System.out.println("image_link_end: "+image_link_end);
		
		String image_link = html_source.substring(image_link_start,
				image_link_end);
		
		System.out.println("Link: "+image_link);
		
		Bitmap bitmap = loadBitmap(image_link);
		System.out.println(bitmap.getHeight());
		
		return bitmap;
	}
	
	protected static Bitmap loadBitmap(String url) throws IOException {
		Bitmap bitmap = null;		
		bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());		   		
	    return bitmap;
	}
	
	protected Bitmap MakeCollageFromBitmapes(SortedMap<Integer, String> photo_map,
			int collage_size, int size_y, float coef) throws IOException {
		
		collage_size = photo_map.size() >= collage_size ? collage_size : photo_map.size();
		int size_x = collage_size / size_y;
		int image_size = (int)(612*coef); // is it always true for the instagram?
		
		Bitmap bg = Bitmap.createBitmap(image_size * size_x,
				image_size * size_y, Config.ARGB_8888);
			            
	    Canvas comboImage = new Canvas(bg);
	    
	    Bitmap image;
	    int i = 0;
	    for (int x=0; x<size_x; x++) {
	    	for (int y=0; y<size_y; y++) {
	    		if (photo_map.size()-1-i >= 0) {
					image = FindAndLoadImage(photo_map.values().toArray()[photo_map.size()-1-i].toString());
					i++;
					comboImage.drawBitmap(image, size_x*x, size_y*y, null);
	    		}
	    	}
	    }
		
	    return bg;
	}
	
}
