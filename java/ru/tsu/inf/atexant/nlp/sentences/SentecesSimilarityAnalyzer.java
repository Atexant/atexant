/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import java.io.Reader;
import java.util.*;
import ru.tsu.inf.atexant.nlp.CoreNLPAccess;

/**
 *
 * @author sufix
 */
public class SentecesSimilarityAnalyzer {
    
    public class SentencePair implements Comparable<SentencePair> {
        String text;
        double similarity;

        public SentencePair(String text, double similarity) {
            this.text = text;
            this.similarity = similarity;
        }

        @Override
        public int compareTo(SentencePair o) {
            return new Double(o.similarity).compareTo(new Double(this.similarity));          
        }
        
        
    }
    
    protected SentenceSimilarityMeasurer similarityMeasurer = new SentenceComplexSimilarityMeasurer();
    private boolean isDebug = false;
    
    public  SentencePair[] getSimilarSentences(String text, String sampleSentenceText) {
        ArrayList< SentencePair > result = new ArrayList<SentencePair>();
        
        Sentence sampleSentence = new Sentence(sampleSentenceText);
        
        for (String sentenceText : CoreNLPAccess.getInstance().getSentencesFromText(text)) {
           SentencePair sp = new SentencePair(
                        sentenceText, 
                        similarityMeasurer.getSimilarityOfSentences(
                                                        sampleSentence, 
                                                        new Sentence(sentenceText)
                                )
                    );
            result.add(sp);
            
            if (isDebug) {
                System.out.println(sp.text + " (" + new Double(sp.similarity) + ")");
            }
        }
        
        SentencePair[] res = new SentencePair[result.size()];
        
        result.toArray(res);
        
        Arrays.sort(res);
        
        return res;
    }
    
    public void searchSimilarToSampleSentence(String text) {
        System.out.print("Sample sentence: ");
        String sentence = new Scanner(System.in).nextLine();
        
        printSimilarSentencesInText(text, sentence);
    }
    
    public void printSimilarSentencesInText(String text, String sentence) {
        SentencePair[] result = getSimilarSentences(text, sentence);
        
        
        System.out.println("Similar sentences:");
        
        for (int i = 0 ; i < result.length; i++) {
            System.out.print(new Integer(i+1).toString() + ". ");
            System.out.println(result[i].text + " (" + new Double(result[i].similarity).toString() + ")");
        }      
    }
}
