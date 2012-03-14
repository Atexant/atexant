package ru.tsu.inf.atexant.nlp;

import java.util.Iterator;
import java.util.List;

public class NLPAccess {
    public Iterable< String > getNormalizedWordsFromText(String text) {
        return new Iterable<String>() {

            @Override
            public Iterator<String> iterator() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
    public Iterable< String > getNormalizedWordsFromPiecesOfText(List< String> pieces) {
        StringBuilder text = new StringBuilder();
        
        for (String s : pieces) {
            text.append(s);
            text.append(".");
        }
        
        return getNormalizedWordsFromText(text.toString());
    }  
    
    public static NLPAccess getInstance() {
        return new NLPAccess();
    }
}
