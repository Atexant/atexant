/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import java.util.*;
import ru.tsu.inf.atexant.nlp.CoreNLPAccess;
import ru.tsu.inf.atexant.nlp.AbstractSentenceDependency;

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
    
    private SentenceTree buildSentenceTreeByTypedDependecies(Collection< AbstractSentenceDependency > deps) {
        SentenceTree result = new SentenceTree();
        
        Map< String, SentenceTreeNode > m = new HashMap< String, SentenceTreeNode>();
        
        for (AbstractSentenceDependency dependency : deps) {
           String dependencyType = dependency.getRelShortName();
           SentenceTreeNode dep = findInMapByIdOrCreateSentenceTreeNode(m, dependency.getDepWord());
           if (dependencyType.equalsIgnoreCase("root")) {
               result.setRoot(dep);
               continue;  
           }
           SentenceTreeNode gov = findInMapByIdOrCreateSentenceTreeNode(m, dependency.getGovWord());
           
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
        for (Collection< AbstractSentenceDependency > deps : getSentencesDependencies(text)) {
            result.add(buildSentenceTreeByTypedDependecies(deps));
        }
        
        return result;
    }

    private List< Collection< AbstractSentenceDependency > > getSentencesDependencies(String sentenceText) {
        return CoreNLPAccess.getInstance().getSentencesDependecies(sentenceText);
    }
    
    private Collection< AbstractSentenceDependency > getSentenceDependencies(String sentenceText) {
        return CoreNLPAccess.getInstance().getSentenceDependencies(sentenceText);
    }
}
