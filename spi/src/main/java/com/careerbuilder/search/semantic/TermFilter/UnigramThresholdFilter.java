package com.careerbuilder.search.semantic.TermFilter;

import com.careerbuilder.search.semantic.Models.*;
import org.apache.solr.common.params.SolrParams;

import java.util.*;

public class UnigramThresholdFilter implements ITermFilter {

    private FrequencyMap frequencyMap;
    private int frequencyThreshold = 0;
    private double probabilityThreshold = 0.0;
    private String thresholdType;
    private ParseParameterSet parameterSet;
    private boolean returnScoringOutput;

    public UnigramThresholdFilter(FrequencyMap frequencyMap,
                                  ParseParameterSet parameterSet)
    {
        this.frequencyMap = frequencyMap;
        this.parameterSet = parameterSet;
        this.returnScoringOutput = parameterSet.params.getBool("returnScoringOutput",
                parameterSet.defaults.getBool("returnScoringOutput", false));
        this.frequencyThreshold = parameterSet.params.getInt("unigramFrequencyThreshold",
                parameterSet.defaults.getInt("unigramFrequencyThreshold", 2));
        this.probabilityThreshold = parameterSet.params.getDouble("unigramProbabilityThreshold",
                parameterSet.defaults.getDouble("unigramProbabilityThreshold", 10e-9));
        this.thresholdType = parameterSet.params.get("unigramThresholdType",
                parameterSet.defaults.get("unigramThresholdType", "frequency"));
    }

    public void filterParsings(TreeSet<Parsing> parsings, List<TreeMap<String,Object>> output){

        if(returnScoringOutput) {
            TreeMap<String, Object> frequencyInformation = new TreeMap<String, Object>();
            frequencyInformation.put("total_unigrams", frequencyMap.descriptionTermFrequencyMap.get(NGramField.content));
            frequencyInformation.put("single_occurrence_probability", 1 / (double)frequencyMap.totalDescriptionTermFrequencyMap.get(NGramField.content));
            frequencyInformation.put("probability_threshold", probabilityThreshold);
            frequencyInformation.put("max_frequency_below_p_threshold",
                    Math.floor(probabilityThreshold*frequencyMap.totalDescriptionTermFrequencyMap.get(NGramField.content)));
            frequencyInformation.put("frequency_threshold", frequencyThreshold);
            frequencyInformation.put("threshold_type", thresholdType);
            output.add(frequencyInformation);
        }

        int parsingCount = 0;
        for(Iterator<Parsing> iter = parsings.descendingIterator(); iter.hasNext() && parsingCount < parameterSet.resultLimit;)
        {
            filterParsing(iter.next());
            parsingCount++;
        }
    }

    private void filterParsing(Parsing parsing) {
        for(Iterator<Integer> iter = parsing.getPhraseIterator(); iter.hasNext();)
        {
            int index = iter.next();
            if(parsing.getPhraseAt(index).getLength() == 1)
            {
                if(thresholdType.compareToIgnoreCase("frequency") == 0
                        && frequencyMap.descriptionTermFrequencyMap.get(parsing.getPhraseAt(index).toLowerCorpusString()) < frequencyThreshold){
                    parsing.includePhraseInParsing(index, false);
                }
                if(thresholdType.compareToIgnoreCase("probability") == 0 &&
                        (frequencyMap.descriptionTermFrequencyMap.get(parsing.getPhraseAt(index).toLowerCorpusString())
                                /(double)frequencyMap.totalDescriptionTermFrequencyMap.get(NGramField.content)) < probabilityThreshold){
                    parsing.includePhraseInParsing(index, false);
                }
            }
        }
    }
}
