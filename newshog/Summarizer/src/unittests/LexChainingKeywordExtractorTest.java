package unittests;

import static org.junit.Assert.*;

import java.util.List;

import newshog.Sentence;
import newshog.lexicalchaining.LexChainingKeywordExtractor;
import newshog.lexicalchaining.LexicalChain;
import newshog.lexicalchaining.LexicalChainingSummarizer;
import newshog.preprocess.DocProcessor;

import org.junit.BeforeClass;
import org.junit.Test;

public class LexChainingKeywordExtractorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testGetKeywords() {
		try {
			DocProcessor dp =new DocProcessor();
			String article = dp.docToString("/Users/ram/dev/summarizer/test/forram/topnews/input/8.txt");
			LexicalChainingSummarizer lcs;
			lcs = new LexicalChainingSummarizer();

			long strt = System.currentTimeMillis();

			List<Sentence> sent = dp.getSentencesFromStr(article);
			List<LexicalChain> vh = lcs.buildLexicalChains(article, sent);
			LexChainingKeywordExtractor ke = new LexChainingKeywordExtractor();
			List<String> keywords = ke.getKeywords(vh, 5);
			//lazy
			System.out.println(keywords);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
