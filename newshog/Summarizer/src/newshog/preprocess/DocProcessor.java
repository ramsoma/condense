package newshog.preprocess;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import newshog.Sentence;
import newshog.util.StringUtil;
import edu.stanford.nlp.process.DocumentPreprocessor;

/*
 * Parse document to sentences..
 */
public class DocProcessor
{
   	//Str - Document or para
	//sentences - List containing returned sentences
	// iidx - if not null update with the words in the sentence + sent id
	// processedSent - Sentences after stemming and stopword removal..
    public void getSentences(String str, List<String> sentences, Hashtable<String, List<Integer>> iidx, List<String> processedSent)
    {
    	
		int oldSentEndIdx = 0;
		int sentEndIdx = 0;
	    Stemmer stemmer = new Stemmer();
	    StopWords sw = StopWords.getInstance();      
	    BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
	    BreakIterator wrdItr = BreakIterator.getWordInstance(Locale.US);
	    iterator.setText(str);
	    int start = iterator.first();
	    int sentCnt = 0;
	
	    for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) 
	    {
			String sentence = str.substring(start,end);//str.substring(oldSentEndIdx, sentEndIdx).trim();

			//Add the sentence as-is; do any processing at the word level..
			//To lower case and trim all punctuations
//			sentence = sentence.replaceAll(",|;|!|:|\\.|\"", "").toLowerCase();
			try{
				if(isScript(sentence)) 
					{
						continue;
					}
			}catch(Exception ex){
				//Ignore exceptions.. treat as if not a script!
			}
			
			sentences.add(sentence);
			
//			String[] words = sentence.split(" ");
			wrdItr.setText(sentence);	
			StringBuffer procSent = new StringBuffer();
			int wrdStrt = 0;
//			for(int i=0;i<words.length;i++)
			
			for(int wrdEnd = wrdItr.next(); wrdEnd != BreakIterator.DONE; 
					wrdStrt = wrdEnd, wrdEnd = wrdItr.next())
			{
				String word = sentence.substring(wrdStrt, wrdEnd);//words[i].trim();
				word.replaceAll("\"|'","");

				//Skip stop words and stem the word..
				if(sw.isStopWord(word)) continue;                        
	            stemmer.stem(word);
				String stemedWrd = stemmer.toString();
				
				//update iidx by adding the current sentence to the list..
				if(iidx!=null)
				{
					if(stemedWrd.length()>1)
					{						
						List<Integer> sentList= iidx.get(stemedWrd);
						if(sentList==null)
						{
							sentList = new ArrayList<Integer>();
						}
				
						sentList.add(sentCnt);
						//Save it back
						iidx.put(stemedWrd, sentList);
					}
				}		
				procSent.append(stemedWrd+" ");                           
			}

			sentCnt++;
			if(processedSent!=null )
				processedSent.add(procSent.toString());
	    }
    }
    
    //Simple rule to get rid of java script: If the number of ;s is greater than maxSemiColons.. 
    int maxSemiColons = 5;
    double alphaRatioThreshold = 1;
    double maxBracCnt = 5;
    static int STANFORD = 1;
    static int CUSTOM = 2; 
    static int SENTENCE_FRAG= STANFORD;
    
    public boolean isScript(String line) throws UnsupportedEncodingException{
    	int cnt = 0;
    	int strt = 0;
    	int nxt;
    	//We will not care about the short lines..
//    	if(line.length()< 30) return false;
    	byte[] chars = line.getBytes("UTF8");
    	int semColCnt=0, alphaCnt=0, nonAlphaCnt=1, bracCnt=0;
    	for(byte b: chars)
    	{
    		if(b==';') semColCnt++;
    		if(b=='{'||b=='}'||b=='('||b==')') bracCnt ++;   		
    		if((b>='a' && b<='z') || (b>='A' && b<='Z')) alphaCnt++;
    		else if(b!=' ') nonAlphaCnt++;
    	}
    	double ratio = ((double) alphaCnt)/(double) nonAlphaCnt;
    	return (semColCnt>maxSemiColons||(bracCnt>maxBracCnt)||(ratio<alphaRatioThreshold));
    }
    
    public String docToString(String fileName)
    {
    	LineNumberReader lnr = null;
        StringBuffer docBuffer = new StringBuffer();
        
        try {	 	
            lnr = new LineNumberReader(new FileReader(fileName));
            String nextLine;
            
            while ((nextLine = lnr.readLine()) != null) {
                String trimmedLine = nextLine.trim();
                if (!trimmedLine.isEmpty() ) {
                    docBuffer.append(trimmedLine.replaceAll("&#?[0-9 a-z A-Z][0-9 a-z A-Z][0-9 a-z A-Z]?;", "")+" ");
                }
            }	    
        } catch (Exception ex) {
            Logger.getLogger(DocProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                lnr.close();
            } catch (IOException ex) {
                Logger.getLogger(DocProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return docBuffer.toString();
    }

    
    //List of sentences form a document
    public List<Sentence> docToSentList(String fileName)
    {
    	List<Sentence> sentList = new ArrayList<Sentence>();
    	LineNumberReader lnr = null;
        StringBuffer docBuffer = new StringBuffer();
        
        try {	 	
            lnr = new LineNumberReader(new FileReader(fileName));
            String nextLine;
            int paraNo =0;
            int sentNo = 0;
            while ((nextLine = lnr.readLine()) != null) {
                String trimmedLine = nextLine.trim();
                if (!trimmedLine.isEmpty()) {
                	List<String> sents = new ArrayList<String>();
                	List<String> cleanedSents = new ArrayList<String>();
                	this.getSentences(trimmedLine, sents, null, cleanedSents);	
                	int paraPos = 1;
                	for(String sen:sents)
                	{
                		Sentence s = new Sentence();
                		s.setSentId(sentNo++);
                		s.setParagraph(paraNo);
                		s.setStringVal(sen);
                		s.setParaPos(paraPos++);
                		sentList.add(s);
                	}
                	paraNo++;
                }
            }
            
            String doc = docBuffer.toString(); 	
        } catch (Exception ex) {
            Logger.getLogger(DocProcessor.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } finally {
            try {
                lnr.close();
            } catch (IOException ex) {
                Logger.getLogger(DocProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

	return sentList;
    }

    
	public List<Sentence> getSentencesFromStr(String text) {
		List<Sentence> ret = new ArrayList<Sentence>();

		List<String> sentStrs = new ArrayList<String>();
        List<String> cleanedSents = new ArrayList<String>();
        //old method
        if(SENTENCE_FRAG==CUSTOM)
               getSentences(text, sentStrs, null, cleanedSents);
        else{
        	StringReader sr = new StringReader(text);
        	DocumentPreprocessor dpp = new DocumentPreprocessor(sr);
        	for(List sentence : dpp)
        	{
        		StringBuffer b = new StringBuffer();
        		for(Object s: sentence)
        		{
        			b.append(s);
        			b.append(" ");
        		}        	
        		try {
            		String line = b.toString();
                	line = StringUtil.formatStanfOutput(line);
					if(!this.isScript(line))
						sentStrs.add(line);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Logger.getAnonymousLogger().info("Error while parsing.. Ignoring the line and marching on.. "+ e.getMessage());
				}
        	}
        }
        int sentNo = 0;
        
        for(String sen:sentStrs)
    	{        	
        	Sentence s = new Sentence();
    		s.setSentId(sentNo);
    		s.setParagraph(1);
    		s.setStringVal(sen);
    		s.setParaPos(sentNo);
    		ret.add(s);
    		sentNo++;
    	}
		return ret;
	}    
	

	public String[] getWords(String sent)
	{
		return sent.split(" ");
	}
}


/*  
while((sentEndIdx = str.indexOf('.',oldSentEndIdx+1))>-1)
    {
	String sentence = str.substring(oldSentEndIdx, sentEndIdx).trim();
	oldSentEndIdx = sentEndIdx+1;

	sentences.add(sentence);

	String[] words = sentence.split(" ");
	StringBuffer procSent = new StringBuffer();

	for(int i=0;i<words.length;i++)
	{
        if(sw.isStopWord(words[i])) continue;
                    
        stemmer.stem(words[i].trim());
		String stemedWrd = stemmer.toString();
        List<Integer> sentList= iidx.get(stemedWrd);
		if(sentList==null)
		{
			sentList = new ArrayList<Integer>();
		}
		sentList.add(sentences.size()-1);
		procSent.append(stemedWrd+" ");                            
		iidx.put(stemedWrd, sentList);
	}
	*/
	// System.out.println("Adding "+procSent.toString());
