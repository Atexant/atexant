/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import java.util.*;
import ru.tsu.inf.atexant.nlp.NLPAccess;
import ru.tsu.inf.atexant.nlp.SentenceDependency;

public class SentenceTreeBuilder {
    
    private static SentenceTreeBuilder instance = new SentenceTreeBuilder();
    
    protected SentenceTreeBuilder() {
        
    }
    
    public static SentenceTreeBuilder getInstance() {
        return instance;
    }
    
    protected SentenceTreeNode buildNewTreeNode(String id) {
        SentenceTreeNode node = new SentenceTreeNode(id);
        
        return node;
    }
    
    private SentenceTreeNode findInMapByIdOrCreateSentenceTreeNode(Map< String, SentenceTreeNode > m, String id) {
        if (m.containsKey(id)) {
            return m.get(id);
        }
        
        SentenceTreeNode node = buildNewTreeNode(id);
        
        m.put(id, node);
        
        return node;
    }
    
    private SentenceTree buildSentenceTreeByTypedDependecies(Collection< SentenceDependency > deps) {
        SentenceTree result = new SentenceTree();
        
        Map< String, SentenceTreeNode > m = new HashMap< String, SentenceTreeNode>();
        
        for (SentenceDependency dependency : deps) {
           SentenceTreeNode gov = findInMapByIdOrCreateSentenceTreeNode(m, dependency.getGovWord());
           SentenceTreeNode dep = findInMapByIdOrCreateSentenceTreeNode(m, dependency.getDepWord());
           String dependencyType = dependency.getRelShortName();
           
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
        for (Collection< SentenceDependency > deps : getSentencesDependencies(text)) {
            result.add(buildSentenceTreeByTypedDependecies(deps));
        }
        
        return result;
    }

    private List< Collection< SentenceDependency > > getSentencesDependencies(String sentenceText) {
        return NLPAccess.getInstance().getSentencesDependecies(sentenceText);
    }
    
    private Collection< SentenceDependency > getSentenceDependencies(String sentenceText) {
        return NLPAccess.getInstance().getSentenceDependencies(sentenceText);
    }
}
