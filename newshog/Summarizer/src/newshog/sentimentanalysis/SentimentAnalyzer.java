package newshog.sentimentanalysis;

import java.util.*;

public interface SentimentAnalyzer {
	
	public Sentiment analyze(String doc);
	public Sentiment analyze(List<String> tokens);
	public Sentiment analyze(List<String> tokens, String category);	
}

