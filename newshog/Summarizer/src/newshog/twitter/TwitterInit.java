package newshog.twitter;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import twitter4j.auth.AccessToken;

public class TwitterInit {

	protected AccessToken accessToken;
	public TwitterInit()
	{
		try{
	        Properties prop = new Properties();
	        OutputStream os = null;
	        os = new FileOutputStream("twitter4j.properties");
	        
	        prop.setProperty("oauth.consumerKey", "h8A0EnCjreot6mlD4GLtSg");
	        prop.setProperty("oauth.consumerSecret", "stHlIvllW0q3Kby5Pt58tMs7blhUu3EJZhMIlVOdXM");
	    	prop.store(os, "twitter4j.properties");
	    	accessToken = new AccessToken("1584407546-ECGIibl92BWxfSximE0fCCu21Tl1XVJHCEwOGWl", 
			"S6FcVqdAqc6j7SmdEZCdgBlrVCf3C6o7hHCzdjiw")	;
	    	}catch(Exception ex)
		{
		}		
	}
}
