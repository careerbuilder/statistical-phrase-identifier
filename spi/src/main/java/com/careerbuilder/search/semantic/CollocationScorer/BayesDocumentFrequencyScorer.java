package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.DocFrequencySearcher;
import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.ScorerParameterSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class BayesDocumentFrequencyScorer implements ICollocationScorer {

    private FrequencyMap frequencyMap;
    private ScorerParameterSet parameterSet;


    public BayesDocumentFrequencyScorer(FrequencyMap map,
                                        ScorerParameterSet parameterSet) throws IOException
    {
        this.frequencyMap = map;
        this.parameterSet = parameterSet;

        DocFrequencySearcher documentSearcher = new DocFrequencySearcher(parameterSet.request);
        documentSearcher.getDocFrequencyHashMap(parameterSet.tokens, map);
    }

    public double evaluateCollocation(NGram nGram, List<TreeMap<String, Object>> output) {
        double score = 0;
        if(nGram.getLength() <= 1 ) {
            return 0;
        }
        else if(nGram.getLength() < 3) {
            score = Math.max((evaluateSubPhraseAgainstTitle(nGram, output) - parameterSet.bigramTitleThreshold),0)
                    + Math.max(evaluateSubPhraseAgainstDescription(nGram, output) - parameterSet.bigramThreshold, 0);
        }
        else
        {
            for(int i = 0; i < nGram.getLength() - 2; ++i) {
               score += Math.max((evaluateSubPhraseAgainstTitle(nGram.getSubNGram(i, 3), output) - parameterSet.trigramTitleThreshold), 0)
                       + Math.max((evaluateSubPhraseAgainstDescription(nGram.getSubNGram(i, 3), output) - parameterSet.trigramThreshold), 0);
            }
            score /= (nGram.getLength() - 2);
        }
        Long totalTerms = frequencyMap.totalDescriptionTermFrequencyMap.get(NGramField.content);
        double corpusSizeFactor = totalTerms/10e9;
        score -= corpusSizeFactor;
        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("phrase", nGram.toLowerCorpusString());
            scoringElement.put("corpusSizeFactor", corpusSizeFactor);
            scoringElement.put("score", score);
            output.add(scoringElement);
        }
        return score;
    }

    private double evaluateSubPhraseAgainstTitle(NGram nGram, List<TreeMap<String, Object>> output)
    {
        long exactTitleFrequency = frequencyMap.titleTermFrequencyMap.get(nGram.toLowerCorpusString());
        long minTitleFrequency = getMinFrequency(nGram, frequencyMap.titleTermFrequencyMap);
        long exactTitleDocFrequency = frequencyMap.titleDocFrequencyMap.get(nGram.toLowerCorpusString());
        if(exactTitleDocFrequency < 10)
            exactTitleDocFrequency = 1000;

        double score =  exactTitleFrequency / ((double)exactTitleDocFrequency * 2) + exactTitleFrequency / (double)minTitleFrequency;
        score *= 10;

        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("phrase", nGram.toLowerCorpusString());
            scoringElement.put("exact_title_freq", exactTitleFrequency);
            scoringElement.put("exact_title_doc_freq", exactTitleDocFrequency);
            scoringElement.put("min_title_doc_freq", minTitleFrequency);
            scoringElement.put("title_score", score);

            output.add(scoringElement);
        }

        return score;
    }

    private double evaluateSubPhraseAgainstDescription(NGram nGram, List<TreeMap<String, Object>> output) {
        long exactDescFrequency = frequencyMap.descriptionTermFrequencyMap.get(nGram.toLowerCorpusString());
        long documentFrequency = frequencyMap.descriptionDocFrequencyMap.get(nGram.toLowerCorpusString());
        long minDescFrequency = getMinFrequency(nGram, frequencyMap.descriptionDocFrequencyMap);
        if(documentFrequency < 5)
            documentFrequency = 1000;

        double score = exactDescFrequency/((double)documentFrequency*2)
                + exactDescFrequency/(double)minDescFrequency;
        score *= 10;

        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("phrase", nGram.toLowerCorpusString());
            scoringElement.put("exact_frequency", exactDescFrequency);
            scoringElement.put("doc_frequency", documentFrequency);
            scoringElement.put("background_ratio", exactDescFrequency/(double)documentFrequency);
            scoringElement.put("min_desc_frequency", minDescFrequency);
            scoringElement.put("min_title_frequency", minDescFrequency);
            scoringElement.put("exact_min_desc_ratio", exactDescFrequency/(double)minDescFrequency);
            scoringElement.put("description_score", score);

            output.add(scoringElement);
        }

        return score;
    }

    private Long getMinFrequency(NGram nGram, HashMap<String, Long> map)
    {
        Long minFrequency = Long.MAX_VALUE;
        Long frequency = 0L;
        for (int i  = 0; i < nGram.getLength(); i++)
        {
            frequency = map.get(nGram.getSubNGram(i,1).toLowerCorpusString());
            frequency = frequency == null ? 0 : frequency;
            if(frequency < minFrequency)
                minFrequency = frequency;
        }
        minFrequency = minFrequency == 0 ? 1 : minFrequency;
        return minFrequency;
    }
}
