package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.CollocationScorer.ICollocationScorer;
import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.ParseParameterSet;
import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import com.careerbuilder.search.semantic.TermFilter.ITermFilter;
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
public class ThesholdedAppendStrategyTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");

    @Mocked private SolrParams mockParams;
    @Mocked private SolrParams mockInvariants;
    @Mocked private SolrParams mockDefaults;
    @Mocked private ICollocationScorer unigramMockScorer;
    @Mocked private ICollocationScorer onePhraseMockScorer;
    @Mocked private ICollocationScorer threePhraseMockScorer;
    @Mocked private ITermFilter mockTermFilter;
    @Mocked ArrayList<ITermFilter> mockFilters;

    ParseParameterSet parameterSet;

    @Before
    public void init(){

        mockParams = new MapSolrParams(new HashMap<String,String>());
        mockFilters = new ArrayList<ITermFilter>();
        mockFilters.add(mockTermFilter);

        new NonStrictExpectations(){{
            mockInvariants.getDouble(anyString, 0.0); returns(1.0);
            mockParams.getDouble("costModelScoreSensitivity", anyDouble); returns(1.0);
            unigramMockScorer.evaluateCollocation((NGram)any, (List<TreeMap<String,Object>>)any); returns(-1.0);
            onePhraseMockScorer.evaluateCollocation((NGram)any, (List<TreeMap<String,Object>>)any); returns(1.0);
            threePhraseMockScorer.evaluateCollocation((NGram)any, (List<TreeMap<String, Object>>) any); result = 1.0; result = -1.0;
            mockTermFilter.filterParsings((TreeSet<Parsing>)any, (List<TreeMap<String,Object>>)any);
        }};

        parameterSet = new ParseParameterSet();
        parameterSet.knownPhrases = "";
    }

    @Test
    public void invoke_unigrams()
    {
        parameterSet.model = unigramMockScorer;
        parameterSet.filters = mockFilters;

        ThresholdedAppendStrategy target = new ThresholdedAppendStrategy(tokens, parameterSet);

        Parsing[] actual = target.invoke();

        for(int i = 0; i < actual[0].getBoundaries().length; ++i)
        {
            Assert.assertTrue(actual[0].getBoundaries()[i]);
        }
    }

    @Test
    public void invoke_onePhrase()
    {
        parameterSet.model = onePhraseMockScorer;
        parameterSet.filters = mockFilters;

        ThresholdedAppendStrategy target = new ThresholdedAppendStrategy(tokens, parameterSet);

        Parsing[] actual = target.invoke();

        for(int i = 0; i < actual[0].getBoundaries().length; ++i)
        {
            Assert.assertFalse(actual[0].getBoundaries()[i]);
        }
    }

    @Test
    public void invoke_threePhrases()
    {
        parameterSet.model = threePhraseMockScorer;
        parameterSet.filters = mockFilters;

        ThresholdedAppendStrategy target = new ThresholdedAppendStrategy(tokens, parameterSet);

        Parsing[] actual = target.invoke();

        for(int i = 0; i < actual[0].getBoundaries().length; ++i)
        {
            if(i < 2)
                Assert.assertFalse(actual[0].getBoundaries()[i]);
            else
                Assert.assertTrue(actual[0].getBoundaries()[i]);
        }
    }
}
