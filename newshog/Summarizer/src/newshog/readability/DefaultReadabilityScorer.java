package newshog.readability;

import newshog.Sentence;

public class DefaultReadabilityScorer implements ReadabilityScorer{

	public DefaultReadabilityScorer()
	{
		
	}
	
	@Override
	public double getReadability(Sentence s) {
		// TODO Auto-generated method s tub
		double sigma = 3;
		double mean = 8;
		return (s.getWordCnt() < 18 && s.getWordCnt() > 5)?1:(s.getWordCnt() >12)?-Math.abs(s.getWordCnt() - 15)*2:s.getWordCnt()-40;//(Math.exp(-Math.pow((s.getWordCnt()- mean),2)/(2*sigma*sigma))/(sigma * Math.sqrt((2 *Math.PI))) -0.06)/0.012 ;
	}
}
