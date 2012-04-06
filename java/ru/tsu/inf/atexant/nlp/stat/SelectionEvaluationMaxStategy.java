package ru.tsu.inf.atexant.nlp.stat;


public class SelectionEvaluationMaxStategy extends SelectionEvaluationStategy {

    @Override
    public double getValue(double[] a) {
        double best = 0.0;
        
        for (double t : a) {
            best = Math.max(t, best);
        }
        
        return best;
    }
    
}
