package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.ScorerParameterSet;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class LikelihoodCollocationScorer implements ICollocationScorer {

    private HashMap<String, Long> frequencyMap;
    private HashMap<NGramField, Long> totalFrequencies;
    private ScorerParameterSet parameterSet;

    public LikelihoodCollocationScorer(HashMap<String, Long> frequencyMap,
                                       HashMap<NGramField, Long> totalFrequencies,
                                       ScorerParameterSet parameterSet)
    {
        this.parameterSet = parameterSet;
        this.frequencyMap = frequencyMap;
        this.totalFrequencies = totalFrequencies;
    }

    public double evaluateCollocation(NGram nGram, List<TreeMap<String,Object>> output) {
        if(nGram.getLength() == 1)
            return 0;
        if(nGram.getLength() == 2)
            return likelihoodBigram(nGram, output) - parameterSet.threshold;
        if(nGram.getLength() > 2) {
            double score = 0;
            for (int i = 0; i < nGram.getLength() - 2; ++i) {
                score += likelihoodTrigram(nGram.getSubNGram(i, 3), output) - parameterSet.threshold;
                score += likelihoodBigram(nGram.getSubNGram(i, 2), output) - parameterSet.threshold;
            }
            score += likelihoodBigram(nGram.getSubNGram(nGram.getLength()-2, 2), output) - parameterSet.threshold;
            return score;
        }
        return 0;
    }

    private double likelihoodBigram(NGram nGram, List<TreeMap<String,Object>> output) {

        Long total = Math.round(totalFrequencies.get(NGramField.content) * parameterSet.corpusSizeMultiplier);
        Long tokenOne = frequencyMap.get(nGram.getSubNGram(0,1).toLowerCorpusString());
        Long tokenTwo = frequencyMap.get(nGram.getSubNGram(1,1).toLowerCorpusString());
        Long bigram = frequencyMap.get(nGram.toLowerCorpusString());

        double score = getLogLikelihood(total, tokenOne, tokenTwo, bigram, output);
        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("token_one", nGram.getSubNGram(0,1).toLowerCorpusString());
            scoringElement.put("freq_one", tokenOne);
            scoringElement.put("token_two", nGram.getSubNGram(1,1).toLowerCorpusString());
            scoringElement.put("freq_two", tokenTwo);
            scoringElement.put("freq_total", total);
            scoringElement.put("bigram", nGram.toLowerCorpusString());
            scoringElement.put("freq_bigram", bigram);
            scoringElement.put("score", score);
            output.add(scoringElement);
        }

        return score;
    }

    private double likelihoodTrigram(NGram nGram, List<TreeMap<String,Object>> output) {

        Long total = Math.round(totalFrequencies.get(NGramField.bigrams) * parameterSet.corpusSizeMultiplier);
        Long tokenOne = frequencyMap.get(nGram.getSubNGram(0,1).toLowerCorpusString());
        Long tokenOneTwo = frequencyMap.get(nGram.getSubNGram(0,2).toLowerCorpusString());
        Long tokenTwoThree = frequencyMap.get(nGram.getSubNGram(1, 2).toLowerCorpusString());
        Long tokenThree = frequencyMap.get(nGram.getSubNGram(2,1).toLowerCorpusString());
        Long trigram = frequencyMap.get(nGram.toLowerCorpusString());

        double score = getLogLikelihood(total, tokenOneTwo, tokenThree, trigram, output)
                + getLogLikelihood(total, tokenOne, tokenTwoThree, trigram, output);
        score *= parameterSet.likelihoodTrigramMultiplier;

        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("token_one", nGram.getSubNGram(0,1).toLowerCorpusString());
            scoringElement.put("freq_one", tokenOne);
            scoringElement.put("token_one_two", nGram.getSubNGram(0, 2).toLowerCorpusString());
            scoringElement.put("freq_one_two", tokenOneTwo);
            scoringElement.put("token_two_three", nGram.getSubNGram(1, 2).toLowerCorpusString());
            scoringElement.put("freq_two_three", tokenTwoThree);
            scoringElement.put("token_three", nGram.getSubNGram(2, 1).toLowerCorpusString());
            scoringElement.put("freq_three", tokenThree);
            scoringElement.put("trigram", nGram.toLowerCorpusString());
            scoringElement.put("freq_total", total);
            scoringElement.put("freq_trigram", trigram);
            scoringElement.put("score", score);
            output.add(scoringElement);
        }
        return score;
    }

    private double getLogLikelihood(Long total, Long tokenOne, Long tokenTwo, Long colloc, List<TreeMap<String,Object>> output) {
        total = total == null ? 0 : total;
        tokenOne = tokenOne == null || tokenOne == 0 ? 1 : tokenOne;
        tokenTwo = tokenTwo == null ? 0 : tokenTwo;
        colloc = colloc == null ? 0 : Math.round(colloc * parameterSet.collocationBoost);

        long nonCollocTokenTwo = Math.max(tokenOne - colloc, 0);

        double pColloc = colloc / (double) tokenOne;
        double pNotColloc= (tokenTwo - colloc) / (double)((total - tokenOne));

        pColloc = pColloc >= 1 ? 1 - Double.MIN_VALUE : pColloc;
        pNotColloc = pNotColloc <= 0 ? Double.MIN_VALUE : pNotColloc;
        pNotColloc = pNotColloc >= 1 ? 1 - Double.MIN_VALUE : pNotColloc;

        double pCollocUnderCollocHypothesis = Math.max(pColloc, pNotColloc);
        double pCollocUnderNotCollocHypothesis = Math.max(Math.min(pColloc, pNotColloc), Double.MIN_VALUE);

        double likelihoodUnderCollocHypothesis =
                        colloc*Math.log(pCollocUnderCollocHypothesis)
                        + nonCollocTokenTwo*Math.log(Math.max(1-pCollocUnderCollocHypothesis, Double.MIN_VALUE))
                        + nonCollocTokenTwo*Math.log(pNotColloc)
                        + (total - tokenOne - nonCollocTokenTwo)*Math.log(1-pNotColloc);

        double likelihoodUnderNotCollocHypothesis =
                        colloc*Math.log(pCollocUnderNotCollocHypothesis)
                        + nonCollocTokenTwo*Math.log(1-pCollocUnderNotCollocHypothesis)
                        + nonCollocTokenTwo*Math.log(pNotColloc)
                        + (total - tokenOne -nonCollocTokenTwo)*Math.log(1-pNotColloc);

        double logLikelihoodRatio = likelihoodUnderCollocHypothesis - likelihoodUnderNotCollocHypothesis;

        double scaledLikelihood = scaleLikelihood(logLikelihoodRatio);

        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("p_colloc", pColloc);
            scoringElement.put("p_not_colloc", pNotColloc);
            scoringElement.put("lr_numerator", likelihoodUnderCollocHypothesis);
            scoringElement.put("lr_denominator", likelihoodUnderNotCollocHypothesis);
            scoringElement.put("log_likelihood_ratio", logLikelihoodRatio);
            scoringElement.put("scaled_llr", scaledLikelihood);
            output.add(scoringElement);
        }

        return scaledLikelihood;
    }

    private double scaleLikelihood(double logLikelihood) {
        return logLikelihood/1e4;
    }
}
