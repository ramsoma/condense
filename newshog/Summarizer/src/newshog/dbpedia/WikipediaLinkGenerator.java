package newshog.dbpedia;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import newshog.Entity;
import newshog.StanfordNER;

public class WikipediaLinkGenerator {
	StanfordNER ner ;
	DBPediaQuery dbp;
	
	public WikipediaLinkGenerator()throws Exception
	{
		Logger.getAnonymousLogger().info("Initializing wikipedialink generator");
        ner = new StanfordNER();		
    	dbp = new DBPediaQuery();		
	}
	
    public List<Entity> getLinks(String doc, int maxEnts) throws ClassCastException, IOException, ClassNotFoundException
    {
    	long subModStrt = System.currentTimeMillis();
		List<Entity> ents = ner.getEntityList(doc);	
		Logger.getAnonymousLogger().info("Get entities in:"+(System.currentTimeMillis() -subModStrt));
		Hashtable<Entity, String> ret = new Hashtable<Entity, String>();
		
		subModStrt = System.currentTimeMillis();

		int cnt = 0;
		for(int i=0;(cnt< maxEnts && i< ents.size());i++)
		{
			try{
				Entity e = ents.get(i);
				dbp.loadEntity(e);
//				if(!e.getWebPage().equals("")) 
				cnt++;
		    }catch(Exception ex)
		    {
		    	ex.printStackTrace();
		    }
		}
		System.out.println(System.currentTimeMillis() -subModStrt);
		return ents;
    }
}
