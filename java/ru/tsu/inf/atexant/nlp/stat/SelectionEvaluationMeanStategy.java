package ru.tsu.inf.atexant.nlp.stat;


public class SelectionEvaluationMeanStategy extends SelectionEvaluationStategy {

    @Override
    public double getValue(double[] a) {
        double sum = 0.0;
        
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        
        return a.length > 0 ? sum / a.length : 0.0;
    }
    
}
