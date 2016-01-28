package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.ParseStrategy.ParseStrategyBase;
import com.careerbuilder.search.semantic.Models.*;
import com.careerbuilder.search.semantic.Utility;

import java.util.*;

public class ThresholdedSplitStrategy extends ParseStrategyBase {

    private TokenizedString tokens;
    ArrayList<TreeMap<String, Object>> scoringOutput;

    public ThresholdedSplitStrategy(TokenizedString tokens,
                                    ParseParameterSet parameterSet) {
        super(parameterSet);
        this.scoringOutput = new ArrayList<TreeMap<String, Object>>();
        this.tokens = tokens;
    }

    @Override
    public List<TreeMap<String, Object>> getOutput() {
        return scoringOutput;
    }

    @Override
    public TreeSet<Parsing> getUnfilteredParsings() {
        boolean[] boundaries = parseTokens();
        TreeSet<Parsing> parsings = new TreeSet<Parsing>();
        parsings.add(new Parsing(tokens, boundaries));
        return parsings;
    }


    private boolean[] parseTokens() {
        boolean [] boundaries = new boolean[tokens.length-1];
        Arrays.fill(boundaries, false);
        int start = 0;

        while(start < tokens.length - 1) {
            start = thresholdPhrase(boundaries, start);
        }
        return boundaries;
    }



    private int thresholdPhrase(boolean [] boundaries, int start)
    {
        int phraseLength = Math.min(3, tokens.length - start);
        NGram phrase = new NGram(tokens, start, phraseLength);
        double score = parameterSet.model.evaluateCollocation(phrase, scoringOutput);
        int nextPhraseStart = start;
        if(score <= 0) {
            int nextPhraseOffset = splitPhrase(phrase);
            nextPhraseStart = start + nextPhraseOffset;
            boundaries[nextPhraseStart - 1] = true;
        }
        else{
            nextPhraseStart += 2;
        }
        return nextPhraseStart;
    }

    private int splitPhrase(NGram phrase) {
        double [] subGramScores = new double[2];
        for(int i = 0; i < 2; i++) {
            subGramScores[i] = parameterSet.model.evaluateCollocation(phrase.getSubNGram(i, phrase.getLength() - 1), scoringOutput);
        }
        int argMax = Utility.getArgMax(subGramScores);
        int nextPhraseOffset;
        if(subGramScores[argMax] <= 0) {
            nextPhraseOffset = 1;
        }
        else {
            nextPhraseOffset = argMax == 0 ? phrase.getLength() - 1: 1;
        }
        return nextPhraseOffset;
    }
}
