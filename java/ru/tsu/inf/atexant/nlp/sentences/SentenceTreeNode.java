package ru.tsu.inf.atexant.nlp.sentences;

import java.util.LinkedList;
import java.util.List;

public class SentenceTreeNode {
    private String word;
    private String wordId = null;
    private List< SentenceTreeEdge > childrenEdges = null;
    private SentenceTreeEdge edgeFromParent = null;
    
    public SentenceTreeNode(String u) {
        word = "";
        
        if (u.contains("-")) {
            String[] a = u.split("-");
            word = a[0];
            wordId = a[1];
        } else {
            word = u;
        }
        
        childrenEdges = new LinkedList<SentenceTreeEdge>();
    }
    
    public String getWord() {
        return word;
    }
    
    public String getWordId() {
        return wordId;
    } 
    
    private void setEdgeFromParent(SentenceTreeEdge edge) {
        edgeFromParent = edge;
    }
    
    public String getParentDependencyType() {
        return edgeFromParent.getType();
    }
    
    public SentenceTreeNode getParent() {
        return edgeFromParent.getFrom();
    }
        
    public void addChidlrenEdge(SentenceTreeNode node, String type) {
        
        
        SentenceTreeEdge edge = new SentenceTreeEdge(this, node, type);
        
        childrenEdges.add(edge);
        node.setEdgeFromParent(edge);
    }
    
    public List< SentenceTreeEdge > getChildrenEdges() {
        return childrenEdges;
    } 
    
    public List< SentenceTreeNode > getChildren() {
        List< SentenceTreeNode > result = new LinkedList<SentenceTreeNode>();
        
        for (SentenceTreeEdge e : getChildrenEdges()) {
            result.add(e.getTo());
        }
        
        return result;
    }
}
