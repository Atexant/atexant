package ru.tsu.inf.atexant.search;

import java.util.LinkedList;

public class FileSystemWordsMentionsStorage extends WordsMentionsStorage {

    @Override
    public void persistMention(String word, Integer documentId) {
        int a=0;
    }
    
    @Override
    public Iterable<Integer> getRelatedDocumentsForWord(String word) {
        return new LinkedList<Integer>();
    }
    
}
