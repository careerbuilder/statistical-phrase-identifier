package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.Models.*;

import java.util.*;

public  class ThresholdedAppendStrategy extends ParseStrategyBase {

    private TokenizedString tokens;

    private ArrayList<TreeMap<String,Object>> scoringOutput;

    public ThresholdedAppendStrategy(TokenizedString tokens,
                                     ParseParameterSet parameterSet)
    {
        super(parameterSet);
        this.scoringOutput = new ArrayList<TreeMap<String,Object>>();
        this.tokens = tokens;
    }

    @Override
    public List<TreeMap<String,Object>> getOutput() {
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
        int firstTokenOfNextPhrase = 0;

        for(int start = 0; start < tokens.length; start++)
        {
            for(int length = 3; length > 1; length--) {

                // don't evaluate nonexistant phrases
                if(start + length > tokens.length) {
                    continue;
                }
                // don't do evaluations that won't grow the current phrase
                if(start + length <= firstTokenOfNextPhrase) {
                    continue;
                }

                double score = parameterSet.model.evaluateCollocation(new NGram(tokens, start, length), scoringOutput);

                if (score > 0) {
                    firstTokenOfNextPhrase = start + length;
                }
                // we've exhausted possibilities of growing current phrase, so start the next phrase
                else if(length == 2)
                {
                    boundaries[start] = true;
                    ++firstTokenOfNextPhrase;
                }
            }
        }
        return boundaries;
    }
}
