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
    private double semanticWeight = 0.6;
    private double syntaticWeight = 0.4;
    
    public SentenceComplexSimilarityMeasurer() {
        
    }
    
    
    public SentenceComplexSimilarityMeasurer(double sem) throws Exception {
        setSemanticWeight(sem);
        
    }
    
    public void setSemanticWeight(double v) throws Exception {
        if (v < 0 || v > 1.0) {
            throw new Exception("out of bounds");
        }
        semanticWeight = v;
        syntaticWeight = 1.0-v;
    }
    
    @Override
    public double getSimilarityOfSentences(Sentence a, Sentence b) {
        return semanticMeasurer.getSimilarityOfSentences(a, b) * semanticWeight + syntacticMeasurer.getSimilarityOfSentences(a, b) * syntaticWeight;
    }
    
}
