package newshog;

import java.util.List;
import java.util.ArrayList;

public class Entity implements Comparable<Entity>{
	public static final int PERSON_TYPE = 1;
	public static final int ORGANIZATION_TYPE=2;
	public static final int PLACE_TYPE=2;
	
	//multiPart names
	List<String> name;
	private int type;
	double score;
	private String webPage;
	private String pictureUrl;
	private String desc;
	
	public Entity(List<String> name, int type)
	{
		this.name = new ArrayList<String>();
		this.name = name;
		this.setType(type);
		this.score = 1;
	}
	
	public void addNamePart(String name)
	{
		
	}
	
	@Override
	public int compareTo(Entity o) {
		// TODO Auto-generated method stub
		return (this.score > o.score)?-1:(this.score == o.score) ? 0: 1;
	}
	
	public boolean compareNames(List<String> n1, List<String> n2)
	{
		boolean ret = false;
		for(String np1 : n1)
		{
			for(String np2:n2)
			{
				if(np1.length()>1 && np1.equals(np2))
				{
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	public boolean equals(Object o)
	{
		boolean ret = false;
		if(! (o instanceof Entity)) 
			ret = false;
		else {
			Entity e = (Entity) o;
			ret = compareNames(this.name, e.name);				
		}
		return ret;	
	}
	
	public double getScore()
	{
		return score;
	}
	public String toString()
	{
		return this.getFullName() +" "+ this.webPage+" ";//this.type + " "+this.score 
	}
	
	private String fullName;
	public String getFullName()
	{
		StringBuffer b = new StringBuffer();
		for(int i=0;i<name.size();i++)
		{
			b.append(this.name.get(i));
			if(i<name.size()-1) b.append(" ");
		}
		fullName = b.toString();
		return b.toString();
	}

	public int hashCode()
	{
		return this.getFullName().hashCode();	
	}

	public String getWebPage() {
		return webPage;
	}

	public void setWebPage(String webPage) {
		this.webPage = webPage;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}	
}