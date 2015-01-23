package newshog.thrift.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.thrift.TException;

import newshog.Entity;
import newshog.MetaSummarizer;
import newshog.SummarizerMain;
import newshog.dbpedia.WikipediaLinkGenerator;
import newshog.textrank.TextRankSummarizer;
import newshog.thrift.server.*;
import newshog.util.Util;

public class NewshogTextAnalysisServiceHandler implements NewshogTextAnalysisService.Iface{

	MetaSummarizer summ;
	TextRankSummarizer trSumm;
	
	WikipediaLinkGenerator wiki;
	
	public NewshogTextAnalysisServiceHandler()
	{
		Logger.getLogger(NewshogTextAnalysisServiceHandler.class.getName()).info("Initializing service handler.");
		long strt = System.currentTimeMillis();
        try {
			summ = new MetaSummarizer();
			trSumm = new TextRankSummarizer();
			Logger.getLogger(NewshogTextAnalysisServiceHandler.class.getName()).info("Loaded Summarizer in "+ (System.currentTimeMillis() - strt));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //TODO:A flag to say if link generator should be started or not..
        try{
        	long strt2 = System.currentTimeMillis();       	
        	wiki = new WikipediaLinkGenerator ();
			Logger.getAnonymousLogger().info("Loaded linkgenerator in "+ (System.currentTimeMillis() - strt2));
        }catch(Exception e){
        	e.printStackTrace();
        }
		Logger.getAnonymousLogger().info("Loaded servicehandler in "+ (System.currentTimeMillis() - strt));
	}
		
	@Override
	public TextAnalysisResult analyze(Article a, AnalysisOptions options, int maxWords, int maxLinks) throws NewshogException
	{
		long strt = System.currentTimeMillis();
		Logger logger = Logger.getLogger(NewshogTextAnalysisServiceHandler.class.getName());
//		logger.addHandler(ThriftTextAnalyticsService.fh);
		
		TextAnalysisResult res = new TextAnalysisResult();
		List<WikiInfo> wikiLinks = new ArrayList<WikiInfo>();
		logger.info("--Summarize---" + options);
		logger.info(a.text.substring(0, Math.min(50, a.text.length()))+"...");
		String summary = null;
		
        if(a.text.length()> SummarizerMain.maxLen)
        	a.text = a.text.substring(0, SummarizerMain.maxLen -1)+".";

		if(options.equals(AnalysisOptions.TextRank))
			summary = trSumm.getSummary(a.text, maxWords);
		else 
			summary = summ.summarize(a.text, maxWords);
			
		logger.info("Summarize finished in "+ (System.currentTimeMillis() - strt));
//		System.out.println(summary);
		if(wiki!=null && (options.equals(options.Wiki)|| options.equals(options.All)))
		{
			List<Entity> e;
			try {
				e = wiki.getLinks(a.text, maxLinks);
				for(Entity en : e) 
				{
					WikiInfo wiki = Util.copyToWikiInfo(en);					
					wikiLinks.add(wiki);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		res.setSummary(summary);//.replaceAll("&#..;|&#...;", "")
		res.setWikiLinks(wikiLinks);
		//Need to set the twitter info too..
		logger.info("Call service in "+ (System.currentTimeMillis() - strt));
		return res;
	}

	@Override
	public void ping() throws TException {
		// TODO Auto-generated method stub
		
	}
}
