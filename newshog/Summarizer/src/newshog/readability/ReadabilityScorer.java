package newshog.readability;

import newshog.Sentence;

public interface ReadabilityScorer {
	public double getReadability(Sentence s);
}
