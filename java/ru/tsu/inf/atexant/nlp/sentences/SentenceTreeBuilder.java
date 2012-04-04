/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import edu.stanford.nlp.trees.TypedDependency;
import java.util.*;
import ru.tsu.inf.atexant.nlp.NLPAccess;

public class SentenceTreeBuilder {
    
    private static SentenceTreeBuilder instance = new SentenceTreeBuilder();
    
    private SentenceTreeBuilder() {
        
    }
    
    public static SentenceTreeBuilder getInstance() {
        return instance;
    }
    
    private SentenceTreeNode findInMapByIdOrCreateSentenceTreeNode(Map< String, SentenceTreeNode > m, String id) {
        if (m.containsKey(id)) {
            return m.get(id);
        }
        
        SentenceTreeNode node = new SentenceTreeNode(id);
        
        m.put(id, node);
        
        return node;
    }
    
    private SentenceTree buildSentenceTreeByTypedDependecies(Collection< TypedDependency > deps) {
        SentenceTree result = new SentenceTree();
        
        Map< String, SentenceTreeNode > m = new HashMap< String, SentenceTreeNode>();
        
        for (TypedDependency dependency : deps) {
           SentenceTreeNode gov = findInMapByIdOrCreateSentenceTreeNode(m, dependency.gov().label().toString());
           SentenceTreeNode dep = findInMapByIdOrCreateSentenceTreeNode(m, dependency.dep().label().toString());
           String dependencyType = dependency.reln().getShortName();
           
           if (dependencyType.equalsIgnoreCase("root")) {
               result.setRoot(dep);
               continue;  
           }
           
           gov.addChidlrenEdge(dep, dependencyType);
           
        }
        
        for (SentenceTreeNode node : m.values()) {
            result.addNode(node);
        }
        
        return result;
    } 
    
    public SentenceTree buildSentenceTree(String sentenceText) {
        return buildSentenceTreeByTypedDependecies(getSentenceDependencies(sentenceText));
    }
    
    public List< SentenceTree> buildSentencesTrees(String text) {
        List< SentenceTree > result = new LinkedList<SentenceTree>();
        for (Collection< TypedDependency > deps : getSentencesDependencies(text)) {
            result.add(buildSentenceTreeByTypedDependecies(deps));
        }
        
        return result;
    }

    private List< Collection< TypedDependency > > getSentencesDependencies(String sentenceText) {
        return NLPAccess.getInstance().getSentencesDependecies(sentenceText);
    }
    
    private Collection< TypedDependency > getSentenceDependencies(String sentenceText) {
        return NLPAccess.getInstance().getSentenceDependencies(sentenceText);
    }
}
