package unittests;

import newshog.lexicalchaining.StanfordPOSTagger;
import junit.framework.TestCase;

public class StanfordPOSTaggerTest extends TestCase {

	public void testGetTaggedStringTest(String article)
	{
		try {
			StanfordPOSTagger post = new StanfordPOSTagger();
			String res = post.getTaggedString("The United States and Russia reached a sweeping agreement on Saturday that called for Syria's arsenal of chemical weapons to be removed or destroyed by the middle of 2014 and indefinitely stalled the prospect of American airstrikes.");
			System.out.println(res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
