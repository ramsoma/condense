package newshog.lexicalchaining;

public interface Word {
	//Lexicon..
	public String getLexicon();
	public void setLexicon(String lex);

	//Sense of a word..
	public Object getSense();
	public void setSense(Object senseID);
	
	//ID for a word..
	public Object getID();
	public void setID(Object id);
}