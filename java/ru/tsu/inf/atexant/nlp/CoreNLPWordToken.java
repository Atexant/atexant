/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

/**
 *
 * @author sufix
 */
public class CoreNLPWordToken extends WordToken {
    private String word = null;
    protected CoreLabel token = null;
    
    public CoreNLPWordToken(String w) {
        super(w);
    }
    
    public CoreNLPWordToken(String w, CoreLabel t) {
        this(w);
        token = t;
    }
    
    public CoreNLPWordToken(CoreLabel t) {
        super();
        token = t;
        word = t.get(CoreAnnotations.TextAnnotation.class);
    }
    
    @Override
    public String getPOS() {
        if (token == null) {
            return null;
        }
        return token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
    }
    
    @Override
    public String getLemma() {
        if (token == null) {
            return null;
        }
        return token.get(CoreAnnotations.LemmaAnnotation.class);
    }
    
}
