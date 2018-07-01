package com.careerbuilder.search.semantic;

import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.ParseStrategy.ParseStrategyBase;
import com.careerbuilder.search.semantic.ParseStrategy.ParseStrategyFactory;
import org.apache.solr.common.SolrException;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import java.util.*;


public class ParseRequestHandler extends RequestHandlerBase
{
    @Override
    public void init(NamedList args) {
        super.init(args);
    }

    @Override
    public void handleRequestBody(SolrQueryRequest request, SolrQueryResponse rsp)
            throws Exception {
        ParseStrategyBase parseStrategy = ParseStrategyFactory.getStrategy(request, invariants, defaults);
        Parsing[] parsings = parseStrategy.invoke();
        List<TreeMap<String,Object>> scoringOutput = parseStrategy.getOutput();
        addOutputToResponse(rsp, parsings, scoringOutput);
    }

    private void addOutputToResponse(SolrQueryResponse rsp, Parsing[] parsings, List<TreeMap<String, Object>> scoringOutput) {
        TreeMap<String, Object>[] output  = new TreeMap[parsings.length];
        for(int i = 0; i < parsings.length; ++i)
        {
            output[i] = new TreeMap<String, Object>();
            output[i].put("score", parsings[i].getScore());
            output[i].put("parsed_phrases", parsings[i].toStringArray());
            output[i].put("parsed_query", parsings[i].toString());
        }

        if(parsings.length > 0) {
            rsp.add("top_parsed_query", parsings[0].toString());
            rsp.add("top_parsed_phrases", parsings[0].toStringArray());
            rsp.add("potential_parsings", output);
            if(scoringOutput != null && scoringOutput.size()>0)
                rsp.add("scoring_information", scoringOutput);
        }
        else{
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "No recognizable terms specified.");
        }
    }

    //////////////////////// SolrInfoMBeans methods //////////////////////

    @Override
    public String getDescription() {
        return "Parses a phrase";
    }


}