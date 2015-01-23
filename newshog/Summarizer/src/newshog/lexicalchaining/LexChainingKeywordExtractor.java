package newshog.lexicalchaining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LexChainingKeywordExtractor {
	
	//Simple logic to pull out the keyword based on longest lexical chains..
	public List<String> getKeywords(List<LexicalChain> lexicalChains, int noOfKeywrds){
		Collections.sort(lexicalChains);
		List<String> ret = new ArrayList<String>();
		for(int i=0;i<Math.min(lexicalChains.size(), noOfKeywrds);i++)
		{
			List<Word> words = lexicalChains.get(i).getWord();
			if(words.size()>0 &&!ret.contains(words.get(0).getLexicon())){
				ret.add(words.get(0).getLexicon());
			}
		}
		return ret;		
	}
}
