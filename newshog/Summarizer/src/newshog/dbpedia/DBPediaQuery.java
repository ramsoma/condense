package newshog.dbpedia;

import java.util.logging.Logger;

import newshog.Entity;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class DBPediaQuery {

	String prefix = "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
			"PREFIX dc: <http://purl.org/dc/elements/1.1/>" +
			"PREFIX : <http://dbpedia.org/resource/>" +
			"PREFIX dbpedia2: <http://dbpedia.org/property/>" +
			"PREFIX dbpedia: <http://dbpedia.org/>" +
			"PREFIX dbpedia-owl: <http://dbpedia.org/resource/classes#>" +
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> ";
	private String dbpediaServer = "http://dbpedia.org/sparql";
//	private String dbpediaServer = "http://dbpedia.org/sparql";
	
	public String getWikipediaLink(String entity)
	{
		String q = prefix + "SELECT DISTINCT ?s  ?lnk WHERE { "
			+ "?s <http://www.w3.org/2000/01/rdf-schema#label> '"+ entity +"'@en ."
			+ "FILTER (!regex(str(?s), '^http://dbpedia.org/resource/Category:'))."
			+ "FILTER (!regex(str(?s), '^http://sw.opencyc.org/'))."
			+ " ?s foaf:isPrimaryTopicOf  ?lnk."
			+ " ?s foaf:isPrimaryTopicOf  ?lnk."			
			+ "} LIMIT 1";
		
		//dbpedia-owl:thumbnail 
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(dbpediaServer, query);
		ResultSet results = qexec.execSelect();
		RDFNode lnk = null;
		if (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			lnk = result.get("lnk");
		}
		
		return lnk!=null?lnk.toString():"";
	}

	public Entity loadFromRedirect(Entity e)
	{
		String q = prefix + "SELECT DISTINCT ?s  ?lnk ?desc ?pic WHERE { "
				+ "?sdash <http://www.w3.org/2000/01/rdf-schema#label> '"+ e.getFullName() +"'@en ."
				+"?sdash <http://dbpedia.org/ontology/wikiPageRedirects> ?s. "
				+ "FILTER (!regex(str(?s), '^http://dbpedia.org/resource/Category:'))."
				+ "FILTER (!regex(str(?s), '^http://sw.opencyc.org/'))."
				+ " ?s foaf:isPrimaryTopicOf  ?lnk."
				+ " ?s foaf:isPrimaryTopicOf  ?lnk."
				+ "optional{?s <http://www.w3.org/2000/01/rdf-schema#comment> ?desc}.FILTER(langMatches(lang(?desc), \"en\"))"
				+" .optional{?s <http://dbpedia.org/ontology/thumbnail> ?pic}"
				+ "} LIMIT 1";
		
		Logger.getAnonymousLogger().info(q);
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(dbpediaServer, query);
		ResultSet results = qexec.execSelect();
		RDFNode s = null;
		if (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			e = this.setEntityFromResult(result, e);
        }
		return e;
	}
	
	public Entity loadEntity(Entity e) {
		String q = prefix + "SELECT DISTINCT ?s  ?lnk ?desc ?pic WHERE { "
				+ "?s <http://www.w3.org/2000/01/rdf-schema#label> '"+ e.getFullName() +"'@en ."
				+ "FILTER (!regex(str(?s), '^http://dbpedia.org/resource/Category:'))."
				+ "FILTER (!regex(str(?s), '^http://sw.opencyc.org/'))."
				+ " ?s foaf:isPrimaryTopicOf  ?lnk."
				+ "optional{?s <http://www.w3.org/2000/01/rdf-schema#comment> ?desc}.FILTER(langMatches(lang(?desc), \"en\"))"
				+" .optional{?s <http://dbpedia.org/ontology/thumbnail> ?pic} "
				+ "} LIMIT 1";
		
		Logger.getAnonymousLogger().info(q);
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(dbpediaServer, query);
		ResultSet results = qexec.execSelect();
		RDFNode lnk = null;
		if (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			e = setEntityFromResult(result,e);
		}
		//try to load from a redirected link if the first one did not work..
		if(e.getPictureUrl()==null) e = loadFromRedirect(e);
		return e;
	}

	private Entity setEntityFromResult(QuerySolution result,Entity e) {
		// TODO Auto-generated method stub
		RDFNode s = result.get("s");
		RDFNode lnk = result.get("lnk");
		RDFNode pic = result.get("pic");
		RDFNode desc = result.get("desc");
		if(lnk!=null)
			e.setWebPage(lnk.toString());
		if(pic!=null)
			e.setPictureUrl(pic.toString());
		if(desc!=null)
			e.setDesc(desc.toString());

		return e;
	}
}
