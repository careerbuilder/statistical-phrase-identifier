package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.ParseParameterSet;
import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;

import java.util.*;

public class CostModelStrategy extends ParseStrategyBase{

    private static final int COST_MODEL_MAX_QUERY_LENGTH = 22;

    private TokenizedString tokens;
    private double[] lengthScoring;
    private LinkedList<Parsing> generatedParsings;

    private ArrayList<TreeMap<String, Object>> scoringOutput;

    public CostModelStrategy(TokenizedString tokens,
                             ParseParameterSet parameterSet){
        super(parameterSet);

        if(tokens.length > COST_MODEL_MAX_QUERY_LENGTH) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                    "Cost model algorithm limited to phrases of length 22 or less");
        }
        this.tokens = tokens;
        this.lengthScoring = new double[COST_MODEL_MAX_QUERY_LENGTH];
        this.scoringOutput = new ArrayList<TreeMap<String, Object>>();
        getDefaultLengthScorings(parameterSet.invariants);
   }

    public void getDefaultLengthScorings(SolrParams invariants) {
        for(int i = 0; i < COST_MODEL_MAX_QUERY_LENGTH; ++i) {
            lengthScoring[i] = invariants.getDouble("costModel" + i + "GramScoring", 0.0);
        }
    }

    @Override
    public List<TreeMap<String,Object>> getOutput() {
        return scoringOutput;
    }

    @Override
    public TreeSet<Parsing> getUnfilteredParsings() {
        TreeSet<Parsing> scoredParsings = new TreeSet<Parsing>();
        if(tokens != null && tokens.length > 0) {
            generateParsings();
            for (Parsing parsing : generatedParsings) {
                scoreParsing(parsing);
                scoredParsings.add(parsing);
            }
        }
        return scoredParsings;
    }

    private void generateParsings()
    {
        generatedParsings = new LinkedList<Parsing>();
        int numPossibleParsings = (int)Math.round(Math.pow(2, tokens.length-1));
        for(int parsingIndex = 0; parsingIndex < numPossibleParsings ; parsingIndex++) {
            generatedParsings.add(new Parsing(tokens, parsingIndex));
        }
    }

    private void scoreParsing(Parsing parsing) {
        double phraseScore = 0;
        double phraseLengthScore = 0;
        for(Iterator<Integer> iter = parsing.getPhraseIterator(); iter.hasNext();)
        {
            int index = iter.next();
            NGram phrase = parsing.getPhraseAt(index);
            phraseScore += parameterSet.model.evaluateCollocation(phrase, scoringOutput);
            phraseLengthScore += lengthScoring[phrase.getLength()];
        }
        phraseScore *= parameterSet.collocationScalingFactor;
        phraseScore *= phraseLengthScore;
        parsing.incrementScore(phraseScore);
    }
}
