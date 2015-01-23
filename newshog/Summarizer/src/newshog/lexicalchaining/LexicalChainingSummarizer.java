package newshog.lexicalchaining;

import java.util.*;

import newshog.Score;
import newshog.Sentence;
import newshog.preprocess.DocProcessor;

public class LexicalChainingSummarizer {

	private Tagger tagger;
	DocProcessor dp;
	WordRelationshipDetermination wordRel;
	
	public LexicalChainingSummarizer() throws Exception
	{
		dp = new DocProcessor();
		wordRel = new WordRelationshipDetermination();
		tagger = new StanfordPOSTagger();
	}	
	
	//Build Lexical chains..
	public List<LexicalChain> buildLexicalChains(String article, List<Sentence> sent)
	{
		// POS tag article
		Hashtable<String, List<LexicalChain>> chains = new Hashtable<String, List<LexicalChain>>();
		List<LexicalChain> lc = new ArrayList<LexicalChain>();
		// Build lexical chains
			// For each sentence
			for(Sentence currSent : sent)
			{
				String taggedSent = tagger.getTaggedString(currSent.getStringVal());
				  List<String> nouns = tagger.getWordsOfType(taggedSent, Tagger.NOUN);
				  // 	For each noun
				  for(String noun : nouns)
				  {
					  int chainsAddCnt = 0;
					  	//  Loop through each LC
					    for(LexicalChain l: lc)
					    {
					    	try{
					    		WordRelation rel = wordRel.getRelation(l, noun, (currSent.getSentId() - l.start)>7);
					    		//  Is the noun an exact match to one of the current LCs (Strong relation)
					    		//  Add sentence to chain
					    		if(rel.relation == WordRelation.STRONG_RELATION)
					    		{
					    			addToChain(rel.dest, l, chains, currSent);
					    			if(currSent.getSentId() - l.last > 10) 
					    				{
					    					l.occurences++; l.start = currSent.getSentId();
					    				}
					    			chainsAddCnt++;
					    		}
						    	else if(rel.relation == WordRelation.MED_RELATION)
						    	{
						    		//  Add sentence to chain if it is 7 sentences away from start of chain	
							    		addToChain(rel.dest, l, chains, currSent);
							    		chainsAddCnt++;
							       //If greater than 7 we will add it but call it a new occurence of the lexical chain... 
							    	if(currSent.getSentId() - l.start > 7)
						    		{	
						    			l.occurences++;
						    			l.start = currSent.getSentId();
						    		}
						    	}
								else if(rel.relation == WordRelation.WEAK_RELATION)
								{
						    		if(currSent.getSentId() - l.start <= 3)
						    		{
							    		addToChain(rel.dest, l, chains, currSent);
							    		chainsAddCnt++;					    			
						    		}
								}
					    	}catch(Exception ex){}
							// add sentence and update last occurence..	
						    //chaincnt++
						 //  else 1 hop-relation in Wordnet (weak relation)
							//  Add sentence to chain if it is 3 sentences away from start of chain				 
					  	   //chaincnt++
					  // End loop LC					    	
					    }
					    //Could not add the word to any existing list.. Start a new lexical chain with the word..
					    if(chainsAddCnt==0)
					    {
					    	List<Word> senses = wordRel.getWordSenses(noun);
				    		for(Word w : senses)
				    		{
						    	LexicalChain newLc = new LexicalChain();
						    	newLc.start = currSent.getSentId();
						    	addToChain(w, newLc, chains, currSent);
				    			lc.add(newLc);
				    		}
					    }
					    if(lc.size()> 20) 
					    	purge(lc, currSent.getSentId(), sent.size());
				  }
		   //End sentence
			}
			
//			diambiguateAndCleanChains(lc, chains);
		// Calculate score
			//	Length of chain * homogeneity
		//sort LC by strength..
		return lc;
	}

	private void purge(List<LexicalChain> lc, int sentId, int totSents) {
		//Do nothing for the first 50 sentences..
		if(lc.size()<20 ) return;
		
		Collections.sort(lc);
		double min = lc.get(0).score();
		double max = lc.get(lc.size()-1).score();
		
		int cutOff = Math.max(3, (int)min);
		Hashtable<String, Boolean> words = new Hashtable<String, Boolean>();
		List<LexicalChain> toRem = new ArrayList<LexicalChain>();
		for(int i=lc.size()-1; i>=0;i--)
		{
			LexicalChain l = lc.get(i);
			if(l.score() < cutOff && (sentId - l.last) > totSents/3)//	 && containsAllWords(words, l.word))
				toRem.add(l);
			//A different sense and added long back..
			else if(words.containsKey(l.getWord().get(0).getLexicon()) && (sentId - l.start) > totSents/10)
				toRem.add(l);
			else
			{
				//Check if this is from a word with different sense..
				for(Word w: l.word) 
					words.put(w.getLexicon(), new Boolean(true));
			}
		}
		
		for(LexicalChain l: toRem) 
			lc.remove(l);
	}

	private boolean containsAllWords(Hashtable<Word, Boolean> words,
			List<Word> word) {
		boolean ret = true;
		for(Word w: word) 
			if(!words.containsKey(word)) return false;
		
		return ret;
	}

	private void addToChain(Word noun, LexicalChain l,
			Hashtable<String, List<LexicalChain>> chains, Sentence sent) {
		
		l.addWord(noun); 
		l.addSentence(sent);
		l.last = sent.getSentId();
		if(!chains.contains(noun))
			chains.put(noun.getLexicon(), new ArrayList<LexicalChain>());
		chains.get(noun.getLexicon()).add(l);		
	}

	//Split article into sentences..
	private String[] getSentences(String taggedArticle) {
		String[] sent = taggedArticle.split(".|?|!");
		return sent;
	}


	/*
	 * 			for(Sentence s: l.sentences){
				if(!summSents.containsKey(s))
				{
					String forStr = s.toString();
					System.out.println(l.word.get(0));
					System.out.println(forStr);
					sb.append(forStr +"\\r\\n");
					summSents.put(s, new Boolean(false));
					break;
				}
			}
	 */
	public int getBestSent(LexicalChain l, Hashtable<Integer, Score> pageRankScores)
	{
		double bestScore = 0; int bestStr=-1;
		for(Sentence s : l.sentences)
		{
			Score sc = pageRankScores.get(new Integer(s.getSentId()));
			if(sc!=null && sc.getScore() > bestScore)
			{
				bestScore = sc.getScore();
				bestStr = sc.getSentId();
			}
		}
		return bestStr;
	}
	
	public String format(String s){
		String[] tokens = s.split(" ");
		StringBuffer b = new StringBuffer();
		for(int i=0;i<tokens.length;i++)
			b.append(tokens[i].split("_")[0]+" ");
		
		return b.toString();		
	}
	
	Tagger getTagger() {
		return tagger;
	}


	void setTagger(Tagger tagger) {
		this.tagger = tagger;
	}	
	
}
	 
 