package com.careerbuilder.search.semantic.Models;

import org.apache.solr.request.SolrQueryRequest;

public class ScorerParameterSet {

    public ScorerParameterSet()
    {
        trigramThreshold = 1.0;
        trigramTitleThreshold = 1.0;
        bigramThreshold = 1.0;
        bigramTitleThreshold = 1.0;
        trigramMultiplier = 1.0;
        collocationBoost = 1.0;
        threshold = 0.0;
        likelihoodTrigramMultiplier = 1.0;
        corpusSizeMultiplier = 1.0;
        returnScoringOutput = false;
    }

    public SolrQueryRequest request;
    public TokenizedString tokens;
    public double trigramThreshold;
    public double trigramTitleThreshold;
    public double bigramThreshold;
    public double bigramTitleThreshold;
    public double trigramMultiplier;
    public double collocationBoost;
    public double threshold;
    public double likelihoodTrigramMultiplier;
    public double corpusSizeMultiplier;
    public boolean returnScoringOutput;
}
