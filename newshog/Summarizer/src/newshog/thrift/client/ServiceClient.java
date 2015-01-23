package newshog.thrift.client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import newshog.preprocess.DocProcessor;
import newshog.thrift.server.*;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.json.*;

import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

public class ServiceClient {
    public static boolean twitterOn = false;
    public static int maxLinks = 3;
    public static int maxLen = (int) 8000;//(20 * 1024d/8d);
    
	public static void main(String[] args) throws SecurityException, IOException
	{
		try{
		      FileHandler fh = new FileHandler("logs/client.log", 1*1024*1024,100);
		      Logger.getLogger("").addHandler(fh);
		      int maxWords = 100;
		      int maxLinks=3;
		      String testFile = "";
		      boolean addWiki = true;
		      boolean lexChaining = true;
		      String summaryDir= null, linksDir= null, inputFile = null;
		      String server = "localhost";
		      if(args.length < 2) {
		    	  System.out.println("Usage java ServiceClient -i filename -o outputDir -w noWords --nowiki --nolex -server ip");
		    	  return;
		      }	
		      
		      for(int i=0;i<args.length;i++)
		      {
		        	if((i+1) < args.length && args[i].equals("-o"))
		        	{
		        		summaryDir = args[i+1] + "/summary/";
		        		linksDir  = args[i+1] + "/wiki-links/";       		
		        	}
		        	else if((i+1) < args.length && args[i].equals("-i"))
		        		inputFile = args[i+1];
		        	else if((i+1) < args.length && args[i].equals("-w"))
		        		maxWords = Integer.parseInt(args[i+1]);
		        	else if(args[i].equals("--nowiki"))
		        		addWiki = false;
		        	else if(args[i].equals("--nolex"))
		        		lexChaining = false;
		        	else if((i+1) < args.length && args[i].equals("-server"))
		        		server = args[i+1];
		    	}
	      TTransport transport;
	      transport = new TSocket(server, 9090);
	      transport.open();
	      TProtocol protocol = new  TBinaryProtocol(transport);
	      NewshogTextAnalysisService.Client client = new NewshogTextAnalysisService.Client(protocol);
	     
	      perform(client, inputFile, summaryDir, linksDir, addWiki, lexChaining, maxWords, maxLinks);
	      transport.close();
	    } catch (TException x) {
	      x.printStackTrace();
	    } 
	}
	
	private static void perform(NewshogTextAnalysisService.Client client, String testFile, String summaryDir, 
								String linksDir, boolean wiki, boolean lex, int maxWords, int maxLinks)
	{
        Logger logger = java.util.logging.Logger.getAnonymousLogger();
		try {
	        logger.info("Summarize: "+ testFile+" "+wiki);
	        int idx = testFile.lastIndexOf("/");
	        if(idx<=-1) idx = 0;
	        
	        String fileName = testFile.substring(idx+1);

	        client.ping();
			DocProcessor dp = new DocProcessor();			
			Article a = new Article();
			String fullDoc = dp.docToString(testFile);
			String docStr = fullDoc.length()>maxLen?fullDoc.substring(0, maxLen):fullDoc;
			a.setText(docStr);
			
			AnalysisOptions options = AnalysisOptions.Summarization;			
			if(!lex) options = AnalysisOptions.TextRank;
			if(wiki) options = AnalysisOptions.All;
			
			long strt = System.currentTimeMillis();
			TextAnalysisResult tar = client.analyze(a, options, maxWords, maxLinks);
			logger.info("Service call took: "+ (System.currentTimeMillis() - strt));	
			
			strt = System.currentTimeMillis();
			PrintWriter pw = new PrintWriter(new FileWriter(summaryDir+ fileName));
	        pw.println(tar.summary);
	        pw.close();
			
	        if(wiki){
				PrintWriter pw2 = new PrintWriter(new FileWriter(linksDir+ fileName));
		        
			pw2.println(newshog.util.Util.toJSON(tar.wikiLinks));
		        
			pw2.close();
	      }
	        logger.info("Writing files took: " + (System.currentTimeMillis() - strt));
		} catch (Exception e) {
	        logger.info("Exception occurred "+e.getStackTrace().toString());
		}		
	}
}
