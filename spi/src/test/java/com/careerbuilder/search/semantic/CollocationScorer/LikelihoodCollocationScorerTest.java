package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.ScorerParameterSet;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import mockit.*;
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
public class LikelihoodCollocationScorerTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");

    ScorerParameterSet parameterSet;

    HashMap<String, Long> frequencies;
    HashMap<NGramField, Long> totalFrequencies;

    NGram nGram;

    long unigramFrequency = 100L;
    long bigramFrequency = 10L;
    long trigramFrequency = 1L;

    @Tested LikelihoodCollocationScorer target;

    @Before
    public void init(){

        parameterSet = new ScorerParameterSet();
        parameterSet.threshold = 0.0;
        parameterSet.collocationBoost = 1.0;
        parameterSet.likelihoodTrigramMultiplier = 1.0;
        parameterSet.corpusSizeMultiplier = 1.0;
        parameterSet.returnScoringOutput = false;

        frequencies = new HashMap<String, Long>();
        totalFrequencies = new HashMap<NGramField, Long>();

        nGram = new NGram(tokens, 0, 5);
        for(int i = 0; i < tokens.length; ++i) {
            frequencies.put(nGram.getSubNGram(i, 1).toLowerCorpusString(), unigramFrequency);
        }
        for(int i = 0; i < tokens.length - 1; ++i) {
            frequencies.put(nGram.getSubNGram(i, 2).toLowerCorpusString(), bigramFrequency);
        }
        for(int i = 0; i < tokens.length - 2; ++i){
            frequencies.put(nGram.getSubNGram(i, 3).toLowerCorpusString(), trigramFrequency);
        }
        totalFrequencies.put(NGramField.content, unigramFrequency*tokens.length);
        totalFrequencies.put(NGramField.bigrams, unigramFrequency*tokens.length);
        totalFrequencies.put(NGramField.trigrams, unigramFrequency*tokens.length);

        target = new LikelihoodCollocationScorer(
                frequencies,
                totalFrequencies,
                parameterSet);
    }

    @Test
    public void evaluateCollocation(){
        new MockUp<LikelihoodCollocationScorer>(){
            @Mock
            double getLogLikelihood(Long total, Long tokenOne, Long tokenTwo, Long colloc, List<TreeMap<String, Object>> output)
            {
               return 1.0;
            }
        };

        double expected = 10.0;
        double actual = target.evaluateCollocation(nGram, new ArrayList<TreeMap<String, Object>>());

        Assert.assertEquals(expected, actual, 1e-4);
    }

    @Test
    public void getLogLikelihood(){
        double expected = -0.002387;
        double actual = target.evaluateCollocation(nGram, new ArrayList<TreeMap<String, Object>>());

        Assert.assertEquals(expected, actual, 1e-4);
    }
}
