/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp;

/**
 *
 * @author sufix
 */
abstract public class AbstractWordSimilarityMeasurer {
    public abstract double getSimilarity(WordToken word1, WordToken word2);
    
    public double getSimilarity(String word1, String word2) {
        return getSimilarity(new WordToken(word1), new WordToken(word2));
    }
}
