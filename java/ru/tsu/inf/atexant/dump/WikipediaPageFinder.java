/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.dump;

import ru.tsu.inf.atexant.WikipediaPage;

/**
 *
 * @author sufix
 */
public class WikipediaPageFinder extends WikipediaPageHandler {
    private WikipediaPageHandler handler = null;
    private Integer id;
    
    public WikipediaPageFinder(WikipediaPageHandler handler) {
        this.handler = handler;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    

    @Override
    public void handle(WikipediaPage page) {
        if (page.id.equals(id)) {
            handler.handle(page);
        }
    }
    
}
