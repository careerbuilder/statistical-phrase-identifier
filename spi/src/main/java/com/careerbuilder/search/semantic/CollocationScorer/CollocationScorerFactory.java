package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.ScorerParameterSet;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;

import java.io.IOException;

public class CollocationScorerFactory {
    public static ICollocationScorer GetCollocationScorer(SolrQueryRequest request,
                                                          FrequencyMap map,
                                                          TokenizedString tokens,
                                                          SolrParams defaults) throws IOException {
        SolrParams params = request.getParams();
        ScorerParameterSet parameterSet = new ScorerParameterSet();
        parameterSet.request = request;
        parameterSet.tokens = tokens;
        parameterSet.collocationBoost = params.getDouble("boost", defaults.getDouble("boost", 1.0));
        parameterSet.threshold = params.getDouble("threshold", defaults.getDouble("threshold", 0.0));
        parameterSet.likelihoodTrigramMultiplier = params.getDouble("likelihoodTrigramMultiplier", defaults.getDouble("likelihoodTrigramMultiplier", 5));
        parameterSet.corpusSizeMultiplier = params.getDouble("corpusSizeMultiplier", defaults.getDouble("corpusSizeMultiplier", 1.0));
        parameterSet.bigramThreshold= params.getDouble("bigramThreshold", defaults.getDouble("bigramThreshold", 0.011));
        parameterSet.bigramTitleThreshold= params.getDouble("bigramTitleThreshold", defaults.getDouble("bigramTitleThreshold", 0.11));
        parameterSet.trigramThreshold= params.getDouble("trigramThreshold", defaults.getDouble("trigramThreshold", 0.01));
        parameterSet.trigramTitleThreshold= params.getDouble("trigramTitleThreshold", defaults.getDouble("trigramTitleThreshold", 0.05));
        parameterSet.returnScoringOutput = params.getBool("returnScoringOutput", defaults.getBool("returnScoringOutput", false));
        String scorer = params.get("scorer", defaults.get("scorer", "likelihood")).toLowerCase();

        if ("likelihood".compareTo(scorer) == 0) {
            return new LikelihoodCollocationScorer(
                    map.descriptionTermFrequencyMap,
                    map.totalDescriptionTermFrequencyMap,
                    parameterSet);
        }

        if ("chi".compareTo(scorer) == 0) {
            return new ChiSquareCollocationScorer(
                    map.descriptionTermFrequencyMap,
                    map.totalDescriptionTermFrequencyMap,
                    parameterSet);
        }

        if ("bayes".compareTo(scorer) == 0) {
            return new BayesCollocationScorer(
                    map,
                    parameterSet);
        }

        if ("bayesdoc".compareTo(scorer) == 0)
        {
            return new BayesDocumentFrequencyScorer(
                    map,
                    parameterSet);
        }
        throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                "Could not determine a valid collocation scorer. '" + scorer + "' not supported");
    }
}
