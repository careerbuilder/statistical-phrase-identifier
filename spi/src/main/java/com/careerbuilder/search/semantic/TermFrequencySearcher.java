package com.careerbuilder.search.semantic;

import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.SolrIndexSearcher;

import java.io.IOException;
import java.util.HashMap;

public class TermFrequencySearcher {
    
    private SolrIndexSearcher searcher;
    
    public TermFrequencySearcher(SolrQueryRequest request)
    {
       this.searcher = request.getSearcher();
    }

    public FrequencyMap getFrequencyHashMap(TokenizedString tokens, FrequencyMap map) throws IOException {

        HashMap<String, Long> descriptionFrequencyMap = new HashMap<String, Long>();
        HashMap<String, Long> titleFrequencyMap = new HashMap<String, Long>();

        for (int i = 0; i < tokens.length; ++i) {
            NGram nGram = new NGram(tokens, i, 1);
            descriptionFrequencyMap.put(nGram.toLowerCorpusString(), getFrequency(NGramField.content, nGram.toLowerCorpusString()));
            titleFrequencyMap.put(nGram.toLowerCorpusString(), getFrequency(NGramField.content_title, nGram.toLowerCorpusString()));
        }
        for (int i = 0; i < tokens.length - 1; ++i) {
            NGram nGram = new NGram(tokens, i, 2);
            descriptionFrequencyMap.put(nGram.toLowerCorpusString(), getFrequency(NGramField.bigrams, nGram.toLowerCorpusString()));
            titleFrequencyMap.put(nGram.toLowerCorpusString(), getFrequency(NGramField.bigrams_title, nGram.toLowerCorpusString()));
        }
        for (int i = 0; i < tokens.length - 2; ++i) {
            NGram nGram = new NGram(tokens, i, 3);
            descriptionFrequencyMap.put(nGram.toLowerCorpusString(), getFrequency(NGramField.trigrams, nGram.toLowerCorpusString()));
            titleFrequencyMap.put(nGram.toLowerCorpusString(), getFrequency(NGramField.trigrams_title, nGram.toLowerCorpusString()));
        }

        map.descriptionTermFrequencyMap = descriptionFrequencyMap;
        map.titleTermFrequencyMap = titleFrequencyMap;
        return map;
    }

    public FrequencyMap getTotalFrequencies(FrequencyMap map) throws IOException {

        HashMap<NGramField, Long> totalFrequencies = new HashMap<NGramField, Long>();
        totalFrequencies.put(NGramField.content, getFrequency(NGramField.content));
        totalFrequencies.put(NGramField.bigrams, getFrequency(NGramField.bigrams));
        totalFrequencies.put(NGramField.trigrams, getFrequency(NGramField.trigrams));

        map.totalDescriptionTermFrequencyMap = totalFrequencies;
        return map;
    }

    private long getFrequency(NGramField field) throws IOException
    {   
        return getFrequency(field, null);
    }

    private long getFrequency(NGramField field, String q) throws IOException {
        long totalTermFreq = 0;
        for (IndexReaderContext readerContext : searcher.getTopReaderContext().leaves()) {
            long val = q == null ? readerContext.reader().getSumTotalTermFreq(field.toString())
                    : readerContext.reader().totalTermFreq(new Term(field.toString(), q));
            if (val == -1) {
                totalTermFreq = -1;
                break;
            } else {
                totalTermFreq += val;
            }
        }
        return totalTermFreq;
    }

}
