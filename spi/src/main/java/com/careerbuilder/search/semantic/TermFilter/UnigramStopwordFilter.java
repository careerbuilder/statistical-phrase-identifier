package com.careerbuilder.search.semantic.TermFilter;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.ParseParameterSet;
import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import java.util.*;

public class UnigramStopwordFilter implements ITermFilter {

    private HashSet<String> stopwords;
    private TokenizedString tokens;
    private ParseParameterSet parameterSet;
    private HashSet<String> protectedStopwords;

    public UnigramStopwordFilter(TokenizedString tokens,
                                 ParseParameterSet parameterSet)
    {
        this.protectedStopwords = new HashSet<String>();
        this.protectedStopwords.add("it");
        this.tokens = tokens;
        this.stopwords = loadStopwords();
        this.parameterSet = parameterSet;
    }

    private HashSet<String> loadStopwords() {
        Iterator iter = EnglishAnalyzer.getDefaultStopSet().iterator();
        HashSet<String> stopwords = new HashSet<String>();
        while (iter.hasNext()) {
            char[] stopword = (char[]) iter.next();
            String stopwordString = new String(stopword);
            if(!protectedStopwords.contains(stopwordString))
                stopwords.add(stopwordString);
        }
        return stopwords;
    }

    public void filterParsings(TreeSet<Parsing> parsings, List<TreeMap<String,Object>> output){

        HashSet<String> uniqueParsings = new HashSet<String>();
        int parsingCount = 0;
        for(Iterator<Parsing> iter = parsings.descendingIterator(); iter.hasNext() && parsingCount < parameterSet.resultLimit;)
        {
            Parsing parsing = iter.next();
            filterParsing(parsing);
            if (uniqueParsings.contains(parsing.toString())) {
                iter.remove();
            }
            else {
                parsingCount++;
                uniqueParsings.add(parsing.toString());
            }
        }
    }

    private void filterParsing(Parsing parsing)
    {
        for(Iterator<Integer> iter = parsing.getPhraseIterator(); iter.hasNext();)
        {
            int index = iter.next();
            if (areAllTokensStopwords(parsing.getPhraseAt(index), stopwords)) {
                parsing.includePhraseInParsing(index, false);
            }
        }
    }

    private boolean areAllTokensStopwords(NGram nGram, HashSet<String> stopwords)
    {
        for(int i = 0; i < nGram.getLength(); ++i) {
           if(!stopwords.contains(nGram.getSubNGram(i, 1).toLowerCorpusString())) {
              return false;
           }
        }
        return true;
    }
}
