package newshog.twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import newshog.sentimentanalysis.MPQASentimentAnalyzer;
import newshog.sentimentanalysis.Sentiment;
import newshog.sentimentanalysis.SentimentAnalyzer;
import twitter4j.HashtagEntity;
import twitter4j.Status;

public class TwitterSearchResults {
	private List<Status> topTweets;
	private int positiveCnt, negativeCnt;
	private SentimentAnalyzer sa;
	
	public TwitterSearchResults()
	{
		topTweets = new ArrayList<Status>();
		try{
			sa = MPQASentimentAnalyzer.getInstance("subjclueslen1-HLTEMNLP05.tff");
		}catch(Exception ex){}
	}
	
	public List<Status> getTopTweets()
	{
		return topTweets;
	}
	
	public List<String> getTopHashtags(int cnt)
	{
		List<String> results = new ArrayList<String>();
		ArrayList<ResultsHashtag> hashtags = new ArrayList<ResultsHashtag>();
		for(int idx = 0; idx < topTweets.size();idx++)
		{
			Status s = topTweets.get(idx);
			HashtagEntity[] entities = s.getHashtagEntities();
			for(int i=0;i< entities.length;i++)
			{
				HashtagEntity entity = entities[i];
				ResultsHashtag t = new ResultsHashtag(); t.tag = entity.getText();
				t.cnt =1;
				if(hashtags.contains(t))
				{
					hashtags.get(hashtags.indexOf(t)).cnt++;
				}
				else hashtags.add(t);
			}			
		}

		Collections.sort(hashtags);
		for(int i=0;i<Math.min(hashtags.size(), cnt);i++)
			results.add(hashtags.get(i).tag);
		
		return results;
	}
	
	public double getPositivePct()
	{
		return 100* (double)getPositiveCnt() / (double) (getNegativeCnt() + getPositiveCnt());
	}

	public double getNegativePct()
	{
		return 100* (double)getNegativeCnt() / (double) (getNegativeCnt() + getPositiveCnt());	
	}	
	
	public void addResult(Status tweet)
	{
		topTweets.add(tweet);
	}

	public void setPositiveCnt(int positiveCnt) {
		this.positiveCnt = positiveCnt;
	}

	public int getPositiveCnt() {
		return positiveCnt;
	}

	public void setNegativeCnt(int negativeCnt) {
		this.negativeCnt = negativeCnt;
	}

	public int getNegativeCnt() {
		return negativeCnt;
	}

	public Sentiment getSentiment()
	{
		if(sa == null) return null;
		StringBuffer buff = new StringBuffer();
		for(Status s: this.topTweets)
		{
			buff.append(s.getText());
		}
		return sa.analyze(buff.toString());
	}
}

class ResultsHashtag implements Comparable{
	String tag;
	int cnt;
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		int ret = 0;
		if(o instanceof ResultsHashtag)
		{
			ResultsHashtag other = (ResultsHashtag)o;
			ret = cnt > other.cnt ? 1: (cnt == other.cnt? 0:-1);		
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ResultsHashtag ? tag.equals(((ResultsHashtag)o).tag): false;
	}
	
}
