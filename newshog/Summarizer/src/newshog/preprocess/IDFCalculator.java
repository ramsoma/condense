package newshog.preprocess;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IDFCalculator
{
	DocProcessor docProc;
	public IDFCalculator()
	{
		docProc = new DocProcessor();
	}

	public int calculateWordCounts(File fileOrDir, Hashtable<String, Integer> wordOcc)
	{
	    int numDocs = 0;	
	    if(fileOrDir.isDirectory())
		{
			//List files in the dir
			File[] children = fileOrDir.listFiles();
			for(File f:children) numDocs += calculateWordCounts(f, wordOcc);
		}
	    else{
		List<String> rawSentences = new ArrayList<String>();
		Hashtable<String, List<Integer>> iidx = new Hashtable<String, List<Integer>>(); 
		List<String> processedSent  = new ArrayList<String>();
		String doc = docProc.docToString(fileOrDir.getPath());
		docProc.getSentences(doc, rawSentences, iidx, processedSent);
		Enumeration<String> en = iidx.keys();
		while(en.hasMoreElements())
		{
		   String s = en.nextElement();	
		   if(wordOcc.get(s) ==null) wordOcc.put(s,1);
		   else {
			int newVal = (wordOcc.get(s).intValue());
			wordOcc.put(s,newVal+1);
		   }	
		}
		
		numDocs = 1;
		}
	  return numDocs;
	}

	public void writeIDFToFile(String directory, String outputFile)
	{;
		try{
		   File f = new File(directory);
		   Hashtable<String, Integer> wordOcc = new Hashtable<String, Integer>();	
		   int numDocs = calculateWordCounts(f,	wordOcc);
	           Enumeration<String> en = wordOcc.keys();
		   PrintWriter pw = new PrintWriter(new FileWriter(outputFile));	
		   while(en.hasMoreElements())
		   {
			 String s = en.nextElement();	
			 int val = (wordOcc.get(s).intValue());
			 double idf = Math.log(numDocs/val);
			 //write word, idf
			 pw.println(s+","+idf);	
		   }
		   pw.close();		
		}catch(Exception ex)
		{
			ex.printStackTrace();
		//Log it	
		}
	}

	public static void main(String[] args)
	{
		IDFCalculator idfc = new IDFCalculator();
		idfc.writeIDFToFile("/home/ram/summarizer/test", "idf.csv");
	}
}
