package newshog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import newshog.cohesion.CohesionScorer;
import newshog.cohesion.LexChainCohesionScorer;
import newshog.lexicalchaining.LexicalChain;
import newshog.lexicalchaining.LexicalChainingSummarizer;
import newshog.preprocess.DocProcessor;
import newshog.readability.DefaultReadabilityScorer;
import newshog.readability.ReadabilityScorer;
import newshog.textrank.TextRankSummarizer;

public class DynamicProgrammingSummarizer extends AbstractSummarizer{

	//Just to make sure we are not brought to our knees by very large articles.
	public static final int MAX_NUMBER_SENTENCES = 1000;
	DocProcessor dp =new DocProcessor();
	LexicalChainingSummarizer lcs;
	TextRankSummarizer textRank;

	int maxWords;
	Solution[][] memoArr;
	Solution[] best;
	List<Sentence> sentences;
	List<Score> initialScores;
	List<LexicalChain> lexChains;
	ReadabilityScorer rs;
	CohesionScorer cs;
	Hashtable<Sentence,List<LexicalChain>> sentLex;
	
	private long lexTotSent = 0;
	
	//Weights to calculate score..
	private double wtpgRnk = 10d;
	private double wtRead = 0.3d;
	private double wtInfo = 20d;
	private double wtCoh = 2d;
	private double wtRed = 1d;
	
	public DynamicProgrammingSummarizer() throws Exception{
		dp = new DocProcessor();
		lcs = new LexicalChainingSummarizer();
		textRank =  new TextRankSummarizer();
	}
	
	public DynamicProgrammingSummarizer(List<Sentence> sentences, List<Score> scores, 
										List<LexicalChain> chains, int maxWords)
	{
		best = new Solution[maxWords];
		memoArr = new Solution[maxWords][Math.min(sentences.size(), MAX_NUMBER_SENTENCES)];
		sentLex = new Hashtable<Sentence, List<LexicalChain>>();
		this.maxWords = maxWords;
		this.lexChains = chains;
		this.initialScores = scores;
		this.sentences = sentences;
		for(LexicalChain ls : chains) lexTotSent += ls.getSentences().size();
		loadLookup();
	}
		
	public void loadLookup(){
		for(Sentence s: sentences){
			List<LexicalChain> c = new ArrayList<LexicalChain>();
			for(LexicalChain lc: lexChains){
				if(lc.getSentences().contains(s))
					c.add(lc);
			}
		 this.sentLex.put(s, c);
		}	
	}
	
	public List<Sentence> summarize()
	{		
		int maxSent = Math.min(sentences.size(), this.MAX_NUMBER_SENTENCES);
		
		for(int i=0;i<maxWords;i++){
			//initialize the best to the old best..
			if(i==0) best[i] = new Solution();
			else best[i] = new Solution(best[i-1]);
			
			for(int j=0;j<maxSent;j++){
				Sentence s = sentences.get(j);
				//This sentence cannot be added to the solution yet.
				if(s.getWordCnt() > i) {
					memoArr[i][j] = new Solution(best[Math.max(i-1, 0)]);
					continue;
				}
				else{
					double addScore = best[i-1].score + score(best[i-s.getWordCnt()], s);
					
					if(i>0 && best[i-1].score < addScore)
					{
						Solution newBest = new Solution(best[i-s.getWordCnt()], s, this.sentLex.get(s), addScore);
						if(best[i].score <newBest.score)
							best[i] = newBest;  
					}
				}
			}
		}		
		return orderSentences(best[maxWords-1].sent);
	}

	//Calculate the score by adding this sentence to the solution..
	private double score(Solution solution, Sentence s) {
		// TODO Auto-generated method stub
		Sentence lastSent = solution.lastSent(s.getSentId());
		double pageRnkScr = s.getPageRankScore().score;
		double cohesionScr = cs.calculateCohesion(lastSent, s);
		double readabilityScr = rs.getReadability(s);
		//redundancy
		double redundancy = getRedundancy(solution, s);
		double infoAdded = getInfoAdded(solution, s);
		
		double score = wtpgRnk * pageRnkScr + wtInfo * infoAdded + wtRead * readabilityScr 
						+ wtCoh * cohesionScr - wtRed * redundancy ;
		
		return score;
	}
	
	//How much more "information" is added based on the new lexical chains covered and their sizes.. 
	public double getInfoAdded(Solution s, Sentence newS)
	{
		double ret = 0;
		List<LexicalChain> chains = this.sentLex.get(newS);
		for(LexicalChain lc: chains){
			if(!s.lexicalChains.contains(lc))
				ret += ((double)lc.getSentences().size())/(sentences.size());
		}
		return ret * newS.getPageRankScore().score;
	}
	
	private double getRedundancy(Solution solution, Sentence s) {
		// TODO Auto-generated method stub
		double ret = 0;
		for(Sentence cs: solution.sent){
			if(cs.getSentId() == s.getSentId())
				return 100;
		}

		List<LexicalChain> sentChains = this.sentLex.get(s);
		for(LexicalChain c : sentChains)
		{
			if(solution.lexicalChains.contains(c)) 
				ret += (sentences.size())/((double)c.getSentences().size());
		}
		return ret;
	}

	public String summarize(String article, int maxWords){
		List<Sentence> sent = dp.getSentencesFromStr(article);
        List<Score> scores = textRank.rankSentences(article, sent, dp, article.length());
		List<LexicalChain> lc = lcs.buildLexicalChains(article, sent);
		Collections.sort(lc);

		this.sentences = sent;
		this.initialScores = scores;
		this.lexChains = lc;
		this.maxWords = 55;

		best = new Solution[maxWords];
		memoArr = new Solution[maxWords][Math.min(sentences.size(), MAX_NUMBER_SENTENCES)];
		sentLex = new Hashtable<Sentence, List<LexicalChain>>();
		for(LexicalChain ls : lc) lexTotSent += ls.getSentences().size();
		loadLookup();

		rs = new DefaultReadabilityScorer();
		cs = new LexChainCohesionScorer(lc);

		List<Sentence> summ = summarize();
		StringBuffer buf = new StringBuffer();

		for(Sentence s: summ)
			buf.append(s.getStringVal());
		return buf.toString();
	}
	
	public static void main(String[] args)
	{
		try{
			DocProcessor dp =new DocProcessor();
			Logger l = Logger.getAnonymousLogger();
			String article = dp.docToString("test/tax.txt");
			DynamicProgrammingSummarizer dps = new DynamicProgrammingSummarizer();
			dps.summarize(article, 60);
			List<Sentence> summ = dps.summarize();
			for(Sentence s: summ)
				System.out.println(s.getWordCnt() +" "+s.getStringVal());
			long strt = System.currentTimeMillis();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

class Solution{
	List<Sentence> sent;
	double score;
	List<LexicalChain> lexicalChains;
	
	Solution(){
		sent = new ArrayList<Sentence>();
		lexicalChains = new ArrayList<LexicalChain>();
		score = 0;
	}
	
	//Copy constructor..
	Solution(Solution sol)
	{
		this();
		sent.addAll(sol.sent);
		lexicalChains.addAll(sol.lexicalChains);
		this.score = sol.score;	
	}
	
	Solution(Solution oldSol, Sentence s, List<LexicalChain> newChains, double score){
		this();
		sent.addAll(oldSol.sent);
		lexicalChains.addAll(oldSol.lexicalChains);
		sent.add(s);
		this.lexicalChains.addAll(oldSol.lexicalChains);
		this.lexicalChains.addAll(newChains);
		this.score = score;
	}
	
	Sentence lastSent(int id)
	{
		if(sent.size()==0) return null;
		Sentence r = null;
		for(Sentence cs: sent){
			if(r!=null && cs.getSentId() < id && r.getSentId() < cs.getSentId())
				r = cs;
		}
		return r;
	}
}
