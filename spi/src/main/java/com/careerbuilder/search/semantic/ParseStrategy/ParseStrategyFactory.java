package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.CollocationScorer.CollocationScorerFactory;
import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.ParseParameterSet;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import com.careerbuilder.search.semantic.TermFilter.TermFilterFactory;
import com.careerbuilder.search.semantic.TermFrequencySearcher;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import java.io.IOException;

public class ParseStrategyFactory {


    public static ParseStrategyBase getStrategy(SolrQueryRequest request,
                                                SolrParams invariants,
                                                SolrParams defaults)
            throws IOException {

        SolrParams params = request.getParams();
        String q = params.get(CommonParams.Q);
        if(q == null) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "No query specified.");
        }

        ParseParameterSet parameterSet = new ParseParameterSet();
        TokenizedString tokens = new TokenizedString(q);
        TermFrequencySearcher searcher = new TermFrequencySearcher(request);
        FrequencyMap map = new FrequencyMap();
        searcher.getFrequencyHashMap(tokens, map);
        searcher.getTotalFrequencies(map);


        String algorithm = params.get("algorithm", defaults.get("algorithm", "cost")).toLowerCase();
        parameterSet.knownPhrases = params.get("knownPhrases", "");
        parameterSet.collocationScalingFactor = params.getDouble("costModelScoreSensitivity", defaults.getDouble("costModelScoreSensitivity", 100.0));
        parameterSet.resultLimit = params.getInt("limit", defaults.getInt("limit", 10));
        parameterSet.params = params;
        parameterSet.invariants = invariants;
        parameterSet.defaults = defaults;
        parameterSet.filters =  TermFilterFactory.getFilters(tokens, map, parameterSet);
        parameterSet.model = CollocationScorerFactory.GetCollocationScorer(request, map, tokens, defaults);

        if("split".compareTo(algorithm) == 0) {
           return new ThresholdedSplitStrategy(tokens, parameterSet);
        }
        if("cost".compareTo(algorithm) == 0) {
           return new CostModelStrategy(tokens, parameterSet);
        }
        if("append".compareTo(algorithm) == 0) {
            return new ThresholdedAppendStrategy(tokens, parameterSet);
        }

        throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                "Could not determine a valid parsing algorithm. '" + algorithm + "' not supported.");
    }


}
