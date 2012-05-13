package ru.tsu.inf.atexant.nlp.sentences;

public class SentenceTreeEdge {
    private String type = null;
    private SentenceTreeNode from = null;
    private SentenceTreeNode to = null;
    
    public SentenceTreeEdge(SentenceTreeNode a, SentenceTreeNode b, String c) {
        from = a;
        to = b;
        type = c;
    } 
    
    public SentenceTreeEdge(SentenceTreeNode b, String c) {
        to = b;
        type = c;
    }
    
    public String getType() {
        return type;
    }
    
    public SentenceTreeNode getTo() {
        return to;
    }
    
    public SentenceTreeNode getFrom() {
        return from;
    }
}
