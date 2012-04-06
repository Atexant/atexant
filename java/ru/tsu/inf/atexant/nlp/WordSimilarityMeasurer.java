/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp;

/**
 *
 * @author sufix
 */
abstract public class WordSimilarityMeasurer {
    public abstract double getSimilarity(WordToken word1, WordToken word2);

}
