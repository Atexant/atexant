package ru.tsu.inf.atexant.nlp;

import java.util.*;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;

public class NLPAccess {
    
    private static NLPAccess instance = new NLPAccess();
    
    public static NLPAccess getInstance() {
        return new NLPAccess();
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
}
