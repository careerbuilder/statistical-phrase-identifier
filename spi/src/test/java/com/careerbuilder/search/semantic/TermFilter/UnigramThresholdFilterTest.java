package com.careerbuilder.search.semantic.TermFilter;

import com.careerbuilder.search.semantic.Models.*;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.apache.solr.common.params.SolrParams;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

@RunWith(JMockit.class)
public class UnigramThresholdFilterTest {

    private TokenizedString tokens = new TokenizedString("Software Developer engineer quality assurance");

    private HashMap<String, Long> frequencies = new HashMap<String, Long>();
    private HashMap<NGramField, Long> totalFrequencies = new HashMap<NGramField, Long>();

    @Mocked SolrParams paramsFrequency;
    @Mocked SolrParams paramsProbability;
    @Mocked SolrParams defaults;
    FrequencyMap frequencyMap;
    ParseParameterSet frequencyParameterSet;
    ParseParameterSet probabilityParameterSet;

    @Before
    public void init()
    {
        frequencyMap = new FrequencyMap();
        frequencyParameterSet = new ParseParameterSet();
        probabilityParameterSet = new ParseParameterSet();

        frequencyParameterSet.params = paramsFrequency;
        frequencyParameterSet.defaults= defaults;
        frequencyParameterSet.resultLimit = 10;
        probabilityParameterSet.params = paramsProbability;
        probabilityParameterSet.defaults= defaults;
        probabilityParameterSet.resultLimit = 10;

        new NonStrictExpectations(){{
            paramsFrequency.getInt("unigramFrequencyThreshold", anyInt); returns(2);
            paramsFrequency.get("unigramThresholdType", anyString); returns("frequency");
            paramsProbability.get("unigramThresholdType", anyString); returns("probability");
            paramsProbability.getDouble("unigramProbabilityThreshold", anyDouble); returns(0.11);
        }};

        frequencies.put("software", 1L);
        frequencies.put("developer", 1L);
        frequencies.put("engineer", 2L);
        frequencies.put("quality", 2L);
        frequencies.put("assurance", 2L);
        frequencyMap.descriptionTermFrequencyMap = frequencies;

        totalFrequencies.put(NGramField.content, 10L);
        frequencyMap.totalDescriptionTermFrequencyMap = totalFrequencies;
    }

    @Test
    public void filterFrequency_unigrams()
    {
        UnigramThresholdFilter target = new UnigramThresholdFilter(frequencyMap, frequencyParameterSet);
        Parsing parsing = new Parsing(tokens, 15);
        String [] expected = new String [] {"engineer", "quality", "assurance"};
        TreeSet<Parsing> parsings = new TreeSet<Parsing>();
        parsings.add(parsing);

        target.filterParsings(parsings, new ArrayList<TreeMap<String,Object>>());

        String [] actual = parsings.first().toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i]) == 0);
        }}

    @Test
    public void filterFrequency_ngrams()
    {
        UnigramThresholdFilter target = new UnigramThresholdFilter(frequencyMap, probabilityParameterSet);
        Parsing parsing = new Parsing(tokens, 14);
        String [] expected = new String [] {"Software Developer", "engineer", "quality", "assurance"};
        TreeSet<Parsing> parsings = new TreeSet<Parsing>();
        parsings.add(parsing);

        target.filterParsings(parsings, new ArrayList<TreeMap<String,Object>>());

        String [] actual = parsings.first().toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i])== 0);
        }
    }

    @Test
    public void filterProbability_unigrams()
    {
        UnigramThresholdFilter target = new UnigramThresholdFilter(frequencyMap, probabilityParameterSet);
        Parsing parsing = new Parsing(tokens, 15);
        String [] expected = new String [] {"engineer", "quality", "assurance"};
        TreeSet<Parsing> parsings = new TreeSet<Parsing>();
        parsings.add(parsing);

        target.filterParsings(parsings, new ArrayList<TreeMap<String,Object>>());

        String [] actual = parsings.first().toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i])== 0);
        }
    }

    @Test
    public void filterProbability_ngrams()
    {
        UnigramThresholdFilter target = new UnigramThresholdFilter(frequencyMap, probabilityParameterSet);
        Parsing parsing = new Parsing(tokens, 14);
        String [] expected = new String [] {"Software Developer", "engineer", "quality", "assurance"};
        TreeSet<Parsing> parsings = new TreeSet<Parsing>();
        parsings.add(parsing);

        target.filterParsings(parsings, new ArrayList<TreeMap<String,Object>>());

        String [] actual = parsings.first().toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i])== 0);
        }
    }

}
