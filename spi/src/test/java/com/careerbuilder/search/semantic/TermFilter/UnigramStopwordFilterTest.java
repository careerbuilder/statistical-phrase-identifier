package com.careerbuilder.search.semantic.TermFilter;

import com.careerbuilder.search.semantic.Models.ParseParameterSet;
import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(JMockit.class)
public class UnigramStopwordFilterTest {

    private TokenizedString tokens = new TokenizedString("in at engineer quality assurance");
    ParseParameterSet parameterSet;

    @Before
    public void init()
    {

        parameterSet = new ParseParameterSet();
        parameterSet.resultLimit = 10;
    }


    @Test
    public void filter_unigrams()
    {
        UnigramStopwordFilter target = new UnigramStopwordFilter(tokens, parameterSet);
        Parsing parsing = new Parsing(tokens, 15);
        TreeSet<Parsing> parsings = new TreeSet<Parsing>();
        parsings.add(parsing);
        String [] expected = new String [] {"engineer", "quality", "assurance"};

        target.filterParsings(parsings, new ArrayList<TreeMap<String,Object>>());

        String [] actual = parsings.first().toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i])== 0);
        }

    }

    @Test
    public void filter_ngrams()
    {
        UnigramStopwordFilter target = new UnigramStopwordFilter(tokens, parameterSet);
        Parsing parsing = new Parsing(tokens, 14);
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
    public void filter_nonStopwords()
    {
        UnigramStopwordFilter target = new UnigramStopwordFilter(tokens, parameterSet);
        Parsing parsing = new Parsing(tokens, 0);
        String [] expected = new String [] {"in at engineer quality assurance"};
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
