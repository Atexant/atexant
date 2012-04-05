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
public class WordToken {
    private String word = null;
    protected CoreLabel token = null;
    
    public WordToken(String w) {
        word = w;
    }
    
    public WordToken(String w, CoreLabel t) {
        this(w);
        token = t;
    }
    
    public WordToken(CoreLabel t) {
        token = t;
        word = t.get(CoreAnnotations.TextAnnotation.class);
    }
    
    public String getWord() {
        return word;
    }
    
    public String getPOS() {
        if (token == null) {
            return null;
        }
        return token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
    }
    
    public String getLemma() {
        if (token == null) {
            return null;
        }
        return token.get(CoreAnnotations.LemmaAnnotation.class);
    }
}
