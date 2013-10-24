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
            		    String htmlCode = new String();
            		    try{
            		    	//URL url = new URL( "http://www.thecinemas.aw/main/" );
            		    	URL url = new URL(urlStr[0]);
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

            		  @Override
            		  protected void onPostExecute(String htmlCode){
            		    // do stuff on UI thread with the html
            		    if (htmlCode.isEmpty()) {
            		    	System.out.println("Error: No internet connection?");
            		    	return;
            		    }
            		    	
            		    //System.out.println(htmlCode);
            		    int curr_index = 0;
            		    SortedMap<Integer, String> MyPhotoMap = new TreeMap<Integer, String>();
            		    while (true) {
            		    	int ind_link = htmlCode.indexOf("\"link\":\"", curr_index);
	            			if (ind_link < 0)
	            				break;            		    	
            		    	int ind_link_end = htmlCode.indexOf("\"", ind_link+8);
            		    	String link = htmlCode.substring(ind_link+8, ind_link_end);
            		    	System.out.println("link = " + link);
	            			int ind_likes = htmlCode.indexOf("\"likes\":{\"count\":", ind_link);
	            			int ind_data = htmlCode.indexOf(",\"data\":", ind_likes);	            			
	            			curr_index = ind_data;
//	            			System.out.println("ind_likes = " + ind_likes +
//	            					" ind_data = " + ind_data);    			
	            			String likes_q = htmlCode.substring(ind_likes+17, ind_data);	            			
	            			Integer likes = Integer.valueOf(likes_q);	            			
	            			System.out.println("likes = " + likes);
	            			
	            			MyPhotoMap.put(likes, link);
            		    }
            		    for(Map.Entry<Integer, String> entry : MyPhotoMap.entrySet()) {
            		        System.out.println(entry.getValue());
            		    } // outputs from smaller to bigger
            		  }
            		}.execute("http://www.instagram.com/tom");
            	
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