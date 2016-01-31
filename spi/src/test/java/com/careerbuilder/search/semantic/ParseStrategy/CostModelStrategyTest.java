package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.CollocationScorer.ICollocationScorer;
import com.careerbuilder.search.semantic.Models.*;
import com.careerbuilder.search.semantic.TermFilter.ITermFilter;
import com.careerbuilder.search.semantic.Utility;
import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(JMockit.class)
public class CostModelStrategyTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");

    double threshold = 0.0;

    private final int COST_MODEL_MAX_QUERY_LENGTH = 16;

    HashMap<String, Long> frequencies;
    HashMap<NGramField, Long> totalFrequencies;

    @Mocked private SolrParams mockParams;
    @Mocked private SolrParams mockInvariants;
    @Mocked private SolrParams mockDefaults;
    @Mocked private ICollocationScorer mockScorer;
    @Mocked private ITermFilter mockTermFilter;
    @Mocked ArrayList<ITermFilter> mockFilters;

    ParseParameterSet parameterSet;

    @Before
    public void init(){

        mockParams = new MapSolrParams(new HashMap<String,String>());
        mockInvariants = new MapSolrParams(new HashMap<String,String>());
        mockFilters = new ArrayList<ITermFilter>();
        mockFilters.add(mockTermFilter);

        new NonStrictExpectations(){{
            mockInvariants.getDouble(anyString, anyDouble); returns(1.0);
            mockParams.getDouble(anyString, anyDouble); returns(1.0);
            mockScorer.evaluateCollocation((NGram)any, (List<TreeMap<String,Object>>)any); returns(1.0);
            mockTermFilter.filterParsings((TreeSet<Parsing>)any, (List<TreeMap<String,Object>>)any);
            parameterSet = new ParseParameterSet();
            parameterSet.knownPhrases = "";
            parameterSet.params = mockParams;
            parameterSet.filters = mockFilters;
            parameterSet.invariants = mockInvariants;
            parameterSet.collocationScalingFactor = 1.0;
            parameterSet.model = mockScorer;
            parameterSet.resultLimit = 10;
        }};
    }

    @Test
    public void generateParsings(){
        CostModelStrategy target = new CostModelStrategy(tokens, parameterSet);
        Deencapsulation.invoke(target, "generateParsings");

        LinkedList<Parsing> result = Deencapsulation.getField(target, "generatedParsings");
        int expectedSize = (int)Math.pow(2, tokens.length-1);
        Assert.assertEquals(result.size(), expectedSize);
    }

    @Test
    public void getUnfilteredParsings(){
        CostModelStrategy target = new CostModelStrategy(tokens, parameterSet);
        HashMap<Integer, Double> expectedParsingScores = getExpectedParsingScores();

        TreeSet<Parsing> actual = target.getUnfilteredParsings();

        for(Iterator<Parsing> iter = actual.iterator(); iter.hasNext();)
        {
            Parsing actualParsing = iter.next();
            Double expectedScore = expectedParsingScores.get(actualParsing.getIndex());
            Assert.assertEquals(expectedScore, actualParsing.getScore(), 1e-4);
        }
    }

    @Test
    public void invoke()
    {
        CostModelStrategy target = new CostModelStrategy(tokens, parameterSet);
        HashMap<Integer, Double> expectedParsingScores = getExpectedParsingScores();

        Parsing[] actual = target.invoke();

        for(Parsing actualParsing: actual)
        {
            Double expectedScore = expectedParsingScores.get(actualParsing.getIndex());
            Assert.assertEquals(expectedScore, actualParsing.getScore(), 1e-4);
        }
    }

    private HashMap<Integer, Double> getExpectedParsingScores() {
        int numParsings = (int)Math.pow(2, tokens.length-1);
        HashMap<Integer, Double> expectedParsingScores = new HashMap<Integer,Double>();
        for(int i = 0; i < numParsings; ++i)
        {
            boolean [] boundaries = new boolean[tokens.length - 1];
            Utility.intToBooleans(i, boundaries);
            int numBoundaries = 0;
            for(int k = 0; k < boundaries.length; ++k)
            {
                if(boundaries[k])
                {
                    ++numBoundaries;
                }
            }
            expectedParsingScores.put(i, (double)(numBoundaries+1)*(numBoundaries+1));
        }
        return expectedParsingScores;
    }
}
