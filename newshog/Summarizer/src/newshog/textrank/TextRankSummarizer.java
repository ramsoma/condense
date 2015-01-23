package newshog.textrank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import newshog.*;
import newshog.dbpedia.DBPediaQuery;
import newshog.preprocess.DocProcessor;
import newshog.preprocess.IDFWeight;
import newshog.preprocess.WordWeight;
import newshog.twitter.TwitterSearch;
import newshog.twitter.TwitterSearchResults;
import twitter4j.Status;

public class TextRankSummarizer
{
    StanfordNER ner;
    DBPediaQuery dbp;
    String idfFile = "resources/idf.csv";
    
    public TextRankSummarizer() throws Exception
    {
    }
    
    public static int min(int s1, int s2)
    {	
    	return s1 < s2 ? s1:s2;
    }

    private static List<Score> reRank(List<Sentence> sentences, List<Score> rankedScores, 
    		Hashtable<String, List<Integer>> iidx, WordWeight wordwt, int noOfWords)
    {
    	List<Score> ret = new ArrayList<Score>();
    	Hashtable<Integer, Boolean> addedSents = new Hashtable<Integer, Boolean>();
    	Hashtable<String, Boolean> addedWords = new Hashtable<String, Boolean>();
    	
    	//Calculate doc wordWt
    	double docWordWt = 0;
    	Enumeration<String> docWrds= iidx.keys();
    	
    	double maxPageRankScore = rankedScores.get(0).getScore();
    	while(docWrds.hasMoreElements())
    	{
    		String wrd = docWrds.nextElement();
    		//If word is not there in iidx, it is probably a stop word..
    		if(iidx.get(wrd)!=null) 
    			docWordWt += wordwt.getWordWeight(wrd) * iidx.get(wrd).size();    		
    	}
    	int currWrds = 0;
    	while(currWrds < noOfWords && rankedScores.size() >0)
    	{
        	int bestSent = 0;
        	double bestScore = 0;

        	for(Score sc:rankedScores)
    		{    			
    			if(addedSents.get(sc.getSentId())!=null) continue; 
    			//Doing for each loop :(
    			Sentence sent = sentences.get(sc.getSentId());
    			String[] wrds = sent.getStringVal().split(" ");
    			double currScore = sc.getScore()/maxPageRankScore;
    			
    			for(String wrd : wrds)
    			{
    				if(addedWords.get(wrd)==null && iidx.get(wrd)!=null)
    				{
    					currScore += (wordwt.getWordWeight(wrd) * iidx.get(wrd).size())/docWordWt;
    				}
    			}
    			if(currScore > bestScore)
    			{
    				bestScore = currScore;
    				bestSent = sc.getSentId();
    			}
    		}
    		Score bestScr = new Score();
    		bestScr.setSentId(bestSent);
    		bestScr.setScore(bestScore);
    		ret.add(bestScr);
    		addedSents.put(bestSent, true);
    		
    		//update added words and docWt
			Sentence sent = sentences.get(bestSent);
			String[] wrds = sent.getStringVal().split(" ");
    		currWrds += wrds.length;

    		for(String wrd : wrds)
			{
				if(!addedWords.contains(wrd) && iidx.contains(wrd))
				{
					docWordWt -= wordwt.getWordWeight(wrd) * iidx.get(wrd).size();
					addedWords.put(wrd, true);
				}
			}
    	}
    	return ret;
    }

    public List<TwitterSearchResults> getTwitterResults(List<Entity> entities, String doc) throws Exception
    {
    	List<TwitterSearchResults> ret = new ArrayList<TwitterSearchResults>();
    	TwitterSearch ts = TwitterSearch.getInstance();
    	for(int i=0;i< Math.min(1, entities.size());i++)
    		ret.add(ts.getResults(entities.get(i).getFullName(), -1));

    	return ret;
    }
    
    public List<Score> rankSentences(String doc, List<Sentence> sentences, 
    							   DocProcessor dp, int maxWords )
    { 
        try {            
    	    //Rank sentences	
            TextRank summ = new TextRank();
            List<String> sentenceStrL = new ArrayList<String>();
            List<String> processedSent = new ArrayList<String>();
            Hashtable<String, List<Integer>> iidx = new Hashtable<String, List<Integer>>();
       //     dp.getSentences(sentences, sentenceStrL, iidx, processedSent);
            
            for(Sentence s : sentences){           	
            	sentenceStrL.add(s.getStringVal());
            	String stemmedSent = s.stem();
            	processedSent.add(stemmedSent);
            	
            	String[] wrds = stemmedSent.split(" ");
            	for(String w: wrds)
            	{
            		if(iidx.get(w)!=null) 
            			iidx.get(w).add(s.getSentId());
            		else{
            			List<Integer> l = new ArrayList<Integer>();
            			l.add(s.getSentId());
            			iidx.put(w, l);
            		}
            	}
            }        
           
            WordWeight wordWt = new IDFWeight(idfFile);////new 
            
    	    List<Score> finalScores = summ.getRankedSentences(doc, sentenceStrL, iidx, processedSent);
    	    List<String> sentenceStrList = summ.getSentences();
    	    
    	   // SentenceClusterer clust = new SentenceClusterer();
    	   //  clust.runClusterer(doc, summ.processedSent);
                
    		Hashtable<Integer,List<Integer>> links= summ.getLinks();

			for(int i=0;i<sentences.size();i++)
			{
				Sentence st = sentences.get(i);
				
				//Add links..
				List<Integer> currLnks = links.get(i);
				if(currLnks==null) continue;
				for(int j=0;j<currLnks.size();j++)
				{
					if(j<i) st.addLink(sentences.get(j));	
				}
			}
			
			for(int i=0;i<finalScores.size();i++)
			{
				Score s = finalScores.get(i);
				Sentence st = sentences.get(s.getSentId());
				st.setPageRankScore(s);
			}

	//		System.out.println("----");
			List<Score> reRank = finalScores;//reRank(sentences, finalScores, iidx, wordWt, maxWords);
			
			return reRank;
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    //Returns the summary as a string. 
    public String getSummary(String text, int maxWords) {
        DocProcessor dp = new DocProcessor();
        List<Sentence> sentences = dp.getSentencesFromStr(text);        
        List<Score> scores = this.rankSentences(text, sentences, dp, maxWords);
        return scores2String(sentences, scores, maxWords);
	}
    
    public String scores2String(List<Sentence> sentences, List<Score> scores, int maxWords)
    {
        StringBuffer b = new StringBuffer();
       // for(int i=0;i< min(maxWords, scores.size()-1);i++)
        int i=0;
        while(b.length()< maxWords && i< scores.size())
        {
        	String sent = sentences.get(scores.get(i).getSentId()).getStringVal();
        	b.append(sent + scores.get(i));
        	i++;
        }
        return b.toString();
    }
    
    public static String formatLinksList(List<Sentence> lst)
    {
    	StringBuffer b = new StringBuffer();
    	for(Sentence s:lst) b.append(s.getSentId()+",");
    	return b.toString();
    }

}


/* Decay..
 *                 do{
                     bestScr = finalScores.get(next++);
                } while (chosenOnes.contains(bestScr.sentId));
                chosenOnes.add(bestScr.sentId);
                String nextBest = s.get(bestScr.sentId);

                System.out.println(nextBest + bestScr.score);
                String words[] = nextBest.split(" ");
                for(String word: words)
                {
                    double wt = 1;
                    String trimWrd = word.trim();
                    if(decayWts.get(trimWrd)!=null)
                        wt = decayWts.get(trimWrd);
                    wt = wt * 0.9;
                    decayWts.remove(trimWrd);
                    decayWts.put(trimWrd, wt);
                 }

 */
/*			
for(int i=0;i<min(maxLineSumm, finalScores.size()-1);i++)
{
     Score sc = finalScores.get(i);
     System.out.println(sc.sentId+". "+sentences.get(sc.sentId) + sc.score);//+ formatLinksList(sentences.get(sc.sentId).getLinks()));// + " "+sc.sentId+" "+sc.score+" "+summ.links.get(i));// +" - " +clust.getCluster(sc.sentId));
}
*/
/*
String fileName = null;
for(int i=0;i<args.length;i++)
{
	if(args[i].equals("-f") && (i+1)<args.length)
	  fileName = args[i+1];
}
	if(fileName==null)
{
		System.out.println("Please specify fileName ");
		return;
}
*/
