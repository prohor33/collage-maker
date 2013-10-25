package prohor33.redmadrobot.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CollageMaker {
	
	public String GimmeCollage(String user_name) {
		String html_code = LoadHtmlCode(user_name);
	    if (html_code.isEmpty()) {
	    	System.out.println("Error: No internet connection?");
	    	return new String();
	    }
	    FindAndSortAllImageLinks(html_code);
		return new String();
	}
	
	protected String LoadHtmlCode(String UserName) {
	    String htmlCode = new String();
	    try{
	    	URL url = new URL("http://www.instagram.com/" + UserName);
	    	URLConnection connection = (URLConnection)url.openConnection();
	    	
	    	// to download desktop page
	    	connection.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)" +
	    			" AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4" );
	    	
	    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	    	String inputLine;

	    	while ((inputLine = in.readLine()) != null) {
		      htmlCode += inputLine;
		      //System.out.println("html: " + inputLine);
		    }

	    	in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Error: " + e.getMessage());
	        System.out.println("HTML CODE: " + htmlCode);
	    }
	    return htmlCode.toString();		
	}
	
	protected SortedMap<Integer, String> FindAndSortAllImageLinks(String html_code) {
	    int curr_index = 0;
	    SortedMap<Integer, String> MyPhotoMap = new TreeMap<Integer, String>();
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
			
			MyPhotoMap.put(likes, link);
	    }
	    for(Map.Entry<Integer, String> entry : MyPhotoMap.entrySet()) {
	        System.out.println(entry.getValue());
	    } // outputs from smaller to bigger		
	    return MyPhotoMap;
	}
	
}
