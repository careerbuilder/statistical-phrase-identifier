package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.Models.TokenizedString;
import org.apache.solr.common.params.SolrParams;

public class KnownPhrasePreserver {

    TokenizedString tokens;
    TokenizedString [] knownPhrases;

    public KnownPhrasePreserver(TokenizedString tokens, String knownPhrases)
    {
        this.tokens = tokens;
        this.knownPhrases = getKnownPhrases(knownPhrases);
    }
    private static TokenizedString [] getKnownPhrases(String rawString) {
        String [] phrases = rawString.split("\\^");
        TokenizedString [] knownPhrases = new TokenizedString[phrases.length];
        for(int i = 0; i < phrases.length; ++i)
        {
            knownPhrases[i] = new TokenizedString(phrases[i]);
        }
        return knownPhrases;
    }

    public boolean[] preserveKnownPhrases(boolean [] boundaries)
    {
        for(TokenizedString phrase : knownPhrases) {
            int start = 0;
            while (start < tokens.tokens.length) {
                start = preservePhraseOccurrance(boundaries, phrase, start);
            }
        }
        return boundaries;
    }

    private int preservePhraseOccurrance(boolean[] boundaries, TokenizedString phrase, int start) {
        int matchOffset = tokenMatchSearch(tokens, phrase, start);
        if(matchOffset != -1)
        {
            for(int i = 0; i < phrase.tokens.length-1; ++i)
            {
                boundaries[matchOffset + i] = false;
            }
            return start + phrase.tokens.length;
        }
        return tokens.length;
    }

    private int tokenMatchSearch(TokenizedString haystack, TokenizedString needle, int start)
    {
        for(int i = start; i < haystack.tokens.length; ++i)
        {
            if(haystack.tokens[i].compareTo(needle.tokens[0]) == 0)
            {
                int k = 0;
                for(;k < needle.tokens.length && i + k < haystack.tokens.length ; ++k)
                {
                    if(haystack.tokens[i+k].compareTo(needle.tokens[k]) != 0)
                        break;
                }
                if(k == needle.tokens.length)
                    return i;
            }
        }
        return -1;
    }
}
