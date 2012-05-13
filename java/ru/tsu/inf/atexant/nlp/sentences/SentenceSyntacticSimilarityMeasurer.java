/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SentenceSyntacticSimilarityMeasurer extends AbstractSentenceSimilarityMeasurer {

    private String getSentenceTreeEdgeKey(SentenceTreeEdge edge) {
        return edge.getType() + "#" + edge.getFrom().getNodePOS().substring(0, 2) + "#" + edge.getTo().getNodePOS().substring(0, 2);
    }
    
    private List< String > getEdgesKeysListByTree(SentenceTree tree) {
        List< String > result = new LinkedList<String>();
        
        for (SentenceTreeNode node : tree.getNodes()) {
            for (SentenceTreeEdge edge : node.getChildrenEdges() ) {
                result.add(getSentenceTreeEdgeKey(edge));
            }
        }
        
        return result;
    }
    
    private double getSimilarityOfEdgeKeysSets(List< String > aEdgesKey, List< String > bEdgesKey) {
        if (aEdgesKey.isEmpty() || bEdgesKey.isEmpty()) {
            return 0.0;
        }
        
        Set< String > setA = new HashSet<String>(aEdgesKey);
        setA.retainAll(bEdgesKey);
        
        int intersectionSize = setA.size();
              
        double result = (2.0 * intersectionSize) / (aEdgesKey.size() + bEdgesKey.size());
        
        return result;
    }
    
    @Override
    public double getSimilarityOfSentences(Sentence a, Sentence b) {
        SentenceTree aTree = buildTree(a);
        SentenceTree bTree = buildTree(b);
        
        List< String > aEdgesKey = getEdgesKeysListByTree(aTree);
        List< String > bEdgesKey = getEdgesKeysListByTree(bTree);
        
        return getSimilarityOfEdgeKeysSets(aEdgesKey, bEdgesKey);
    }
    
}
