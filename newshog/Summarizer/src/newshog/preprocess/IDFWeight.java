package newshog.preprocess;

import java.util.Hashtable;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
	
public class IDFWeight implements WordWeight
{
	Hashtable<String, Double> idf;
	private static IDFWeight instance;
	
	public IDFWeight(String fileName)
	{
		idf = new Hashtable<String,Double>();
		load(fileName);
	}

	public static IDFWeight getInstance(String fileName)
	{
		if(instance==null) 
			instance = new IDFWeight(fileName);
		return instance;
	}
	
	public double getWordWeight(String s)
	{
		if(idf==null) return 1d;		
		
		Double d = idf.get(s);
		if(d == null)
		{
			   return 1;
		}
	    return d.doubleValue();
	}

	public void load(String fileName)
	{
		try{
		    LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
		    String nextLine;
		    
		    while ((nextLine = lnr.readLine()) != null) 
		    {
		        String trimmedLine = nextLine.trim();
		        if (!trimmedLine.isEmpty()) 
		        {
				  String[] tokens = trimmedLine.split(",");
				  String word = tokens[0]; double idfVal = Double.parseDouble(tokens[1]);
				  idf.put(word, idfVal); 
		        }
		    }   	
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}	
}
