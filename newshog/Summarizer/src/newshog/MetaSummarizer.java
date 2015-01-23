package newshog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import newshog.lexicalchaining.LexicalChain;
import newshog.lexicalchaining.LexicalChainingSummarizer;
import newshog.preprocess.DocProcessor;
import newshog.textrank.TextRankSummarizer;
import newshog.util.StringUtil;

import java.util.logging.*;

public class MetaSummarizer extends AbstractSummarizer{
	DocProcessor dp ;
	TextRankSummarizer textRank;
	LexicalChainingSummarizer lcs;
	
	public MetaSummarizer() throws Exception
	{
		Logger.getAnonymousLogger().info("Initializing Meta Summarizer");
		dp = new DocProcessor();
		textRank = new TextRankSummarizer();
		lcs = new LexicalChainingSummarizer();
	}

	// Rank sentences by merging the scores from lexical chaining and text rank..
	// maxWords -1 indicates rank all sentences..
	public List<Score> rankSentences(String article, List<Sentence> sent, int maxWords)
	{
		List<LexicalChain> lc = lcs.buildLexicalChains(article, sent);
		Collections.sort(lc);
		Hashtable<Integer, Score> sentScores = new Hashtable<Integer, Score>();
		try{
	        List<Score> scores = textRank.rankSentences(article, sent, dp, article.length());
	        for(Score s: scores) sentScores.put(s.getSentId(), s);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		Hashtable<Sentence, Boolean> summSents = new Hashtable<Sentence,Boolean>();
		List<Score> finalSc = new ArrayList<Score>();
		int currWordCnt = 0;
		for(int i=lc.size()-1;i>=0;i--)
		{
			LexicalChain l = lc.get(i);
			boolean added =false;
			while(l.getSentences().size()>0)
			{
				int sentId = lcs.getBestSent(l, sentScores);
				if(sentId == -1) break;
				
				Sentence s = sent.get(sentId);
				
				//Sentence already added, try again..
				if(summSents.containsKey(s)) 
					l.getSentences().remove(s);
				else{
					finalSc.add(sentScores.get(s.getSentId()));
					summSents.put(s, true);
					currWordCnt += s.getWordCnt();
					break;
				}
			}
			if(maxWords>0 && currWordCnt>maxWords) break;
		}
		
		order(finalSc);
		return finalSc;
	}
	
	//Default Summarization using only lexical chains..
	public String summarize(String article, int maxWords)
	{
		//Build lexical Chains..
		List<Sentence> sent = dp.getSentencesFromStr(article);
		List<Score>finalSc = rankSentences(article, sent, maxWords);

		StringBuilder sb = new StringBuilder();
		for(int i=0;i<finalSc.size();i++) 
		{
			String line = StringUtil.fixFormatting(sent.get(finalSc.get(i).getSentId()).toString().trim());
			sb.append(line +".. ");
		}
		// Pick sentences		
		return sb.toString();
	}

	public static void main(String[] args)
	{
		try{
			DocProcessor dp =new DocProcessor();
			Logger l = Logger.getAnonymousLogger();
			MetaSummarizer lcs = new MetaSummarizer();
			String article = dp.docToString("test/tax.txt");
			long strt = System.currentTimeMillis();
			System.out.println(lcs.summarize(article, 50));
			System.out.println(System.currentTimeMillis() - strt);
			
			article = dp.docToString("test/houston-rep-nopara.txt");
			strt = System.currentTimeMillis();
			System.out.println(lcs.summarize(article, 100));
			System.out.println(System.currentTimeMillis() - strt);
	
			article = dp.docToString("gunman.txt");
			strt = System.currentTimeMillis();
			System.out.println(lcs.summarize(article, 50));
			System.out.println(System.currentTimeMillis() - strt);
			
			article = dp.docToString("satellite.txt");
			strt = System.currentTimeMillis();
			System.out.println(lcs.summarize(article, 50));
			System.out.println(System.currentTimeMillis() - strt);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
