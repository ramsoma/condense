package newshog.twitter;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.text.DateFormatter;


public class TwitterSearch extends TwitterInit implements StatusListener{
	public static TwitterSearch inst;

	private TwitterSearch()
	{
		super();
	}
	
	public static TwitterSearch getInstance()
	{
		if(inst ==null) 
			inst = new TwitterSearch();
		
		return inst;
	}	
	
	public Twitter getConnection()
	{
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthAccessToken(accessToken);
        return twitter;
	}
	
	public TwitterSearchResults getResults(String searchStr, int days) throws Exception
	{
		TwitterSearchResults res = new TwitterSearchResults();
        try {
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        	Calendar c = Calendar.getInstance();
        	c.setTime(new Date());
        	c.add(Calendar.DATE, days);
        	String date = df.format(c.getTime());
        	String[] sentimentStrs = {""};//{":)",":("};
        	
        	for(int i=0;i<sentimentStrs.length;i++)
        	{
        		String sentimentStr = sentimentStrs[i];
        		Twitter twitter = getConnection();
        		Query query = new Query(searchStr+" "+sentimentStr+" since:"+date );
	            QueryResult result;
	            int cnt = 0;
	            do {
	                result = twitter.search(query);
	                List<Status> tweets = result.getTweets();	       
	                
	                for (Status tweet : tweets) {	                		
	                	res.addResult(tweet);
//	                	System.out.println(tweet.getText() +" "+tweet.getRetweetCount() +" "+tweet.isRetweet());
//	                	System.out.println(tweet.getUser().getFollowersCount() 
//	                			+" "+tweet.getUser().getFavouritesCount() + tweet.getUser().getFriendsCount());	                
	                }	                
	            } while ((query = result.nextQuery()) != null );//&& cnt++ <10
        	}
        } catch (Exception te) {
//            te.printStackTrace();
            throw new Exception("Failed to search tweets: " + te.getMessage());
        }	
        return res;
	}
	
	public void track(String[] terms)
	{
		FilterQuery fq = new FilterQuery();
		fq = fq.track(terms);
		TwitterStream ts = new TwitterStreamFactory().getInstance();
        ts.setOAuthAccessToken(accessToken);
		ts.addListener(this);
		ts.filter(fq);
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ts.shutdown();
	}
	
	public static void main(String[] args)
	{
		try{
			TwitterSearch inst = TwitterSearch.getInstance();
/*			
			TwitterSearchResults res = inst.getResults("Pro Bowl", -1);
			System.out.println(":) " + res.getPositivePct() +"%, :( " +res.getNegativePct() +"%");
			List<String> tags = res.getTopHashtags(10);
			for(String tag: tags)
				System.out.println(tag);
			System.out.println("-----");			
			int i=0;
			for(Status s:res.getTopTweets())
				System.out.println((i++)+": "+s.getText());
			System.out.println("-----");			
			System.out.println("-----");			
*/		
			////{"Britney Spears","Cyrus","Bieber","Kardashian"};//{"Sudan", "syria","NSA","AAP","Pro Bowl","Unemployment","egypt"};
			String[] terms = {"Cyrus"};
			inst.track(terms);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
		
	}

	int i=0;
	@Override
	public void onStatus(Status status) {
		// TODO Auto-generated method stub
		System.out.println((i++)+": "+status.getText());
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
		
	}
}

//if(sentimentStr.equals(":)"))
//	res.setPositiveCnt(tweets.size());
//else
//	res.setNegativeCnt(tweets.size());

