package ru.tsu.inf.atexant.nlp;

import java.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;

public class Lemmatisation
{
    private StanfordCoreNLP pipeline ;

    public void init()
    {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse");
        pipeline = new StanfordCoreNLP(props);
    }

    public void process(String text, Tree tree)
    {
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) 
	{
	    // traversing the words in the current sentence
	    // a CoreLabel is a CoreMap with additional token-specific methods
	    tree = sentence.get(TreeAnnotation.class);
        }
        
  
    }
    
    
}
