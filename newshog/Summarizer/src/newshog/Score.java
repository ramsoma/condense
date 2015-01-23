package newshog;

public class Score implements Comparable<Score>
{
    int sentId;
    public double score;

    public Score()
    {
    	score = 0;
    }

    public int getSentId(){
    	return sentId;
    }
    
    public double getScore()
    {
    	return score;
    }
    
    public void setScore(double score)
    {
    	this.score = score;  	
    }
    
    public void setSentId(int sentId)
    {
    	this.sentId = sentId;
    }
    
    public int compareTo(Score o)
    {

	if(o.score > score) return 1;
	else if (o.score < score) return -1;
	return 0;
    }
    
    public String toString()
    {
    	return sentId +" "+score;
    }
}
