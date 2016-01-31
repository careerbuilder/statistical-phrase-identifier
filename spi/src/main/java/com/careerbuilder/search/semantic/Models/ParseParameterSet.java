package com.careerbuilder.search.semantic.Models;

import com.careerbuilder.search.semantic.CollocationScorer.ICollocationScorer;
import com.careerbuilder.search.semantic.TermFilter.ITermFilter;
import org.apache.solr.common.params.SolrParams;

import java.util.ArrayList;

public class ParseParameterSet {

    public ParseParameterSet()
    {
        resultLimit = 1;
        collocationScalingFactor = 1.0;
        knownPhrases = "";
    }

    public int resultLimit;
    public double collocationScalingFactor;
    public String knownPhrases;
    public SolrParams params;
    public SolrParams invariants;
    public SolrParams defaults;
    public ArrayList<ITermFilter> filters;
    public ICollocationScorer model;
}
