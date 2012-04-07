/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tsu.inf.atexant.nlp.sentences;

import java.util.*;
import ru.tsu.inf.atexant.nlp.WordNetSimilarityMeasurer;
import ru.tsu.inf.atexant.nlp.WordSimilarityMeasurer;

public class SentenceSemanticSimilarityMeasurer extends SentenceSimilarityMeasurer {
    
    public static String[] usefulTypes = new String[]{ "amod", "rcmod", "acmod", "admod", "neg" };
    
    private class SemanticNode {
        public SentenceTreeNode N;
        public List< SentenceTreeNode > ws = new LinkedList<SentenceTreeNode>();
        public int isVerb = 0;
    }
    
    private SentenceTreeBuilder treeBuilder = new SentenceTreeWithWordTokensBuilder();
    private WordSimilarityMeasurer wsm = WordNetSimilarityMeasurer.getInstance();
    
    private double NodeTopWeight = 0.7;
    private double NodeChildrenWeight = 0.3;

    public void setNodeTopWeight(double a) {
        if (a > 1.0 || a < 0.0) {
            throw new AssertionError("should be between [0..1]");
        }
        this.NodeTopWeight = a;
        this.NodeChildrenWeight = 1.0-a;
    }
    
    private double NounNodeWeight = 0.6;
    private double VerbNodeWeight = 0.4;

    public void setNounNodeWeight(double a) {
        if (a > 1.0 || a < 0.0) {
            throw new AssertionError("should be between [0..1]");
        }
        
        this.NounNodeWeight = a;
        this.VerbNodeWeight = 1 - a;
    }
    
    private SentenceTree buildTree(Sentence a) {
        return treeBuilder.buildSentenceTree(a.getText());
    }
    
    private boolean isUsefulRelType(String typeName) {
        for (String type : usefulTypes) {
            if (type.equalsIgnoreCase(typeName)) {
                return true;
            }
        }
        
        return false;
    }
    
    private SemanticNode buildSemanticNodeBySentenceTreeNode(SentenceTreeNode node) {
        SemanticNode result = new SemanticNode();
        result.N = node;
        
        if (node.getNodePOS().startsWith("V")) {
            result.isVerb = 1;
        } else if (node.getNodePOS().startsWith("NN")) {
            result.isVerb = 0;
        } else {
            return null;
        }
        
        for (SentenceTreeEdge edge : node.getChildrenEdges() ) {
            if (isUsefulRelType(edge.getType())) {
                result.ws.add(edge.getTo());
            }
        }

        return result;
    }
    
    private ArrayList< SemanticNode >[] buildSemanticNodeCollection(SentenceTree tree) {
        ArrayList< SemanticNode >[] result = new ArrayList[2];
        
        result[0] = new ArrayList<SemanticNode>();
        result[1] = new ArrayList<SemanticNode>();
                
        for (SentenceTreeNode node : tree.getNodes()) {
            SemanticNode current = buildSemanticNodeBySentenceTreeNode(node);
            if (current != null) {
                result[current.isVerb].add(current);
            }
        }
        
        return result;
    }
    
    private double getWordsSimilarity(SentenceTreeNode n1, SentenceTreeNode n2) {
        return wsm.getSimilarity(n1.getWordToken(), n2.getWordToken());
    }
    
    private double getCoefOneSentenceTreeNodeListSimilarToAnother(List< SentenceTreeNode > s1, List< SentenceTreeNode > s2) {
        double avg = 0.0;
        for (SentenceTreeNode tn1 : s1) {
            double max = 0.0;
            for (SentenceTreeNode tn2 : s2) {
                double sim = getWordsSimilarity(tn1, tn2);
                
                max = Math.max(max, sim);
            }
            
            avg += max;
        }
        
        return s1.size() >  0 ? avg / s1.size() : 0.0; 
    }
    
    protected double getSentenceTreeNodeListSimilarity(List< SentenceTreeNode > s1, List< SentenceTreeNode > s2) {
        double sim1 = getCoefOneSentenceTreeNodeListSimilarToAnother(s1, s2);
        double sim2 = getCoefOneSentenceTreeNodeListSimilarToAnother(s2, s1);
        
        return (sim1+sim2)/2.0;
    }
    
    private double getSimilarityOfSemanticNodes(SemanticNode a, SemanticNode b) {
        double topSim = getWordsSimilarity(a.N, b.N);
        
        if (a.ws.isEmpty() && b.ws.isEmpty()) {
            return topSim;
        }
        
        double childSim = getSentenceTreeNodeListSimilarity(a.ws, b.ws);
        
        return topSim * NodeTopWeight + childSim * NodeChildrenWeight;
    }
    
    private ArrayList< Double > getVectorForSpaceOfCollection(ArrayList< SemanticNode > space, ArrayList< SemanticNode > collection) {
        ArrayList< Double > result = new ArrayList<Double>();
        
        for (int i = 0; i < space.size(); i++) {
            double best = 0.0;
            
            for (int j = 0; j < collection.size(); j++) {
                best = Math.max(best, getSimilarityOfSemanticNodes(space.get(i), collection.get(j)));   
            }
            
            result.add(best);
        }
        
        return result;
    }
    
    private double getVectorsCosineMeasure(ArrayList< Double > a, ArrayList< Double > b) {
        double res = 0.0;
        
        if (a.size() != b.size()) {
            return 0.0;
        }
        double ad = 0.0;
        double bd = 0.0;
        for (int i = 0; i < a.size(); i++) {
            res += a.get(i)*b.get(i);
            ad += Math.pow(a.get(i), 2.0);
            bd += Math.pow(b.get(i), 2.0);
        }
        
        res /= Math.sqrt(ad) * Math.sqrt(bd);
        
        return Math.pow(res, Math.E);
    }
        
    @Override
    public double getSimilarityOfSentences(Sentence a, Sentence b) {
        
        SentenceTree aTree = buildTree(a);
        SentenceTree bTree = buildTree(b);
        
        ArrayList< SemanticNode >[] aCollection = buildSemanticNodeCollection(aTree);
        ArrayList< SemanticNode >[] bCollection = buildSemanticNodeCollection(bTree);  
        
        ArrayList< SemanticNode >[] spaces = new ArrayList[2];
        ArrayList< Double >[] aVectors = new ArrayList[2]; 
        ArrayList< Double >[] bVectors = new ArrayList[2]; 
        
        for (int i = 0; i < 2; i++) {
            spaces[i] = (ArrayList< SemanticNode >)aCollection[i].clone();
            spaces[i].addAll(bCollection[i]);
            
            aVectors[i] = getVectorForSpaceOfCollection(spaces[i], aCollection[i]);
            bVectors[i] = getVectorForSpaceOfCollection(spaces[i], bCollection[i]);
            
        }
        
        double[] cosines = new double[2];
        
        for (int i = 0; i < 2; i++) {
            cosines[i] = getVectorsCosineMeasure(aVectors[i], bVectors[i]);
        }
        
        return cosines[0] * NounNodeWeight + cosines[1] * VerbNodeWeight;
    }
    
}
