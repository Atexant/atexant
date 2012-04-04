/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

/**
 *
 * @author sufix
 */
public class SentenceComplexSimilarityMeasurer extends SentenceSimilarityMeasurer {
    private SentenceSimilarityMeasurer semanticMeasurer = new SentenceSemanticSimilarityMeasurer();
    private SentenceSimilarityMeasurer syntacticMeasurer = new SentenceSyntacticSimilarityMeasurer();
    
    @Override
    public double getSimilarityOfSentences(Sentence a, Sentence b) {
        return (semanticMeasurer.getSimilarityOfSentences(a, b) + syntacticMeasurer.getSimilarityOfSentences(a, b))/2.0;
    }
    
}
