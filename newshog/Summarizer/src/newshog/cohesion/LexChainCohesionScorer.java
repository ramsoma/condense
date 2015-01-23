package newshog.cohesion;

import java.util.List;

import newshog.Sentence;
import newshog.lexicalchaining.LexicalChain;

// Default cohesion calculator based on lexical chains in an article. Cohesion is simply calculated as the
// number of lexical chains both the sentences belong to (overlapping concepts)..
public class LexChainCohesionScorer implements CohesionScorer{
	
	List<LexicalChain> lexChains ;
	
	public LexChainCohesionScorer(List<LexicalChain> lexChains)
	{
		this.lexChains = lexChains;
	}
	
	@Override
	// Simply returns the number of 
	public double calculateCohesion(Sentence s1, Sentence s2) {
		// TODO Auto-generated method stub
		double ret = 0;
		if(s1==null || s2==null) return 1;
		for(LexicalChain lc: lexChains){
			if(lc.getSentences().contains(s1) && lc.getSentences().contains(s2)){
				ret++;
			}
		}
//		if(s1.getSentId() > s2.getSentId()) ret --;
		if(ret==0) ret-=5;
		return ret/(1 + Math.min(s1.getWordCnt(), s2.getWordCnt()));
	}
}
