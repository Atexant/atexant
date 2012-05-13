/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp;

import edu.stanford.nlp.trees.TypedDependency;

/**
 *
 * @author sufix
 */
public class CoreNLPSentenceDependency extends AbstractSentenceDependency {
    private TypedDependency dependency = null;
    
    public CoreNLPSentenceDependency(TypedDependency td) {
        dependency = td;
    }
    
    @Override
    public String getDepWord() {
        return dependency.dep().label().toString();
    }
    
    @Override
    public String getGovWord() {
        return dependency.gov().label().toString();
    }

    @Override
    public String getRelShortName() {
        return dependency.reln().getShortName();
    }

}
