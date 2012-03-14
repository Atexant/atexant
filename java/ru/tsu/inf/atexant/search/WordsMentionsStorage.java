package ru.tsu.inf.atexant.search;

import java.util.LinkedList;
import java.util.List;

public abstract class WordsMentionsStorage {
    public abstract void persistMention(String word, Integer documentId);
    public abstract Iterable< Integer > getRelatedDocumentsForWord(String word);
    
    public Iterable< Integer > getRelatedDocumentsForWords(List< String > words) {
        List< Integer > result = new LinkedList< Integer >();
        
        return result;
    }
}
