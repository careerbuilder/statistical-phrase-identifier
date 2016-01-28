package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.Models.ParseParameterSet;
import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;

import java.util.*;

public abstract class ParseStrategyBase {

    ParseParameterSet parameterSet;
    List<TreeMap<String,Object>> scoringOutput;


    public ParseStrategyBase(ParseParameterSet parameterSet)
    {
        this.scoringOutput = new ArrayList<TreeMap<String, Object>>();
        this.parameterSet = parameterSet;
    }

    abstract TreeSet<Parsing> getUnfilteredParsings();

    public abstract List<TreeMap<String, Object>> getOutput();

    public Parsing[] invoke(){

        TreeSet<Parsing> parsings = preserveKnownPhrases(getUnfilteredParsings());

        for(int filterIndex = 0; filterIndex < parameterSet.filters.size(); ++filterIndex) {
            parameterSet.filters.get(filterIndex).filterParsings(parsings, scoringOutput);
        }
        return convertParsingsToArray(parsings);
    }

    private TreeSet<Parsing> preserveKnownPhrases(TreeSet<Parsing> parsings)
    {
        TokenizedString tokens = parsings.first().getTokens();
        KnownPhrasePreserver preserver = new KnownPhrasePreserver(tokens,
                parameterSet.knownPhrases);
        Iterator<Parsing> it = parsings.descendingIterator();
        boolean [] boundaries;
        while(it.hasNext())
        {
            Parsing parsing = it.next();
            parsing.setBoundaries(preserver.preserveKnownPhrases(parsing.getBoundaries()));
        }
        return parsings;
    }

    private Parsing[] convertParsingsToArray(TreeSet<Parsing> parsings) {

        if(parsings == null)
            return new Parsing[0];
        Parsing [] parsingArray = new Parsing[Math.min(parsings.size(), parameterSet.resultLimit)];

        Iterator<Parsing> it = parsings.descendingIterator();
        int i = -1;
        while(it.hasNext() && ++i < parameterSet.resultLimit) {
            Parsing parsing = it.next();
            parsingArray[i] = parsing;
        }
        return parsingArray;
    }
}
