/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import java.util.Properties;

/**
 *
 * @author sufix
 */
public class SentenceComplexSimilarityMeasurer extends AbstractSentenceSimilarityMeasurer {
    private AbstractSentenceSimilarityMeasurer semanticMeasurer = new SentenceSemanticSimilarityMeasurer();
    private AbstractSentenceSimilarityMeasurer syntacticMeasurer = new SentenceSyntacticSimilarityMeasurer();
    private double semanticWeight = 0.6;
    private double syntaticWeight = 0.4;
    
    public SentenceComplexSimilarityMeasurer() {
        
    }
    
    
    public SentenceComplexSimilarityMeasurer(double sem) throws Exception {
        setSemanticWeight(sem);
    }
    
    public SentenceComplexSimilarityMeasurer(Properties props) throws Exception {
        if (props.containsKey("semantic_weight")) {
            double w = Double.valueOf(props.getProperty("semantic_weight", "0.6"));
            setSemanticWeight(w);
        }
        
        semanticMeasurer = new SentenceSemanticSimilarityMeasurer(props);
    }
    
    public void setSemanticWeight(Double v) throws Exception {
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
