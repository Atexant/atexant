package ru.tsu.inf.atexant.nlp.sentences;

public class Sentence {
    protected String text;
    private SentenceTree tree = null;
    
    public Sentence(String s) {
        text = s;
    }
      
    public String getText() {
        return text;
    }
    
    public SentenceTree getTree() {
        if (tree == null) {
            tree = SentenceTreeBuilder.getInstance().buildSentenceTree(text);
        }
        
        return tree;
    }
}
