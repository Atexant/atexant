package ru.tsu.inf.atexant.nlp.sentences;

import java.util.LinkedList;
import java.util.List;

public class SentenceTree {
    private SentenceTreeNode root = null;
    private List< SentenceTreeNode > nodes = new LinkedList<SentenceTreeNode>();
    
    public SentenceTree() {
        
    }
    
    public SentenceTree(SentenceTreeNode r) {
        root = r;
    }
    
    public SentenceTreeNode getRoot() {
        return root;
    }
    
    public void setRoot(SentenceTreeNode newRoot) {
        root = newRoot;
    }
    
    public void addNode(SentenceTreeNode node) { 
        nodes.add(node);
    }
    
    public List< SentenceTreeNode > getNodes() {
        return nodes;
    }
}
