package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.ScorerParameterSet;
import org.apache.lucene.search.Scorer;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class BayesCollocationScorer implements ICollocationScorer {

    private FrequencyMap frequencyMap;
    private ScorerParameterSet parameterSet;

    public BayesCollocationScorer(FrequencyMap frequencyMap, ScorerParameterSet parameterSet){
        this.frequencyMap = frequencyMap;
        this.parameterSet = parameterSet;
    }

    public double evaluateCollocation(NGram nGram, List<TreeMap<String, Object>> output) {

        if(nGram.getLength() <= 1 ) {
            return 0;
        }
        else if(nGram.getLength() < 3) {
            return evaluateSubPhrase(nGram, output) - parameterSet.bigramThreshold;
        }
        else
        {
            double score = 0;
            for(int i = 0; i < nGram.getLength() - 2; ++i) {
               score += evaluateSubPhrase(nGram.getSubNGram(i, 3), output) - parameterSet.trigramThreshold;
            }
            return score/(nGram.getLength()-2);
        }
    }

    private double evaluateSubPhrase(NGram nGram, List<TreeMap<String, Object>> output) {
        long exactFrequency = getExactFrequency(nGram);
        long minFrequency = getMinFrequency(nGram);

        double exactOverMin = (exactFrequency * exactFrequency / ((double)minFrequency * minFrequency));
        double dynamicThreshold = (0.1 / (Math.pow(10, (minFrequency - exactFrequency)/((double)1000))));
        double score = exactOverMin - dynamicThreshold;
        score *= 1e5;

        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("phrase", nGram.toLowerCorpusString());
            scoringElement.put("exact_over_min", exactOverMin);
            scoringElement.put("dynamic_threshhold", dynamicThreshold);
            scoringElement.put("score", score);

            output.add(scoringElement);
        }
        return score;
    }

    private Long getMinFrequency(NGram nGram)
    {
        Long minFrequency = Long.MAX_VALUE;
        Long frequency = 0L;
        for (int i  = 0; i < nGram.getLength(); i++)
        {
            frequency = frequencyMap.descriptionTermFrequencyMap.get(nGram.getSubNGram(i, 1).toLowerCorpusString());
            frequency = frequency == null ? 0 : frequency;
            if(frequency < minFrequency)
                minFrequency = frequency;
        }
        return minFrequency;
    }

    private Long getExactFrequency(NGram nGram) {
        Long exactFrequency = frequencyMap.descriptionTermFrequencyMap.get(nGram.toLowerCorpusString());
        return exactFrequency == null ? 0 : exactFrequency;
    }

    private Long getMinTitleFrequency(NGram nGram)
    {
        Long minFrequency = Long.MAX_VALUE;
        Long frequency = 0L;
        for (int i  = 0; i < nGram.getLength(); i++)
        {
            frequency = frequencyMap.titleTermFrequencyMap.get(nGram.getSubNGram(i,1).toLowerCorpusString());
            frequency = frequency == null ? 0 : frequency;
            if(frequency < minFrequency)
                minFrequency = frequency;
        }
        return minFrequency;
    }

    private Long getExactTitleFrequency(NGram nGram) {
        Long exactFrequency = frequencyMap.titleTermFrequencyMap.get(nGram.toLowerCorpusString());
        return exactFrequency == null ? 0 : exactFrequency;
    }
}
