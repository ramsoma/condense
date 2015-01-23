package unittests;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import newshog.Entity;
import newshog.dbpedia.DBPediaQuery;

import org.junit.BeforeClass;
import org.junit.Test;

public class DBPediaQueryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		List<String> name = new ArrayList<String>();
		name.add("Miley"); name.add("Cyrus");
		Entity e = new Entity(name, 0);
		DBPediaQuery q = new DBPediaQuery();
		e = q.loadEntity(e);
		System.out.println(e.getDesc());
		System.out.println(e.getPictureUrl());
		assertEquals(e.getDesc(),"");
		assertEquals(e.getPictureUrl(),"");
	}

}
