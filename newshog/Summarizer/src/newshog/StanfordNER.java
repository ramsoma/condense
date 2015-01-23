package newshog;

import edu.stanford.nlp.ie.crf.*; 
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.*;

public class StanfordNER {
	
	CRFClassifier<CoreLabel> classifier;
	String modelFile = "./resources/english.all.3class.distsim.crf.ser.gz";
	
	//Stanford NER types..
	private static final String person = "PERSON";
	private static final String org    = "ORGANIZATION";
	private static final String place  = "PLACE";
	
	public StanfordNER() throws ClassCastException, IOException, ClassNotFoundException
	{
		classifier = CRFClassifier.getClassifier(modelFile);
	}
	
	private Entity makeEntity(List<String > name, int entityType) {
		return new Entity(name, entityType);
	}		

	private int getEntityType(String type)
	{
		int ret = Entity.PERSON_TYPE;
		if(type.equals(org)) ret = Entity.ORGANIZATION_TYPE;
		else if(type.equals(place)) ret = Entity.PLACE_TYPE;
		return ret;
	}
	
	private List<Entity> mergeAndRankEntities(List<Entity> inpList)
	{
		Hashtable<Entity, Entity> merged = new Hashtable<Entity, Entity>();
		
		for(Entity e: inpList)
		{
			if(merged.get(e)!=null)
			{
				Entity me = merged.get(e);
				me.score += e.score;
				merged.put(e, me);
			}
			else
				merged.put(e,e);
		}
		
		Enumeration<Entity> e = merged.keys();
		List<Entity> out = new ArrayList<Entity>();
		
		while(e.hasMoreElements())
		{
			out.add(merged.get(e.nextElement()));
		}
		out = removeMultiNameDups(out);
		Collections.sort(out);
		return out;
	}
	
	private List<Entity> removeMultiNameDups(List<Entity> out) {
		List<Entity> newL = new ArrayList<Entity>();
		for(Entity e: out)
		{
			boolean isNew = true;
			for(Entity e1: newL)
			{
				if(e.equals(e1)){
					isNew = false;
					e1.score += e.score;
					
					//Give preference to two part names for person names..
					if(e.getType() == e.PERSON_TYPE && e.name.size() == 2 && e1.name.size()!=2)
						e1.name = e.name;
						
				}
			}
			if(isNew) 
			{
				newL.add(e);
			}
		}		
		return newL;
	}

	public List<Entity> getEntityList(String article)
	{
		List<Entity> ents = new ArrayList<Entity>();
		List<List<CoreLabel>> res = classifier.classify(article);
		
		for(List<CoreLabel> sent: res)
		{
			Iterator<CoreLabel> it = sent.iterator();
			boolean startEntity = false;
			List<String> currEntityName = null;
			String prevTokenType = ""; 
			
			while(it.hasNext())
			{
				CoreLabel m = it.next();
				String k = m.get(CoreAnnotations.AnswerAnnotation.class);
				
				//Merge multi-part names..
				if(!k.equals(prevTokenType) && !prevTokenType.equals("O") && currEntityName!=null)
				{
					    //getScore()
						Entity e = makeEntity(currEntityName, this.getEntityType(prevTokenType));
						ents.add(e);
				}
		
				if(k.equals(person)||k.equals(org)||k.equals(place))
				{
					if(!k.equals(prevTokenType))
						currEntityName = new ArrayList<String>();

					currEntityName.add(m.get(CoreAnnotations.TextAnnotation.class));															
				}
				prevTokenType = k;
			}
		}		
		return mergeAndRankEntities(ents);
	}
}