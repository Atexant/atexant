package ru.tsu.inf.atexant.nlp;

import java.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class Lemmatisation
{
    private StanfordCoreNLP pipeline ;

    public void init()
    {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse");
        pipeline = new StanfordCoreNLP(props);
    }

    public List< Collection< TypedDependency > > getSentencesStructures(String text)
    {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory grammaticalStructureFactory = tlp.grammaticalStructureFactory();
        
        Tree tree;
        
        List< Collection< TypedDependency > > result = new LinkedList< Collection< TypedDependency > >();
        
        for(CoreMap sentence: sentences) 
	{
	    tree = sentence.get(TreeAnnotation.class);
            
            GrammaticalStructure structure = grammaticalStructureFactory.newGrammaticalStructure(tree);
            
            result.add(structure.typedDependenciesCollapsedTree());
        }
        
        return result;
    }
    
     
}
