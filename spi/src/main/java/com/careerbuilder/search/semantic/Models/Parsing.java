package com.careerbuilder.search.semantic.Models;

import com.careerbuilder.search.semantic.Utility;

import java.util.*;

public class Parsing implements Comparable<Parsing>
{
    private int index;
    private double score;
    private boolean [] boundaries;
    private TreeMap<Integer, NGram> phrases;
    private TreeMap<Integer, Boolean> includeInParsing;
    private TokenizedString tokens;

    public Parsing(TokenizedString tokens, int index, double score)
    {
        this(tokens, index);
        this.score = score;
    }

    public Parsing(TokenizedString tokens, boolean [] boundaries, double score) {
        this(tokens, boundaries);
        this.score = score;
    }

    public Parsing(TokenizedString tokens, int index)
    {
        this.index = index;
        this.tokens = tokens;
        this.boundaries = new boolean[tokens.length-1];
        Utility.intToBooleans(index, this.boundaries);
        buildPhrases();
    }

    public Parsing(TokenizedString tokens, boolean [] boundaries){
        this.tokens=tokens;
        this.boundaries = boundaries;
        this.index = Utility.booleansToInt(boundaries);
        buildPhrases();
    }

    private void buildPhrases()
    {
        this.phrases= new TreeMap<Integer, NGram>();
        this.includeInParsing = new TreeMap<Integer, Boolean>();
        int i = 0;
        int nextStart= 0;
        while(i < tokens.length){
            nextStart= findNextPhraseStart(i);
            phrases.put(i, new NGram(tokens, i, nextStart - i));
            includeInParsing.put(i, true);
            i = nextStart;
        }
    }

    public void includePhraseInParsing(int index, boolean include){
        includeInParsing.put(index, include);
    }

    public int findNextPhraseStart(int after)
    {
        int i  = after;
        while(i < boundaries.length && !boundaries[i]) {
            ++i;
        }
        return i+1;
    }

    private String buildParsedQuery() {
        StringBuilder parsedQuery = new StringBuilder();
        boolean start = true;
        int index = 0;
        for(Iterator<Integer> it = getPhraseIterator(); it.hasNext();)
        {
            index = it.next();
            if(!start) {
                parsedQuery.append(tokens.separators[index-1]);
            } parsedQuery.append("{");
            start= false;
            parsedQuery.append(phrases.get(index).toString());
            parsedQuery.append("}");
        }
        return parsedQuery.toString();
    }

    private String [] buildPhrasesArray() {
        LinkedList<String> phrasesList = new LinkedList<String>();
        for(Iterator<Integer> it = getPhraseIterator(); it.hasNext();) {
            int index = it.next();
            if(includeInParsing.get(index)) {
                phrasesList.add(phrases.get(index).toString());
            }
        }
        return phrasesList.toArray(new String[phrasesList.size()]);
    }

    public int compareTo(Parsing p)
    {
        int comparison = Double.compare(score, p.score);
        return comparison == 0 ? Integer.compare(index, p.index) : comparison;
    }

    public Iterator<Integer> getPhraseIterator()
    {
        return phrases.navigableKeySet().iterator();
    }

    public NGram getPhraseAt(int index)
    {
        return phrases.get(index);
    }

    public TokenizedString getTokens() { return tokens; }


    @Override
    public String toString() {
        return buildParsedQuery();
    }

    public String[] toStringArray() {
        return buildPhrasesArray();
    }

    public double getScore() {return score; }

    public void incrementScore(double score) { this.score += score;}

    public int getIndex() { return index; }

    public boolean [] getBoundaries() {return boundaries; }

    public void setBoundaries(boolean [] boundaries) {
        this.boundaries = boundaries;
        buildPhrases();
    }
}
