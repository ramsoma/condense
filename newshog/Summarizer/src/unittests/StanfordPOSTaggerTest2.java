package unittests;

import static org.junit.Assert.*;
import newshog.lexicalchaining.StanfordPOSTagger;

import org.junit.BeforeClass;
import org.junit.Test;

public class StanfordPOSTaggerTest2 {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testStanfordPOSTagger() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaggedString() {
		try {
			StanfordPOSTagger post = new StanfordPOSTagger();
			String res = post.getTaggedString("The United States and Russia reached a sweeping agreement on "
					+ "Saturday that called for Syria's arsenal of chemical weapons to be removed or destroyed by "
					+ "the middle of 2014 and indefinitely stalled the prospect of American airstrikes.");
			System.out.println(res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Test
	public void testGetWordsOfType() {
		fail("Not yet implemented");
	}

}
