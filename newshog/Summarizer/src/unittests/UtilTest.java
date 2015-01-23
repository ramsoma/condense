package unittests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import newshog.Entity;
import newshog.dbpedia.DBPediaQuery;
import newshog.thrift.server.WikiInfo;
import newshog.util.Util;

import org.junit.BeforeClass;
import org.junit.Test;

public class UtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		Util u = new Util();
		DBPediaQuery gen = new DBPediaQuery();
		List<String> name = new ArrayList<String>(); 
		name.add("Miley"); name.add("Cyrus");
		Entity ent = new Entity(name, Entity.PERSON_TYPE);
		gen.loadEntity(ent);
		WikiInfo wiki = u.copyToWikiInfo(ent);
		List<WikiInfo> ws = new ArrayList<WikiInfo>();
		ws.add(wiki);
		String jsonStr = u.toJSON(ws);
		System.out.println(jsonStr);
	}
}
