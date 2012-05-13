/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp;

/**
 *
 * @author sufix
 */
abstract public class AbstractSentenceDependency {
    public abstract String getGovWord();
    public abstract String getDepWord();
    public abstract String getRelShortName();
}
