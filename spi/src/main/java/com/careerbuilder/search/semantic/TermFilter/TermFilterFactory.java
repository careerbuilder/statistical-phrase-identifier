package com.careerbuilder.search.semantic.TermFilter;
import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.ParseParameterSet;
import com.careerbuilder.search.semantic.Models.TokenizedString;

import java.util.ArrayList;

public class TermFilterFactory {
    public static ArrayList<ITermFilter> getFilters(TokenizedString tokens,
                                                    FrequencyMap frequencyMap,
                                                    ParseParameterSet parameterSet){

        ArrayList<ITermFilter> filters = new ArrayList<ITermFilter>();
        filters.add(0, new UnigramStopwordFilter(tokens, parameterSet));
        filters.add(1, new UnigramThresholdFilter(frequencyMap, parameterSet));
        return filters;
    }
}
