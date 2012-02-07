package ru.tsu.inf.atexant.storages;

import ru.tsu.inf.atexant.WikipediaPage;

import java.lang.Iterable;

public abstract class WikipediaPageStorage {
    public abstract void savePage(WikipediaPage p) throws Exception;
    public abstract WikipediaPage findById(int id) throws Exception;    
    public abstract Iterable< WikipediaPage > getAll() throws Exception;
    
    public WikipediaPage findByTitle(String title) throws Exception {
        throw new Exception("undefined behavior");
    }
    
    protected static WikipediaPage buildNew() {
        return new WikipediaPage();
    }
}
