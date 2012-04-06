package ru.tsu.inf.atexant.nlp.stat;

import java.util.Arrays;


public class SelectionEvaluationQuantilStategy extends SelectionEvaluationStategy {

    @Override
    public double getValue(double[] a) {
        Arrays.sort(a);
        
        int k = a.length/2;
        if (a.length%2 == 0) { 
            return 0.5 * (a[k] + a[k+1]);
        } else {
            return a[k+1];
        }
    }
    
}
