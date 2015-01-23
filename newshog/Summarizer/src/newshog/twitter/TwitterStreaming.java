package newshog.twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import twitter4j.*;

public class TwitterStreaming extends TwitterInit 
			implements StatusListener {
	
	TwitterStream twitterStream; 
	private List<Status> unreadStatus;
	
	public TwitterStreaming()
	{
		super();
		setUnreadStatus(new LinkedList<Status>());
	}
	
    @Override
    public void onStatus(Status status) {
    	getUnreadStatus().add(status);
  //      System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText() +" "+status.getCreatedAt());
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        System.out.println("Got stall warning:" + warning);
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
    }

    public void startListening(List<String> trackKeywords)
    {
		setUnreadStatus(new LinkedList<Status>());
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.setOAuthAccessToken(accessToken);
        twitterStream.addListener(this);    	
        String[] trackArray = new String[trackKeywords.size()];
        trackKeywords.toArray(trackArray);
        twitterStream.filter(new FilterQuery(0, null, trackArray));
    }

    private static boolean isNumericalArgument(String argument) {
        String args[] = argument.split(",");
        boolean isNumericalArgument = true;
        for (String arg : args) {
            try {
                Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
                isNumericalArgument = false;
                break;
            }
        }
        return isNumericalArgument;
    }
    
    public void stopListening()
    {
    	this.twitterStream.shutdown();
    }
    
	public static void main(String[] args) throws TwitterException {
        ArrayList<Long> follow = new ArrayList<Long>();
        ArrayList<String> track = new ArrayList<String>();
        
        for (String arg : args) {
            if (isNumericalArgument(arg)) {
                for (String id : arg.split(",")) {
                    follow.add(Long.parseLong(id));
                }
            } else {
                track.addAll(Arrays.asList(arg.split(",")));
            }
        }
        
        long[] followArray = new long[follow.size()];
        for (int i = 0; i < follow.size(); i++) {
            followArray[i] = follow.get(i);
        }
        String[] trackArray = track.toArray(new String[track.size()]);

        // filter() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        TwitterStreaming ts = new TwitterStreaming();
        List<String> keywords = new ArrayList<String>();
        keywords.add("zimmerman");
        keywords.add("mickelson");
        keywords.add("Detroit");
        keywords.add("Shinzo Abe");
        keywords.add("Yankees");
        ts.startListening(keywords);
        try {
			Thread.currentThread().sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ts.stopListening();
    }

	public List<Status> getUnreadStatus() {
		return unreadStatus;
	}

	public void setUnreadStatus(List<Status> unreadStatus) {
		this.unreadStatus = unreadStatus;
	}
}
