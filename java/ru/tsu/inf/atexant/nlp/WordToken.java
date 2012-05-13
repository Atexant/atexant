/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp;

/**
 *
 * @author sufix
 */
public class WordToken {
    protected String word = null;
    protected String pos = null;
    protected String lemma = null;
    
    protected WordToken() {
        
    }
    
    public WordToken(String w) {
        word = w;
    }
    
    public WordToken(String w, String p) {
        this(w);
        pos = p;
    }
    
    public WordToken(String w, String p, String l) {
        this(w, p);
        lemma = l;
    }
    
    public String getWord() {
        return word;
    }
    
    public String getPOS() {
        return pos;
    }
    
    public String getLemma() {
        return lemma;
    }
    
    public String getLemmaOrWord() {
        if (getLemma() != null) {
            return getLemma();
        }
        
        return word;
    }
}
