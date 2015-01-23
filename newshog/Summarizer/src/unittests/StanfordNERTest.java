package unittests;

import newshog.*;
import newshog.dbpedia.DBPediaQuery;
import newshog.preprocess.DocProcessor;
import junit.framework.TestCase;

import java.util.*;

public class StanfordNERTest extends TestCase {

	public void testGetEntityListTest()
	{
		try{
			StanfordNER ner= new StanfordNER();
			String article = new DocProcessor().docToString("/home/ram/summarizer/test/tax.txt");
			List<Entity> rankedE = ner.getEntityList(article);
			List<String> links = new ArrayList<String>();

			DBPediaQuery dbpedia = new DBPediaQuery();
			for(Entity e: rankedE)
			{
				String lnk = dbpedia.getWikipediaLink(e.getFullName());
				System.out.println(e.getFullName()+" "+ e.getScore()+" "+lnk);
			}			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}	
}
