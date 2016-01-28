package com.careerbuilder.search.semantic;

import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.SolrIndexSearcher;

import java.io.IOException;
import java.util.HashMap;

public class DocFrequencySearcher {

    private SolrIndexSearcher searcher;

    public DocFrequencySearcher(SolrQueryRequest request)
    {
       this.searcher = request.getSearcher();
    }

    public FrequencyMap getDocFrequencyHashMap(TokenizedString tokens, FrequencyMap map) throws IOException {

        HashMap<String, Long> descFrequencyMap = new HashMap<String, Long>();
        HashMap<String, Long> titleFrequencyMap = new HashMap<String, Long>();

        for(int length = 1; length <= 3; ++length) {
            for (int i = 0; i < tokens.length - length + 1; ++i) {
                NGram nGram = new NGram(tokens, i, length);
                descFrequencyMap.put(nGram.toLowerCorpusString(), getDocFrequency(nGram, NGramField.content));
                titleFrequencyMap.put(nGram.toLowerCorpusString(), getDocFrequency(nGram, NGramField.content_title));
            }
        }
        map.descriptionDocFrequencyMap = descFrequencyMap;
        map.titleDocFrequencyMap = titleFrequencyMap;
        return map;
    }

    private long getDocFrequency(NGram nGram, NGramField field) throws IOException {
        TermQuery[] query = new TermQuery[nGram.getLength()];
        BooleanQuery booleanQuery = new BooleanQuery();

        for(int i = 0; i < nGram.getLength(); ++i)
        {
            query[i] = new TermQuery(new Term(field.toString(), nGram.getSubNGram(i,1).toLowerCorpusString()));
            booleanQuery.add(query[i], BooleanClause.Occur.MUST);
        }
        TotalHitCountCollector collector = new TotalHitCountCollector();
        searcher.search(booleanQuery, collector);
        return collector.getTotalHits();
    }
}
