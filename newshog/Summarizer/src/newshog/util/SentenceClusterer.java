package newshog.util;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.FastVector;
import weka.core.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import java.util.List;
import java.util.ArrayList;
import weka.core.tokenizers.WordTokenizer;
import weka.clusterers.*;

public class SentenceClusterer
{
	Instances filteredSent;
	Clusterer c;
			
	private Instances getPreFilterInstances()
        {
	    FastVector fv = new FastVector();	
	    Attribute messageAtt = new Attribute("text", (FastVector)null);
            fv.addElement(messageAtt);

	    Instances ret = new Instances("data", fv, 1);
	    return ret;
        }

	private Instance makeInstance(String text, Instances dataset) {
	    // Create instance of length two.
	    Instance instance = new Instance(1);
	    // Give instance access to attribute information from the dataset.
	    instance.setDataset(dataset);
	    Attribute att = dataset.attribute(0); 	
	    // Set value for message attribute
	    instance.setValue(0, att.addStringValue(text));
	    return instance;
	}

	public StringToWordVector createFilter(String doc)
	{
	    StringToWordVector filter = new StringToWordVector();
 	    Instances data = getPreFilterInstances();
	    data.add(makeInstance(doc, data));
	    //  print(data);	 

	    // Initialize filter and tell it about the input format.
	    try{
		    WordTokenizer t = new WordTokenizer();	
		    t.setDelimiters(" ");
		    filter.setTokenizer(t);		
		    //filter.setIDFTransform(true);
	
		    filter.setInputFormat(data);
		    Instances filterdDataStructure = Filter.useFilter(data, filter);
	//	    print(filterdDataStructure);
  	    }catch(Exception ex){}
	    
	    return filter;	
	}
	
	private void print(Instances inst)
	{
	   for(int i=0;i<inst.numAttributes();i++) 
		System.out.println(inst.attribute(i));
	
	   for(int i=0;i<inst.numInstances();i++)
		System.out.println(inst.instance(i));
	}

	public Instances getFilteredInstances(String doc, List<String> sentences)
        {
	    StringBuffer docB = new StringBuffer();
	    for(String s: sentences) docB.append(s+" ");	
	    StringToWordVector filter = createFilter(docB.toString());
	
	    Instances filtered = null;
		
	    Instances inst = getPreFilterInstances();
	    for(String sent: sentences)
	    {
		inst.add(makeInstance(sent, inst));		 	    		    
	    }
 		
	    try{
	    	filtered = Filter.useFilter(inst, filter);	
		// print(filtered);
	   }catch(Exception ex){}
	   return filtered;
	}

	public Clusterer clusterSentences(Instances filteredSent) throws Exception
	{
		SimpleKMeans c = new SimpleKMeans();
		c.setNumClusters(2);
		//c.setMaxNumClusters(5);
		c.buildClusterer(filteredSent);
		return c;
	}
	
	public Clusterer runClusterer(String doc, List<String> sentences)
	{
		this.filteredSent = getFilteredInstances(doc, sentences);
		try{
		   c = clusterSentences(filteredSent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return c;
	}
	
	public int getCluster(int idx) throws Exception
	{
		if(filteredSent==null)
		  throw new Exception("Run filter before calling this method..");
		if(c==null)
		  throw new Exception("Create the clusterer before calling this method..");
				
		Instance i = filteredSent.instance(idx);
		return c.clusterInstance(i);
	}

	public static void main(String[] args)
	{
		String doc = "a b c, d e f,a b c d, d e f c,g h i, a b c d e f";
		SentenceClusterer sc = new SentenceClusterer();
		String[] words = doc.split(",");
	        ArrayList<String> l = new ArrayList<String>();
		
		for(String word: words) l.add(word);
		
//		sc.print(f);
	}
}
