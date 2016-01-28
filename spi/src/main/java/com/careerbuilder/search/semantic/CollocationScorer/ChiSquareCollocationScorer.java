package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.ScorerParameterSet;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.lucene.search.Scorer;

import java.util.*;

public class ChiSquareCollocationScorer implements ICollocationScorer {

    private final double MIN_EXPECTED = 10e-10;

    private HashMap<String, Long> frequencyMap;
    private HashMap<NGramField, Long> totalFrequencies;
    private ScorerParameterSet parameterSet;

    public ChiSquareCollocationScorer(HashMap<String, Long> frequencyMap,
                                      HashMap<NGramField, Long> totalFrequencies,
                                      ScorerParameterSet parameterSet)
    {
        this.frequencyMap = frequencyMap;
        this.totalFrequencies = totalFrequencies;
        this.parameterSet = parameterSet;
    }

    public double evaluateCollocation(NGram nGram, List<TreeMap<String,Object>> output)
    {
        if(nGram.getLength() == 1)
            return 0;
        if(nGram.getLength()== 2)
            return 1 - chiSquareBigram(nGram, output) - parameterSet.threshold;
        if(nGram.getLength() > 2) {
            double score = 0;
            for(int i = 0; i < nGram.getLength() - 2; ++i) {
                score += 1 - chiSquareTrigram(nGram.getSubNGram(i, 3), output) - parameterSet.threshold;
            }
            return score / (nGram.getLength() - 2);
        }
        return 0;
    }

    private double chiSquareTrigram(NGram nGram, List<TreeMap<String, Object>> output) {

        double pvalue = 0.0;
        long[] observed = new long[8];
        double[] expected = new double[8];
        long total = totalFrequencies.get(NGramField.trigrams);

        ChiSquareTest chi = new ChiSquareTest();

        String trigram = nGram.toLowerCorpusString();
        String bigramOne = nGram.getSubNGram(0, 2).toLowerCorpusString();
        String bigramTwo = nGram.getSubNGram(1, 2).toLowerCorpusString();
        Long trigramFreq = Math.round(parameterSet.collocationBoost * frequencyMap.get(trigram));
        Long bigramOneFreq = frequencyMap.get(bigramOne);
        Long bigramTwoFreq = frequencyMap.get(bigramTwo);
        Long splitBigramFreq = frequencyMap.get(trigram);
        Long tokenOneFreq = frequencyMap.get(nGram.getSubNGram(0,1).toLowerCorpusString());
        Long tokenTwoFreq = frequencyMap.get(nGram.getSubNGram(1,1).toLowerCorpusString());
        Long tokenThreeFreq = frequencyMap.get(nGram.getSubNGram(2,1).toLowerCorpusString());

        trigramFreq = trigramFreq == null ? 0 : trigramFreq;
        bigramOneFreq = bigramOneFreq == null ? 0 : bigramOneFreq;
        bigramTwoFreq = bigramTwoFreq == null ? 0 : bigramTwoFreq;
        splitBigramFreq = splitBigramFreq == null ? 0 : splitBigramFreq;
        tokenOneFreq = tokenOneFreq == null ? 0 : tokenOneFreq;
        tokenTwoFreq = tokenTwoFreq == null ? 0 : tokenTwoFreq;
        tokenThreeFreq = tokenThreeFreq == null ? 0 : tokenThreeFreq;

        double expectedTrigram = Math.min(tokenOneFreq * bigramTwoFreq / (double) total, tokenThreeFreq * bigramOneFreq / (double) total);
        double expectedBigramOne = bigramOneFreq - expectedTrigram;
        double expectedBigramTwo = bigramTwoFreq - expectedTrigram;

        double expectedTokenOne = tokenOneFreq - expectedBigramTwo;
        double expectedTokenTwo = tokenTwoFreq - splitBigramFreq;
        double expectedTokenThree = tokenThreeFreq - expectedBigramOne;

        observed[0] = trigramFreq;
        expected[0] = Math.max(MIN_EXPECTED, expectedTrigram);
        observed[1] = Math.max(0, tokenOneFreq - trigramFreq);
        expected[1] = Math.max(MIN_EXPECTED, expectedTokenOne);
        observed[2] = Math.max(0, tokenTwoFreq - trigramFreq);
        expected[2] = Math.max(MIN_EXPECTED, expectedTokenTwo);
        observed[3] = Math.max(0, tokenThreeFreq - trigramFreq);
        expected[3] = Math.max(MIN_EXPECTED, expectedTokenThree);
        observed[4] = Math.max(0, bigramOneFreq - trigramFreq);
        expected[4] = Math.max(MIN_EXPECTED, expectedBigramOne);
        observed[5] = Math.max(0, bigramTwoFreq - trigramFreq);
        expected[5] = Math.max(MIN_EXPECTED, expectedBigramTwo);
        observed[6] = Math.max(0, splitBigramFreq - trigramFreq);
        expected[6] = Math.max(MIN_EXPECTED, splitBigramFreq - trigramFreq);
        observed[7] = Math.max(0, total - tokenOneFreq - tokenTwoFreq - tokenThreeFreq + 2 * trigramFreq);
        expected[7] = Math.max(MIN_EXPECTED, total - expectedTokenOne - expectedTokenTwo - expectedTokenThree + 2 * expectedTrigram);

        if (observed[0] >= expected[0])
            pvalue = chi.chiSquareTest(expected, observed);
        else
            pvalue = 1.0;

        if (parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("token1", nGram.getSubNGram(0,1).toLowerCorpusString());
            scoringElement.put("token2", nGram.getSubNGram(1,1).toLowerCorpusString());
            scoringElement.put("token3", nGram.getSubNGram(2,1).toLowerCorpusString());
            scoringElement.put("token1freq", tokenOneFreq);
            scoringElement.put("expectedtoken1", expectedTokenOne);
            scoringElement.put("token2freq", tokenTwoFreq);
            scoringElement.put("expectedtoken2", expectedTokenTwo);
            scoringElement.put("token3freq", tokenThreeFreq);
            scoringElement.put("expectedtoken3", expectedTokenThree);
            scoringElement.put("observed", observed[0]);
            scoringElement.put("expected", expected[0]);
            scoringElement.put("p", pvalue);
            output.add(scoringElement);
        }

        return pvalue;
    }

    private double chiSquareBigram(NGram nGram, List<TreeMap<String, Object>> output) {
        String bigram;
        double pvalue = 0.0;
        long [] observed = new long[4];
        double [] expected = new double[4];
        long total = totalFrequencies.get(NGramField.bigrams);

        ChiSquareTest chi = new ChiSquareTest();

        bigram = nGram.toLowerCorpusString();
        Long bigramFreq = Math.round(parameterSet.collocationBoost* frequencyMap.get(bigram));
        Long tokenOneFreq = frequencyMap.get(nGram.getSubNGram(0,1).toLowerCorpusString());
        Long tokenTwoFreq = frequencyMap.get(nGram.getSubNGram(1,1).toLowerCorpusString());

        double expectedBigram = tokenTwoFreq * tokenOneFreq  / (double)total;
        double expectedTokenOne = tokenOneFreq - tokenTwoFreq * tokenOneFreq  / (double)total;
        double expectedTokenTwo = tokenTwoFreq - tokenTwoFreq * tokenOneFreq  / (double)total;

        bigramFreq = bigramFreq == null ? 0 : bigramFreq;
        tokenOneFreq = tokenOneFreq == null? 0 : tokenOneFreq;
        tokenTwoFreq = tokenTwoFreq == null ? 0 : tokenTwoFreq;


        observed[0] = bigramFreq;
        expected[0] = Math.max(MIN_EXPECTED, expectedBigram);
        observed[1] = Math.max(0,tokenOneFreq - bigramFreq);
        expected[1] = Math.max(MIN_EXPECTED, expectedTokenOne);
        observed[2] = Math.max(0,tokenTwoFreq - bigramFreq);
        expected[2] = Math.max(MIN_EXPECTED, expectedTokenTwo);
        observed[3] = Math.max(0,total - tokenOneFreq - tokenTwoFreq + bigramFreq);
        expected[3] = Math.max(MIN_EXPECTED, total - expectedTokenOne - expectedTokenTwo + expectedBigram);

        if(observed[0] >= expected[0])
            pvalue = chi.chiSquareTest(expected, observed);
        else
            pvalue = 1.0;

        if(parameterSet.returnScoringOutput) {
            TreeMap<String,Object> scoringElement = new TreeMap<String,Object>();
            scoringElement.put("token1", nGram.getSubNGram(0, 1).toLowerCorpusString());
            scoringElement.put("token2", nGram.getSubNGram(1, 1).toLowerCorpusString());
            scoringElement.put("token1freq", tokenOneFreq);
            scoringElement.put("expectedtoken1", expectedTokenOne);
            scoringElement.put("token2freq", tokenTwoFreq);
            scoringElement.put("expectedtoken2", expectedTokenTwo);
            scoringElement.put("observed", observed[0]);
            scoringElement.put("expected", expected[0]);
            scoringElement.put("p", pvalue);
            output.add(scoringElement);
        }

        return pvalue;
    }
}
