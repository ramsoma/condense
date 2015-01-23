package newshog.lexicalchaining;

import java.util.ArrayList;
import java.util.List;

import newshog.Sentence;


public class LexicalChain implements Comparable<LexicalChain>{
		List<Word> word;
		
		List<Sentence> sentences;
		
		int start, last;
		int score;
		int occurences=1;
		
		public LexicalChain()
		{
			word = new ArrayList<Word>();
			sentences = new ArrayList<Sentence>();
		}
		
		public double score()
		{
			return length() ;//* homogeneity();
		}
		
		public int length(){
			return word.size();
		}
		
		public float homogeneity()
		{
			return (1.0f - (float)occurences/(float)length());
		}
		
		public void addWord(Word w)
		{
			word.add(w);
		}
		
		public void addSentence(Sentence sent)
		{
			if(!sentences.contains(sent))
				sentences.add(sent);
		}
		
		public List<Word> getWord()
		{
			return word;
		}
		
		public List<Sentence>getSentences()
		{
			return this.sentences;
		}

		@Override
		public int compareTo(LexicalChain o) {
			double diff = (score() - o.score());
			return diff ==0? 0: diff > 0 ?1:-1;
		}
		
		@Override
		public boolean equals(Object o){
			return super.equals(o);
		}
}
