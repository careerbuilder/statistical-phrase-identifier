package com.careerbuilder.search.semantic;

import com.careerbuilder.search.semantic.Models.FrequencyMap;
import com.careerbuilder.search.semantic.Models.NGramField;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.SolrIndexSearcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@RunWith(JMockit.class)
public class TermFrequencySearcherTest {
    @Mocked SolrIndexSearcher mockSearcher;
    @Mocked IndexReaderContext mockedTopContext;
    @Mocked List<IndexReaderContext> mockedLeafList;
    @Mocked Iterator<IndexReaderContext> mockedIterator;
    @Mocked IndexReaderContext mockedContext;
    @Mocked IndexReader mockedReader;
    @Mocked SolrQueryRequest mockedRequest;

    @Before
    public void init()
            throws IOException
    {

    }

    private void setUpRequestMock()
            throws IOException
    {
        new Expectations() {{
            mockedRequest.getSearcher(); returns(mockSearcher);
        }};
    }

    private void setUpFrequencyMocks(final NGramField field, final String term, final Long returnedValue)
            throws IOException
    {
        new Expectations() {{
            mockSearcher.getTopReaderContext(); returns(mockedTopContext);
            mockedTopContext.leaves(); returns(mockedLeafList);
            mockedLeafList.iterator(); returns(mockedIterator);
            mockedIterator.hasNext(); returns(true);
            mockedIterator.next(); returns(mockedContext);
            mockedContext.reader(); returns(mockedReader);
            if(term == null) {
                mockedReader.getSumTotalTermFreq(field.toString());
                returns(returnedValue);
            }
            else {
                mockedReader.totalTermFreq(new Term(field.toString(), term));
                returns(returnedValue);
            }
            mockedIterator.hasNext(); returns(false);
        }};
    }

    @Test
    public void getFrequencyHashMap() throws IOException
    {
        setUpRequestMock();
        setUpFrequencyMocks(NGramField.content, "one", 1L);
        setUpFrequencyMocks(NGramField.content_title, "one", 1L);
        setUpFrequencyMocks(NGramField.content, "two", 1L);
        setUpFrequencyMocks(NGramField.content_title, "two", 1L);
        setUpFrequencyMocks(NGramField.content, "three", 1L);
        setUpFrequencyMocks(NGramField.content_title, "three", 1L);
        setUpFrequencyMocks(NGramField.bigrams, "one two", 1L);
        setUpFrequencyMocks(NGramField.bigrams_title, "one two", 1L);
        setUpFrequencyMocks(NGramField.bigrams, "two three", 1L);
        setUpFrequencyMocks(NGramField.bigrams_title, "two three", 1L);
        setUpFrequencyMocks(NGramField.trigrams, "one two three", 1L);
        setUpFrequencyMocks(NGramField.trigrams_title, "one two three", 1L);

        System.out.println(NGramField.content_title);
        FrequencyMap map = new FrequencyMap();
        TermFrequencySearcher target = new TermFrequencySearcher(mockedRequest);
        map = target.getFrequencyHashMap(
                new TokenizedString("one two three"), map);
        Assert.assertEquals("term frequency for unigram 'one' expected equals actual",
                (long) 1L, (long) map.descriptionTermFrequencyMap.get("one"));
        Assert.assertEquals("term total frequency for unigram 'two' expected equals actual",
                (long) 1L, (long) map.descriptionTermFrequencyMap.get("two"));
        Assert.assertEquals("term total frequency for unigram 'three' expected equals actual",
                (long)1L, (long)map.descriptionTermFrequencyMap.get("three"));
        Assert.assertEquals("term frequency for bigram 'one two' expected equals actual",
                (long)1L, (long)map.descriptionTermFrequencyMap.get("one two"));
        Assert.assertEquals("term total frequency for bigram 'two three' expected equals actual",
                (long)1L, (long)map.descriptionTermFrequencyMap.get("two three"));
        Assert.assertEquals("term total frequency for unigram 'one two three' expected equals actual",
                (long)1L, (long)map.descriptionTermFrequencyMap.get("one two three"));
    }

    @Test
    public void getFrequency() throws IOException
    {
        setUpRequestMock();
        setUpFrequencyMocks(NGramField.bigrams, "underwater basketweaver", 1L);
        TermFrequencySearcher target = new TermFrequencySearcher(mockedRequest);
        Long actual = Deencapsulation.invoke(target,"getFrequency", NGramField.bigrams, "underwater basketweaver");
        Long expected = 1L;
        Assert.assertEquals("term frequency expected equals actual", expected, actual);
    }

    @Test
    public void getTotalFrequency() throws IOException
    {
        setUpRequestMock();
        setUpFrequencyMocks(NGramField.content, null, 1L);
        setUpFrequencyMocks(NGramField.bigrams, null, 1L);
        setUpFrequencyMocks(NGramField.trigrams, null, 1L);
        TermFrequencySearcher target = new TermFrequencySearcher(mockedRequest);

        FrequencyMap map = new FrequencyMap();

        Deencapsulation.invoke(target,"getTotalFrequencies", map);
        Assert.assertEquals("term total frequency for unigrams expected equals actual",
                (long) 1L, (long) map.totalDescriptionTermFrequencyMap.get(NGramField.content));
        Assert.assertEquals("term total frequency for bigrams expected equals actual",
                (long)1L, (long)map.totalDescriptionTermFrequencyMap.get(NGramField.bigrams));
        Assert.assertEquals("term total frequency for trigrams expected equals actual",
                (long)1L, (long)map.totalDescriptionTermFrequencyMap.get(NGramField.trigrams));
    }

}
