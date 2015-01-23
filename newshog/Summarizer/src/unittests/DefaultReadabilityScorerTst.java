package unittests;

import static org.junit.Assert.*;
import junit.framework.Assert;
import newshog.Sentence;
import newshog.readability.DefaultReadabilityScorer;

import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultReadabilityScorerTst {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		DefaultReadabilityScorer s = new DefaultReadabilityScorer();
		Sentence sent = new Sentence(0);
		sent.setStringVal("Budget Crunching");
		double p1 = s.getReadability(sent);
		sent.setStringVal("Budget Crunching is good for your health ya ");
		double p2 = s.getReadability(sent);
		sent.setStringVal("On Benghazi probe , GOP 's Issa says ` Hillary Clinton 's not a target ' By Carrie Dann , Political Reporter , NBC News A top GOP critic pushed back Sunday on charges that Republican efforts to investigate last year 's Benghazi attack are designed to inflict political damage on former Secretary of State Hillary Clinton .");
		double p3 = s.getReadability(sent);
		System.out.println(p1+","+p2+","+p3);
		Assert.assertTrue("smaller worse", p1 < p2);
		Assert.assertTrue("larger worse", p3 < p2);
	}

}
