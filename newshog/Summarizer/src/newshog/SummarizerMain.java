package newshog;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import newshog.dbpedia.WikipediaLinkGenerator;
import newshog.preprocess.DocProcessor;
import newshog.textrank.TextRankSummarizer;
import newshog.thrift.server.WikiInfo;
import newshog.twitter.TwitterSearchResults;
import newshog.util.Util;
import twitter4j.Status;

import java.util.logging.*;

/*
 * The entry class to the summarizer. Calls the summarizer and the DBPedia interface to create summary and links.
 * 
 */
public class SummarizerMain {
    public static boolean twitterOn = false;
    public static int maxLinks = 3;
    public static int maxLen = (int) 8000;//(20 * 1024d/8d);
    
    public static void main(String[] args)
    {
        String summaryDir = null;
        String linksDir = null;
        String inputDir = null;
        int maxWords = 100;
        boolean addWiki = true;
        boolean lexChaining = true;
        
        // -i inputdir -o outputdir -w words
        for(int i=0;i<args.length;i++)
    	{
        	if((i+1) < args.length && args[i].equals("-o"))
        	{
        		summaryDir = args[i+1] + "/summary/";
        		linksDir  = args[i+1] + "/wiki-links/";       		
        	}
        	else if((i+1) < args.length && args[i].equals("-i"))
        		inputDir = args[i+1];
        	else if((i+1) < args.length && args[i].equals("-w"))
        		maxWords = Integer.parseInt(args[i+1]);
        	else if(args[i].equals("--nowiki"))
        		addWiki = false;
        	else if(args[i].equals("--nolex"))
        		lexChaining = false;
    	}
        
        if(inputDir==null || summaryDir==null ||linksDir==null)
        {
        	System.out.println("Usage.. java -jar Summarizer -i inputDir -o outputDir -w noOfWords");
        	return;
        }
        
        long time = System.currentTimeMillis();
        long ioTime = 0, wikiTime = 0, summTime = 0, parseTime= 0;
        Logger logger = java.util.logging.Logger.getAnonymousLogger();
        
        try{
	        File dir = new File(inputDir);
	        File[] files = dir.listFiles();
//	        MetaSummarizer summ = new MetaSummarizer();
	        DynamicProgrammingSummarizer summ = new DynamicProgrammingSummarizer();
	        TextRankSummarizer trSumm = new TextRankSummarizer();
	        WikipediaLinkGenerator wiki = null; 
	        try{
	        	if(addWiki)
	        		wiki = new WikipediaLinkGenerator ();
	        }catch(Exception ex){
	        	logger.severe(ex.getMessage());
	        }
	        for(File f: files)
	        {
	        	try{
		        	if(f.isDirectory()) continue;
		        	
			        String fileName = f.getName();
			        String fullFileName = f.getAbsolutePath();
			        
			        logger.info("Starting summarization of.."+fileName);
			        DocProcessor dp = new DocProcessor();
			        long submodStrt = System.currentTimeMillis();
			        String doc = dp.docToString(fullFileName);
			        if(doc.length()> maxLen)
			        	doc = doc.substring(0, maxLen -1)+".";

			        ioTime += System.currentTimeMillis() - submodStrt;	
			        
			        submodStrt = System.currentTimeMillis();
			        String summStr = null;
			        if(lexChaining)
			        	summStr = summ.summarize(doc, maxWords);//
			        else
			        	summStr = trSumm.getSummary(doc, maxWords);

			        submodStrt = System.currentTimeMillis();
			        
			        //Get links from summary..
			        PrintWriter pw = new PrintWriter(new FileWriter(summaryDir+fileName));
			        pw.println(summStr);
			        pw.close();
			        			        	
			        if(addWiki && wiki!=null)
			        {
				        pw = new PrintWriter(new FileWriter(linksDir+fileName));
			        	logger.info("Writing out wiki links");
				        List<Entity> ents = wiki.getLinks(doc, maxLinks);//summ.getLinks(doc);
				        wikiTime += System.currentTimeMillis() - submodStrt;
				        List<TwitterSearchResults> twitRes = null;
				        int cnt = 0;
				        List<WikiInfo> wikis = new ArrayList<WikiInfo>();
				        for(int i=0; (cnt < maxLinks && i< ents.size());i++)
				        {
				        	wikis.add(Util.copyToWikiInfo(ents.get(i)));
			        	}
				        pw.println(Util.toJSON(wikis));
			        }			        
			        pw.close();
	        	}catch(Exception ex){
	        		ex.printStackTrace();
	        		logger.severe( "Could not process file: "+ f.getName() +" "+ ex.getMessage());
	        	}
	        }
	        
	        logger.info("Processed "+ files.length +" in: "+ (System.currentTimeMillis() - time )/1000);
	        logger.info("IO: "+ ioTime +", Parse:"+ parseTime+", Summ:"+ summTime+", wiki: "+wikiTime);
        }catch(Exception ex)
        {
        	logger.severe("Could not start the summarizer. Please report the failure with the following message:  "+ ex.getMessage());;
        	ex.printStackTrace();
        }
        
        //sc.sentId+". "++ sc.score
		//View graph..
//		YEdGraphCreator graphCreator = new YEdGraphCreator();
//		graphCreator.printGraphML(sentences, links, "/home/ram/summarizer/test/results/houston.gml");

    }
}

/*
List<Entity> ents = summ.getLinks(doc, maxLinks);//summ.getLinks(doc);
wikiTime += System.currentTimeMillis() - submodStrt;
List<TwitterSearchResults> twitRes = null;
if(twitterOn){
    try{
    	twitRes = summ.getTwitterResults(ents, doc);
    }catch(Exception ex){
    	ex.printStackTrace();
    }
    }    
int min = Math.min(sentences.size(),20);
maxLineSumm = sentences.size()/10 > min ? sentences.size()/10:min;

PrintWriter pw = new PrintWriter(new FileWriter(resultDir+fileName));
pw.println(sentences.get(0));
pw.println("----------------");
for(int i=0;i<Math.min(maxLineSumm, reRank .size()-1);i++)
{
     Score sc = reRank.get(i);
     if(sc.sentId>0)
    	 pw.println(sentences.get(sc.sentId));//+ formatLinksList(sentences.get(sc.sentId).getLinks()));// + " "+sc.sentId+" "+sc.score+" "+summ.links.get(i));// +" - " +clust.getCluster(sc.sentId));
}
pw.println("-----");

for(int i=0; i < Math.min(maxLinks, ents.size());i++)
	pw.println(ents.get(i).getFullName() + " "+ents.get(i).getWebPage());

if(twitRes !=null && twitRes.size() >0)
{
	TwitterSearchResults res = twitRes.get(0);
	List<Status> tweets = res.getTopTweets();
	for(int i=0;i< Math.min(10, tweets.size());i++)
		pw.println(tweets.get(i).getText());
	Sentiment s = res.getSentiment();	
		pw.println("Sentiment -- "+s.category+" "+s.strength);
	List<String> tags = res.getTopHashtags(10);
	for(int i=0;i< Math.min(10,tags.size());i++)
		pw.println("#"+tags.get(i));
}
pw.close();
*/