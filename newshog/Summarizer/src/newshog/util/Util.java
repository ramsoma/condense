package newshog.util;

import java.util.List;
import java.util.logging.Logger;

import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import newshog.Entity;
import newshog.thrift.server.WikiInfo;

//Utility methods
public class Util {
	
	//Copy an entityobject to a (wire friendly) wiki-info object..
	public static WikiInfo copyToWikiInfo(Entity en){
		WikiInfo wiki = new WikiInfo();
		if(en.getFullName()!=null)   wiki.setTitle(en.getFullName());
		if(en.getDesc()!=null)       wiki.setDesc(en.getDesc());
		if(en.getWebPage()!= null)   wiki.setPageUrl(en.getWebPage());
		if(en.getPictureUrl()!=null) wiki.setPicUrl(en.getPictureUrl());
		return wiki;
	}

	//Converts a list of WikiInfo objects to JSON..
	public static String toJSON(List<WikiInfo> wikiLinks)
		{
			JSONObject ret = new JSONObject();
			JSONArray arr = new JSONArray();
			for(WikiInfo w : wikiLinks)
			{
				JSONObject o= new JSONObject();
				try {
					o.put("Title", w.getTitle());
					if(w.getDesc()!=null) o.put("Description",w.getDesc());
					if(w.getPageUrl()!=null) o.put("PageUrl", w.getPageUrl());
					if(w.getPicUrl()!=null) o.put("PicUrl", w.getPicUrl());
				} catch (JSONException e) {
					//logger.info("Error occurred while setting json object." + e.getMessage());					
				}
				arr.put(o);
			}
			try {
				ret.put("WikiLinks", arr);
			} catch (JSONException e) {
				// logger.info("Error occurred while setting json object." + e.getMessage());
			}
			return ret.toString();
		}
}
