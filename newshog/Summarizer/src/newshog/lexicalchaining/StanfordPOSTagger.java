package newshog.lexicalchaining;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import newshog.preprocess.DocProcessor;

//import com.sun.tools.doclets.internal.toolkit.util.Util;

public class StanfordPOSTagger implements Tagger{
	MaxentTagger tagger;
	String modelFile = "./resources/english-left3words-distsim.tagger";
	DocProcessor dp ;
	Hashtable<Integer, String[]> tagMap;
	
	public StanfordPOSTagger() throws Exception
	{
		  tagger = new MaxentTagger(modelFile);
		  dp = new DocProcessor();
		  initTagMap();
	}
	
	String[] nounTags = {"NN", "NNS","NNP","NNPS"};
	private void initTagMap()
	{
	    tagMap = new Hashtable<Integer, String[]>();		  
		tagMap.put(Tagger.NOUN, nounTags);
	}
	
	public String getTaggedString(String article)
	{
        return tagger.tagString(article);	
	}
	
	//Returns true if the typestring belongs to one of the tags for the type..
	public boolean isType(String typeStr, int type)
	{
		boolean ret = false;
		String[] tags = tagMap.get(type);
		for(String tag: tags)
			if(typeStr.equalsIgnoreCase(tag)) ret = true;
		
		return ret;
	}
	
	//Get all the words of a particular POS type (NOUN, VERB..)
	public List<String> getWordsOfType(String sent, int type)
	{
		List<String> ret = new ArrayList<String>();
		String[] tokens = dp.getWords(sent);
		for(String t:tokens)
		{
			String[] wordPlusType = t.split("_");
			if(wordPlusType.length ==2)
			{
				if(isType(wordPlusType[1], type))
					ret.add(wordPlusType[0]);
			}
		}
		return ret;
	}
}

/* -- Reference : List of all POS tags in Penn Bank---
1.	CC 	Coordinating conjunction
2. 	CD 	Cardinal number
3. 	DT 	Determiner
4. 	EX 	Existential there
5. 	FW 	Foreign word
6. 	IN 	Preposition or subordinating conjunction
7. 	JJ 	Adjective
8. 	JJR 	Adjective, comparative
9. 	JJS 	Adjective, superlative
10. 	LS 	List item marker
11. 	MD 	Modal
12. 	NN 	Noun, singular or mass
13. 	NNS 	Noun, plural
14. 	NNP 	Proper noun, singular
15. 	NNPS 	Proper noun, plural
16. 	PDT 	Predeterminer
17. 	POS 	Possessive ending
18. 	PRP 	Personal pronoun
19. 	PRP$ 	Possessive pronoun
20. 	RB 	Adverb
21. 	RBR 	Adverb, comparative
22. 	RBS 	Adverb, superlative
23. 	RP 	Particle
24. 	SYM 	Symbol
25. 	TO 	to
26. 	UH 	Interjection
27. 	VB 	Verb, base form
28. 	VBD 	Verb, past tense
29. 	VBG 	Verb, gerund or present participle
30. 	VBN 	Verb, past participle
31. 	VBP 	Verb, non-3rd person singular present
32. 	VBZ 	Verb, 3rd person singular present
33. 	WDT 	Wh-determiner
34. 	WP 	Wh-pronoun
35. 	WP$ 	Possessive wh-pronoun
36. 	WRB 	Wh-adverb 
*/