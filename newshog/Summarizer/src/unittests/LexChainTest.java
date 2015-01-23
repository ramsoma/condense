package unittests;

import static org.junit.Assert.*;
import newshog.Sentence;
import newshog.lexicalchaining.LexicalChainingSummarizer;
import newshog.lexicalchaining.LexicalChain;
import newshog.lexicalchaining.StanfordPOSTagger;
import newshog.lexicalchaining.Tagger;
import newshog.lexicalchaining.Word;
import newshog.lexicalchaining.WordRelation;
import newshog.lexicalchaining.WordRelationshipDetermination;
import newshog.lexicalchaining.WordnetWord;
import newshog.preprocess.DocProcessor;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class LexChainTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testLexicalChainingSummarizer() {
		fail("Not yet implemented");
	}

	@Test
	public void testBuildLexicalChains() {
		try {
			/*
			String article = "US President Barack Obama has welcomed an agreement between the US and Russia under which Syria's chemical weapons must be destroyed or removed by mid-2014 as an \"important step\"."
					+ "But a White House statement cautioned that the US expected Syria to live up to its public commitments. "
					+ "The US-Russian framework document stipulates that Syria must provide details of its stockpile within a week. "
					+ "If Syria fails to comply, the deal could be enforced by a UN resolution. "
					+ " China, France, the UK, the UN and Nato have all expressed satisfaction at the agreement. "
					+ " In Beijing, Foreign Minister Wang Yi said on Sunday that China welcomes the general agreement between the US and Russia.";
*/
			DocProcessor dp =new DocProcessor();
			String article = dp.docToString("/Users/ram/dev/summarizer/test/forram/technology/output/summary/9.txt");
			LexicalChainingSummarizer lcs = new LexicalChainingSummarizer();

			long strt = System.currentTimeMillis();

			List<Sentence> sent = dp.getSentencesFromStr(article);
			List<LexicalChain> vh = lcs.buildLexicalChains(article, sent);
			Collections.sort(vh);
			
			List<Sentence> s = dp.getSentencesFromStr(article);
			Hashtable<String, Boolean> comp = new Hashtable<String, Boolean>(); 
			System.out.println(vh.size());
			Tagger t = new StanfordPOSTagger();
			System.out.println(t.getTaggedString(article));
			for(int i=vh.size()-1;i>=Math.max(vh.size()-50, 0);i--)
			{
				LexicalChain lc = vh.get(i);
				
				if(! (comp.containsKey(lc.getWord().get(0).getLexicon())))
				{
					comp.put(lc.getWord().get(0).getLexicon(), new Boolean(true));
					for(int j=0;j<lc.getWord().size();j++)
						System.out.print(lc.getWord().get(j) + "-- ");
					System.out.println(lc.score());
					for(Sentence sid : lc.getSentences())
					{
						//if(sid>=0 && sid<s.size())
						System.out.println(sid);
					}
				}
				System.out.println("--------");
			}
			System.out.println((System.currentTimeMillis() - strt)/1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testGetRelation() {
		try {
			
			WordRelationshipDetermination lcs = new WordRelationshipDetermination();
			LexicalChain l = new LexicalChain();
			List<Word> words = lcs.getWordSenses("sing");
			
			l.addWord(words.get(0));
//			int rel = lcs.getRelation(l, "nation");
			WordRelation rel2 = lcs.getRelation(l, "music", true);
			WordRelation rel3 = lcs.getRelation(l, "rock", true);
			
	//		assertEquals(rel, LexicalChainingSummarizer.STRONG_RELATION);
			assertEquals( WordRelation.MED_RELATION, rel2.relation);
			assertEquals( WordRelation.MED_RELATION, rel3.relation);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Test
	public void testSummarize() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTagger() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetTagger() {
		fail("Not yet implemented");
	}
}
