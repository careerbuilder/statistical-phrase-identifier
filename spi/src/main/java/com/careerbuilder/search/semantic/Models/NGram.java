package com.careerbuilder.search.semantic.Models;

import org.apache.solr.common.SolrException;

public class NGram {
    private TokenizedString tokens;
    private int start;
    private int length;

    public NGram(TokenizedString tokens, int start, int length) {
        if(length < 0)
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Attempt to create an NGram with length less than 0.");
        this.tokens = tokens;
        this.start = start;
        this.length = length;
    }

    public String toCorpusString() {
        StringBuilder builder = new StringBuilder(tokens.tokens[start]);
        for (int i = start + 1; i < start + length; ++i){
            builder.append(" ");
            builder.append(tokens.tokens[i]);
        }
        return builder.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(tokens.tokens[start]);
        for (int i = start + 1; i < start + length; ++i){
            builder.append(tokens.separators[i-1]);
            builder.append(tokens.tokens[i]);
        }
        return builder.toString();
    }

    public String toLowerCorpusString(){
        return this.toCorpusString().toLowerCase();
    }

    public NGram getSubNGram(int from, int len)
    {
        return new NGram(tokens, start+from, len);
    }

    public int getStart() {
        return start;
    }

    public int getLength(){
        return length;
    }

}
