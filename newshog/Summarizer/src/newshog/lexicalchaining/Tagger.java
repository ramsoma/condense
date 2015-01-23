package newshog.lexicalchaining;

import java.util.List;

public interface Tagger {
	//Tagger types..
	public static final int NOUN=0;
	public static final int VERB=1;
	public static final int ADJECTIVE=2;
	public static final int ADVERB=3;
	public static final int PRONOUN=4;

	public String getTaggedString(String article);
	public List<String> getWordsOfType(String sent, int type);	
}
