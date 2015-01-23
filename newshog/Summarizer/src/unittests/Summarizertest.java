package unittests;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import newshog.*;
import newshog.preprocess.Stemmer;
import newshog.textrank.TextRank;
import junit.framework.TestCase;

public class Summarizertest extends TestCase {

	public void testSummarizer() {
		fail("Not yet implemented");
	}

	public void testGetScore() {
		fail("Not yet implemented");
	}

	public void testGetWeightedSimilarity() {
		fail("Not yet implemented");
	}

	public void testGetCurrentScore() {
		fail("Not yet implemented");
	}

	public void testGetWeightedScore() {
		fail("Not yet implemented");
	}

	public void testGetRawScores() {
		System.out.println("Test start");
		TextRank s = new TextRank();
        Stemmer stemmer = new Stemmer();
        Hashtable<String, Double> wts = new Hashtable<String, Double>();
        Hashtable<String, List<Integer>> iidx = new Hashtable<String, List<Integer>>();
		List<String> sen = new ArrayList<String>();
		sen.add("whitney houston dies");
		sen.add("whitney was young");
		sen.add("houston was a druggie");
		List<Integer> whit = new ArrayList<Integer>(); whit.add(new Integer(1)); whit.add(new Integer(2));
		List<Integer> hous = new ArrayList<Integer>(); hous.add(new Integer(1)); hous.add(new Integer(3));
		List<Integer> dies = new ArrayList<Integer>(); dies.add(new Integer(1)); 
		stemmer.stem("whitney");
		iidx.put(stemmer.toString(),whit);
		
		stemmer.stem("houston");
		iidx.put(stemmer.toString(),hous);

		stemmer.stem("dies");
		iidx.put(stemmer.toString(),dies);
		
		final List<Score> ret = s.getNeighborsSigmaWtSim(sen, iidx, wts);

		for(Score sc : ret)
			System.out.println(sc.toString());
		
		System.out.println("Finished test..");
		//ret.clear();
	}

	public void testGetWeightedScores() {
		fail("Not yet implemented");
	}

	public void testGetRankedSentences() {
		fail("Not yet implemented");
	}

	public void testGetRankedSentencesFromFile() {
		fail("Not yet implemented");
	}

	public void testMin() {
		fail("Not yet implemented");
	}

}
