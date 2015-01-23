package newshog.sentimentanalysis;

public class Sentiment{
	public static final String POSITIVE = "positive";
	public static final String NEGATIVE = "negative";
	public static final String NEUTRAL = "neutral";

	String category;
	double strength;
	
	public Sentiment()
	{
		this.category = NEUTRAL;
	}
	
	public boolean isPositive()
	{
		return category.equals("positive");
	}

	public boolean isNegative()
	{
		return category.equals("negative");
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCategory(String cat)
	{
		this.category = cat;
	}
}