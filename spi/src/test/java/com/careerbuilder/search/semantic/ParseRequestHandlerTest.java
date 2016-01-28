package com.careerbuilder.search.semantic;

import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import com.careerbuilder.search.semantic.ParseStrategy.ParseStrategyBase;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.TreeMap;

@RunWith(JMockit.class)
public class ParseRequestHandlerTest{

    private TokenizedString tokens = new TokenizedString("associate sales manager");
    Parsing[] testParsings = new Parsing[4];

    @Mocked private SolrQueryResponse response;
    @Mocked private SolrQueryRequest mockedRequest;
    @Mocked private ParseStrategyBase mockedStrategy;


    @Before
    public void init()
    {
        for(int i = 0; i < testParsings.length; ++i)
        {
            testParsings[i] = new Parsing(tokens, i);
            testParsings[i].incrementScore(i);
        }
    }


    @Test
    public void addOutputToResponse()
    {
        new Expectations(){{
            response.add("top_parsed_query", testParsings[0].toString());
            response.add("top_parsed_phrases", testParsings[0].toStringArray());
            response.add("potential_parsings", any);
        }};

        ParseRequestHandler target = new ParseRequestHandler();
        Deencapsulation.invoke(target, "addOutputToResponse", response, testParsings, new ArrayList<TreeMap<String, Object>>());
    }
}
