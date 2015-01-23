package newshog.cohesion;

import newshog.Sentence;

//Interface for the cohesion calculator..
public interface CohesionScorer {
	public double calculateCohesion(Sentence s1, Sentence s2);
}
