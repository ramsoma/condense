package newshog.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

import newshog.Sentence;

public class YEdGraphCreator {

	public void printGraphML(List<Sentence> s, Hashtable<Integer, List<Integer>> links, String fileName)
	{
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//System.out;
		//Header
		out.println("graph [");
		out.println("comment \"This is a sample graph\"");
		out.println("directed 1");
		out.println("id 42");
		out.println("label \"Hello, I am a graph\"");		
		
		//Print Nodes..
		for(int i=0;i<s.size();i++)
		{
			out.println("      node [");
			out.println("    id "+(i + 1));
			
			out.println("    label \""+s.get(i).getStringVal().replaceAll("\"", "")+"\"");
			out.println("    score "+ s.get(i).getPageRankScore().getScore());			
			out.println("]");
		}
		
		//Print Links..
		for(int i=0;i<s.size();i++)
		{
			List<Integer> currLinks = links.get(i);
			if(currLinks == null) continue;
			
			for(Integer j: currLinks)
			{
				if(j>i)
				{
					out.println("      edge");
					out.println("[    source "+(i+1));
					out.println("    target "+(j+1));
					out.println("    ]");
				}
			}			
		}
		
		//Footer
		out.println("    ]");
	}
}
