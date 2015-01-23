package unittests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.Dictionary;

public class WordnetDictionary {

	public static void main(String[] args)
	{
		Dictionary dictionary = new Dictionary(new File("resources/dict"));//, ILoadPolicy.IMMEDIATE_LOAD);
		
	}
}
