package newshog.sentimentanalysis;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MPQASentimentAnalyzer implements SentimentAnalyzer{

	private Hashtable<String, Sentiment> lookup;
	private static MPQASentimentAnalyzer inst;
	
	private MPQASentimentAnalyzer(String fileName) throws Exception
	{	
		lookup = new Hashtable<String, Sentiment>();
		load(fileName);
	}
	
	public static MPQASentimentAnalyzer getInstance(String fileName) throws Exception
	{
		try{
			if(inst== null)
				inst = new MPQASentimentAnalyzer(fileName);
		}catch(Exception ex){
			throw ex;
		}
		return inst;
	}
	
	private String getVal(String mpqaToken)
	{
		String[] tokens = mpqaToken.split("=");
		return tokens[1];
	}
	
	private void load(String fileName)throws Exception
	{
		LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
		String nextLine;
		while((nextLine = lnr.readLine())!=null)
		{
			String[] tokens = nextLine.split(" ");
			if(tokens.length != 6) continue;
			
			String type = getVal(tokens[0]);
			String word = getVal(tokens[2]);
			String polarity = getVal(tokens[5]);
			Sentiment s = new Sentiment();
			s.category = polarity;
			if(!s.category.equals("neutral"))
				s.strength = type.equals("strongsubj") ? 1d:0.5d;
			else
				s.strength = 0;
			this.lookup.put(word, s);
		}
	}
	
	@Override
	public Sentiment analyze(List<String> tokens) {
		Sentiment ret = new Sentiment();
		double posScore = 0;
		double negScore = 0;

		for(String token : tokens)
		{
			Sentiment s = lookup.get(token);
			//Unknown word..
			if(s==null) continue;
			
			if(s.isPositive())
				posScore += s.strength;
			else if(s.isNegative())
				negScore += s.strength;
		}
		
		Sentiment overall = new Sentiment();
		if(posScore > negScore)
			overall.setCategory("positive");
		else if (posScore < negScore)
			overall.setCategory("negative");			
		else
			overall.setCategory("neutral");			
		overall.strength = Math.abs(posScore - negScore);
		return overall;
	}

	@Override
	public Sentiment analyze(List<String> tokens, String category) {
		return analyze(tokens);
	}


	private List<String> tokenize(String t1) {

		List<String> ret = new ArrayList<String>();
		String[] tkns = t1.split(" ");
		for(String tkn: tkns) ret.add(tkn.toLowerCase());
		return ret;
	}

	@Override
	public Sentiment analyze(String doc) {		
		return analyze(tokenize(doc));
	}	
}
