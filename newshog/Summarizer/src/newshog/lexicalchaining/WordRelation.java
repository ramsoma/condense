package newshog.lexicalchaining;

public class WordRelation {
	//Match strength constants for lexical chains..
	public static int STRONG_RELATION = 0;
	public static int MED_RELATION = 1;
	public static int WEAK_RELATION = 2;
	public static int NO_RELATION = 3;
	
	public Word src;
	public Word dest;
	public int relation;
}
