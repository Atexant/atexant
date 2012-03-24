package ru.tsu.inf.atexant.nlp;

import java.util.*;

import ru.tsu.inf.atexant.nlp.sentences.SentenceTree;
import ru.tsu.inf.atexant.nlp.sentences.SentenceTreeNode;
import ru.tsu.inf.atexant.nlp.sentences.SentenceTreeEdge;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;

public class NLPAccess {
    
    private static NLPAccess instance = new NLPAccess();
    
    public static NLPAccess getInstance() {
        return instance;
    }
    
    private StanfordCoreNLP pipeline = null;
    
    private void init()
    {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse");
        pipeline = new StanfordCoreNLP(props);
    }
    
    
    private NLPAccess() {
        init();
    }
    
    public boolean isMeaningfulPOS(String pos) {
        //FW, JJ*, NN*, RB*, V*
        if (pos.startsWith("FW") || pos.startsWith("JJ") || pos.startsWith("NN") || pos.startsWith("RB") || pos.startsWith("V")) {
            return true;
        }
        
        return false;
    }
    
    public Iterable< String > getNormalizedWordsFromText(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        HashSet< String > wordsSet = new HashSet<String>();
        
        List< String > result = new LinkedList<String>();
        
        String p;
        
        for(CoreMap sentence: sentences) 
	{
            for (CoreLabel token: sentence.get(TokensAnnotation.class))  {
                p = token.get(LemmaAnnotation.class).toLowerCase();
                
                if (p.charAt(0) < 'a' || p.charAt(0) > 'z') {
                    continue;
                }
                
                String pos = token.get(PartOfSpeechAnnotation.class);
                
                if (isMeaningfulPOS(pos) && !wordsSet.contains(p) && p.length() > 2) {      
                    wordsSet.add(p);
                    result.add(p);
                }
            }
        }
        
        return result;
    }
    
    public Iterable< String > getNormalizedWordsFromPiecesOfText(List< String> pieces) {
        StringBuilder text = new StringBuilder();
        
        for (String s : pieces) {
            text.append(s);
            text.append(".");
        }
        
        return getNormalizedWordsFromText(text.toString());
    }
    
    public double sentencesSimilarity(String sentence1, String Sentence2) {
        return 0.0;
    }
    
    public List< Collection< TypedDependency > > getSentencesDependecies(String text)
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
    
    public Collection< TypedDependency > getSentenceDependencies(String sentenceText) {
        return getSentencesDependecies(sentenceText).get(0);
    }
    
    private SentenceTreeNode findInMapByIdOrCreateSentenceTreeNode(Map< String, SentenceTreeNode > m, String id) {
        if (m.containsKey(id)) {
            return m.get(id);
        }
        
        SentenceTreeNode node = new SentenceTreeNode(id);
        
        m.put(id, node);
        
        return node;
    }
    
    private SentenceTree buildSentenceTreeByTypedDependecies(Collection< TypedDependency > deps) {
        SentenceTree result = new SentenceTree();
        
        Map< String, SentenceTreeNode > m = new HashMap< String, SentenceTreeNode>();
        
        for (TypedDependency dependency : deps) {
           SentenceTreeNode gov = findInMapByIdOrCreateSentenceTreeNode(m, dependency.gov().label().toString());
           SentenceTreeNode dep = findInMapByIdOrCreateSentenceTreeNode(m, dependency.dep().label().toString());
           String dependencyType = dependency.reln().getShortName();
           
           if (dependencyType.equalsIgnoreCase("root")) {
               result.setRoot(dep);
               continue;  
           }
           
           gov.addChidlrenEdge(dep, dependencyType);
           
        }
        
        for (SentenceTreeNode node : m.values()) {
            result.addNode(node);
        }
        
        return result;
    } 
    
    public SentenceTree buildSentenceTree(String sentenceText) {
        return buildSentenceTreeByTypedDependecies(getSentenceDependencies(sentenceText));
    }
    
    public List< SentenceTree> buildSentencesTrees(String text) {
        List< SentenceTree > result = new LinkedList<SentenceTree>();
        for (Collection< TypedDependency > deps : getSentencesDependecies(text)) {
            result.add(buildSentenceTreeByTypedDependecies(deps));
        }
        
        return result;
    }

}
