/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

abstract public class AbstractSentenceSimilarityMeasurer {
    protected SentenceTreeBuilder treeBuilder = new SentenceTreeWithWordTokensBuilder();
    
        
    protected SentenceTree buildTree(Sentence a) {
        return treeBuilder.buildSentenceTree(a.getText());
    }
    abstract public double getSimilarityOfSentences(Sentence a, Sentence b);
    public double getSimilarityOfSentences(String a, String b) {
        return getSimilarityOfSentences(new Sentence(a), new Sentence(b));
    }
}
