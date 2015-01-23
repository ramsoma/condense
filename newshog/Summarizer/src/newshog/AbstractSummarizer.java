package newshog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AbstractSummarizer {
	
	public List<Score> order(List<Score> s)
	{
		Collections.sort(s, new Comparator<Score>()
		{

			@Override
			public int compare(Score o1, Score o2) {
				// TODO Auto-generated method stub
				
				return o1.sentId - o2.sentId;
			}			
		});
		return s;
	}	

	public List<Sentence> orderSentences(List<Sentence> s)
	{
		Collections.sort(s, new Comparator<Sentence>()
		{

			@Override
			public int compare(Sentence o1, Sentence o2) {
				// TODO Auto-generated method stub
				
				return o1.getSentId() - o2.getSentId();
			}			
		});
		return s;
	}	

}
