package ru.tsu.inf.atexant.nlp.sentences;

public abstract class Sentence {
    protected String text;
    
    public Sentence(String s) {
        text = s;
    }
    
    public void setText(String s) {
        text = s;
    }
    
    public String getText() {
        return text;
    }
    
    public double getSimilarityWith(Sentence s) {
        return 0.5 * (s.getMeasureOfContainingIn(this) + getMeasureOfContainingIn(s));
    }
    
    public double getMeasureOfContainingIn(Sentence s) {
        return 0.0;
    }
}
