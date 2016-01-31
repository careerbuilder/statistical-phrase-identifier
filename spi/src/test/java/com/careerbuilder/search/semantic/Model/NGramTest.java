package com.careerbuilder.search.semantic.Model;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class NGramTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");

    @Test
    public void _toCorpusString()
    {
        String expected = "Software Test engineer quality assurance";
        NGram target = new NGram(tokens, 0, 5);
        String actual = target.toCorpusString();
        Assert.assertTrue(expected.compareTo(actual) == 0);
    }

    @Test
    public void toLowerCorpusString()
    {
        String expected = "software test engineer quality assurance";
        NGram target = new NGram(tokens, 0, 5);
        String actual = target.toLowerCorpusString();
        Assert.assertTrue(expected.compareTo(actual) == 0);
    }

    public void getSubNGram()
    {
        String expected = "Software Test engineer";
        NGram target = new NGram(tokens, 0, 5);
        NGram subNGram = target.getSubNGram(0, 3);
        String actual = subNGram.toCorpusString();
        Assert.assertTrue(expected.compareTo(actual) == 0);
    }
}
