/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import java.util.ArrayList;
import ru.tsu.inf.atexant.nlp.CoreNLPAccess;
import ru.tsu.inf.atexant.nlp.WordToken;

/**
 *
 * @author sufix
 */
public class SentenceTreeWithWordTokensBuilder extends SentenceTreeBuilder {

    ArrayList< WordToken > posTags = null;
    
    @Override
    protected SentenceTreeNode buildNewTreeNode(String id) {
        SentenceTreeNode node = super.buildNewTreeNode(id);
        if (node.getWordId() > 0) {
            node.setWordToken(posTags.get(node.getWordId()-1));
        }
        return node;
    }

    @Override
    public SentenceTree buildSentenceTree(String sentenceText) {
        posTags = CoreNLPAccess.getInstance().getWordTokensFromText(sentenceText);
        return super.buildSentenceTree(sentenceText);
    }  
    
}
