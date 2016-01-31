package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.*;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

@RunWith(JMockit.class)
public class BayesCollocationScorerTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");

    double threshold = 0.0;

    HashMap<String, Long> frequencies;
    HashMap<NGramField, Long> totalFrequencies;

    NGram nGram;

    ScorerParameterSet parameterSet;
    FrequencyMap frequencyMap;

    long unigramFrequency = 100L;
    long bigramFrequency = 10L;
    long trigramFrequency = 1L;

    @Tested BayesCollocationScorer target;

    @Before
    public void init(){

        parameterSet = new ScorerParameterSet();
        parameterSet.threshold = 0.0;
        parameterSet.returnScoringOutput = false;

        frequencies = new HashMap<String, Long>();
        totalFrequencies = new HashMap<NGramField, Long>();

        nGram = new NGram(tokens, 0, 5);
        for(int i = 0; i < tokens.length; ++i) {
            frequencies.put(nGram.getSubNGram(i, 1).toLowerCorpusString(), 100L);
        }
        for(int i = 0; i < tokens.length - 1; ++i) {
            frequencies.put(nGram.getSubNGram(i, 2).toLowerCorpusString(), 10L);
        }
        for(int i = 0; i < tokens.length - 2; ++i){
            frequencies.put(nGram.getSubNGram(i, 3).toLowerCorpusString(), 1L);
        }
        totalFrequencies.put(NGramField.content, unigramFrequency*tokens.length);
        totalFrequencies.put(NGramField.bigrams, bigramFrequency*tokens.length);
        totalFrequencies.put(NGramField.trigrams, trigramFrequency*tokens.length);

        frequencyMap = new FrequencyMap();
        frequencyMap.descriptionTermFrequencyMap = frequencies;

        target = new BayesCollocationScorer(
                frequencyMap,
                parameterSet);

    }

    @Test
    public void evaluateCollocation(){
        double expected = -7952.59350417;
        double actual = target.evaluateCollocation(nGram, new ArrayList<TreeMap<String, Object>>());

        Assert.assertEquals(expected, actual, 1e-4);
    }
}
