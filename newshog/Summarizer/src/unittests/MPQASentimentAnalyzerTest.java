package unittests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import newshog.*;
import newshog.sentimentanalysis.MPQASentimentAnalyzer;
import newshog.sentimentanalysis.Sentiment;

public class MPQASentimentAnalyzerTest extends TestCase {

	public void testAnalyzeListOfString() {
		try{
			MPQASentimentAnalyzer sentA = MPQASentimentAnalyzer.getInstance("subjclueslen1-HLTEMNLP05.tff");
			String t1 = "Same mfs tweeting that George Zimmerman that he petty for killing a black person the " +
							"same mfs tweeting today I'm Finna go shoot some opps";
			String t2 = "Casey Anthony And George Zimmerman Not Guilty For Killing Kids. " +
							"Meanwhile Mike Vick kills A Dog & Is Guilty, White Peo… ";
			String t3 = "I'm back to Tim Lincecum. I've been waiting since 2008. I've been a fan since 2008.";
			String t4 = "@m_lewis30 - @JSwanson28 the Yankees are terrible";
			
			Sentiment s1 = sentA.analyze(t1);
			Sentiment s2 = sentA.analyze(t2);
			Sentiment s3 = sentA.analyze(t3);
			Sentiment s4 = sentA.analyze(t4);
			
			assertEquals(s1.getCategory(), Sentiment.NEGATIVE);
//			assertEquals(s2.getCategory(), Sentiment.POSITIVE);
			assertEquals(s3.getCategory(), Sentiment.POSITIVE);
			assertEquals(s4.getCategory(), Sentiment.NEGATIVE);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
