package ru.tsu.inf.atexant.nlp;

import java.util.*;


import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;

public class CoreNLPAccess {
    
    private static CoreNLPAccess instance = new CoreNLPAccess();
    
    public static CoreNLPAccess getInstance() {
        return instance;
    }
    
    private StanfordCoreNLP splitPipeline = null;
    private StanfordCoreNLP posPipeline = null;
    private StanfordCoreNLP parsepipeline = null;
    
    private void init()
    {
        Properties props = new Properties();
        
        props.put("annotators", "tokenize, ssplit");
        splitPipeline = new StanfordCoreNLP(props);
        
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        posPipeline = new StanfordCoreNLP(props);
        
        props.put("annotators", "tokenize, ssplit, parse, pos, lemma");
        parsepipeline = new StanfordCoreNLP(props);
    }
    
    
    private CoreNLPAccess() {
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
        posPipeline.annotate(document);

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
    
    private Iterable< CoreLabel > getCoreLabelsOfWordsInText(String text) {
        Annotation document = new Annotation(text);
        posPipeline.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
               
        ArrayList< CoreLabel > result = new ArrayList<CoreLabel>();
        
        String p;
        
        for(CoreMap sentence: sentences) 
	{
            for (CoreLabel token: sentence.get(TokensAnnotation.class))  {                        
                result.add(token);
            }
        }
        
        return result;
    }
    
    public ArrayList< WordToken > getWordTokensFromText(String text) {
        ArrayList< WordToken > result = new ArrayList<WordToken>();
        
        for (CoreLabel cl : getCoreLabelsOfWordsInText(text)) {
            result.add(new CoreNLPWordToken(cl));
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
    
    public List< Collection< AbstractSentenceDependency > > getSentencesDependecies(String text)
    {
        Annotation document = new Annotation(text);
        parsepipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory grammaticalStructureFactory = tlp.grammaticalStructureFactory();
        
        Tree tree;
        
        List< Collection< AbstractSentenceDependency > > result = new LinkedList< Collection< AbstractSentenceDependency > >();
        
        for(CoreMap sentence: sentences) 
	{
	    tree = sentence.get(TreeAnnotation.class);
            
            GrammaticalStructure structure = grammaticalStructureFactory.newGrammaticalStructure(tree);
            
            Collection< AbstractSentenceDependency > current = new LinkedList<AbstractSentenceDependency> ();
            
            for (TypedDependency td : structure.typedDependenciesCollapsedTree()) {
                current.add(new CoreNLPSentenceDependency(td));
            }
            
            result.add(current);
        }
        
        return result;
    }
    
    public Collection< AbstractSentenceDependency > getSentenceDependencies(String sentenceText) {
        return getSentencesDependecies(sentenceText).get(0);
    }
    
    public Collection< String > getSentencesFromText(String text) {
        Collection< String > result = new LinkedList<String>();
        
        Annotation document = new Annotation(text);
        splitPipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        for (CoreMap sentence : sentences) {
            result.add(sentence.get(TextAnnotation.class));
        }
        
        return result;
    }
    
}
