package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.ScorerParameterSet;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@RunWith(JMockit.class)
public class ChiSquareCollocationScorerTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");

    HashMap<String, Long> frequencies;
    HashMap<NGramField, Long> totalFrequencies;

    NGram nGram;

    long unigramFrequency = 100L;
    long bigramFrequency = 10L;
    long trigramFrequency = 1L;

    @Tested ChiSquareCollocationScorer target;

    @Before
    public void init(){

        frequencies = new HashMap<String, Long>();
        totalFrequencies = new HashMap<NGramField, Long>();

        ScorerParameterSet parameterSet = new ScorerParameterSet();
        parameterSet.threshold = 0.0;
        parameterSet.collocationBoost = 1.0;
        parameterSet.returnScoringOutput = false;

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

        target = new ChiSquareCollocationScorer(frequencies,
                totalFrequencies, parameterSet);
    }

    public void evaluateCollocation(){
        new MockUp<ChiSquareCollocationScorer>(){
            @Mock
            double chiSquareTrigram(NGram nGram, List<TreeMap<String,Object>> output)
            {
                return 1.0;
            }
            @Mock
            double chiSquareBigram(NGram nGram, List<TreeMap<String,Object>> output)
            {
                return 1.0;
            }
        };

        double expected = 10.0;
        double actual = target.evaluateCollocation(nGram, new ArrayList<TreeMap<String, Object>>());

        Assert.assertEquals(actual, expected, 1e-4);
    }

    @Test
    public void chiSquareTrigram(){
        double expected = 0.0;
        double actual = target.evaluateCollocation(nGram, new ArrayList<TreeMap<String, Object>>());
        Assert.assertEquals(expected, actual, 1e-4);
    }


    @Test
    public void chiSquareBigram(){
        double expected = 0.0;
        double actual = target.evaluateCollocation(nGram.getSubNGram(0, 2), new ArrayList<TreeMap<String, Object>>());
        Assert.assertEquals(expected, actual, 1e-4);
    }
}
