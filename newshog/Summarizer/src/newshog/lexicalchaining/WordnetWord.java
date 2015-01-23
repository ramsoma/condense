package newshog.lexicalchaining;

import java.util.Hashtable;
import java.util.List;

import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWordID;

public class WordnetWord implements Word{
	String lexicon;
	ISenseKey wordSense;
	IWordID id;
	
	//Cache..
	ISynset synonyms;
	Hashtable<IPointer, List<ISynsetID>>rels;
	
	public WordnetWord()
	{
		rels = new Hashtable<IPointer, List<ISynsetID>>();
	}
	
	@Override
	public String getLexicon() {
		return lexicon;
	}

	@Override
	public Object getSense() {
		return wordSense;
	}

	@Override
	public Object getID() {
		return id;
	}

	@Override
	public void setLexicon(String lex) {
		this.lexicon = lex;
	}

	@Override
	public void setSense(Object senseID) {
		this.wordSense = (ISenseKey) senseID;
	}

	@Override
	public void setID(Object id) {
		this.id = (IWordID)id;
	}
	
	@Override
	public String toString()
	{
		return this.lexicon;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
}
